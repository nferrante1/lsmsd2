package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.MarketInfo;

final class MarketListMenu extends PagedMenu
{
	private final String dataSource;
	private final String nameFilter;

	MarketListMenu(String dataSource, String nameFilter)
	{
		super("Select a market");
		this.dataSource = dataSource;
		if (nameFilter != null && nameFilter.contains("/"))
			this.nameFilter = nameFilter.replaceFirst("/", "[/-]?");
		else
			this.nameFilter = nameFilter;
	}

	MarketListMenu(String nameFilter)
	{
		this(null, nameFilter);
	}

	public MarketListMenu()
	{
		this(null, null);
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
		int i = 1;
		for (MarketInfo market : markets) {
			menu.add(new MenuEntry(i, market.getDisplayName(), true, this::handleViewMarket, market));
			i++;
		}

		return menu;
	}

	private void handleViewMarket(MenuEntry entry)
	{
		new MarketMenu((MarketInfo)entry.getHandlerData()).show();
	}
}
