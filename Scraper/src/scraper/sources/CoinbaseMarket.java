package scraper.sources;

public class CoinbaseMarket {
	
	public String id;
	public String base_currency;
	public String quote_currency;
	public String base_min_size;
	public String base_max_size;
	public String quote_increment;
	
	
	public Market toMarket() {
		
		return new Market(base_currency + "-" +quote_currency);
		
	}

}
