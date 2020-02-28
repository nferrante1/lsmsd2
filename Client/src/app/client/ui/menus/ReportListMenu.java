	package app.client.ui.menus;

	import java.time.LocalDate;
	import java.util.HashMap;
	import java.util.SortedSet;
	import java.util.TreeSet;

	import app.client.ui.Console;
	import app.client.ui.menus.MenuEntry;

public class ReportListMenu extends Menu {
	
	public ReportListMenu()
	{
		super("All the reports of this strategy");
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		//richiedere una pagina di report al server
		//gestire casi di errore o se non ci sono report

		SortedSet<MenuEntry> menu = new TreeSet<>();
		
		//per ogni report i trovato ...
		menu.add(new MenuEntry(i, report.getName(), true, this::handleReportSelection, report));
				
		menu.add(new MenuEntry(1, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleReportSelection(MenuEntry entry)
	{
		//richiedere informazioni report al server
		//stampare informazioni report Console.print(strategy.toString);
		
		currentPage = 0;
		new ReportMenu(entry.getHandlerData()).show();
	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		
	}
}
