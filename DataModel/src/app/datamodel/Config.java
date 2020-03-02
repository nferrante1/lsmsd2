package app.datamodel;

import java.time.Instant;

import app.datamodel.mongo.NestedDataObject;

public class Config extends NestedDataObject {
	protected Market market;
	protected boolean inverseCross;
	protected int granularity;
	protected Instant startTime;
	protected Instant endTime;
}
