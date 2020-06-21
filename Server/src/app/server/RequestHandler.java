package app.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BaseReportInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.FileContent;
import app.common.net.entities.KVParameter;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.ParameterInfo;
import app.common.net.entities.ProgressInfo;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.StrategyInfo;
import app.common.net.entities.UserInfo;
import app.common.net.entities.enums.ParameterType;
import app.datamodel.AuthTokenManager;
import app.datamodel.DataSourceManager;
import app.datamodel.MarketDataManager;
import app.datamodel.PojoCursor;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.DataSource;
import app.datamodel.pojos.Market;
import app.datamodel.pojos.Report;
import app.datamodel.pojos.Strategy;
import app.datamodel.pojos.StrategyRun;
import app.datamodel.pojos.User;
import app.library.ExecutableStrategy;
import app.library.annotations.StrategyParameter;
import app.server.annotations.RequestHandlerMethod;
import app.server.managers.BaseReportInfoManager;
import app.server.managers.MarketInfoManager;
import app.server.runner.StrategyFile;
import app.server.runner.StrategyRunner;

final class RequestHandler extends Thread
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

		dispatchMessage(reqMsg).send(outputStream);
	}

	private ResponseMessage dispatchMessage(RequestMessage reqMsg)
	{
		String handlerName = "handle" + reqMsg.getAction().toCamelCaseString();
		try {
			Method handler = getClass().getDeclaredMethod(handlerName, RequestMessage.class);
			handler.setAccessible(true);
			if (!handler.isAnnotationPresent(RequestHandlerMethod.class))
				throw new NoSuchMethodException();
			boolean requiresAdmin = handler.getAnnotation(RequestHandlerMethod.class).value();
			boolean requiresLogin = requiresAdmin ? true : handler.getAnnotation(RequestHandlerMethod.class).requiresLogin();
			String receivedAuthToken = reqMsg.getAuthToken();
			if (!requiresLogin)
				return (ResponseMessage)handler.invoke(this, reqMsg);
			if (receivedAuthToken == null)
				return new ResponseMessage("NO-AUTH: user not authenticated.");
			authToken = (new AuthTokenManager()).find(receivedAuthToken).next();
			if (authToken == null)
				return new ResponseMessage("NO-AUTH: can not find auth token.");
			if (requiresAdmin && !authToken.isAdmin())
				return new ResponseMessage("This action requires admin privileges.");
			return (ResponseMessage)handler.invoke(this, reqMsg);
		} catch (NoSuchMethodException e) {
			return new ResponseMessage("Invalid action.");
		} catch (InvocationTargetException e) {
			return new ResponseMessage("ERROR: " + e.getCause().getMessage());
		} catch (IllegalAccessException | IllegalArgumentException
			| SecurityException e) {
			e.printStackTrace();
			return new ResponseMessage("Can not run action handler.");
		}
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteUser(RequestMessage reqMsg)
	{
		KVParameter userInfo = reqMsg.getEntity(KVParameter.class);
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		if (userManager.delete(userInfo.getValue()) == 0)
			return new ResponseMessage("User '" + userInfo.getValue() + "' does not exists.");
		return new ResponseMessage();
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleAddUser(RequestMessage reqMsg)
	{
		LoginInfo loginInfo = reqMsg.getEntity(LoginInfo.class);
		User user = new User(loginInfo.getUsername(), loginInfo.getPassword());
		StorablePojoManager<User> userManager = new StorablePojoManager<User>(User.class);
		userManager.save(user);
		return new ResponseMessage();
	}

	@RequestHandlerMethod(true)
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

	@RequestHandlerMethod(true)
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

	@RequestHandlerMethod(requiresLogin=false)
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

	@RequestHandlerMethod
	private ResponseMessage handleLogout(RequestMessage reqMsg)
	{
		AuthTokenManager authTokenManager = new AuthTokenManager();
		authToken.delete();
		authTokenManager.save(authToken);
		authToken = null;
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleBrowseMarkets(RequestMessage reqMsg)
	{
		KVParameter sourceFilter = null;
		KVParameter marketFilter = null;
		KVParameter fullIdFilter = null;
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
		List<MarketInfo> marketInfos = marketInfoManager.findMarketInfo(sourceName, marketName, !authToken.isAdmin(), browseInfo.getPage(), browseInfo.getPerPage()).toList();

		return new ResponseMessage(marketInfos.toArray(new MarketInfo[0]));
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleBrowseDataSources(RequestMessage reqMsg)
	{
		List<DataSource> sources = new DataSourceManager().find(null, Projections.exclude("markets"), null).toList();
		List<SourceInfo> sourceInfos = new ArrayList<SourceInfo>(sources.size());
		for (DataSource source: sources)
			sourceInfos.add(new SourceInfo(source.getName(), source.isEnabled()));
		return new ResponseMessage(sourceInfos.toArray(new SourceInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleBrowseStrategies(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = reqMsg.getEntity(BrowseInfo.class);
		KVParameter filter = reqMsg.getEntity(KVParameter.class);
		String filterValue = filter == null ? null : filter.getValue();
		StorablePojoManager<Strategy> strategyManager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> cursor = (StorablePojoCursor<Strategy>)strategyManager.findPaged(
			filterValue == null ? null : Filters.regex("name", Pattern.compile(filterValue, Pattern.CASE_INSENSITIVE)),
			Projections.fields(Projections.excludeId(), Projections.exclude("runs")),
			Sorts.ascending("name"),
			browseInfo.getPage(), browseInfo.getPerPage());
		List<StrategyInfo> strategyInfos = new ArrayList<StrategyInfo>();
		while (cursor.hasNext()) {
			Strategy strategy = cursor.next();
			strategyInfos.add(new StrategyInfo(
				strategy.getName(), strategy.getAuthor(),
				authToken.isAdmin() || authToken.getUsername().equals(strategy.getAuthor())
				));
		}
		return new ResponseMessage(strategyInfos.toArray(new StrategyInfo[0]));
	}

	@RequestHandlerMethod(true)
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

	@RequestHandlerMethod
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
		} catch(DuplicateKeyException | MongoWriteException e) {
			strategyFile.delete();
			return new ResponseMessage("A strategy with this name already exists.");
		}
		return new ResponseMessage(new StrategyInfo(strategy.getName(), strategy.getAuthor(), true));
	}

	@RequestHandlerMethod
	private ResponseMessage handleDownloadStrategy(RequestMessage reqMsg)
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
		} catch (FileNotFoundException ex) {
			return new ResponseMessage("Can not find strategy's file: " + ex.getMessage());
		}
		try {
			return new ResponseMessage(new FileContent(strategyFile.getJavaFilePath()));
		} catch (IOException ex) {
			return new ResponseMessage("Error while reading strategy's file: " + ex.getMessage());
		}
	}

	@RequestHandlerMethod
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
			Logger.getLogger(RequestHandler.class.getName()).info("Strategy file already deleted.");
		}

		strategy.delete();
		manager.save(strategy);
		return new ResponseMessage();
	}

	@RequestHandlerMethod(true)
	private ResponseMessage handleDeleteData(RequestMessage reqMsg)
	{
		List<KVParameter> parameters = reqMsg.getEntities(KVParameter.class);
		String source = null;
		String market = null;
		Instant date = null;
		for (KVParameter parameter: parameters)
			if (parameter.getName().equals("SOURCE"))
				source = parameter.getValue();
			else if (parameter.getName().equals("MARKET"))
				market = parameter.getValue();
			else if (parameter.getName().equals("DATE"))
				date = Instant.parse(parameter.getValue());

		MarketDataManager manager = new MarketDataManager();
		manager.delete(source, market, date);
		return new ResponseMessage();
	}

	@RequestHandlerMethod
	private ResponseMessage handleGetStrategyParameters(RequestMessage reqMsg)
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
		} catch (FileNotFoundException ex) {
			return new ResponseMessage("Can not find strategy's file: " + ex.getMessage());
		}
		ExecutableStrategy strategyInstance = strategyFile.getStrategy();
		if (strategyInstance == null)
			return new ResponseMessage("Can not load strategy class.");
		List<ParameterInfo> parameters = new ArrayList<ParameterInfo>();
		for (Field field: strategyInstance.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(StrategyParameter.class))
				continue;
			String name = field.getAnnotation(StrategyParameter.class).value();
			name = name.isBlank() ? field.getName() : name;
			if (name.equals("market") || name.equals("inverseCross") || name.equals("granularity") || name.equals("startTime") || name.equals("endTime"))
				continue;
			ParameterType type = ParameterType.getFromType(field.getType());
			if (type == null)
				return new ResponseMessage("Strategy defined an invalid type for parameter '" + field.getName() + "'. Allowed types: int, double, boolean, Instant, String.");
			parameters.add(new ParameterInfo(name, type));
		}
		return new ResponseMessage(parameters.toArray(new ParameterInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleRunStrategy(RequestMessage reqMsg)
	{
		List<KVParameter> kvParameters = reqMsg.getEntities(KVParameter.class);
		String strategyName = null;
		Map<String, Object> parameters = new HashMap<String, Object>(kvParameters.size() - 1);
		for(KVParameter parameter: kvParameters)
			if(parameter.getName().equals("STRATEGYNAME"))
				strategyName = parameter.getValue();
			else
				parameters.put(parameter.getName(), parameter.getConvertedValue());

		StorablePojoManager<Strategy> manager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> strategies = (StorablePojoCursor<Strategy>)manager.find(Filters.eq("name", strategyName));

		if(!strategies.hasNext())
			return new ResponseMessage("Strategy '" + strategyName + "' not found.");

		Strategy strategy = strategies.next();
		StrategyFile strategyFile;
		try {
			strategyFile = new StrategyFile(strategy.getId());
		} catch (FileNotFoundException e) {
			return new ResponseMessage("Can not find strategy's file.");
		}
		ExecutableStrategy strategyInstance = strategyFile.getStrategy();
		if (strategyInstance == null)
			return new ResponseMessage("Can not load strategy class.");

		double lastReturnedProgress = -1.0;
		double progress = 0.0;
		StrategyRunner runner = new StrategyRunner(strategyInstance, parameters);
		runner.setDaemon(true);
		runner.setPriority(MIN_PRIORITY);
		runner.start();
		while (runner.isAlive() && runner.getException() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			lastReturnedProgress = runner.progress();
			if (lastReturnedProgress < 0)
				continue;
			if (lastReturnedProgress > progress)
				progress = lastReturnedProgress;
			// Not an estimation of the execution completion (there is no way
			// to get the completion progress of an aggregation pipeline): this
			// is just to create an animation on the client that says "hey! I'm
			// still alive!".
			new ResponseMessage(new ProgressInfo(progress)).send(outputStream);
			if (progress < 0.75)
				progress += 0.01*(0.75 - progress);
		}
		if (runner.getException() != null)
			return new ResponseMessage("Error during the execution of the strategy: " + runner.getException().getMessage());
		if (runner.isInterrupted())
			return new ResponseMessage("Strategy execution thread has been interrupted.");

		new ResponseMessage(new ProgressInfo(1.0)).send(outputStream);

		StrategyRun strategyRun = runner.generateStrategyRun();
		strategyRun.setUser(authToken.getUsername());
		strategy.addRun(strategyRun);
		manager.save(strategy);

		Report report = strategyRun.getReport();
		return new ResponseMessage(new ReportInfo(
			strategyRun.getId().toHexString(), strategy.getName(),
			runner.getMarketId(), report.getNetProfit(),
			strategyRun.getUser(), report.getGrossProfit(),
			report.getGrossLoss(), report.getHodlProfit(),
			report.getTotalTrades(), report.getOpenTrades(),
			report.getWinningTrades(), report.getMaxConsecutiveLosing(),
			report.getAvgAmount(), report.getAvgDuration(),
			report.getMaxDrawdown(), true));
	}

	@RequestHandlerMethod
	private ResponseMessage handleBrowseReports(RequestMessage reqMsg)
	{
		BrowseInfo browseInfo = reqMsg.getEntity(BrowseInfo.class);
		List<KVParameter> parameters = reqMsg.getEntities(KVParameter.class);
		String strategyName = null;
		String marketId = null;
		for (KVParameter parameter: parameters) {
			if (parameter.getName().equals("STRATEGYNAME"))
				strategyName = parameter.getValue();
			else if (parameter.getName().equals("MARKETID"))
				marketId = parameter.getValue();
		}

		BaseReportInfoManager reportManager = new BaseReportInfoManager();
		PojoCursor<BaseReportInfo> cursor = reportManager.getBaseReportInfo(strategyName, marketId, browseInfo.getPage(), browseInfo.getPerPage());

		List<BaseReportInfo> reportInfos = cursor.toList();
		return new ResponseMessage(reportInfos.toArray(new BaseReportInfo[0]));
	}

	@RequestHandlerMethod
	private ResponseMessage handleViewReport(RequestMessage reqMsg)
	{
		String reportId = reqMsg.getEntity(KVParameter.class).getValue();
		StorablePojoManager<Strategy> strategyManager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> cursor = (StorablePojoCursor<Strategy>)strategyManager.find(Filters.eq("runs.id", new ObjectId(reportId)), Projections.fields(Projections.include("name") ,Projections.elemMatch("runs", Filters.eq("id", new ObjectId(reportId)))),null);
		if (!cursor.hasNext())
			return new ResponseMessage("Details of report '" + reportId + "' not found.");

		Strategy strategy = cursor.next();
		StrategyRun run = strategy.getRun(reportId);
		if (run == null)
			return new ResponseMessage("Can not find the specified report.");
		Report report = run.getReport();
		List<KVParameter> parameters = new ArrayList<KVParameter>();
		Map<String, Object> paramMap = run.getParameters();
		for(Map.Entry<String, Object> entry : paramMap.entrySet() )
			if(Integer.class.isAssignableFrom(entry.getValue().getClass()))
				parameters.add(new KVParameter(entry.getKey(), (int) entry.getValue()));
			else if(Double.class.isAssignableFrom(entry.getValue().getClass()))
				parameters.add(new KVParameter(entry.getKey(), (double) entry.getValue()));
			else if(Instant.class.isAssignableFrom(entry.getValue().getClass()))
				parameters.add(new KVParameter(entry.getKey(), (Instant) entry.getValue()));
			else if(Date.class.isAssignableFrom(entry.getValue().getClass()))
				parameters.add(new KVParameter(entry.getKey(), ((Date)entry.getValue()).toInstant()));
			else if(Boolean.class.isAssignableFrom(entry.getValue().getClass()))
				parameters.add(new KVParameter(entry.getKey(), (boolean) entry.getValue()));
			else 
				parameters.add(new KVParameter(entry.getKey(), (String)entry.getValue()));

		List<Entity> entities = new ArrayList<Entity>();
		entities.addAll(parameters);
		entities.add(new ReportInfo(
				reportId, strategy.getName(), run.getParameter("market").toString(), 
				report.getNetProfit(), run.getUser(), 
				report.getGrossProfit(), report.getGrossLoss(), 
				report.getHodlProfit(),report.getTotalTrades(), 
				report.getOpenTrades(), report.getWinningTrades(), 
				report.getMaxConsecutiveLosing(),report.getAvgAmount(), 
				report.getAvgDuration(), report.getMaxDrawdown(), authToken.isAdmin() || authToken.getUsername().equals(run.getUser())));

		return new ResponseMessage(entities); 
	}

	@RequestHandlerMethod
	private ResponseMessage handleDeleteReport(RequestMessage reqMsg)
	{
		String reportId = reqMsg.getEntity(KVParameter.class).getValue();
		StorablePojoManager<Strategy> manager = new StorablePojoManager<Strategy>(Strategy.class);
		StorablePojoCursor<Strategy> cursor = (StorablePojoCursor<Strategy>)manager.find(Filters.eq("runs.id", new ObjectId(reportId)), Projections.fields(Projections.elemMatch("runs", Filters.eq("id", new ObjectId(reportId)))), null);
		if(!cursor.hasNext())
			return new ResponseMessage("Report '" + reportId + "' not found.");
		Strategy strategy = cursor.next();
		StrategyRun run = strategy.getRun(reportId);
		if (run == null)
			return new ResponseMessage("Report '" + reportId + "' not found.");
		run.delete();
		manager.save(strategy);
		return new ResponseMessage();
	}

}
