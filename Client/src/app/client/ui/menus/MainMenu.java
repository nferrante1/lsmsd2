package app.client.ui.menus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.SearchForm;
import app.client.ui.menus.forms.StrategyFileForm;
import app.client.ui.menus.forms.UserForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.StrategyInfo;

final class MainMenu extends Menu
{
	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "Browse strategies", this::handleBrowseStrategies));
		menu.add(new MenuEntry(2, "Upload a new strategy", this::handleAddStrategy));
		if (Protocol.getInstance().isAdmin()) {
			menu.add(new MenuEntry(3, "Browse users", this::handleBrowseUsers));
			menu.add(new MenuEntry(4, "Create user", this::handleAddUser));
			menu.add(new MenuEntry(5, "Browse data sources", this::handleBrowseDataSource));
		}
		menu.add(new MenuEntry(0, "Log-Out", true, this::handleLogout));
		return menu;
	}

	private void handleLogout(MenuEntry entry)
	{
		Protocol.getInstance().performLogout();
	}

	private void handleBrowseStrategies(MenuEntry entry)
	{
		HashMap<String, String> response = new SearchForm("Strategy Name").show();
		new StrategyListMenu(response.get("Strategy Name")).show();
	}

	private void handleAddStrategy(MenuEntry entry)
	{
		HashMap<String, String> response = new StrategyFileForm("Insert a strategy file to upload (the file must be .java)", true).show();
		ResponseMessage resMsg;
		try {
			resMsg = Protocol.getInstance().addStrategy(response.get("File"));
		} catch (IOException e) {
			Console.println("Can not read file '" + response.get("File") + "': " + e.getMessage());
			return;
		}
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Strategy successfully added.");
		Console.newLine();
		new StrategyMenu(resMsg.getEntity(StrategyInfo.class)).show();

	}

	private void handleBrowseUsers(MenuEntry entry)
	{
		HashMap<String, String> response = new SearchForm("Username").show();
		new UserListMenu(response.get("Username")).show();
	}

	private void handleAddUser(MenuEntry entry)
	{
		HashMap<String, String> response = new UserForm("Create user", true).show();
		ResponseMessage resMsg = Protocol.getInstance().addUser(response.get("USERNAME"), response.get("PASSWORD"));
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("User successfully created.");
	}

	private void handleBrowseDataSource(MenuEntry entry)
	{
		new DataSourceListMenu().show();
	}
}