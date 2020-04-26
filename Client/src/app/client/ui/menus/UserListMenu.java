package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.Entity;
import app.common.net.entities.UserInfo;

public class UserListMenu extends Menu
{
	protected String filter;
	protected int currentPage;

	public UserListMenu(String filter)
	{
		super("Select a user to DELETE it:");
		this.filter = filter;
		this.currentPage = 1;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseUsers(currentPage, filter);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<UserInfo> users = new ArrayList<UserInfo>();
		for(Entity entity: resMsg.getEntities())
			users.add((UserInfo)entity);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i =1;
		for(UserInfo user : users)
			menu.add(new MenuEntry(i, user.getUsername() + " (admin: " + user.isAdmin() + ")", true, this::handleDeleteUser, user));
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleDeleteUser(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteUser(((UserInfo)entry.getHandlerData()).getUsername());
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("User correctly deleted.");
	}

	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
	}
}
