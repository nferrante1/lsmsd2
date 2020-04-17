package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import app.common.net.ActionRequest;
import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.User;


public class Client extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private AuthToken authToken;

	Client(Socket clientSocket)
	{
		socket = clientSocket;
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		process();
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void process()
	{
		RequestMessage reqMsg = RequestMessage.receive(inputStream);
		if (reqMsg == null) {
			Thread.currentThread().interrupt();
			return;
		}
		if (!reqMsg.isValid()) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		
//		if(reqMsg.getAction() != ActionRequest.LOGIN) {
//			StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
//			StorablePojoCursor<AuthToken> cursor = (StorablePojoCursor<AuthToken>)authTokenManager.find(reqMsg.getAuthToken());
//			
//			if(!cursor.hasNext()) {
//				new ResponseMessage("User not authenticated.").send(outputStream);
//				return;
//			}
//			authToken = cursor.next();
//		}
		
		
		
		switch(reqMsg.getAction()) {
		case ADD_USER:
		case BROWSE_DATA_SOURCE:
			if(!authToken.isAdmin()) {
				new ResponseMessage("This action requires admin privileges.").send(outputStream);
				return;
			}
		default:
		}

		ResponseMessage resMsg = null;
		//TODO: call handler by reflection
		switch (reqMsg.getAction()) {
		case LOGIN:
				resMsg = handleLogin(reqMsg);
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
			resMsg = handleBrowseMarket(reqMsg); 
			break;
		case BROWSE_DATA_SOURCE:
			//resMsg = handleBrowseDataSource(reqMsg); //TODO
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

		resMsg.send(outputStream);
		
	}
	
	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		LoginInfo userInfo = (LoginInfo)reqMsg.getEntity(0);
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		StorablePojoCursor<User> cursor = (StorablePojoCursor<User>)userManager.find(userInfo.getUsername());
		if (!cursor.hasNext())
			return new ResponseMessage("User not registered.");
		User user = cursor.next();
		if(!user.checkPassword(userInfo.getPassword()))
			return new ResponseMessage("Invalid password.");
		StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
		AuthToken authToken = new AuthToken(user.getUsername(), user.isAdmin());
		authTokenManager.save(authToken);
		return new ResponseMessage(new AuthTokenInfo(authToken.getId()));
	}
	
	private ResponseMessage handleLogout()
	{
		StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
		authToken.delete();
		authTokenManager.save(authToken);
		authToken = null;
		return new ResponseMessage();
	}
	
	// TODO: use new [Storable]PojoManager
	private ResponseMessage handleBrowseMarket(RequestMessage reqMsg)
	{
		int pageSize = 20;
		BrowseInfo browse = (BrowseInfo)reqMsg.getEntity();
		MarketInfoManager manager = new MarketInfoManager();
		PojoCursor<MarketInfo> cursor = manager.getMarketInfo(browse.getFilter(), browse.getPage(), pageSize);
		List<MarketInfo> markets = cursor.toList();
		return new ResponseMessage(markets.toArray(new MarketInfo[0]));
	}
	
	/*private ResponseMessage handleBrowseDataSource(RequestMessage reqMsg)
	{
		SourcesManager manager = new SourcesManager();
		List<DataSource> sources = manager.find(false).toList();
		ResponseList<app.common.net.entities.DataSource> response = new ResponseList<app.common.net.entities.DataSource> ();
		for(DataSource source: sources)
			response.add(new app.common.net.entities.DataSource(source.getName(), source.isEnabled()));
		return response;
	}
	*/
}
