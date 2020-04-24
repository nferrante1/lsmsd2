package app.client.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import app.client.net.Protocol;
import app.client.ui.Console;
import app.common.net.ResponseMessage;
import app.common.net.entities.ReportInfo;

public class ReportMenu extends Menu
{
	protected ReportInfo report;
	protected double amount;

	public ReportMenu(ReportInfo report, double amount)
	{
		super("Report of: " + report.getMarketname());
		this.report = report;
		this.amount = amount;

		Console.println("Data Range: " + report.getStart() + " - " + report.getEnd() );
		if(report.isCanDelete()) {
			Console.println("Author: You");
		}
		else {
			Console.println("Author: " + report.getAuthor());
		}
		Console.println("Net Profit: " + report.getNetProfit()*amount);
		Console.println("Gross Profit: " + report.getGrossProfit()*amount);
		Console.println("Gross Loss: "  + report.getGrossLoss()*amount);
		Console.println("Hodl Profit: " + report.getHodlProfit()*amount);
		Console.println("Total Trades: " + report.getTotalTrades());
		Console.println("Open Trades: " + report.getOpenTrades());
		Console.println("Winning Trades: " + report.getWinningTrades());
		Console.println("Max Consecutive Loosing: " + report.getMaxConsecutiveLosing());
		Console.println(" Average Amount: " + report.getAvgAmount()*amount);
		Console.println("Average Duration: " + report.getAvgDuration());
		Console.println("Max Drawdown: " + report.getMaxDrawdown()*amount);
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		
		if(report.isCanDelete()) {
			menu.add(new MenuEntry(1, "Delete report", true, this::handleDeleteReport));
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}

	private void handleDeleteReport(MenuEntry entry)
	{
		ResponseMessage resMsg = Protocol.getInstance().deleteReport(report);
		if(!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
		}
		else {
			Console.println("Report correctly deleted!");
		}
	}
}
