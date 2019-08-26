package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import client.game.ObjectArena;
import client.game.Objectif;
import client.game.Obstacle;
import client.game.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {
    public final static int SIZE = 400;
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
    static int win_cap = 5;
    static int refresh_tickrate = 1_000_000;
    static ArrayList<Obstacle> obstacles = new ArrayList<>();
    static int nbThrust = 0;
    static double sumOfAngle = 0;
    static Listener listener;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(SIZE, SIZE);
        objectif = new Objectif();
        addGameObject(clientPlayer.v);
        addGameObject(objectif);
        for (Obstacle o : obstacles) {
            addGameObject(o);
        }

        for (Player p : players) {
            if (!p.name.equals(clientPlayer.name)) {
                addGameObject(p.v);
            }
        }
        clientPlayer.v.ve_radius.setTranslateX(clientPlayer.v.x);
        clientPlayer.v.ve_radius.setTranslateY(clientPlayer.v.y);
        root.getChildren().add(clientPlayer.v.ve_radius);


        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - this.lastUpdate) >= refresh_tickrate) {
                    for (Player p : players) {

                        onUpdate(p);
                    }
                    sendComms();
                    this.lastUpdate = now;
                }

            }
        };
        timer.start();
        return root;
    }

    private void onUpdate(Player p) {

        for (Player player : players) {
            if (p.v.isColliding(player.v) && !p.name.equals(player.name)) {
                p.v.dx = -p.v.dx;
                p.v.dy = -p.v.dy;
            }
        }
        for (Obstacle obstacle : obstacles) {
            if (p.v.isColliding(obstacle)) {
                p.v.dx = -p.v.dx;
                p.v.dy = -p.v.dy;
            }
        }
        p.v.update();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.getScene().setOnKeyPressed(event -> {
            handleSendProtocole(event.getCode());
        });
        stage.setTitle("Roquette lig");
        stage.show();
    }

    public static void handleSendProtocole(KeyCode clientInput) {

        if ((clientInput == KeyCode.C) && !isConnected) {
            System.out.println("user input " + clientInput);
            listener = new Listener(socket);
            Thread l = new Thread(listener);
            l.start();
            out.println("CONNECT/" + username + "/");

        }

        if (clientInput == KeyCode.V) {
            //Listener.getObstaclesFromServer("X12Y12|X988Y55|X55Y44|X255Y255");
        }

        if (clientInput == KeyCode.LEFT) {
            clientPlayer.v.clock();
            sumOfAngle -= clientPlayer.v.turnit;
        }

        if (clientInput == KeyCode.RIGHT) {
            clientPlayer.v.anticlock();
            sumOfAngle += clientPlayer.v.turnit;
        }

        if (clientInput == KeyCode.UP) {
            clientPlayer.v.thrust();
            nbThrust += 1;
        }

        if ((clientInput == KeyCode.E) && isConnected && inGame) {
            out.println("EXIT/" + username + "/");

        }
    }

    private void checkWinner(Player p) {
        if (win_cap == p.score) {
            System.out.println("the winner is " + p.name);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information we have a WINNER");
            alert.setHeaderText("end of the game");
            alert.setContentText(p.name + " is the winner");
            alert.show();
            inGame = false;
        }
    }

    public static void listCommands() {
        System.out.println("press C to connect to the server game");
        System.out.println("press E to exit the game");
        System.out.println("press LEFT key to rotate clockwise the vehicule");
        System.out.println("press RIGHT key to rotate anticlockwise your vehicule");
        System.out.println("press UP key to give a thrust to the vehicule");
    }

    public void sendPos() {
        if (isConnected && inGame) {
            double sendx = clientPlayer.v.x - (SIZE / 2);
            double sendy = clientPlayer.v.y - (SIZE / 2);
            out.println("NEWPOS/X" + sendx + "Y" + sendy);
        }
    }

    public void sendComms() {
        if (isConnected && inGame) {
            out.println("NEWCOM/A" + sumOfAngle + "T" + nbThrust + "/X" + clientPlayer.v.x + "Y" + clientPlayer.v.y);
            sumOfAngle = 0;
            nbThrust = 0;
        }
    }

    public static void addGameObject(ObjectArena object) {
        object.getView().setTranslateX(object.getX());
        object.getView().setTranslateY(object.getY());
        root.getChildren().add(object.getView());
    }

    public static void main(String[] args) {
        try {
            System.out.println("choose your username");
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            username = stdIn.readLine();
            socket = new Socket(InetAddress.getLocalHost(), 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            clientPlayer = new Player(username);
            players.add(clientPlayer);
            listCommands();
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