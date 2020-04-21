	package app.client.ui.menus;

	import java.time.LocalDate;
	import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
	import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
	import app.client.ui.menus.MenuEntry;
import app.common.net.ResponseMessage;
import app.common.net.entities.BrowseInfo;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.StrategyInfo;

public class ReportListMenu extends Menu {
	
	protected StrategyInfo strategy;
	protected List<ReportInfo> reports;
	protected String filter;
	protected int currentPage;
	
	public ReportListMenu(StrategyInfo strategy, String filter)
	{
		super("Selected reports of this strategy");
		this.strategy = strategy;
		this.filter = filter;
		this.currentPage = 1;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		
		ResponseMessage resMsg = Protocol.getInstance().browseReports(new BrowseInfo(filter, currentPage));
		
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}
		
		for(int i=0; i<resMsg.getEntityCount(); i++) {
			reports.add((ReportInfo)resMsg.getEntity(i));
		}

		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i=1;
		for(ReportInfo report : reports) {
			if(report.isCanDelete()) {
				menu.add(new MenuEntry(i, "Market Name: " + report.getMarketName() + ", " 
						+ "Time Range: " + report.getStart() + "-" + report.getEnd() + ", "
						+ "you are the author!", this::handleReportSelection, report));
			}
			else {
				menu.add(new MenuEntry(i, "Market Name: " + report.getMarketName() + ", " 
						+ "Time Range: " + report.getStart() + "-" + report.getEnd() + ", "
						+ report.getAuthor(), this::handleReportSelection, report));
			}
		}
				
		menu.add(new MenuEntry(1, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleReportSelection(MenuEntry entry)
	{

	}
	
	private void handleLoadNewPage(MenuEntry entry) 
	{
		currentPage++;
		getMenu();
		
	}
}
