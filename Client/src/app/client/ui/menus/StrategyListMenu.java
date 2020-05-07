package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.StrategyInfo;

public class StrategyListMenu extends PagedMenu
{
	protected String filter;

	public StrategyListMenu(String filter)
	{
		super("Select a strategy");
		this.filter = filter;
	}

	@Override
	protected List<MenuEntry> getEntries()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseStrategies(getPage(), getPerPage(), filter);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<StrategyInfo> strategies = resMsg.getEntities(StrategyInfo.class);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for (StrategyInfo strategy : strategies) {
			menu.add(new MenuEntry(i, strategy.getName() + " (by: " + strategy.getAuthor() + ")", true, this::handleStrategySelection, strategy));
			++i;
		}
		return menu;
	}

	private void handleStrategySelection(MenuEntry entry)
	{
		new StrategyMenu((StrategyInfo)entry.getHandlerData()).show();
	}
}
