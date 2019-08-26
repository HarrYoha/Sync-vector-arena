package client.message;

public class MessageParser {
	public static String[] parseMessage(String message) {
		return message.split("/");
	}
}
