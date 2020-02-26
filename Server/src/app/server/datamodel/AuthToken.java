package app.server.datamodel;

import java.time.Instant;

import app.server.datamodel.mongo.DataObject;

public class AuthToken extends DataObject {
	protected String id;
	protected String username;
	protected boolean isAdmin;
	protected Instant expireTime;
}
