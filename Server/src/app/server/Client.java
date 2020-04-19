package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.ActionRequest;
import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.UserInfo;
import app.datamodel.PojoCursor;
import app.datamodel.PojoManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.DataSource;
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
		
		if(reqMsg.getAction() != ActionRequest.LOGIN) {
			StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
			StorablePojoCursor<AuthToken> cursor = (StorablePojoCursor<AuthToken>)authTokenManager.find(reqMsg.getAuthToken());
			
			if(!cursor.hasNext()) {
				new ResponseMessage("User not authenticated.").send(outputStream);
				return;
			}
			authToken = cursor.next();
		}
		
		
		
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
			resMsg = handleBrowseDataSource(reqMsg);
			break;
		case CHANGE_DATA_SOURCE:
			resMsg = handleChangeDataSource(reqMsg);
			break;
		case BROWSE_USERS:
			resMsg = handleBrowseUsers(reqMsg);
			break;
		case ADD_USER:
			resMsg = handleAddUser(reqMsg);
			break;
		case DELETE_USER:
			resMsg = handleDeleteUser(reqMsg);
			break;
		case DELETE_DATA:
	
			break;
		case CONFIG_MARKET:

			break;
		default:

		}

		resMsg.send(outputStream);
		
	}
	
	private ResponseMessage handleDeleteUser(RequestMessage reqMsg) {
		
		UserInfo userinfo = (UserInfo)reqMsg.getEntity();
		StorablePojoManager<User> user_manager = new StorablePojoManager<User>(User.class);
		StorablePojoCursor<User> cursor = (StorablePojoCursor<User>)user_manager.find(userinfo.getUsername());
		if(!cursor.hasNext())
			return new ResponseMessage("User not found");
		User user = cursor.next();
		user.delete();
		user_manager.save(user);
		return new ResponseMessage();
		
	}

	private ResponseMessage handleAddUser(RequestMessage reqMsg) {
		
		LoginInfo logininfo = (LoginInfo)reqMsg.getEntity();
		User user = new User(logininfo.getUsername(), logininfo.getPassword());
		StorablePojoManager<User> user_manager = new StorablePojoManager<User>(User.class);
		user_manager.save(user);
		return new ResponseMessage();
	}

	private ResponseMessage handleChangeDataSource(RequestMessage reqMsg) {
		
		SourceInfo sourceinfo = (SourceInfo)reqMsg.getEntity();
		StorablePojoManager<DataSource> data_source_manager = new StorablePojoManager<DataSource>(DataSource.class);
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)data_source_manager.find(sourceinfo.get_id());
		if(!cursor.hasNext())
			return new ResponseMessage("Source not found");
		DataSource source = cursor.next();
		source.setEnabled(sourceinfo.isEnabled());
		data_source_manager.save(source);
		return new ResponseMessage();
	}

	private ResponseMessage handleBrowseUsers(RequestMessage reqMsg) {
		
		int pageSize = 20;
		BrowseInfo browse = (BrowseInfo)reqMsg.getEntity();
		PojoManager<UserInfo> manager = new PojoManager<UserInfo>(UserInfo.class, "Users");
		PojoCursor<UserInfo> cursor = manager.findPaged(null, Projections.fields(Projections.computed("username", "$_id"), 
				Projections.include("isAdmin"), Projections.excludeId()), Sorts.ascending("username"), browse.getPage(), pageSize);
		List<UserInfo> users = cursor.toList();
		return new ResponseMessage(users.toArray(new UserInfo[0]));
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
	
	private ResponseMessage handleBrowseDataSource(RequestMessage reqMsg)
	{
		PojoManager<SourceInfo> manager = new PojoManager<SourceInfo>(SourceInfo.class, "Sources");
		List<SourceInfo> sources = manager.find(null, Projections.fields(Projections.exclude("markets"), Projections.excludeId(), Projections.computed("name", "$_id"))).toList();
		return new ResponseMessage(sources.toArray(new SourceInfo[0]));
	}
	
}
