package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;
import app.client.ui.menus.forms.LoginForm;

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
		HashMap<Integer, String> response = new LoginForm().show();
		doLogin(response.get(0), response.get(1));
	}

	private void doLogin(String username, String password)
	{
		ResponseMessage resMsg = protocol.performLogin(new Customer(username, password));
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		loggedUser = (User)resMsg.getEntity();
		Console.println("Successfully logged in as " + loggedUser.getUsername() + "!");
		new UserMenu().show();
	}
}
