package test;

import client.message.MessageParser;

public class TestMessageParser {

	public static void main(String[] args) {
		String message = "TEST/Bonjour/encore/une ligne/";

		String[] parsed = MessageParser.parseMessage(message);

		for (String part : parsed) {
			System.out.println("|" + part + "|");
		}

	}

}
