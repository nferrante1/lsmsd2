package app.client.ui.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.SearchForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.SourceInfo;

public class DataSourceMenu extends Menu
{
	protected SourceInfo dataSource;

	public DataSourceMenu(SourceInfo dataSource)
	{
		super(dataSource.getName() + " | select an action");
		this.dataSource = dataSource;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "View details", this::handleViewDataSource));
		menu.add(new MenuEntry(2, (dataSource.isEnabled() ? "Dis" : "En") + "able Data Source", this::handleEditDataSource, !dataSource.isEnabled()));
		menu.add(new MenuEntry(3, "Browse Markets", this::handleBrowseMarket, dataSource));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleViewDataSource(MenuEntry entry)
	{
		Console.println("Name: " + dataSource.getName());
		Console.println("Enabled: " + dataSource.isEnabled());
		Console.pause();
	}

	private void handleEditDataSource(MenuEntry entry)
	{
		boolean enable = (boolean)entry.getHandlerData();
		ResponseMessage resMsg = Protocol.getInstance().editDataSource(dataSource.getName(), enable);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		dataSource.setEnabled(enable);
		Console.println("Done!");
	}

	private void handleBrowseMarket(MenuEntry entry)
	{
		HashMap<String, String> response = new SearchForm("Market Name").show();
		String nameFilter = response.get("Market Name");
		nameFilter = nameFilter != null ? nameFilter.trim() : null;
		new MarketListMenu(dataSource.getName(), nameFilter).show();
	}
}
