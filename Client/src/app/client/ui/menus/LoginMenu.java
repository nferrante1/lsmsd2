package app.client.ui.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.UserForm;
import app.common.net.ResponseMessage;

public class LoginMenu extends Menu
{
	public LoginMenu()
	{
		super();
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "Log-In", this::handleLogin));
		menu.add(new MenuEntry(0, "Exit", true));
		return menu;
	}

	private void handleLogin(MenuEntry entry)
	{
		HashMap<String, String> response = new UserForm().show();
		doLogin(response.get("USERNAME"), response.get("PASSWORD"));
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
