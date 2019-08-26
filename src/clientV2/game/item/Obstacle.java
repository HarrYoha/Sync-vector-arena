package clientV2.game.item;

import clientV2.game.ObjectArena;
import clientV2.game.Window;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class Obstacle implements ObjectArena {

	public double x = 0, y = 0;
	public Node view;

	public Obstacle() {
		this.x = Math.random() * Window.W_SIZE;
		this.y = Math.random() * Window.W_SIZE;
		this.view = new Rectangle(Math.random() * 50, Math.random() * 50, Math.random() * 50, Math.random() * 50);
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
