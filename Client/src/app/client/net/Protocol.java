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
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.Market;

public class Protocol implements AutoCloseable
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private static Protocol instance;
	private String authToken;

	private Protocol() throws IOException
	{
		Configuration config = Configuration.getConfig();
		this.socket = new Socket("127.0.0.1", 8888);//config.getServerIp(), config.getServerPort());
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
	}

	public static Protocol getInstance()
	{
		if(instance == null)
			try {
				instance = new Protocol();
			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		return instance;
	}

	public ResponseMessage performLogin(String username, String password)
	{
		return performLogin(new LoginInfo(username, password));
	}

	public ResponseMessage performLogin(LoginInfo loginInfo)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.LOGIN, loginInfo);
		if(resMsg.isSuccess())
			authToken = ((AuthTokenInfo)resMsg.getEntity(0)).getAuthToken();
		resMsg.getEntities().clear();
		return resMsg;
	}

	public ResponseMessage performLogout()
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.LOGOUT);
		authToken = null;
		return resMsg;
	}

	public ResponseMessage browseMarkets(String filter, int page)
	{
		return browseMarkets(new BrowseInfo(filter, page));
	}

	public ResponseMessage browseMarkets(int page)
	{
		return browseMarkets(new BrowseInfo(page));
	}

	public ResponseMessage browseMarkets(BrowseInfo browseInfo)
	{
		return sendRequest(ActionRequest.BROWSE_MARKET, browseInfo);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		new RequestMessage(actionRequest, authToken, entities).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		return resMsg != null && resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}

	private ResponseMessage getProtocolErrorMessage()
	{
		return new ResponseMessage("Invalid response from server.");
	}

	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
