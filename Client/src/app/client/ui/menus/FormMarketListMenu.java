	package app.client.ui.menus;

	import java.time.LocalDate;
	import java.util.HashMap;
	import java.util.SortedSet;
	import java.util.TreeSet;
	import java.util.function.Function;

	import app.client.ui.Console;
	import app.client.ui.menus.MenuEntry;

public class FormMarketListMenu extends FormMenu<Market> {
	
	
	public FormMarketListMenu()
	{
		super("List of the available markets");
	}
	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		//richiedere una pagina di market al server
		//gestire casi di errore o se non ci sono market

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i=1;
		//per ogni market i trovato ...

		menu.add(new FormMenuEntry<MenuEntry, Market>(i, market.getName() + " " + market.getGranularity(), this::handleSelectMarket, market));
				
		menu.add(new MenuEntry(i, "Load a new page", this::handleLoadNewPage));
		
		return menu;
	}
	
	private Market handleSelectMarket(MenuEntry entry)
	{
		currentPage = 0;
		return (Market) entry.getHandlerData();
	}
	
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		
	}
}
