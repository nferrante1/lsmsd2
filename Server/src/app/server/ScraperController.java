package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ScraperController {
	private static Socket socket;
	private static DataInputStream input;
	private static DataOutputStream output;
	private static boolean connected;
	static private synchronized void connect() {
		if(connected)
			return;
		try {
			socket = new Socket("127.0.0.1",5656);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	static private synchronized void close() {
		if(!connected)
			return;
		try {		
			input.close();
			output.close();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	static public synchronized void start() {
		try {
		output.writeUTF("START");
		output.flush();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		close();
	};
	static public synchronized void stop() {
		connect();
		try {
		output.writeUTF("STOP");
		output.flush();
		String ack = input.readUTF();
		if(!ack.equals("ACK"))
			//Errore
			return;
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
