package app.client.ui.menus;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;
import app.common.net.entities.SourceInfo;

public class DataSourceListMenu extends Menu {
	List<SourceInfo> sources;

	DataSourceListMenu(List<SourceInfo> sources)
	{
		super("This is the list of all available Data Sources");
		this.sources = sources;
	}
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		for(SourceInfo source : sources) {
			menu.add(new MenuEntry(i, source.get_id(), this::handleDataSourceSelection, source));
			++i;
		}
		
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	void handleDataSourceSelection(MenuEntry entry)
	{
		SourceInfo info = (SourceInfo)entry.getHandlerData();
		Console.println("Selected Data Source is: " + info.get_id());
		Console.println("Enabled: " + info.isEnabled());
		
	}

}
