package clientV2.game;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import clientV2.game.item.Joueur;
import clientV2.game.item.Objectif;
import clientV2.game.item.Obstacle;
import clientV2.message.MessageReceiver;
import clientV2.message.MessageSender;
import clientV2.message.Protocole;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Window extends Application {

	private static Window window;

	public static Window getInstance() {
		if (window == null) {
			return new Window();
		}
		return window;
	}

	public final static int W_SIZE = 400;
	public static final int refresh_tickrate = 60;

	public static String addr;
	public static int port;

	private Pane root;
	private Joueur joueur;

	private List<Joueur> joueurs;
	private Objectif objectif;
	private List<Obstacle> obstacles;

	private MessageSender sender;
	private MessageReceiver receiver;

	@Override
	public void start(Stage stage) throws Exception {
		String name = "";
		Scanner sc = new Scanner(System.in);

		this.root = new Pane();

		try (Socket sock = new Socket(addr, port)) {
			this.sender = new MessageSender(sock);
			this.receiver = new MessageReceiver(sock);

			nameSelection: do {
				do {
					System.out.println("Choose your username : (letters only)");
					name = sc.nextLine();
				} while (!nomCorrecte(name));

				this.sender.sendConnect(name);

				String[] message = this.receiver.waitMessage();
				Protocole protocole = Protocole.valueOf(MessageReceiver.getProtocole(message));

				switch (protocole) {
				case WELCOME:
					this.joueur = new Joueur(name);
					startGame(stage, message);
					System.out.println("OK");
					break nameSelection;
				case DENIED:
					System.out.println("Erreur : Nom déjà utilisé ?");
					break;
				default:
					System.err.println("Message inattendu de type " + protocole.name());
					System.exit(-1);
					break;
				}

			} while (true);

			// TODO thread de lecture sur la socket

			while (true) {

				String[] message = this.receiver.waitMessage();

				Protocole protocole = Protocole.valueOf(MessageReceiver.getProtocole(message));
				System.out.println(protocole.name());

			}

		} catch (UnknownHostException e) {
			System.err.println("host not found");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("exception with the I/O");

		}
		sc.close();
	}

	private void startGame(Stage stage, String[] message) {
		System.out.println("startGame");
		stage.setScene(new Scene(createContent()));

		stage.getScene().setOnKeyPressed(event -> {
			handleSendProtocole(event.getCode());
		});
		stage.setTitle("Roquette lig");
		stage.show();

	}

	private void handleSendProtocole(KeyCode clientInput) {
		if (clientInput == KeyCode.LEFT) {
			this.joueur.getVehicule().clock();
		}

		if (clientInput == KeyCode.RIGHT) {
			this.joueur.getVehicule().anticlock();
		}

		if (clientInput == KeyCode.UP) {
			this.joueur.getVehicule().thrust();
		}

		if (clientInput == KeyCode.E) {
			this.sender.sendExit(this.joueur.getNom());
		}
	}

	private Parent createContent() {
		this.root = new Pane();
		this.root.setPrefSize(Window.W_SIZE, Window.W_SIZE);

		this.objectif = new Objectif();
		System.out.println(this.root == null);
		System.out.println(this.joueur == null);

		this.root.getChildren().add(this.joueur.getVehicule());

		AnimationTimer timer = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if ((now - this.lastUpdate) >= refresh_tickrate) {
					for (Joueur j : Window.this.joueurs) {
						j.getVehicule().move();
					}

					Window.this.joueur.getVehicule().move();

					this.lastUpdate = now;
				}
			}
		};
		timer.start();
		return this.root;
	}

	public static void main(String[] args) {

		addr = args[0];
		port = Integer.parseInt(args[1]);

		launch(args);

	}

	public static boolean nomCorrecte(String name) {
		for (int i = 0; i < name.length(); i++) {
			if (!Character.isAlphabetic(name.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
