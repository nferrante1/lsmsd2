package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.Entity;
import app.common.net.entities.StrategyInfo;

public class StrategyListMenu extends Menu
{
	protected String filter;
	protected int currentPage;

	public StrategyListMenu(String filter)
	{
		super("Select a strategy");
		this.filter = filter;
		this.currentPage = 1;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseStrategies(currentPage, filter);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<StrategyInfo> strategies = new ArrayList<StrategyInfo>();
		for(Entity entity: resMsg.getEntities())
			strategies.add((StrategyInfo)entity);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for(StrategyInfo strategy: strategies) {
			menu.add(new MenuEntry(i, strategy.getName() + " (by: " + strategy.getUsername() + ")", true, this::handleStrategySelection, strategy));
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
	}
}
