package app.server.datamodel;

import app.server.datamodel.mongo.NestedDataObject;

public class Report extends NestedDataObject {
	protected double netProfit;
	protected double grossProfit;
	protected double grossLoss;
	protected double hodlProfit;
	protected int totalTrades;
	protected int openTrades;
	protected int winningTrades;
	protected int maxConsecutiveLosing;
	protected double avgMount;
	protected double avgDuration;
	protected double avgDrawdown;
}
