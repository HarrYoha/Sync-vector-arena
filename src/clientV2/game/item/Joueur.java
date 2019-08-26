package clientV2.game.item;

public class Joueur {
	private String nom;
	private int points;
	private Vehicule vehicule;

	public Joueur(String nom) {
		this.nom = nom;
		this.points = 0;
		this.vehicule = new Vehicule();
	}

	public String getNom() {
		return this.nom;
	}

	public int getPoints() {
		return this.points;
	}

	public Vehicule getVehicule() {
		return this.vehicule;
	}
}
