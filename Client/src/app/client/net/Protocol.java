package app.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.common.net.RequestMessage;
import app.common.net.ResponseMessage;
import app.common.net.entities.AuthTokenInfo;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.BrowseReportInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.FileContent;
import app.common.net.entities.KVParameter;
import app.common.net.entities.LoginInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.enums.ActionRequest;

public class Protocol implements AutoCloseable
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private boolean connected;
	private static Protocol instance;
	private String authToken;
	private LoginInfo loginInfo;
	private String serverAddress = "localhost";
	private int serverPort = 8888;

	private Protocol()
	{
	}

	public static Protocol getInstance()
	{
		if (instance == null)
			instance = new Protocol();
		return instance;
	}

	private void connect()
	{
		if (connected)
			return;
		try {
			socket = new Socket(serverAddress, serverPort);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			System.err.println("ERROR: error while trying to connect to the server: " + ex.getMessage());
			System.exit(1);
		}
		connected = true;
	}

	public ResponseMessage performLogin(String username, String password)
	{
		this.loginInfo = new LoginInfo(username, password);
		return performLogin();
	}

	protected ResponseMessage performLogin()
	{
		if (loginInfo == null)
			return new ResponseMessage("Username/Password not specified.");
		connect();
		new RequestMessage(ActionRequest.LOGIN, loginInfo).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		close();
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

	public ResponseMessage browseMarkets()
	{
		return browseMarkets(0);
	}

	public ResponseMessage browseMarkets(int page)
	{
		return browseMarkets(0, 20);
	}

	public ResponseMessage browseMarkets(int page, int perPage)
	{
		return browseMarkets(page, perPage, null);
	}

	public ResponseMessage browseMarkets(int page, int perPage, String sourceName)
	{
		return browseMarkets(page, perPage, null, null);
	}

	public ResponseMessage browseMarkets(int page, String sourceName)
	{
		return browseMarkets(page, sourceName, null);
	}

	public ResponseMessage browseMarkets(String sourceName)
	{
		return browseMarkets(sourceName, null);
	}

	public ResponseMessage browseMarkets(int page, String sourceName, String nameFilter)
	{
		return browseMarkets(page, 20, sourceName, nameFilter);
	}

	public ResponseMessage browseMarkets(String sourceName, String nameFilter)
	{
		return browseMarkets(0, sourceName, nameFilter);
	}

	public ResponseMessage browseMarkets(int page, int perPage, String sourceName, String nameFilter)
	{
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new BrowseInfo(page, perPage));
		if (sourceName != null && !sourceName.isBlank()) {
			entities.add(new KVParameter("SOURCE", sourceName));
			if (nameFilter != null && !nameFilter.isBlank())
				entities.add(new KVParameter("MARKET", nameFilter));
		} else if (nameFilter != null && !nameFilter.isBlank()) {
			entities.add(new KVParameter("FULLID", nameFilter));
		}
		return sendRequest(ActionRequest.BROWSE_MARKETS, entities);
	}

	public ResponseMessage browseStrategies()
	{
		return browseStrategies(0);
	}

	public ResponseMessage browseStrategies(int page)
	{
		return browseStrategies(0, 20);
	}

	public ResponseMessage browseStrategies(int page, int perPage)
	{
		return browseStrategies(page, perPage, null);
	}

	public ResponseMessage browseStrategies(int page, String strategyName)
	{
		return browseStrategies(page, 20, strategyName);
	}

	public ResponseMessage browseStrategies(String strategyName)
	{
		return browseStrategies(0, strategyName);
	}

	public ResponseMessage browseStrategies(int page, int perPage, String strategyName)
	{
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new BrowseInfo(page, perPage));
		if (strategyName != null && !strategyName.isBlank())
			entities.add(new KVParameter("STRATEGYNAME", strategyName));
		return sendRequest(ActionRequest.BROWSE_STRATEGIES, entities);
	}

	public ResponseMessage browseReports(int page, String strategyName, String marketId)
	{
		return browseReports(page, 20, strategyName, marketId);
	}

	public ResponseMessage browseReports(int page, int perPage, String strategyName)
	{
		return browseReports(page, perPage, strategyName, null);
	}

	public ResponseMessage browseReports(int page, String strategyName)
	{
		return browseReports(page, strategyName, null);
	}

	public ResponseMessage browseReports(String strategyName)
	{
		return browseReports(0, strategyName);
	}

	public ResponseMessage browseReports(int page, int perPage, String strategyName, String marketId)
	{
		Entity browseReportInfo;
		if (marketId != null && !marketId.isBlank())
			browseReportInfo = new BrowseReportInfo(strategyName, marketId, page, perPage);
		else
			browseReportInfo = new BrowseReportInfo(strategyName, page, perPage);
		return sendRequest(ActionRequest.BROWSE_REPORTS, browseReportInfo);
	}

	public ResponseMessage browseUsers()
	{
		return browseUsers(0);
	}

	public ResponseMessage browseUsers(int page)
	{
		return browseUsers(0, 20);
	}

	public ResponseMessage browseUsers(int page, int perPage)
	{
		return browseUsers(page, perPage, null);
	}

	public ResponseMessage browseUsers(int page, String username)
	{
		return browseUsers(page, 20, username);
	}

	public ResponseMessage browseUsers(String username)
	{
		return browseUsers(0, 20, username);
	}

	public ResponseMessage browseUsers(int page, int perPage, String username)
	{
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new BrowseInfo(page, perPage));
		if (username != null && !username.isBlank())
			entities.add(new KVParameter("USERNAME", username));
		return sendRequest(ActionRequest.BROWSE_USERS, entities);
	}

	public ResponseMessage browseDataSources()
	{
		return sendRequest(ActionRequest.BROWSE_DATA_SOURCES);
	}

	public ResponseMessage receiveProgress()
	{
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		if (resMsg == null) {
			close();
			return getProtocolErrorMessage();
		}
		if (!resMsg.isSuccess()) {
			close();
			return resMsg;
		}
		if (!resMsg.isValid(ActionRequest.RUN_STRATEGY)) {
			close();
			return getProtocolErrorMessage();
		}
		if (resMsg.getEntity() instanceof ReportInfo)
			close();
		return resMsg;
	}

	public ResponseMessage runStrategy(String strategyName, String market, boolean inverseCross, int granularity)
	{
		return runStrategy(strategyName, market, inverseCross, granularity, null);
	}

	public ResponseMessage runStrategy(String strategyName, String market, boolean inverseCross, int granularity,
		List<KVParameter> parameters)
	{
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new KVParameter("STRATEGYNAME", strategyName));
		entities.add(new KVParameter("market", market));
		entities.add(new KVParameter("inverseCross", inverseCross ? "true" : "false"));
		entities.add(new KVParameter("granularity", Integer.toString(granularity)));
		if (parameters != null)
			entities.addAll(parameters);
		connect();
		new RequestMessage(ActionRequest.RUN_STRATEGY, authToken, entities).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		if (resMsg == null) {
			close();
			return getProtocolErrorMessage();
		}
		if (!resMsg.isSuccess() && resMsg.getErrorMsg().startsWith("NO-AUTH")) {
			close();
			resMsg = performLogin();
			if (!resMsg.isSuccess()) {
				close();
				return resMsg;
			}
			new RequestMessage(ActionRequest.RUN_STRATEGY, authToken, entities).send(outputStream);
			if (resMsg == null || !resMsg.isSuccess() || !resMsg.isValid(ActionRequest.RUN_STRATEGY)
				|| resMsg.getEntity() instanceof ReportInfo)
				close();
		}
		return resMsg != null && resMsg.isValid(ActionRequest.RUN_STRATEGY) ? resMsg : getProtocolErrorMessage();
	}

	public ResponseMessage addStrategy(String className, String fileName) throws IOException
	{
		return sendRequest(ActionRequest.ADD_STRATEGY, new KVParameter("CLASSNAME", className), new FileContent(fileName));
	}

	public ResponseMessage downloadStrategy(String strategyName)
	{
		return sendRequest(ActionRequest.DOWNLOAD_STRATEGY, new KVParameter("STRATEGYNAME", strategyName));
	}

	public ResponseMessage deleteStrategy(String strategyName)
	{
		return sendRequest(ActionRequest.DELETE_STRATEGY, new KVParameter("STRATEGYNAME", strategyName));
	}

	public ResponseMessage viewReport(String reportId)
	{
		return sendRequest(ActionRequest.VIEW_REPORT, new KVParameter("REPORTID", reportId));
	}

	public ResponseMessage deleteReport(String reportId)
	{
		return sendRequest(ActionRequest.DELETE_REPORT, new KVParameter("REPORTID", reportId));
	}

	public ResponseMessage addUser(String username, String password)
	{
		return sendRequest(ActionRequest.ADD_USER, new LoginInfo(username, password));
	}

	public ResponseMessage deleteUser(String username)
	{
		return sendRequest(ActionRequest.DELETE_USER, new KVParameter("REPORTID", username));
	}

	public ResponseMessage editDataSource(String sourceName, boolean enabled)
	{
		return sendRequest(ActionRequest.EDIT_DATA_SOURCE, new SourceInfo(sourceName, enabled));
	}

	public ResponseMessage editMarket(String sourceName, String marketId, int granularity, boolean selectable,
		boolean sync)
	{
		return sendRequest(ActionRequest.EDIT_MARKET, new MarketInfo(sourceName, marketId, granularity, selectable, sync));
	}

	public ResponseMessage deleteData(String source, String market, Instant date)
	{
		List<Entity> parameters = new ArrayList<Entity>();
		parameters.add(new KVParameter("SOURCE", source));
		if (market != null)
			parameters.add(new KVParameter("MARKET", market));
		if (date != null)
			parameters.add(new KVParameter("DATE", date));
		return sendRequest(ActionRequest.DELETE_DATA, parameters);
	}

	public ResponseMessage deleteData(String source, String market)
	{
		return deleteData(source, market, null);
	}

	public ResponseMessage deleteData(String source)
	{
		return deleteData(source, null, null);
	}

	public ResponseMessage deleteData(String source, Instant date)
	{
		return deleteData(source, null, date);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		return sendRequest(actionRequest, Arrays.asList(entities));
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, List<Entity> entities)
	{
		connect();
		new RequestMessage(actionRequest, authToken, entities).send(outputStream);
		ResponseMessage resMsg = ResponseMessage.receive(inputStream);
		close();
		if (resMsg == null)
			return getProtocolErrorMessage();
		if (!resMsg.isSuccess() && resMsg.getErrorMsg().startsWith("NO-AUTH")) {
			resMsg = performLogin();
			if (!resMsg.isSuccess()) {
				close();
				return resMsg;
			}
			new RequestMessage(actionRequest, authToken, entities).send(outputStream);
			resMsg = ResponseMessage.receive(inputStream);
			close();
		}
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
	public void close()
	{
		if (!connected)
			return;
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			System.err.println("ERROR: error while closing the connection with the server: " + ex.getMessage());
		}
		connected = false;
	}

	public void setServerAddress(String serverAddress)
	{
		this.serverAddress = serverAddress;
	}

	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
}