package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.Entity;
import app.common.net.entities.MarketInfo;

public class MarketListMenu extends Menu
{

	protected String dataSource;
	protected String nameFilter;
	protected int currentPage;
	protected boolean justSelect;
	protected String selectedMarketId;
	
	public MarketListMenu(String nameFilter, boolean justSelect)
	{
		this(null, nameFilter);
		this.justSelect = justSelect;
	}

	public MarketListMenu(String dataSource, String nameFilter)
	{
		super("Select a market");
		this.dataSource = dataSource;
		this.nameFilter = nameFilter;
		this.currentPage = 1;
	}

	public MarketListMenu(String nameFilter)
	{
		this(nameFilter, true);
	}

	public MarketListMenu()
	{
		this(null, true);
	}
	
	public String getSelectedMarketId() 
	{
		return this.selectedMarketId;
	}
	@Override
	protected List<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseMarkets(currentPage, dataSource, nameFilter);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<MarketInfo> markets = resMsg.getEntities(MarketInfo.class);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for(MarketInfo market: markets) {
			menu.add(new MenuEntry(i, market.getDisplayName(), true, justSelect ? this::handleSelectMarket : this::handleViewMarket, market));
			i++;
		}

		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));

		return menu;
	}

	private void handleViewMarket(MenuEntry entry)
	{
		this.selectedMarketId = ((MarketInfo) entry.getHandlerData()).getFullId();
		new MarketMenu((MarketInfo)entry.getHandlerData()).show();
	}

	private void handleSelectMarket(MenuEntry entry)
	{
		this.selectedMarketId = ((MarketInfo) entry.getHandlerData()).getFullId();
	}

	private void handleLoadNewPage(MenuEntry entry)
	{
		currentPage++;
	}
}
