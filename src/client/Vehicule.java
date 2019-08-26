package client;

public class Vehicule {
	protected double x = 0, y = 0;
	protected double dx = 0, dy = 0;
	protected double turnit = 1, thrustit = 1;
	protected double angle = 0;

	public void move() {
		this.x += this.dx;
		this.y += this.dy;
	}

	public void clock() {
		this.angle -= this.turnit;
		if (this.angle < 0) {
			this.angle += 2 * Math.PI;
		}
	}

	public void anticlock() {
		this.angle += this.turnit;
		if (this.angle > (Math.PI / 2)) {
			this.angle -= 2 * Math.PI;
		}
	}

	public void thrust() {
		this.dx += Math.cos(this.angle) * this.thrustit;
		this.dy += Math.sin(this.angle) * this.thrustit;
	}

	@Override
	public String toString() {
		return "Vehicule [x=" + this.x + ", y=" + this.y + ", dx=" + this.dx + ", dy=" + this.dy + ", angle="
				+ this.angle + "]";
	}

}
