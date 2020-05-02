package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ScraperController {
	
	static public void start() {
		try {
			Socket socket = new Socket("127.0.0.1", 5656);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			
			output.writeUTF("START");
			output.flush();
			String msg = "";
			while(!msg.equals("ACK"))
				msg= input.readUTF();
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	static public void stop() {
		
		try {
			Socket socket = new Socket("127.0.0.1", 5656);
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			
			output.writeUTF("STOP");
			output.flush();
			String msg = "";
			while(!msg.equals("ACK"))
				msg= input.readUTF();
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
