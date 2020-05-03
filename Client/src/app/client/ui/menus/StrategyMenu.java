package app.client.ui.menus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.PathForm;
import app.client.ui.menus.forms.SearchForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.FileContent;
import app.common.net.entities.StrategyInfo;


public class StrategyMenu extends Menu
{
	protected StrategyInfo strategy;

	public StrategyMenu(StrategyInfo strategy)
	{
		super(strategy.getName() + " | Select an action");
		this.strategy = strategy;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "View details", this::handleViewStrategy));
		menu.add(new MenuEntry(2, "Browse reports", this::handleBrowseReports));
		menu.add(new MenuEntry(3, "Run strategy", this::handleRunStrategy));
		menu.add(new MenuEntry(4, "Download strategy", this::handleDownloadStrategy));
		if(strategy.isDeletable()) {
			menu.add(new MenuEntry(5, "Delete strategy", true, this::handleDeleteStrategy));
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleViewStrategy(MenuEntry entry)
	{
		Console.println("Name: " + strategy.getName());
		Console.println("Author: " + strategy.getAuthor());
		Console.pause();
	}

	private void handleBrowseReports(MenuEntry entry)
	{
		HashMap<String, String> response = new SearchForm("Market Name").show();
		MarketListMenu list = new MarketListMenu(response.get("Market Name"));
		list.show();
		String market = list.getSelectedMarketId();
		new ReportListMenu((StrategyInfo)entry.getHandlerData(), market).show();
	}

	private void handleRunStrategy(MenuEntry entry)
	{
		//TODO
	}

	private void handleDeleteStrategy(MenuEntry entry)
	{
		Protocol.getInstance().deleteStrategy(strategy.getName());
	}

	private void handleDownloadStrategy(MenuEntry entry)
	{
		HashMap<String, String> response = new PathForm("Select a path where store the strategy:").show();
		ResponseMessage resMsg = Protocol.getInstance().downloadStrategy(strategy.getName());
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		try (FileOutputStream fos = new FileOutputStream(response.get("Path") + "/" + strategy.getName())) {
			FileContent file = resMsg.getEntity(FileContent.class);
			fos.write(file.getContent());
			fos.close();
			Console.println("Strategy correctly added");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
