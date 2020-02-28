package app.client.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;

public class DataSourceMenu extends Menu
{
	protected DataSource data_source;

	public DataSourceMenu(DataSource data_source)
	{
		super(data_source.getName() + " | select an action");
		this.data_source = data_source;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		
		menu.add(new MenuEntry(1, "Enable Data Source", true, this::handleEnableDataSource));
		menu.add(new MenuEntry(2, "Disable Data Source", true, this::handleDisableDataSource));
		menu.add(new MenuEntry(3, "Browse Markets", true, this::handleBrowseMarket, data_source));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleEnableDataSource(MenuEntry entry)
	{
		// Invio comando di abilitazione al server
	}
	
	private void handleDisableDataSource(MenuEntry entry)
	{
		// Invio comando di disabilitazione al server
	}
	
	private void handleBrowseMarket(MenuEntry entry)
	{
		new MarketListMenu(entry.getHandlerData()).show();
	}
}
