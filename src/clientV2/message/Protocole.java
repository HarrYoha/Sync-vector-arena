package clientV2.message;

public enum Protocole {
	CONNECT("CONNECT"), NEWPLAYER("NEWPLAYER"), WELCOME("WELCOME"), DENIED("DENIED");

	private String name;

	private Protocole(String s) {
		this.name = s;
	}
}
