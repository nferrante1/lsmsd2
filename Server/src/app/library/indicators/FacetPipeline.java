package app.library.indicators;

import java.util.List;

import org.bson.conversions.Bson;

public interface FacetPipeline
{
	public String name();
	public List<Bson> pipeline();
}
