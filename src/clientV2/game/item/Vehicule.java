package clientV2.game.item;

import clientV2.game.Window;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Vehicule extends Rectangle {
	public static final int longueur = 40;
	public static final int largeur = 20;
	public static final double turnit = (2 * Math.PI) / 16;
	public static final double thrustit = 0.1;

	private double dx = 0, dy = 0;
	private double angle;

	private Circle ve_radius = new Circle();

	public Vehicule() {
		this.ve_radius.setRadius(
				Math.sqrt((Vehicule.longueur * Vehicule.longueur) + (Vehicule.largeur * Vehicule.largeur)) / 2);
		this.ve_radius.setCenterX(this.getX() + (Vehicule.longueur / 2));
		this.ve_radius.setCenterY(this.getY() + (Vehicule.largeur / 2));
		this.ve_radius.setVisible(false);
		this.setFill(Paint.valueOf("RED"));
		this.setX(translateServerToHost(0.0));
		this.setY(translateServerToHost(0.0));
	}

	public double translateServerToHost(double value) {
		return ((value * (Window.W_SIZE / 2)) + (Window.W_SIZE / 2));
	}

	public double translateHostToServer() {
		return (getX() - (Window.W_SIZE / 2)) / (Window.W_SIZE / 2);

	}

	public void move() {
		this.setX(this.getX() + this.dx);
		this.setY(this.getY() + this.dy);
	}

	public void clock() {
		this.angle -= Vehicule.turnit;
		this.setRotate(Math.toDegrees(this.angle));
	}

	public void anticlock() {
		this.angle += Vehicule.turnit;
		this.setRotate(Math.toDegrees(this.angle));
	}

	public void thrust() {
		this.dx += Math.cos(Math.toRadians(this.angle)) * Vehicule.thrustit;
		this.dy += Math.sin(Math.toRadians(this.angle)) * Vehicule.thrustit;
		this.dx = this.dx > 3 ? 3 : this.dx;
		this.dx = this.dx < -3 ? -3 : this.dx;
		this.dy = this.dy < -3 ? -3 : this.dy;
		this.dy = this.dy > 3 ? 3 : this.dy;
	}

	public void update() {
		move();
		if (this.getX() < 0) {
			System.out.println(toString());
			this.setX(getX() + Window.W_SIZE);
		}
		if (this.getY() < 0) {
			System.out.println(toString());
			this.setY(getY() + Window.W_SIZE);
		}

		if (this.getX() > Window.W_SIZE) {
			System.out.println(toString());
			this.setX(getX() - Window.W_SIZE);
		}
		if (this.getY() > Window.W_SIZE) {
			System.out.println(toString());
			this.setY(getY() - Window.W_SIZE);
		}

		// System.out.println("dx "+dx +"dy "+dy);

		this.ve_radius.setCenterX(this.getX());
		this.ve_radius.setCenterY(this.getY());
	}

	@Override
	public String toString() {
		return "Vehicule [x=" + this.getX() + ", y=" + this.getY() + ", dx=" + this.dx + ", dy=" + this.dy + ", angle="
				+ this.angle + "]";
	}

}