package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.common.net.ActionRequest;
import app.common.net.Message;
import app.common.net.RequestBrowse;
import app.common.net.RequestById;
import app.common.net.RequestLogin;
import app.common.net.RequestMessage;
import app.common.net.ResponseLogin;
import app.common.net.ResponseMessage;
import app.datamodel.AuthTokenManager;
import app.datamodel.PojoCursor;
import app.datamodel.SourcesManager;
import app.datamodel.UsersManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.StringWrapper;
import app.datamodel.pojos.User;


public class Client extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private AuthToken authToken;
	Client(Socket clientSocket)
	{
		Logger.getLogger(Client.class.getName()).info("New incoming connection from " +
			clientSocket.getRemoteSocketAddress() + "." +
			"Request handled by " + this.getName() + ".");
		socket = clientSocket;
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
			process();
		Logger.getLogger(Client.class.getName()).warning(getName() + ": interrupted. Exiting...");
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void process()
	{
		RequestMessage reqMsg = (RequestMessage)Message.receive(inputStream);
		if (reqMsg == null) {
			Logger.getLogger(Client.class.getName()).warning(getName() + ": failure in receiving message. Client probably terminated.");
			Thread.currentThread().interrupt();
			return;
		}
		/*if (!reqMsg.isValid()) {
			Logger.getLogger(Client.class.getName()).warning(getName() +
				": received an invalid request" +
				(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}*/
		
		Logger.getLogger(Client.class.getName()).info(getName() +
			": received " + reqMsg.getMessageType() + " request.");

		if(reqMsg.getMessageType() != ActionRequest.LOGIN) {
			AuthTokenManager authMan = new AuthTokenManager();
			authToken = authMan.find(reqMsg.getAuthToken());
			if(authToken == null) {
				ResponseMessage resMsg= new ResponseMessage(reqMsg.getMessageType(), false, "not authorized");
			}
		}
		ResponseMessage resMsg = null;
		switch (reqMsg.getMessageType()) {
		case LOGIN:
				resMsg = handleLogin((RequestLogin)reqMsg);
			break;
		case LOGOUT:
				resMsg = handleLogout();
			break;
		case BROWSE_STRATEGY:
			
			break;
		case VIEW_STRATEGY:
			
			break;
		case RUN_STRATEGY:
			
			break;
		case ADD_STRATEGY:
			
			break;
		case DELETE_STRATEGY:
			
			break;
		case DOWNLOAD_STRATEGY:
			
			break;
		case BROWSE_REPORT:
			
			break;
		case VIEW_REPORT:
	
			break;
		case DELETE_REPORT:
	
			break;
		case BROWSE_MARKET:
	
			break;
		case BROWSE_DATA_SOURCE:

			break;
		case ENABLE_DATA_SOURCE:
		
			break;
		case DISABLE_DATA_SOURCE:
			
			break;
		case BROWSE_USERS:
			
			break;
		case ADD_USER:

			break;
		case DELETE_USER:
		
			break;
		case DELETE_DATA:
	
			break;
		case CONFIG_MARKET:

			break;
		default:

		}

		Logger.getLogger(Client.class.getName()).info(getName() +
			": sending response.");
	}
	
	ResponseMessage handleLogin(RequestLogin reqLogin) {
		
		UsersManager usMan = new UsersManager();
		User u = usMan.find(reqLogin.getUsername());
		if(!u.checkPassword(reqLogin.getPassword())) {
			return new ResponseMessage(reqLogin.getMessageType(), false, "ssssss");
		}
		AuthToken auth = new AuthToken(u.getUsername(), u.isAdmin());
		AuthTokenManager authMan = new AuthTokenManager();
		authMan.save(auth);
		return new ResponseLogin(auth.getId());	
	}
	
	ResponseMessage handleLogout() {
		AuthTokenManager authMan = new AuthTokenManager();
		authMan.delete(authToken);
		return new ResponseMessage(ActionRequest.LOGOUT, true);
	}
	
	ResponseMessage handleBrowseMarket(RequestBrowse reqMsg) {
		int pageSize = 20;
		SourcesManager marketMan = new SourcesManager();
		PojoCursor<StringWrapper> cursor = marketMan.findMarketName(reqMsg.getNumPage()*pageSize, pageSize);
		
		return null;
		
	}

}
