package app.datamodel;

import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import app.datamodel.mongo.NestedDataObject;

public class Report extends NestedDataObject {
	protected double netProfit;
	protected double grossProfit;
	protected double grossLoss;
	protected double hodlProfit;
	protected int totalTrades;
	protected int openTrades;
	protected int winningTrades;
	protected int maxConsecutiveLosing;
	protected double avgAmount;
	protected double avgDuration;
	protected double maxDrawdown;
	
	
	public void setNetProfit(double netProfit)
	{
		updateField("netProfit", netProfit);
	}
	public void setGrossProfit(double grossProfit)
	{
		updateField("grossProfit", grossProfit);
		
	}
	public void setGrossLoss(double grossLoss)
	{
		updateField("grossLoss", grossLoss);
	}
	public void setHodlProfit(double hodlProfit)
	{
		updateField("hodlProfit", hodlProfit);
		
	}
	public void setTotalTrades(int totalTrades)
	{
		updateField("totalTrades", totalTrades);
	
	}
	public void setOpenTrades(int openTrades)
	{
		updateField("openTrades", openTrades);
	}
	public void setWinningTrades(int winningTrades)
	{
		updateField("winningTrades", winningTrades);
		
	}
	public void setMaxConsecutiveLosing(int maxConsecutiveLosing)
	{
		updateField("maxConsecutiveLosing", maxConsecutiveLosing);
		
	}
	public void setAvgAmount(double avgAmount)
	{
		updateField("avgAmount", avgAmount);
		
	}
	public void setAvgDuration(double avgDuration)
	{
		updateField("avgDuration", avgDuration);
		
	}
	public void setMaxDrawdown(double maxDrawdown)
	{
		updateField("maxDrawdown", maxDrawdown);
	}
	public double getNetProfit()
	{
		return netProfit;
	}
	public double getGrossProfit()
	{
		return grossProfit;
	}
	public double getGrossLoss()
	{
		return grossLoss;
	}
	public double getHodlProfit()
	{
		return hodlProfit;
	}
	public int getTotalTrades()
	{
		return totalTrades;
	}
	public int getOpenTrades()
	{
		return openTrades;
	}
	public int getWinningTrades()
	{
		return winningTrades;
	}
	public int getMaxConsecutiveLosing()
	{
		return maxConsecutiveLosing;
	}
	public double getAvgAmount()
	{
		return avgAmount;
	}
	public double getAvgDuration()
	{
		return avgDuration;
	}
	public double getMaxDrawdown()
	{
		return maxDrawdown;
	}
	
	public static Report load(ObjectId id) 
	{
		List<Report> reports = load(Report.class, Filters.eq("id", id));
		if(reports.isEmpty()) 
			return null;
		Report report =  reports.get(0);
		return report;
	}
}
