package app.client.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;

public class UserMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Find all strategies", this::handleBrowseStrategies));
		menu.add(new MenuEntry(2, "Add a new strategy", this::handleAddStrategy));
		menu.add(new MenuEntry(0, "Log-Out", true, this::handleLogout));
		return menu;
	}

	private void handleLogout(MenuEntry entry)
	{
		ResponseMessage resMsg = protocol.performLogout();
		loggedUser = null;
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Sucessfully logged out!");
	}

	private void handleBrowseStrategies(MenuEntry entry)
	{
	
	}

	private void handleAddStrategy(MenuEntry entry)
	{
		
	}

}