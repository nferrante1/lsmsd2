package app.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import app.client.config.Configuration;
import app.common.net.ActionRequest;
import app.common.net.Message;
import app.common.net.RequestBrowse;
import app.common.net.RequestLogin;
import app.common.net.RequestMessage;
import app.common.net.ResponseList;
import app.common.net.ResponseLogin;
import app.common.net.ResponseMessage;
import app.common.net.entities.Market;

public class Protocol implements AutoCloseable
{
	private Socket socket = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	private static Protocol instance = null;
	protected static String authToken = null;

	private Protocol() throws IOException
	{
		Configuration config = Configuration.getConfig();
		this.socket = new Socket("127.0.0.1", 8888);//config.getServerIp(), config.getServerPort());
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		//Logger.getLogger(Protocol.class.getName()).info("Connected to " + config.getServerIp() + ":" + config.getServerPort() + ".");
	}

	public static Protocol getInstance()
	{
		if(instance == null)
			try {
				instance = new Protocol();
			} catch (IOException ex) {
				Logger.getLogger(Protocol.class.getName()).severe("Unhable to connect to server: " + ex.getMessage());
				System.exit(1);
			}
		return instance;
	}


	public ResponseMessage performLogin(String username, String password)
	{
		RequestLogin request = new RequestLogin(ActionRequest.LOGIN, username, password);
		request.send(outputStream);
		ResponseLogin response = (ResponseLogin) Message.receive(inputStream);
		if(response.isSuccess())
			authToken = response.getAuthToken();
		return response;
	}

	public ResponseMessage performLogout()
	{
		RequestMessage request = new RequestMessage(ActionRequest.LOGOUT, authToken);
		request.send(outputStream);
		ResponseMessage response = (ResponseMessage) Message.receive(inputStream);
		authToken = null;
		return response;
	}

	public ResponseMessage browseMarket(String filter, int numPage)
	{
		RequestBrowse request = new RequestBrowse(ActionRequest.BROWSE_MARKET, authToken, filter, numPage);
		request.send(outputStream);
		ResponseList<Market> response = (ResponseList<Market>) Message.receive(inputStream);
		return response;
	}

	
	
//	private ResponseMessage getProtocolErrorMessage()
//	{
//		Logger.getLogger(Protocol.class.getName()).warning("Received an invalid response from server.");
//		return new ResponseMessage("Invalid response from server.");
//	}

	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
