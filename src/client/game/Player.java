package client.game;

public class Player {
    public String name;
    public Vehicule v;
    public int score = 0;
    public Player(String name){
        this.name = name;
        this.v = new Vehicule();
    }

    public Player(String name, int score){
        this.name = name;
        this.v = new Vehicule();
        this.score = score;
    }
}
