package app.client.ui.menus;

import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.BaseReportInfo;
import app.common.net.entities.Entity;
import app.common.net.entities.KVParameter;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.StrategyInfo;

public class ReportListMenu extends Menu
{
	protected StrategyInfo strategy;
	protected String marketId;
	protected int currentPage;

	public ReportListMenu(StrategyInfo strategy, String marketId)
	{
		super(strategy.getName() + " | Select a report");
		this.strategy = strategy;
		this.marketId = marketId;
		this.currentPage = 1;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseReports(currentPage, strategy.getName(), marketId);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<BaseReportInfo> reports = resMsg.getEntities(BaseReportInfo.class);

		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for(BaseReportInfo report: reports)
			menu.add(new MenuEntry(i, "on " + report.getMarket() + " (profit: " + report.getNetProfit() + ")", this::handleSelectReport, report));
		menu.add(new MenuEntry(1, "Load a new page", this::handleLoadNewPage));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleSelectReport(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().viewReport(((BaseReportInfo)entry.getHandlerData()).getId());
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}

		ReportInfo info = resMsg.getEntity(ReportInfo.class);
		List<KVParameter> parameters = resMsg.getEntities(KVParameter.class);
		new ReportMenu(info, parameters);

	}

	private void handleLoadNewPage(MenuEntry entry)
	{
		currentPage++;
	}
}
