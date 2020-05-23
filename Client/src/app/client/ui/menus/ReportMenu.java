package app.client.ui.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
		super("Report of " + report.getStrategyName() + " on " + report.getMarket() + " | Select an action");
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
		HashMap<String, String> response = new AmountForm().show();
		double amount = Double.parseDouble(response.get("Amount"));
		showReport(report, parameters, amount);
	}
	public static void showReport(ReportInfo report, List<KVParameter> parameters, double amount)
	{
		DecimalFormat df = new DecimalFormat("#,##0.00");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		df.setGroupingSize(3);
		df.setPositivePrefix("+");
		DecimalFormat pf = new DecimalFormat("0.##%");
		pf.setRoundingMode(RoundingMode.HALF_EVEN);
		pf.setPositivePrefix("+");
		DecimalFormat of = new DecimalFormat("#,##0.00");
		of.setRoundingMode(RoundingMode.HALF_EVEN);
		of.setGroupingSize(3);

		Console.println("Id: " + report.getId());
		Console.println("Strategy: " + report.getStrategyName());
		Console.println("Market: " + report.getMarket());
		Console.println("Executed By: " + report.getUser());
		Console.println("Initial Amount: " + of.format(amount));
		Console.println("Parameters:");
		for (KVParameter parameter: parameters)
			Console.println("\t" + parameter.getName() + ": " + parameter.getValue());
		Console.println("Final Amount: " + of.format(amount + report.getNetProfit() * amount));
		Console.println("Net Profit: " + df.format(report.getNetProfit() * amount) + " (" + pf.format(report.getNetProfit()) + ")");
		Console.println("Gross Profit: " + df.format(report.getGrossProfit() * amount) + " (" + pf.format(report.getGrossProfit()) + ")");
		Console.println("Gross Loss: " + df.format(report.getGrossLoss() * amount) + " (" + pf.format(report.getGrossLoss()) + ")");
		Console.println("Hodl Profit: " + df.format(report.getHodlProfit() * amount) + " (" + pf.format(report.getHodlProfit()) + "; strategy relative performance: " + pf.format(report.getNetProfit() - report.getHodlProfit()) + ")");
		Console.println("Total Trades: " + report.getTotalTrades());
		Console.println("Total Completed Trades: " + report.getClosedTrades() + (report.getTotalTrades() > 0 ? "(" + pf.format(report.getClosedTrades() / report.getTotalTrades()) + ")" : ""));
		Console.println("Open Trades At End: " + report.getOpenTrades() + (report.getTotalTrades() > 0 ? "(" + pf.format(report.getOpenTrades() / report.getTotalTrades()) + ")" : ""));
		Console.println("Winning Trades: " + report.getWinningTrades() + (report.getClosedTrades() > 0 ? "(" + pf.format(report.getWinningTrades() / report.getClosedTrades()) + " of completed trades)" : ""));
		Console.println("Losing Trades: " + report.getLosingTrades() + (report.getClosedTrades() > 0 ? "(" + pf.format(report.getLosingTrades() / report.getClosedTrades()) + " of completed trades)" : ""));
		Console.println("Max Consecutive Losing Trades: " + report.getMaxConsecutiveLosing());
		Console.println("Average Traded Amount: " + of.format(report.getAvgAmount() * amount));
		Console.println("Average Trade Duration: " + of.format(report.getAvgDuration()) + " trading days");
		Console.println("Max Drawdown: " + df.format(report.getMaxDrawdown() * amount) + " (" + pf.format(report.getMaxDrawdown()) + ")");
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
