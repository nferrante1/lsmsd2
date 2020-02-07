package app.scraper.data;

import java.util.List;

import com.google.gson.annotations.Expose;

public class BinanceExchangeInfo
{
	@Expose
	public List<Market> symbols;
}
