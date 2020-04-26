package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.SearchByForm;
import app.client.ui.menus.forms.UserForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.LoginInfo;

public class UserMenu extends Menu
{
	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "Find all strategies", this::handleBrowseStrategies));
		menu.add(new MenuEntry(2, "Add a new strategy", this::handleAddStrategy));
		if (Protocol.getInstance().isAdmin()) {
			menu.add(new MenuEntry(3, "Find all users", this::handleBrowseUsers));
			menu.add(new MenuEntry(4, "Add a new user", this::handleAddUser));
			menu.add(new MenuEntry(5, "Find all data sources", this::handleBrowseDataSource));
			menu.add(new MenuEntry(6, "Delete data", this::handleDeleteData));
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
		HashMap<String, String> response = new SearchByForm("Strategy Name").show();
		new StrategyListMenu(response.get("Strategy Name")).show();
	}

	private void handleAddStrategy(MenuEntry entry)
	{
	}

	private void handleBrowseUsers(MenuEntry entry)
	{
		HashMap<String, String> response = new SearchByForm("Username").show();
		new UserListMenu(response.get("Username")).show();
	}

	private void handleAddUser(MenuEntry entry)
	{
		HashMap<String, String> response = new UserForm("Create user").show();
		ResponseMessage resMsg = Protocol.getInstance().addUser(response.get("USERNAME"), response.get("PASSWORD"));
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("User correctly created");
	}

	private void handleBrowseDataSource(MenuEntry entry)
	{
		new DataSourceListMenu().show();
	}

	private void handleDeleteData(MenuEntry entry)
	{
	}
}