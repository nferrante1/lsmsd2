package app.client.ui.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.client.ui.menus.forms.AmountForm;
import app.common.net.ResponseMessage;
import app.common.net.entities.KVParameter;
import app.common.net.entities.ReportInfo;

public class ReportMenu extends Menu
{
	protected ReportInfo report;
	protected List<KVParameter> parameters;

	public ReportMenu(ReportInfo report, List<KVParameter> parameters)
	{
		super(report.getStrategyName() + " on " + report.getMarket() + " | Select an action");
		this.report = report;
		this.parameters = parameters;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> menu = new ArrayList<MenuEntry>();
		menu.add(new MenuEntry(1, "View report", this::handleViewReport));
		if (report.isDeletable())
			menu.add(new MenuEntry(2, "Delete report", true, this::handleDeleteReport));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleViewReport(MenuEntry entry)
	{
		double amount = Double.parseDouble(new AmountForm().show().get("Amount"));
		showReport(report, parameters, amount);
	}
	public static void showReport(ReportInfo report, List<KVParameter> parameters, double amount)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_EVEN);

		Console.println("Id: " + report.getId());
		Console.println("Strategy: " + report.getStrategyName());
		Console.println("Market: " + report.getMarket());
		Console.println("Initial Amount: " + df.format(amount));
		Console.println("Parameters:");
		for (KVParameter parameter: parameters)
			Console.println("\t" + parameter.getName() + ": " + parameter.getValue());
		Console.println("Author: " + report.getUser());
		Console.println("Net Profit: " + df.format(report.getNetProfit() * amount));
		Console.println("Gross Profit: " + df.format(report.getGrossProfit() * amount));
		Console.println("Gross Loss: " + df.format(report.getGrossLoss() * amount));
		double relativePerf = (report.getNetProfit() / report.getHodlProfit()) * 100;
		Console.println("Hodl Profit: " + df.format(report.getHodlProfit() * amount) + " (strategy's relative performance: " + (relativePerf < 0.0 ? "+" : "") + df.format(relativePerf) + "%)");
		Console.println("Total Trades: " + report.getTotalTrades());
		Console.println("Open Trades: " + report.getOpenTrades());
		Console.println("Winning Trades: " + report.getWinningTrades());
		Console.println("Max Consecutive Losing Trades: " + report.getMaxConsecutiveLosing());
		Console.println("Average Amount: " + df.format(report.getAvgAmount() * amount));
		Console.println("Average Duration: " + report.getAvgDuration());
		Console.println("Max Drawdown: " + df.format(report.getMaxDrawdown() * amount));
		Console.pause();
	}

	private void handleDeleteReport(MenuEntry entry)
	{
		if (!Console.askConfirm()) {
			Console.println("Aborting...");
			return;
		}
		ResponseMessage resMsg = Protocol.getInstance().deleteReport(report.getId());
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Report successfully deleted!");
	}
}
