package app.scraper.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import app.scraper.data.mongo.DBManager;

public class DataSource
{
	@SerializedName(value = "_id")
	protected String name;
	protected boolean enabled;
	protected List<Market> markets;
	
	@SuppressWarnings("unused")
	private DataSource()
	{
	}
	
	public DataSource(String name)
	{
		this.name = name;
		markets = new ArrayList<Market>();
	}
	
	public static DataSource[] loadSources()
	{
		Gson gson = new Gson();
		List<String> jsonDocuments = DBManager.getInstance().find("sources");
		List<DataSource> sources = new ArrayList<DataSource>();
		for (String jsonDocument: jsonDocuments)
			sources.add(gson.fromJson(jsonDocument, DataSource.class));
		return sources.toArray(new DataSource[0]);
	}
	
	public String getName()
	{
		return name;
	}
}
