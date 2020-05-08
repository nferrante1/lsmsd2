package app.library.indicators;

import java.util.List;

import org.bson.conversions.Bson;

public interface ComputableIndicator {
	public String getName();
	public List<Bson> getPipeline();
}
