package app.client.ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.MenuEntry;
import app.common.net.ResponseMessage;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.MarketInfo;
import app.common.net.entities.StrategyInfo;
import app.common.net.entities.UserInfo;

public class StrategyListMenu extends Menu
{

	protected List<StrategyInfo> strategies = new ArrayList<StrategyInfo>();
	protected String filter;
	protected int currentPage;
	
	public StrategyListMenu(String filter)
	{
		super("All the available strategies");
		this.filter = filter;
		this.currentPage = 1;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseStrategy(new BrowseInfo(filter, currentPage));
		
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}
		strategies.clear();
		for(Entity entity: resMsg.getEntities()) {
			strategies.add((StrategyInfo)entity);
		}

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		for(StrategyInfo strategy : strategies) {
			menu.add(new MenuEntry(i, strategy.getName(), true, this::handleStrategySelection, strategy));
			++i;
		}		
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleStrategySelection(MenuEntry entry)
	{
		new StrategyMenu((StrategyInfo)entry.getHandlerData()).show();
	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		getMenu();
		
	}
}
