package clientV2.game.item;

import clientV2.game.ObjectArena;
import clientV2.game.Window;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Objectif implements ObjectArena {
	public double x = 0, y = 0;
	public Node view;

	public Objectif() {

		this.x = Math.random() * Window.W_SIZE;
		this.y = Math.random() * Window.W_SIZE;

		this.view = new Circle(10, 10, 10, Paint.valueOf("BLUE"));
	}

	public void move() {
		this.view.setTranslateX(Math.random() * Window.W_SIZE);
		this.view.setTranslateY(Math.random() * Window.W_SIZE);
	}

	public void initFromServer(double x, double y) {
		this.x = x;
		this.y = y;
		this.view.setTranslateX(x);
		this.view.setTranslateY(y);
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