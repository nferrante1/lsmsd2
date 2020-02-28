package app.client.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.ui.Console;

public class ReportMenu extends Menu
{
	protected Report report;

	public ReportMenu(Report report)
	{
		super(report.getName() + " | select an action");
		this.report = report;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		
		menu.add(new MenuEntry(1, "Delete report", true, this::handleDeleteReport));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleDeleteReport(MenuEntry entry)
	{
		boolean confirm = Console.askConfirm("This will remove your report. Are you sure?");
		if (confirm) {
			//mandare la server la cancellazione del report
		}
		Console.newLine();
	}
}
