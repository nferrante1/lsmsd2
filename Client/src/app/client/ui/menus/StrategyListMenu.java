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
		ResponseMessage resMsg;
		resMsg = protocol.getStrategies();

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			Console.newLine();
		} else if (resMsg.getEntityCount() < 1) {
			Console.println("No strategies available.");
			Console.newLine();
		} else {
			for (Entity entity: resMsg.getEntities()) {
				Strategy strategy = (Strategy)entity;
				menu.add(new MenuEntry(i, strategy.getName(), true, this::handleStrategySelection, strategy));
				i++;
			}
		}
		menu.add(new MenuEntry(1, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleStrategySelection(MenuEntry entry)
	{

	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		
	}
}
