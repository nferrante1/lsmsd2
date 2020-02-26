package app.client.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;

public class StrategyMenu extends Menu
{
	protected Strategy strategy;

	public RestaurantMenu(Strategy strategy)
	{
		super(strategy.getName() + " | select an action");
		this.strategy = strategy;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Browse reports", this::handleBrowseReports));
		menu.add(new MenuEntry(2, "Run strategy", this::handleRunStrategy));
		menu.add(new MenuEntry(3, "Download strategy", this::handleDownloadStrategy));
		//Se la strategia è dell' utente if(strategy.canDelete) {
			menu.add(new MenuEntry(4, "Delete strategy", true, this::handleDeleteStrategy));
		//}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleBrowseReports(MenuEntry entry)
	{
		new ReportListMenu().show();
	}

	private void handleRunStrategy(MenuEntry entry)
	{
		
	}

	private void handleDeleteStrategy(MenuEntry entry)
	{
		boolean confirm = Console.askConfirm("This will remove your strategy. Are you sure?");
		if (confirm) {
			//mandare la server la cancellazione della strategia
		}
		Console.newLine();
	}

	private void handleDownloadStrategy(MenuEntry entry)
	{
		//mandare al server la richiesta di scaricamento della strategia
	}
}
