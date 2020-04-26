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

public class MarketListMenu extends Menu
{

	protected String dataSource;
	protected String nameFilter;
	protected int currentPage;
	protected boolean justSelect;

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
		this(null, nameFilter);
	}

	public MarketListMenu()
	{
		this(null);
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseMarkets(currentPage, dataSource, nameFilter);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<MarketInfo> markets = new ArrayList<MarketInfo>();
		for(Entity entity: resMsg.getEntities())
			markets.add((MarketInfo)entity);

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
		new MarketMenu((MarketInfo)entry.getHandlerData()).show();
	}

	private void handleSelectMarket(MenuEntry entry)
	{
		// TODO: market selection for strategy run configuration
	}

	private void handleLoadNewPage(MenuEntry entry)
	{
		currentPage++;
	}
}
