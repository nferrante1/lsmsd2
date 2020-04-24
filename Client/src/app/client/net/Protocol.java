package app.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import app.common.net.ActionRequest;
import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.UserInfo;

public class Protocol implements AutoCloseable
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private static Protocol instance;
	private String authToken;
	private LoginInfo loginInfo;

	private Protocol()
	{
	}

	public static Protocol getInstance()
	{
		if(instance == null)
			instance = new Protocol();
		return instance;
	}

	private void connect()
	{
		try {
			socket = new Socket("127.0.0.1", 8888); // TODO: get ip and port from cmdline
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public ResponseMessage performLogin(String username, String password)
	{
		return performLogin(new LoginInfo(username, password));
	}

	public ResponseMessage performLogin(LoginInfo loginInfo)
	{
		this.loginInfo = loginInfo;
		return performLogin();
	}

	protected ResponseMessage performLogin()
	{
		if (loginInfo == null)
			return new ResponseMessage("Username/Password not specified.");
		connect();
		new RequestMessage(ActionRequest.LOGIN, loginInfo).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		if (!resMsg.isSuccess())
			return resMsg;
		if (!resMsg.isValid(ActionRequest.LOGIN))
			return getProtocolErrorMessage();
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

	public ResponseMessage browseDataSource() 
	{
		return sendRequest(ActionRequest.BROWSE_DATA_SOURCE);
	}

	public ResponseMessage changeDataSource(SourceInfo info)
	{
		return sendRequest(ActionRequest.CHANGE_DATA_SOURCE);
	}

	public ResponseMessage browseUsers(BrowseInfo browseInfo)
	{
		return sendRequest(ActionRequest.BROWSE_USERS, browseInfo);
	}

	public ResponseMessage addUser(String username, String password)
	{
		return addUser(new LoginInfo(username, password));
	}

	public ResponseMessage addUser(LoginInfo info)
	{
		return sendRequest(ActionRequest.ADD_USER);
	}

	public ResponseMessage deleteUser(UserInfo info)
	{
		return sendRequest(ActionRequest.DELETE_USER);
	}

	public ResponseMessage browseStrategy(BrowseInfo info)
	{
		return sendRequest(ActionRequest.BROWSE_STRATEGY, info);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		connect();
		ResponseMessage resMsg;
		new RequestMessage(actionRequest, authToken, entities).send(outputStream);
		resMsg = ResponseMessage.receive(inputStream);
		if (!resMsg.isSuccess() && resMsg.getErrorMsg().startsWith("NO-AUTH")) {
			resMsg = performLogin();
			if (!resMsg.isSuccess())
				return resMsg;
		}
		new RequestMessage(actionRequest, authToken, entities).send(outputStream);
		resMsg = ResponseMessage.receive(inputStream);
		return resMsg != null && resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}

	private ResponseMessage getProtocolErrorMessage()
	{
		return new ResponseMessage("Invalid response from server.");
	}

	public boolean isAdmin()
	{
		return authToken.charAt(0) == '0';
	}

	@Override
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}