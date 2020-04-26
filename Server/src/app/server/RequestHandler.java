package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.KVParameter;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.StrategyInfo;
import app.common.net.entities.UserInfo;
import app.common.net.enums.ActionRequest;
import app.datamodel.PojoManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.User;
import app.server.dm.MarketInfoManager;

public class RequestHandler extends Thread
{

	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	private AuthToken authToken;

	RequestHandler(Socket clientSocket)
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
		if (reqMsg == null)
			return;

		if (!reqMsg.isValid()) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}

		if(reqMsg.getAction() != ActionRequest.LOGIN) {
			StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
			StorablePojoCursor<AuthToken> cursor = (StorablePojoCursor<AuthToken>)authTokenManager.find(reqMsg.getAuthToken());

			if(!cursor.hasNext()) {
				new ResponseMessage("NO-AUTH: User not authenticated.").send(outputStream);
				return;
			}
			authToken = cursor.next();
		}

		switch(reqMsg.getAction()) {
		case BROWSE_USERS:
		case ADD_USER:
		case DELETE_USER:
		case DELETE_DATA:
		case BROWSE_DATA_SOURCES:
		case EDIT_DATA_SOURCE:
		case EDIT_MARKET:
			if(!authToken.isAdmin()) {
				new ResponseMessage("This action requires admin privileges.").send(outputStream);
				return;
			}
		default:
		}

		ResponseMessage resMsg;
		String handlerName = "handle" + reqMsg.getAction().toCamelCaseString();
		try {
			Method handler = getClass().getDeclaredMethod(handlerName, RequestMessage.class);
			handler.setAccessible(true);
			resMsg = (ResponseMessage)handler.invoke(this, reqMsg);
		} catch (NoSuchMethodException e) {
			resMsg = new ResponseMessage("Invalid action.");
		} catch (IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | SecurityException e) {
			resMsg = new ResponseMessage("Can not run action handler.");
			e.printStackTrace();
		}

		resMsg.send(outputStream);
		try {
			outputStream.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleDeleteUser(RequestMessage reqMsg)
	{
		KVParameter userInfo = (KVParameter)reqMsg.getEntity();
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		StorablePojoCursor<User> cursor = (StorablePojoCursor<User>)userManager.find(userInfo.getValue());
		if(!cursor.hasNext())
			return new ResponseMessage("User '" + userInfo.getValue() + "' does not exists.");
		User user = cursor.next();
		user.delete();
		userManager.save(user);
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleAddUser(RequestMessage reqMsg)
	{
		LoginInfo loginInfo = (LoginInfo)reqMsg.getEntity();
		User user = new User(loginInfo.getUsername(), loginInfo.getPassword());
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		userManager.save(user);
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleEditDataSource(RequestMessage reqMsg)
	{
		SourceInfo sourceInfo = (SourceInfo)reqMsg.getEntity();
		StorablePojoManager<DataSource> dataSourceManager = new StorablePojoManager<DataSource>(DataSource.class);
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)dataSourceManager.find(sourceInfo.getName());
		if(!cursor.hasNext())
			return new ResponseMessage("Source '" + sourceInfo.getName() + "' not found.");
		DataSource source = cursor.next();
		source.setEnabled(sourceInfo.isEnabled());
		dataSourceManager.save(source);
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseUsers(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = null;
		KVParameter filter = null;
		for(Entity entity: reqMsg.getEntities()) 
		{
			if(entity instanceof BrowseInfo)
				browseInfo = (BrowseInfo)entity;
			else if(entity instanceof KVParameter)
				filter = (KVParameter)entity;
		}
		
		List<Bson> stages = new ArrayList<Bson>();
		if(filter != null)
			stages.add(Aggregates.match(Filters.regex("_id", Pattern.compile(filter.getValue(), Pattern.CASE_INSENSITIVE))));
			
		stages.add(Aggregates.project(Projections.fields(
						Projections.computed("username", "$_id"),
						Projections.include("admin")
						
			)));
		stages.add(Aggregates.sort(Sorts.ascending("username")));
		stages.add(Aggregates.skip((browseInfo.getPage()-1)*(browseInfo.getPerPage())));
		stages.add(Aggregates.limit(browseInfo.getPerPage()));
		PojoManager<UserInfo> userInfoManager = new PojoManager<UserInfo>(UserInfo.class, "Users");
		List<UserInfo> userInfos = userInfoManager.aggregate(stages).toList();
		return new ResponseMessage(userInfos.toArray(new UserInfo[0]));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		LoginInfo loginInfo = (LoginInfo)reqMsg.getEntity();
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		StorablePojoCursor<User> cursor = (StorablePojoCursor<User>)userManager.find(loginInfo.getUsername());
		if (!cursor.hasNext())
			return new ResponseMessage("User '" + loginInfo.getUsername() + "' not registered.");
		User user = cursor.next();
		if(!user.checkPassword(loginInfo.getPassword()))
			return new ResponseMessage("Invalid password.");
		StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
		authToken = user.generateToken();
		authTokenManager.save(authToken);
		return new ResponseMessage(new AuthTokenInfo(authToken.getId()));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		StorablePojoManager<AuthToken> authTokenManager = new StorablePojoManager<AuthToken>(AuthToken.class);
		authToken.delete();
		authTokenManager.save(authToken);
		authToken = null;
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseMarkets(RequestMessage reqMsg)
	{
		
		KVParameter sourceFilter= null;
		KVParameter marketFilter= null;
		KVParameter fullIdFilter= null;
		BrowseInfo browseInfo = null;
		for(Entity entity : reqMsg.getEntities()) {
			if(entity instanceof BrowseInfo)
				browseInfo = (BrowseInfo)entity;
			if(entity instanceof KVParameter) {
				KVParameter parameter = (KVParameter)entity;
				if(parameter.getName().equals("SOURCE"))
					sourceFilter = parameter;
				else if(parameter.getName().equals("MARKET"))
					marketFilter = parameter;
				else if(parameter.getName().equals("FULLID"))
					fullIdFilter = parameter;
			}
				
		}		
		String sourceName = null;
		String marketName = null;
		if(fullIdFilter != null)
		{
			String fullId = fullIdFilter.getValue();
			if(fullId.contains(":"))
			{
				String[] split = fullId.split(":", 2);
				sourceName = split[0];
				marketName = split[1];
			}
		} else {
			if(sourceFilter != null)
				sourceName = sourceFilter.getValue();
			if(marketFilter != null)
				marketName = marketFilter.getValue();
		}
		
		MarketInfoManager marketInfoManager = new MarketInfoManager();
		List<MarketInfo> marketInfos;
		
		marketInfos = marketInfoManager.getMarketInfo(sourceName, marketName ,browseInfo.getPage(), browseInfo.getPerPage()).toList();
		
		return new ResponseMessage(marketInfos.toArray(new MarketInfo[0]));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseDataSources(RequestMessage reqMsg)
	{
		PojoManager<SourceInfo> manager = new PojoManager<SourceInfo>(SourceInfo.class, "Sources");
		List<SourceInfo> sources = manager.aggregate(
			Aggregates.project(
				Projections.fields(
					Projections.excludeId(),
					Projections.include("enabled"),
					Projections.computed("name", "$_id")
				)
			)
		).toList();
		return new ResponseMessage(sources.toArray(new SourceInfo[0]));
	}

	//TODO: rewrite
	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseStrategies(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = null;
		KVParameter filter = null;
		for(Entity entity: reqMsg.getEntities()) 
		{
			if(entity instanceof BrowseInfo)
				browseInfo = (BrowseInfo)entity;
			else if(entity instanceof KVParameter)
				filter = (KVParameter)entity;
		}
		
		List<Bson> projections = new ArrayList<Bson>();
		projections.add(Projections.excludeId());	
		PojoManager<StrategyInfo> manager = new PojoManager<StrategyInfo>(StrategyInfo.class, "Strategies");
		List<StrategyInfo> strategies = manager.findPaged(filter == null ? null : 
				Filters.regex("name", Pattern.compile(filter.getValue(), Pattern.CASE_INSENSITIVE)),
				Projections.fields(projections),
				browseInfo.getPage(),
				browseInfo.getPerPage()).toList();
		
		for(StrategyInfo strategy : strategies)
		{
			if(authToken.isAdmin() || authToken.getUsername().equals(strategy.getUsername()))
				strategy.setDeletable(true);
		}

		return new ResponseMessage((Entity[])strategies.toArray(new StrategyInfo[0]));
	}
}
