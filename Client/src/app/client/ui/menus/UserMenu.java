package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;
import app.client.ui.menus.forms.UserForm;

public class UserMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Find all strategies", this::handleBrowseStrategies));
		menu.add(new MenuEntry(2, "Add a new strategy", this::handleAddStrategy));
		if (loggedUser.isAdmin()) {
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
		//mandare logout al server
	}

	private void handleBrowseStrategies(MenuEntry entry)
	{
		new StrategyListMenu().show();
	}

	private void handleAddStrategy(MenuEntry entry)
	{
		
	}
	
	private void handleBrowseUsers(MenuEntry entry)
	{
		new UserListMenu().show();
	}
	
	private void handleAddUser(MenuEntry entry)
	{
		HashMap<Integer, String> response = new UserForm("insert username and password").show();
		// Richiesta di aggiunta utente al server
	}
	
	private void handleBrowseDataSource(MenuEntry entry)
	{
		
	}
	
	private void handleDeleteData(MenuEntry entry)
	{
		
	}

}