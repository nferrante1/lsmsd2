package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.SearchByNameForm;
import app.client.ui.menus.forms.UserForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.LoginInfo;

public class UserMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
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
		HashMap<Integer, String> response = new SearchByNameForm("Strategy Name").show();
		new StrategyListMenu(response.get(0)).show();
	}

	private void handleAddStrategy(MenuEntry entry)
	{
	}

	private void handleBrowseUsers(MenuEntry entry)
	{
		HashMap<Integer, String> response = new SearchByNameForm("Username").show();
		new UserListMenu(response.get(0)).show();
	}

	private void handleAddUser(MenuEntry entry)
	{
		HashMap<Integer, String> response = new UserForm("Create user").show();
		ResponseMessage resMsg = Protocol.getInstance().addUser(new LoginInfo(response.get(0), response.get(1)));
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