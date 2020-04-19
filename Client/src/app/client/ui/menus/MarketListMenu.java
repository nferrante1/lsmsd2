package app.client.ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.MenuEntry;
import app.common.net.ResponseMessage;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.MarketInfo;


public class MarketListMenu extends Menu {
	
	protected List<MarketInfo> markets;
	protected String filter;
	protected String dataSource;
	protected int currentPage;
	
	public MarketListMenu(String filter)
	{
		this(filter, null);
	}
	
	public MarketListMenu(String filter, String dataSource)
	{
		super("The list of all markets:");
		this.dataSource = dataSource;
		this.filter = filter;	
		this.currentPage = 1;
	}
	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg;
		if(this.dataSource  == null) {
			resMsg = Protocol.getInstance().browseMarkets(new BrowseInfo(filter, currentPage));
		}
		else {
			Protocol.getInstance().browseMarketsOfDataSource(dataSource, new BrowseInfo(filter, currentPage));
		}
		
		for(int i=0; i<resMsg.getEntityCount(); i++) {
			markets.add((MarketInfo)resMsg.getEntity(i));
		}
		
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i=1;
		for(MarketInfo market : markets) {
			if(this.dataSource.equals(null)) {
				menu.add(new MenuEntry(i, market.getId() + " " + market.getGranularity() + " " + market.isSync() + " "
						+ market.isSelectable(), this::handleConfigMarket, market));
			}
			else {
				menu.add(new MenuEntry(i, market.getId() + " " + market.getGranularity(), this::handleConfigMarket, market));
			}
		}
				
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		
		menu.add(new MenuEntry(0, "Go back", true));
		
		return menu;
	}
	
	private void handleConfigMarket(MenuEntry entry)
	{
		//richiedere informazioni market al server
		//stampare informazioni market Console.print(strategy.toString);
		
		currentPage = 1;
		new ConfigMarketForm(entry.getHandlerData()).show();
	}
	
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		getMenu();
	}
}
