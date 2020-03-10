	package app.client.ui.menus;

	import java.time.LocalDate;
	import java.util.HashMap;
	import java.util.SortedSet;
	import java.util.TreeSet;
	import java.util.function.Function;

	import app.client.ui.Console;
	import app.client.ui.menus.MenuEntry;
import app.datamodel.DataSource;

public class MarketListMenu extends Menu {
	
	protected DataSource data_source;
	
	public MarketListMenu(DataSource data_source)
	{
		super("All the markets of this data source");
		this.data_source = data_source;
	}
	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		//richiedere una pagina di market al server
		//gestire casi di errore o se non ci sono market

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i=1;
		//per ogni market i trovato ...

		menu.add(new MenuEntry(i, market.getName() + " " + market.getGranularity() + " " + market.getDataSync() + " "
			+ market.getSelectable(), this::handleConfigMarket, market));
				
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		
		menu.add(new MenuEntry(0, "Go back", true));
		
		return menu;
	}
	
	private void handleConfigMarket(MenuEntry entry)
	{
		//richiedere informazioni market al server
		//stampare informazioni market Console.print(strategy.toString);
		
		currentPage = 0;
		new ConfigMarketForm(entry.getHandlerData()).show();
	}
	
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		
	}
}
