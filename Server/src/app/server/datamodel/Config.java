package app.server.datamodel;

import java.time.Instant;

import app.server.datamodel.mongo.NestedDataObject;

public class Config extends NestedDataObject {
	protected Market market;
	protected boolean inverseCross;
	protected int granularity;
	protected Instant startTime;
	protected Instant endTime;
}
