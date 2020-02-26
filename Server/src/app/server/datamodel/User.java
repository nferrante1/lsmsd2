package app.server.datamodel;

import java.util.List;

import app.server.datamodel.mongo.DataObject;

public class User extends DataObject {
	protected String username;
	protected String passwordHash;
	protected boolean isAdmin;
	protected List<AuthToken> tokens;
}
