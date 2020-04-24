package app.client.ui.menus;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.UserForm;
import app.common.net.entities.StrategyInfo;


public class StrategyMenu extends Menu
{
	protected StrategyInfo strategy;

	public StrategyMenu(StrategyInfo strategy)
	{
		super("Strategy Name: " + strategy.getName());
		this.strategy = strategy;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		if(strategy.isCanDelete()) {
			Console.println("You are the author of this strategy!");
		}
		else {
			Console.println("Author: " + strategy.getUsername());
		}
		
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Browse reports", this::handleBrowseReports, strategy));
		menu.add(new MenuEntry(2, "Run strategy", this::handleRunStrategy));
		menu.add(new MenuEntry(3, "Download strategy", this::handleDownloadStrategy));
		if(strategy.isCanDelete()) {
			menu.add(new MenuEntry(4, "Delete strategy", true, this::handleDeleteStrategy));
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleBrowseReports(MenuEntry entry)
	{
		HashMap<Integer, String> response = new UserForm("Market Name: ").show();
		new ReportListMenu((StrategyInfo)entry.getHandlerData(), response.get(0)).show();
	}

	private void handleRunStrategy(MenuEntry entry)
	{
		
	}

	private void handleDeleteStrategy(MenuEntry entry)
	{
		Protocol.getInstance().deleteStrategy((StrategyInfo)entry.getHandlerData());
	}

	private void handleDownloadStrategy(MenuEntry entry)
	{

	}
}
