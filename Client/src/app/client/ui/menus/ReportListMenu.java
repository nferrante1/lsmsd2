package app.client.ui.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.BaseReportInfo;
import app.common.net.entities.KVParameter;
import app.common.net.entities.ReportInfo;
import app.common.net.entities.StrategyInfo;

public class ReportListMenu extends PagedMenu
{
	protected StrategyInfo strategy;
	protected String marketId;

	public ReportListMenu(StrategyInfo strategy, String marketId)
	{
		super(strategy.getName() + " | Select a report");
		this.strategy = strategy;
		this.marketId = marketId;
	}

	@Override
	protected List<MenuEntry> getEntries()
	{
		ResponseMessage resMsg = Protocol.getInstance().browseReports(getPage(), getPerPage(), strategy.getName(), marketId);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return null;
		}

		List<BaseReportInfo> reports = resMsg.getEntities(BaseReportInfo.class);

		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		int i = 1;
		for (BaseReportInfo report : reports) {
			menu.add(new MenuEntry(i, "on " + report.getMarket() + " (profit: " + df.format(report.getNetProfit() * 100) + "%)", this::handleSelectReport, report));
			i++;
		}
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
		new ReportMenu(info, parameters).show();

	}
}
