package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.Entity;
import app.common.net.entities.SourceInfo;

public class DataSourceMenu extends Menu
{
	protected SourceInfo data_source;

	public DataSourceMenu(SourceInfo data_source)
	{
		super("Selected Data Source is: "  + data_source.getName() + "Enabled: " + data_source.isEnabled() + " | select an action");
		this.data_source = data_source;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		if(data_source.isEnabled()) {
			menu.add(new MenuEntry(1, "Disable Data Source", true, this::handleDisableDataSource));
		}
		else {
			menu.add(new MenuEntry(1, "Enable Data Source", true, this::handleEnableDataSource));
		}
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
		//il riempimento della MarketListMenu si fa nel costruttore o fuori??
		// Domanda relativa a come fare la searchbyName e la paginazione
	}
}
