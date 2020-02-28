package app.client.ui.menus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;
import app.client.ui.menus.MenuEntry;

public class StrategyListMenu extends Menu
{

	public StrategyListMenu()
	{
		super("All the available strategies");
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		//richiedere una pagina di strategie al server
		//gestire casi di errore o se non ci sono strategie

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		//per ogni strategia i trovata ...
		menu.add(new MenuEntry(i, strategy.getName(), true, this::handleStrategySelection, strategy));
				
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleStrategySelection(MenuEntry entry)
	{
		//richiedere informazioni strategia al server
		//stampare informazioni strategia Console.print(trategy.toString);
		
		currentPage = 0;
		//new StrategyMenu(entry.getHandlerData()).show();
		new StrategyMenu(strategy).show();
	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		
	}
}
