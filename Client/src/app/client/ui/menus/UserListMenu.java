package app.client.ui.menus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.MenuEntry;
import app.common.net.ResponseMessage;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.UserInfo;

public class UserListMenu extends Menu
{
	protected String filter;
	protected int currentPage;
	
	protected List<UserInfo> users;

	public UserListMenu(String filter)
	{
		super("List of the users. Select an user to DELETE it:");
		this.filter = filter;
		this.currentPage = 1;
		
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		
		ResponseMessage resMsg = Protocol.getInstance().browseUsers(new BrowseInfo(filter, currentPage));
		
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}
		
		for(int i=0; i<resMsg.getEntityCount(); i++) {
			users.add((UserInfo)resMsg.getEntity(i));
		}
		
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i =1;
		for(UserInfo user : users) {
			menu.add(new MenuEntry(i, user.getUsername() + Boolean.toString(user.isAdmin()), true, this::handleDeleteUser, user));
		}		
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleDeleteUser(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteUser((UserInfo)entry.getHandlerData());
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
		}
		else {
			Console.println("User correctly deleted.");
		}
		
	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		getMenu();
		
	}
}
