package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.MarketInfo;

public class SelectMarketMenu extends SelectMenu<MarketInfo>
{
	protected String dataSource;
	protected String nameFilter;
	protected boolean allowAll;

	public SelectMarketMenu(String dataSource, String nameFilter, boolean allowAll)
	{
		super("Select a market", allowAll ? 19 : 20);
		this.dataSource = dataSource;
		if (nameFilter != null && nameFilter.contains("/"))
			this.nameFilter = nameFilter.replaceFirst("/", "[/-]?");
		else
			this.nameFilter = nameFilter;
		this.allowAll = allowAll;
	}

	public SelectMarketMenu(String dataSource, String nameFilter)
	{
		this(dataSource, nameFilter, false);
	}

	public SelectMarketMenu(String nameFilter, boolean allowAll)
	{
		this(null, nameFilter, allowAll);
	}

	public SelectMarketMenu(String nameFilter)
	{
		this(nameFilter, false);
	}

	public SelectMarketMenu(boolean allowAll)
	{
		this(null, allowAll);
	}

	public SelectMarketMenu()
	{
		this(false);
	}

	@Override
	protected List<MenuEntry> getEntries()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseMarkets(getPage(), getPerPage(), dataSource, nameFilter);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<MarketInfo> markets = resMsg.getEntities(MarketInfo.class);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		if (allowAll)
			menu.add(new MenuEntry(1, "All Markets", true, this::handleSelectAll));
		int i = allowAll ? 2 : 1;
		for (MarketInfo market : markets) {
			menu.add(new MenuEntry(i, market.getDisplayName(), true, this::handleSelectMarket, market));
			i++;
		}

		return menu;
	}

	private void handleSelectMarket(MenuEntry entry)
	{
		setSelection(((MarketInfo)entry.getHandlerData()));
	}

	private void handleSelectAll(MenuEntry entry)
	{
		setSelection(new MarketInfo("", ""));
	}
}
