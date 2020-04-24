package app.client.ui.menus;

import java.util.HashMap;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.SearchByNameForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.SourceInfo;

public class DataSourceMenu extends Menu
{
	protected SourceInfo dataSource;

	public DataSourceMenu(SourceInfo dataSource)
	{
		super("Selected Data Source is: "  + dataSource.getName() + "Enabled: " + dataSource.isEnabled() + " | select an action");
		this.dataSource = dataSource;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		if(dataSource.isEnabled()) {
			menu.add(new MenuEntry(1, "Disable Data Source", true, this::changeDataSource, dataSource));
		}
		else {
			menu.add(new MenuEntry(1, "Enable Data Source", true, this::changeDataSource, dataSource));
		}
		menu.add(new MenuEntry(3, "Browse Markets", true, this::handleBrowseMarket, dataSource));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void changeDataSource(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().changeDataSource((SourceInfo)entry.getHandlerData());
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
		}
		else {
			Console.println("data source correctly setted");
		}
	}
	
	private void handleBrowseMarket(MenuEntry entry)
	{
		HashMap<Integer, String> response = new SearchByNameForm("MarketName").show();
		new MarketListMenu(response.get(0), dataSource);
	}
}
