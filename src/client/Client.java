package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import client.game.ObjectArena;
import client.game.Objectif;
import client.game.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Client extends Application {
	public final static int SIZE = 400;
	public final int MAX_SCORE = 10;
	static Player clientPlayer;
	static Objectif objectif;
	static Pane root;
	static Socket socket = null;
	static PrintWriter out = null;
	static BufferedReader in = null;
	static ArrayList<Player> players = new ArrayList<>();
	static boolean isConnected = false;
	static boolean inGame = false;
	static String username;
	static BufferedReader stdIn;
	static int i = 0;
	static int win_cap = 5;
	static int refresh_tickrate = 1_000_000;
	static int server_tickrate = 100_000_000;

	private static void addGameObject(ObjectArena object, double x, double y) {
		object.getView().setTranslateX(x);
		object.getView().setTranslateY(y);
		root.getChildren().add(object.getView());
	}

	public static void handleSendProtocole(KeyCode clientInput) {

		if ((clientInput == KeyCode.C) && !isConnected) {
			System.out.println("user input " + clientInput);
			out.println("CONNECT/" + username + "/");
			read();
		}

		// for test
		if (clientInput == KeyCode.V) {
			out.println(" " + username + "/");
			read();
		}

		if (clientInput == KeyCode.LEFT) {
			clientPlayer.v.clock();
		}

		if (clientInput == KeyCode.RIGHT) {
			clientPlayer.v.anticlock();
		}

		if (clientInput == KeyCode.UP) {
			clientPlayer.v.thrust();
		}

		if ((clientInput == KeyCode.E) && isConnected && inGame) {
			out.println("EXIT/" + username + "/");
			read();
		}
	}

	private Parent createContent() {
		root = new Pane();
		root.setPrefSize(SIZE, SIZE);
		objectif = new Objectif();
		addGameObject(clientPlayer.v, clientPlayer.v.x + (Math.random() * SIZE),
				clientPlayer.v.y + (Math.random() * SIZE));
		addGameObject(objectif, objectif.x + 233, objectif.y + 933);
		AnimationTimer timer = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if ((now - this.lastUpdate) >= refresh_tickrate) {
					for (Player p : players) {
						onUpdate(p);
						// sendPos();
					}
					this.lastUpdate = now;
				}
				if ((now - this.lastUpdate) >= server_tickrate) {
					// read();
				}
			}
		};
		timer.start();
		return root;
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new Scene(createContent()));

		stage.getScene().setOnKeyPressed(event -> {
			System.out.println(event.getCode());
			handleSendProtocole(event.getCode());
		});
		stage.setTitle("Roquette lig");
		stage.show();
	}

	private void onUpdate(Player p) {
		// if(inGame && isConnected) {
		if (p.v.isColliding(objectif)) {
			objectif.move();
			p.score++;
			checkWinner(p);
			System.out.println("colision");
			System.out.println("Le joueur " + p.name + " a marque un point !");
		}
		i++;
		// System.out.println("i" + i);
		p.v.update();

		// }
	}

	private void checkWinner(Player p) {
		if (win_cap == p.score) {
			System.out.println("the winner is " + p.name);
		}
		// fin de session arreter la partie
	}

	public static void listCommands() {
		System.out.println("type \"C\" to connect to the server game");
		System.out.println("type \"E\" to exit the game");

		// a modifier
		System.out.println("type \"newpos\" to send the postion to the server");

	}

	private static void read() {
		try {
			String[] message = in.readLine().split("/");
			System.out.println("protocole " + message[0].trim());
			handleReceivedProtocole(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleReceivedProtocole(String[] message) {
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
		// a prendre en compte dans le jeu
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
	}

	public static void playerLeftReceived(String[] message) {
		System.out.println("Le joueur " + message[1] + " s'est deconnecté");
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).name.equals(message[1])) {
				players.remove(players.get(i));
			}
		}
		for (Player p : players) {
			System.out.println("the players are " + p.name);
		}
	}

	public static void newPlayerReceived(String[] message) {
		System.out.println("Nouveau joueur connecté " + message[1]);
		players.add(new Player(message[1]));
	}

	public static void winnerReceived(String[] message) {
		System.out.println("les scores finaux sont " + message[1]);
		setScore(message[1], players);
	}

	// a modifier
	public static void sessionReceived(String[] message) {
		System.out.println("Debut d'une nouvelle session les coordonees du vehicule sont" + message[1]
				+ " et l'objectif est situe " + message[2]);

		getCoordSFromServer(message[1]);
		for (Player p : players) {
			if (!p.name.equals(username)) {
				addGameObject(p.v, p.v.x, p.v.y);
			}
		}
		printScore(players);
		objectif.initFromServer(getCoord(message[2])[0], getCoord(message[2])[1]);
	}

	public static void tickReceived(String[] message) {
		getCoordSFromServer(message[1]);
	}

	private static double[] getCoord(String s) {
		String regex = "[^X]*Y";
		System.out.println("gex of 1" + s.split(regex)[0]);
		double y = Double.parseDouble(s.split(regex)[1]);
		regex = "Y.*";
		double x = Double.parseDouble(s.split(regex)[0].substring(1));
		double[] res = { x, y };
		return res;
	}

	public static void getCoordSFromServer(String s) {
		String[] player = s.split("\\|");

		for (int i = 0; i < player.length; i++) {
			String[] score = player[i].split(":");
			for (Player p : players) {
				System.out.println("score de 0 " + score[0]);
				System.out.println("score de 1 " + score[1]);
				if (p.name.equals(score[0])) {
					p.v.x = getCoord(score[1])[0];
					p.v.y = getCoord(score[1])[1];
				}
			}
		}
	}

	public static List<String[]> getScore(String s) {
		List<String[]> res = new ArrayList<>();
		String[] player = s.split("\\|");
		for (int i = 0; i < player.length; i++) {
			res.add(player[i].split(":"));
		}

		return res;
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

	public void sendPos() {
		if (isConnected && inGame) {
			out.println("NEWPOS/X" + clientPlayer.v.x + "Y" + clientPlayer.v.y);
		}

	}

	// a prendre en compte
	public static void newObjReceived(String[] message) {
		System.out.println("nouveau objectif au coordonne " + message[1]);
		objectif.initFromServer(getCoord(message[1])[0], getCoord(message[1])[1]);
		setScore(message[2], players);
		printScore(players);
	}

	public static void main(String[] args) {
		try {
			System.out.println("choose your username");
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			username = stdIn.readLine();
			socket = new Socket("10.64.0.187", 8080);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			clientPlayer = new Player(username);
			players.add(clientPlayer);
			players.add(new Player("riri"));
			players.add(new Player("fifi"));
		} catch (UnknownHostException e) {
			System.err.println("host not found");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("exception with the I/O");

			System.exit(1);
		}
		launch(args);
	}
}