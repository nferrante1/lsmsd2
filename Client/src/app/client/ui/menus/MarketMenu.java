package app.client.ui.menus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.DateTimeForm;
import app.client.ui.menus.forms.MarketGranularityForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.MarketInfo;

public class MarketMenu extends Menu
{
	protected MarketInfo market;

	public MarketMenu(MarketInfo market)
	{
		super(market.getDisplayName() + " | Select an action");
		this.market = market;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "View details", this::handleViewDetails));
		menu.add(new MenuEntry(2, "Change granularity", this::handleChangeGranularity));
		menu.add(new MenuEntry(3, (market.isSelectable() ? "Dis" : "En") + "able selectability", this::handleChangeSelectability, !market.isSelectable()));
		menu.add(new MenuEntry(4, (market.isSync() ? "Dis" : "En") + "able data sync", this::handleChangeSync, !market.isSync()));
		menu.add(new MenuEntry(5, "Delete data", this::handleDeleteData));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleViewDetails(MenuEntry entry)
	{
		Console.println("Name: " + market.getDisplayName());
		Console.println("Source: " + market.getSourceName());
		Console.println("Id: " + market.getMarketId());
		Console.println("Base currency: " + market.getBaseCurrency());
		Console.println("Quote currency: " + market.getQuoteCurrency());
		Console.println("Selectable: " + market.isSelectable());
		Console.println("Data sync: " + market.isSync());
		Console.pause();
	}

	private void handleChangeGranularity(MenuEntry entry)
	{
		HashMap<String, String> response = new MarketGranularityForm(market.getGranularity()).show();
		int granularity = Integer.parseInt(response.get("Granularity"));
		if (granularity == market.getGranularity()) {
			Console.println("Done!");
			return;
		}
		if ((granularity < market.getGranularity() || granularity % market.getGranularity() != 0)
			&& !Console.askConfirm("Setting a granularity that is not a multiple of the previous granularity (" + market.getGranularity() + ") will delete all downloaded market data. Are you sure?")) {
			Console.println("Aborting...");
			return;
		}
		ResponseMessage resMsg = Protocol.getInstance().editMarket(market.getSourceName(), market.getMarketId(), granularity, market.isSelectable(), market.isSync());
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		market.setGranularity(granularity);
		Console.println("Done!");
	}

	private void handleChangeSelectability(MenuEntry entry)
	{
		boolean enable = (boolean)entry.getHandlerData();
		ResponseMessage resMsg = Protocol.getInstance().editMarket(market.getSourceName(), market.getMarketId(), market.getGranularity(), enable, market.isSync());
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		market.setSelectable(enable);
		Console.println("Done!");
	}

	private void handleChangeSync(MenuEntry entry)
	{
		boolean enable = (boolean)entry.getHandlerData();
		ResponseMessage resMsg = Protocol.getInstance().editMarket(market.getSourceName(), market.getMarketId(), market.getGranularity(), market.isSelectable(), enable);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		market.setSync(enable);
		Console.println("Done!");
	}

	private void handleDeleteData(MenuEntry entry)
	{
		HashMap<String, String> response = new DateTimeForm().show();
		ResponseMessage resMsg;
		String dateString = response.get("Date");
		if (dateString == null) {
			resMsg = Protocol.getInstance().deleteData(market.getSourceName(), market.getMarketId());
		} else {
			Instant date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-M-d H:m")).atZone(ZoneId.of("UTC")).toInstant();
			resMsg = Protocol.getInstance().deleteData(market.getSourceName(), market.getMarketId(), date);
		}

		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Done!");
	}

}
