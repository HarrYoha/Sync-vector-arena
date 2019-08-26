package test;

import client.Vehicule;

public class TestVehicule {

	public static void main(String[] args) {
		Vehicule v = new Vehicule();

		v.thrust();
		System.out.println(v);

		v.clock();
		System.out.println(v);

		v.move();
		System.out.println(v);

		v.thrust();
		System.out.println(v);

		v.clock();
		System.out.println(v);

		v.move();
		System.out.println(v);

	}

}
