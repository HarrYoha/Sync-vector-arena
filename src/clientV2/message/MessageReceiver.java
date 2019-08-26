package clientV2.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import client.message.MessageParser;

public class MessageReceiver {
	private BufferedReader reader;

	public MessageReceiver(Socket socket) {
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] waitMessage() {
		try {
			String message = this.reader.readLine();
			return MessageParser.parseMessage(message);

		} catch (IOException e) {
			System.err.println("Connection interrumpu");
			return null;
		}
	}

	public static String getProtocole(String message) {
		return getProtocole(message.split("/"));
	}

	public static String getProtocole(String[] messageParsed) {
		return messageParsed[0];
	}

}
