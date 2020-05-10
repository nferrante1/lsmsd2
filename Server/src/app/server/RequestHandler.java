package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.DeleteDataFilter;
import app.common.net.entities.Entity;
import app.common.net.entities.FileContent;
import app.common.net.entities.KVParameter;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.StrategyInfo;
import app.common.net.entities.UserInfo;
import app.common.net.enums.ActionRequest;
import app.datamodel.AuthTokenManager;
import app.datamodel.DataSourceManager;
import app.datamodel.MarketDataManager;
import app.datamodel.PojoManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.Strategy;
import app.datamodel.pojos.User;
import app.server.dm.MarketInfoManager;
import app.server.runner.StrategyRunner;

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
			AuthTokenManager authTokenManager = new AuthTokenManager();
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
		} catch (InvocationTargetException e) {
			resMsg = new ResponseMessage(e.getCause().getMessage());
		} catch (IllegalAccessException | IllegalArgumentException
			| SecurityException e) {
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
		KVParameter userInfo = reqMsg.getEntity(KVParameter.class);
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
		LoginInfo loginInfo = reqMsg.getEntity(LoginInfo.class);
		User user = new User(loginInfo.getUsername(), loginInfo.getPassword());
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		userManager.save(user);
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleEditDataSource(RequestMessage reqMsg)
	{
		SourceInfo sourceInfo = reqMsg.getEntity(SourceInfo.class);
		DataSourceManager dataSourceManager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)dataSourceManager.find(sourceInfo.getName());
		if(!cursor.hasNext())
			return new ResponseMessage("Source '" + sourceInfo.getName() + "' not found.");
		DataSource source = cursor.next();
		source.setEnabled(sourceInfo.isEnabled());
		ScraperController.stop();
		dataSourceManager.save(source);
		ScraperController.start();
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseUsers(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = reqMsg.getEntity(BrowseInfo.class);
		KVParameter filter = reqMsg.getEntity(KVParameter.class);
		String filterValue = filter == null ? null : filter.getValue();
		List<User> users = new StorablePojoManager<User>(User.class).findPaged(
			filterValue == null ? null : Filters.regex("_id", Pattern.compile(filterValue, Pattern.CASE_INSENSITIVE)),
			Projections.exclude("passwordHash"),
			Sorts.ascending("username"),
			browseInfo.getPage(), browseInfo.getPerPage()).toList();
		List<UserInfo> userInfos = new ArrayList<UserInfo>(users.size());
		for (User user: users)
			userInfos.add(new UserInfo(user.getUsername(), user.isAdmin()));
		return new ResponseMessage(userInfos.toArray(new UserInfo[0]));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleLogin(RequestMessage reqMsg)
	{
		LoginInfo loginInfo = reqMsg.getEntity(LoginInfo.class);
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		StorablePojoCursor<User> cursor = (StorablePojoCursor<User>)userManager.find(loginInfo.getUsername());
		if (!cursor.hasNext())
			return new ResponseMessage("User '" + loginInfo.getUsername() + "' not registered.");
		User user = cursor.next();
		if(!user.checkPassword(loginInfo.getPassword()))
			return new ResponseMessage("Invalid password.");
		AuthTokenManager authTokenManager = new AuthTokenManager();
		authToken = user.generateToken();
		authTokenManager.save(authToken);
		return new ResponseMessage(new AuthTokenInfo(authToken.getId()));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		AuthTokenManager authTokenManager = new AuthTokenManager();
		authToken.delete();
		authTokenManager.save(authToken);
		authToken = null;
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseMarkets(RequestMessage reqMsg) // TODO: check selectability; return only selectable markets to non-admin users
	{
		KVParameter sourceFilter= null;
		KVParameter marketFilter= null;
		KVParameter fullIdFilter= null;
		BrowseInfo browseInfo = reqMsg.getEntity(BrowseInfo.class);
		List<KVParameter> filters = reqMsg.getEntities(KVParameter.class);
		for (KVParameter filter: filters)
			if (filter.getName().equals("SOURCE"))
				sourceFilter = filter;
			else if (filter.getName().equals("MARKET"))
				marketFilter = filter;
			else if (filter.getName().equals("FULLID"))
				fullIdFilter = filter;

		String sourceName = sourceFilter == null ? null : sourceFilter.getValue();
		String marketName = marketFilter == null ? null : marketFilter.getValue();
		String fullId = fullIdFilter == null ? null : fullIdFilter.getValue();
		if(fullId != null)
			if(fullId.contains(":"))
			{
				String[] split = fullId.split(":", 2);
				sourceName = split[0];
				marketName = split[1];
			} else {
				sourceName = null;
				marketName = fullId;
			}

		MarketInfoManager marketInfoManager = new MarketInfoManager();
		List<MarketInfo> marketInfos;

		marketInfos = marketInfoManager.getMarketInfo(sourceName, marketName ,browseInfo.getPage(), browseInfo.getPerPage()).toList();

		return new ResponseMessage(marketInfos.toArray(new MarketInfo[0]));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseDataSources(RequestMessage reqMsg)
	{
		List<DataSource> sources = new DataSourceManager().find(
			null,
			Projections.exclude("markets"),
			null, 0, 0).toList();
		List<SourceInfo> sourceInfos = new ArrayList<SourceInfo>(sources.size());
		for (DataSource source: sources)
			sourceInfos.add(new SourceInfo(source.getName(), source.isEnabled()));
		return new ResponseMessage(sourceInfos.toArray(new SourceInfo[0]));
	}

	//TODO: rewrite
	@SuppressWarnings("unused")
	private ResponseMessage handleBrowseStrategies(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = reqMsg.getEntity(BrowseInfo.class);
		KVParameter filter = reqMsg.getEntity(KVParameter.class);
		String filterValue = filter == null ? null : filter.getValue();
		PojoManager<StrategyInfo> manager = new PojoManager<StrategyInfo>(StrategyInfo.class, "Strategies");
		List<StrategyInfo> strategies = manager.findPaged(filterValue == null ? null :
				Filters.regex("name", Pattern.compile(filterValue, Pattern.CASE_INSENSITIVE)),
				Projections.excludeId(),
				Sorts.ascending("name"),
				browseInfo.getPage(),
				browseInfo.getPerPage()).toList();

		for(StrategyInfo strategy: strategies)
			if(authToken.isAdmin() || authToken.getUsername().equals(strategy.getAuthor()))
				strategy.setDeletable(true);

		return new ResponseMessage((Entity[])strategies.toArray(new StrategyInfo[0]));
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleEditMarket(RequestMessage reqMsg)
	{
		MarketInfo marketInfo = reqMsg.getEntity(MarketInfo.class);
		DataSourceManager dataSourceManager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)dataSourceManager.find(marketInfo.getSourceName());
		if(!cursor.hasNext())
			return new ResponseMessage("Source '" + marketInfo.getSourceName() + "' not found.");
		DataSource source = cursor.next();
		Market market = source.getMarket(marketInfo.getMarketId());
		if(market == null)
			return new ResponseMessage("Market '" + marketInfo.getMarketId() + "' not found.");
		market.setGranularity(marketInfo.getGranularity());
		market.setSync(marketInfo.isSync());
		market.setSelectable(marketInfo.isSelectable());
		ScraperController.stop();
		dataSourceManager.save(source);
		ScraperController.start();
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleAddStrategy(RequestMessage reqMsg)
	{
		KVParameter name = reqMsg.getEntity(KVParameter.class);
		FileContent strategyContent = reqMsg.getEntity(FileContent.class);
		StrategyFile strategyFile = new StrategyFile(name.getValue(), strategyContent.getContent());
		try {
			strategyContent.writeFile(strategyFile.getJavaFilePath());
		} catch (IOException e) {
			strategyFile.delete();
			return new ResponseMessage("Can not save strategy's file.");
		}
		if (!strategyFile.compile()) {
			strategyFile.delete();
			return new ResponseMessage("Can not compile Strategy.");
		}
		String strategyName = strategyFile.getStrategyName();
		if (strategyName == null) {
			strategyFile.delete();
			return new ResponseMessage("The strategy must implement the ExecutableStrategy interface and the method getName().");
		}

		Strategy strategy = new Strategy(strategyFile.getHash(), strategyName, authToken.getUsername());
		StorablePojoManager<Strategy> strategyManager = new StorablePojoManager<Strategy>(Strategy.class);
		try {
			strategyManager.save(strategy);
		} catch(DuplicateKeyException e) {
			strategyFile.delete();
			return new ResponseMessage("A strategy with this name already exists.");
		}
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleDownloadStrategy(RequestMessage reqMsg)
	{
		String strategyName = reqMsg.getEntity(KVParameter.class).getValue();
		StorablePojoManager<Strategy> manager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> strategies = (StorablePojoCursor<Strategy>)manager.find(Filters.eq("name", strategyName));

		if(!strategies.hasNext())
			return new ResponseMessage("Strategy '" + strategyName + "' not found.");

		Strategy strategy = strategies.next();
		StrategyFile strategyFile;
		try {
			strategyFile = new StrategyFile(strategy.getId());
		} catch (FileNotFoundException ex) {
			strategy.delete();
			manager.save(strategy);
			return new ResponseMessage(ex.getMessage());
		}
		try {
			return new ResponseMessage(new FileContent(strategyFile.getJavaFilePath()));
		} catch (IOException ex) {
			return new ResponseMessage("Error while reading strategy's file: " + ex.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleDeleteStrategy(RequestMessage reqMsg)
	{
		String strategyName = reqMsg.getEntity(KVParameter.class).getValue();
		StorablePojoManager<Strategy> manager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> cursor = (StorablePojoCursor<Strategy>)manager.find(Filters.eq("name", strategyName));
		if(!cursor.hasNext())
			return new ResponseMessage("Strategy '" + strategyName + "' not found.");
		Strategy strategy = cursor.next();
		StrategyFile strategyFile;
		try {
			strategyFile = new StrategyFile(strategy.getId());
			strategyFile.delete();
		} catch (FileNotFoundException e) {
			//TODO: log
		}

		strategy.delete();
		manager.save(strategy);
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleDeleteData(RequestMessage reqMsg) //TODO: use KVParameter(s)
	{
		DeleteDataFilter filter = reqMsg.getEntity(DeleteDataFilter.class);
		MarketDataManager manager = new MarketDataManager();
		manager.delete(filter.getSource(), filter.getMarketId(), filter.getDate());
		return new ResponseMessage();
	}

	@SuppressWarnings("unused")
	private ResponseMessage handleRunStrategy(RequestMessage reqMsg)
	{
		List<KVParameter> parameters = reqMsg.getEntities(KVParameter.class);
		String strategyName = null;
		for(KVParameter parameter : parameters)
			if(parameter.getName().equals("STRATEGYNAME"))
				strategyName = parameter.getValue();

		StorablePojoManager<Strategy> manager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> strategies = (StorablePojoCursor<Strategy>)manager.find(Filters.eq("name", strategyName));

		if(!strategies.hasNext())
			return new ResponseMessage("Strategy '" + strategyName + "' not found.");

		Strategy strategy = strategies.next();	
		try {
			StrategyFile strategyFile = new StrategyFile(strategy.getId());
			StrategyRunner runner = new StrategyRunner(strategyFile);
			runner.start();
			runner.join();
		} catch (FileNotFoundException | InterruptedException e) {
			//TODO: log
		}
		return new ResponseMessage();
	}
}
