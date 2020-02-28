package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.menus.forms.*;

public class LoginMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Log-In", this::handleLogin));
		menu.add(new MenuEntry(0, "Exit", true));
		return menu;
	}

	private void handleLogin(MenuEntry entry)
	{
		HashMap<Integer, String> response = new UserForm().show();
		doLogin(response.get(0), response.get(1));
	}

	private void doLogin(String username, String password)
	{
		//mandare la login al server. Gestire errori
		new UserMenu().show();
	}
}
