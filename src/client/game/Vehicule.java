package client.game;

import client.App;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Vehicule implements ObjectArena {
	public double x = 0;
	public double y = 0;
	public int longueur = 40;
	public int largeur = 20;
	public double dx = 0, dy = 0;
	public double turnit = 6 * (Math.PI / 180); // En radiant
	public static double thrustit = 0.005;
	public double angle = 0;
	public Rectangle view = new Rectangle(this.x, this.y, this.longueur, this.largeur);
	public Circle ve_radius = new Circle();

	public static final double MAX_SPEED = 0.05;

	public Vehicule() {
		this.ve_radius.setRadius(Math.sqrt((this.longueur * this.longueur) + (this.largeur * this.largeur)) / 2);
		this.ve_radius.setCenterX(this.x + (this.longueur / 2));
		this.ve_radius.setCenterY(this.y + (this.largeur / 2));
		this.ve_radius.setVisible(false);
		this.view.setFill(Paint.valueOf("RED"));
		this.x = 0;
		this.y = 0;
	}

	public void move() {
		this.x += this.dx;
		this.y += this.dy;

	}

	public void clock() {
		this.angle -= this.turnit;
		this.view.setRotate(Math.toDegrees(this.angle));
	}

	public void anticlock() {
		this.angle += this.turnit;
		this.view.setRotate(Math.toDegrees(this.angle));
	}

	public void thrust() {
		this.dx += Math.cos(this.angle) * Vehicule.thrustit;
		this.dy += Math.sin(this.angle) * Vehicule.thrustit;
		this.dx = this.dx > MAX_SPEED ? MAX_SPEED : this.dx;
		this.dx = this.dx < -MAX_SPEED ? -MAX_SPEED : this.dx;
		this.dy = this.dy < -MAX_SPEED ? -MAX_SPEED : this.dy;
		this.dy = this.dy > MAX_SPEED ? MAX_SPEED : this.dy;
	}

	public void update() {

		move();
		if (translateServerToHost(this.x) < 0) {
			// System.out.println(toString());
			this.x += 2;
		}
		if (translateServerToHost(this.y) < 0) {
			// System.out.println(toString());
			this.y += 2;
		}

		if (translateServerToHost(this.x) > App.SIZE) {
			// System.out.println(toString());
			this.x -= 2;
		}
		if (translateServerToHost(this.y) > App.SIZE) {
			// System.out.println(toString());
			this.y -= 2;
		}

		// System.out.println("dx "+dx +"dy "+dy);
		this.view.setTranslateX(translateServerToHost(this.x));
		this.view.setTranslateY(translateServerToHost(this.y));
		this.ve_radius.setTranslateX(translateServerToHost(this.x));
		this.ve_radius.setTranslateY(translateServerToHost(this.y));
	}

	public static double translateServerToHost(double value) {
		return ((value * (App.SIZE / 2)) + (App.SIZE / 2));
	}

	public static double translateHostToServer(double value) {
		return (value - (App.SIZE / 2)) / (App.SIZE / 2);
	}

	@Override
	public String toString() {
		return "Vehicule [x=" + this.x + ", y=" + this.y + ", dx=" + this.dx + ", dy=" + this.dy + ", angle="
				+ this.angle + "]";
	}

	@Override
	public Node getView() {
		return this.view;
	}

	public boolean isColliding(ObjectArena other) {
		return this.ve_radius.getBoundsInParent().intersects(other.getView().getBoundsInParent());
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