package client;

import static client.App.SIZE;
import static client.App.addGameObject;
import static client.App.clientPlayer;
import static client.App.inGame;
import static client.App.isConnected;
import static client.App.objectif;
import static client.App.obstacles;
import static client.App.players;
import static client.App.root;
import static client.App.stdIn;
import static client.App.username;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.game.Obstacle;
import client.game.Player;
import client.game.Vehicule;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Listener implements Runnable {
	public BufferedReader in = null;
	public Socket socket = null;

	public Listener(Socket socket) {
		this.socket = socket;
	}

	public static void handleReceivedProtocole(String[] message) {
		String protocole = message[0].trim();
		System.out.println(protocole);
		if (protocole.equals("WELCOME")) {
			welcomeReceived(message);
		}
		if (protocole.equals("DENIED")) {
			deniedReceived();
		}
		if (protocole.equals("NEWPLAYER")) {
			newPlayerReceived(message);
		}
		if (protocole.equals("PLAYERLEFT")) {
			playerLeftReceived(message);
		}
		if (protocole.equals("SESSION")) {
			sessionReceived(message);
		}
		if (protocole.equals("WINNER")) {
			winnerReceived(message);
		}
		if (protocole.equals("TICK")) {
			tickReceived(message);
		}
		if (protocole.equals("NEWOBJ")) {
			newObjReceived(message);
		}

	}

	public static void welcomeReceived(String[] message) {
		System.out.println("Nous sommes en phase de " + message[1]);
		System.out.println("Les scores sont à " + message[2]);
		if (message[1].equals("jeu")) {
			inGame = true;
		}
		if (message[1].equals("attente")) {
			inGame = false;
		}
		List<String[]> scores = getScore(message[2]);
		for (String[] score : scores) {
			players.add(new Player(score[0], Integer.parseInt(score[1])));

		}

		printScore(players);
		double x = getCoord(message[3])[0];
		double y = getCoord(message[3])[1];
		objectif.initFromServer(x, y);
		isConnected = true;
		System.out.println("Le prochain objectif est dans X" + objectif.x + "Y " + objectif.y);
	}

	public static void deniedReceived() {
		System.out.println("Connexion refusée, essayer avec un nouveau nom ?");
		try {
			players.remove(clientPlayer);
			username = stdIn.readLine();
			clientPlayer = new Player(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void playerLeftReceived(String[] message) {
		System.out.println("Le joueur " + message[1] + " s'est deconnecté");

		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).name.equals(message[1])) {
				System.out.println("hello " + players.get(i).name);
				root.getChildren().remove(players.get(i).v.getView());
				players.remove(players.get(i));
			}
		}
		for (Player p : players) {
			System.out.println("the players are " + p.name);
		}
	}

	public static void newPlayerReceived(String[] message) {
		System.out.println("Nouveau joueur connecté " + message[1]);
		Platform.runLater(() -> {
			boolean found = false;
			for (Player p : players) {
				if (p.name.equals(message[1])) {
					found = true;
				}
			}
			if (!found) {
				Player player = new Player(message[1]);
				addGameObject(player.v);
			}
		});

	}

	public static void winnerReceived(String[] message) {
		Platform.runLater(() -> {

			setScore(message[1], players);
			int s = 0;
			String name = "";
			for (Player p : players) {
				if (p.score > s) {
					s = p.score;
					name = p.name;
				}
			}
			inGame = false;
			System.out.println("les scores finaux sont " + message[1]);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("There is a winner ");
			alert.setHeaderText("Winner");
			alert.setContentText("The winner is " + name + " !");

			alert.show();

			System.out.println("le gagnant est " + name);
		});
	}

	public static void sessionReceived(String[] message) {
		for (int i = 0; i < message.length; i++) {
			System.out.println(i + " " + message[i]);
		}

		System.out.println("Debut d'une nouvelle session les coordonees du vehicule sont" + message[1]
				+ " et l'objectif est situe " + message[2]);

		Platform.runLater(() -> {
			getCoordSFromServer(message[1]);
			for (Player p : players) {
				if (!p.name.equals(clientPlayer.name)) {
					addGameObject(p.v);
				}
			}
			printScore(players);
			objectif.initFromServer(getCoord(message[2])[0], getCoord(message[2])[1]);
			getObstaclesFromServer(message[3]);
		});
	}

	public static void tickReceived(String[] message) {
		getVcoords(message[1]);
		// getCoordSFromServer(message[1]);
	}

	private static double[] getCoord(String s) {
		String regex = "[^X]*Y";
		double y = Double.parseDouble(s.split(regex)[1]);
		regex = "Y.*";
		double x = Double.parseDouble(s.split(regex)[0].substring(1));
		double[] res = { x, y };
		return res;
	}

	public static void getObstaclesFromServer(String s) {
		System.out.println("recus " + s);
		String[] obs = s.split("\\|");
		Platform.runLater(() -> {

			for (int i = 0; i < obs.length; i++) {
				System.out.println(obs[i]);
				double[] res = getCoord(obs[i]);
				Obstacle o = new Obstacle(res[0], res[1]);
				System.out.println("obstacle x " + o.x + " y " + o.y);
				o.view.setTranslateX(Vehicule.translateServerToHost(o.x));
				o.view.setTranslateY(Vehicule.translateServerToHost(o.y));
				o.ob_radius.setTranslateX(Vehicule.translateServerToHost(o.x));
				o.ob_radius.setTranslateY(Vehicule.translateServerToHost(o.y));
				obstacles.add(o);
			}

			for (Obstacle o : obstacles) {
				App.addGameObject(o);
			}
		});
	}

	public static void getCoordSFromServer(String s) {
		Platform.runLater(() -> {

			String[] player = s.split("\\|");

			for (int i = 0; i < player.length; i++) {
				String[] score = player[i].split(":");
				for (Player p : players) {
					System.out.println("score de 1 " + score[1]);
					if (p.name.equals(score[0])) {
						p.v.x = getCoord(score[1])[0];
						p.v.y = getCoord(score[1])[1];
						p.v.view.setTranslateX(p.v.x + (SIZE / 2));
						p.v.view.setTranslateY(p.v.y + (SIZE / 2));
						p.v.ve_radius.setTranslateX(p.v.x + (SIZE / 2));
						p.v.ve_radius.setTranslateY(p.v.y + (SIZE / 2));

					}
				}
			}
		});
	}

	public static List<String[]> getScore(String s) {
		List<String[]> res = new ArrayList<>();
		String[] player = s.split("\\|");
		for (int i = 0; i < player.length; i++) {
			res.add(player[i].split(":"));
		}

		return res;
	}

	public static void getVcoords(String s) {
		Platform.runLater(() -> {
			String[] listPlayer = s.split("\\|");

			for (int i = 0; i < listPlayer.length; i++) {
				String[] player = listPlayer[i].split(":");

				String name = player[0];
				for (Player p : players) {
					if (p.name.equals(name)) {

						String coord = player[1];
						String xy = coord.split("VX.*")[0];
						p.v.x = Double.parseDouble(xy.split("Y.*")[0].substring(1));
						p.v.y = Double.parseDouble(xy.split("^X.*Y")[1]);
						String[] vxy = coord.split("T");
						p.v.angle = Double.parseDouble(vxy[1]);
						String[] a = vxy[0].split("X.*VX");
						p.v.dx = Double.parseDouble(a[1].split("VY")[0]);
						p.v.dy = Double.parseDouble(a[1].split("VY")[1]);
						p.v.view.setTranslateX(p.v.x + p.v.dx);
						p.v.view.setTranslateY(p.v.y + p.v.dy);
						p.v.ve_radius.setTranslateX(p.v.x + p.v.dx);
						p.v.ve_radius.setTranslateY(p.v.y + p.v.dy);
					}
				}
			}
		});
	}

	public static void setScore(String score, List<Player> players) {
		for (Player p : players) {
			for (String[] s : getScore(score)) {
				if (p.name.equals(s[0])) {
					p.score = Integer.parseInt(s[1]);
				}
			}
		}
	}

	public static void printScore(List<Player> players) {
		for (Player p : players) {
			System.out.println("player " + p.name + " score " + p.score);
		}
	}

	public static void newObjReceived(String[] message) {
		Platform.runLater(() -> {

			System.out.println("nouveau objectif au coordonne " + message[1]);
			objectif.initFromServer(getCoord(message[1])[0], getCoord(message[1])[1]);
			setScore(message[2], players);
			printScore(players);
		});
	}

	@Override
	public void run() {
		try {
			System.out.println("Thread for input stream launch");
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			while (true) {
				String s;
				try {
					s = this.in.readLine();
					if (s == null) {
						break;
					}
					String[] message = s.split("/");
					handleReceivedProtocole(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
