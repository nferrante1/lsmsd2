package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.*;
import app.common.net.ResponseMessage;

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
		ResponseMessage resMsg = Protocol.getInstance().performLogin(username, password);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Successfully logged in as " + username);
		new UserMenu().show();
	}
}
