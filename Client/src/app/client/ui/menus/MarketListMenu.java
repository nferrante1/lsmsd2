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
import app.client.ui.menus.forms.ConfigMarketForm;
import app.client.ui.menus.forms.UserForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.SourceInfo;
import app.common.net.entities.StrategyInfo;


public class MarketListMenu extends Menu {
	
	protected List<MarketInfo> markets = new ArrayList<MarketInfo>();
	protected String filter;
	protected Entity entity;
	protected int currentPage;
	
	public MarketListMenu(String filter)
	{
		this(filter, null);
	}
	
	public MarketListMenu(String filter, Entity entity)
	{
		super("The list of all markets:");
		this.entity = entity;
		this.filter = filter;	
		this.currentPage = 1;
	}
	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg;
		if(this.entity instanceof StrategyInfo && this.entity != null) {
			resMsg = Protocol.getInstance().browseMarkets(new BrowseInfo(filter, currentPage));
		}
		else {
			HashMap<String, String> filters = new HashMap<String,String>();
			SourceInfo dataSource = (SourceInfo) entity;
			filters.put("dataSource", dataSource.getName());
			filters.put("filter", filter);
			resMsg = Protocol.getInstance().browseMarkets(new BrowseInfo(filters, currentPage));
		}
		
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}
		
		markets.clear();
		for(Entity entity: resMsg.getEntities()) {
			markets.add((MarketInfo)entity);
		}
		
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i=1;
		for(MarketInfo market : markets) {
			if(this.entity instanceof SourceInfo && this.entity != null) {
				menu.add(new MenuEntry(i, market.getId() + " " + market.getGranularity() + " " + market.isSync() + " "
						+ market.isSelectable(), this::handleConfigMarket, market));
			}
			else {
				menu.add(new MenuEntry(i, market.getId() + " " + market.getGranularity(), this::handleSelectMarket, market));
			}
		}
				
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		
		menu.add(new MenuEntry(0, "Go back", true));
		
		return menu;
	}
	
	private void handleConfigMarket(MenuEntry entry)
	{
		currentPage = 1;
		MarketInfo market = (MarketInfo) entry.getHandlerData();
		HashMap<Integer, String> response = new ConfigMarketForm().show();
		market.setGranularity(Integer.parseInt(response.get(0)));
		market.setSelectable(Boolean.parseBoolean(response.get(1)));
		market.setSync(Boolean.parseBoolean(response.get(2)));
		
		ResponseMessage resMsg = Protocol.getInstance().configMarket(market);
		if(!resMsg.isSuccess()) {
			Console.print(resMsg.getErrorMsg());
		}
		else {
			Console.println("Market correctly configured!");
		}
	}
	
	private void handleSelectMarket(MenuEntry entry)
	{
		
	}
	
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		getMenu();
	}
}
