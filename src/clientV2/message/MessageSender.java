package clientV2.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender {

	private PrintWriter writer;

	public MessageSender(Socket socket) {
		try {
			this.writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String... args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append("/");
		}

		this.writer.println(sb.toString());
		this.writer.flush();
	}

	public void sendConnect(String name) {
		send("CONNECT", name);
	}

	public void sendExit(String name) {
		send("EXIT", name);

	}
}
