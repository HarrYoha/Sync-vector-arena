package client.game;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Obstacle implements ObjectArena {

	public double x = 0, y = 0;
	public Node view;
	public Circle ob_radius;
	public int longueur = 40;
	public int largeur = 40;

	public Obstacle(double x, double y) {
		this.x = x;
		this.y = y;
		this.view = new Rectangle(Vehicule.translateServerToHost(x), Vehicule.translateServerToHost(y), this.longueur,
				this.largeur);
		this.ob_radius = new Circle();
		this.ob_radius.setRadius(20);
		this.ob_radius.setCenterX(x + (this.longueur / 2));
		this.ob_radius.setCenterY(y + (this.largeur / 2));
		this.ob_radius.setVisible(false);
	}

	@Override
	public Node getView() {
		return this.view;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}
}
