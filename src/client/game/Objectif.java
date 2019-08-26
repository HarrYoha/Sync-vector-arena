package client.game;

import client.App;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Objectif implements ObjectArena {
	public double x = 0, y = 0;
	public Node view;

	public Objectif() {

		this.x = Math.random() * App.SIZE;
		this.y = Math.random() * App.SIZE;

		this.view = new Circle(10, 10, 10, Paint.valueOf("BLUE"));
	}

	public void move() {
		this.view.setTranslateX(Math.random() * App.SIZE);
		this.view.setTranslateY(Math.random() * App.SIZE);
	}

	public void initFromServer(double x, double y) {
		this.x = y;
		this.y = y;
		this.view.setTranslateX(Vehicule.translateServerToHost(x));
		this.view.setTranslateY(Vehicule.translateServerToHost(y));
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