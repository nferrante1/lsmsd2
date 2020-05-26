package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.UserInfo;

final class UserListMenu extends PagedMenu
{
	private final String filter;

	UserListMenu(String filter)
	{
		super("Select a user to DELETE it");
		this.filter = filter;
	}

	@Override
	protected List<MenuEntry> getEntries()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseUsers(getPage(), getPerPage(), filter);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<UserInfo> users = resMsg.getEntities(UserInfo.class);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for (UserInfo user : users) {
			menu.add(new MenuEntry(i, user.getUsername() + " (admin: " + user.isAdmin() + ")", true, this::handleDeleteUser, user));
			++i;
		}
		return menu;
	}

	private void handleDeleteUser(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteUser(((UserInfo)entry.getHandlerData()).getUsername());
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("User correctly deleted.");
	}
}
