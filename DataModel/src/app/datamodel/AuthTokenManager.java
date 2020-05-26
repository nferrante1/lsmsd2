package app.datamodel;

import com.mongodb.ReadConcern;
import com.mongodb.client.MongoCollection;

import app.datamodel.pojos.AuthToken;

public final class AuthTokenManager extends StorablePojoManager<AuthToken>
{
	public AuthTokenManager()
	{
		super(AuthToken.class);
	}

	@Override
	protected MongoCollection<AuthToken> getCollection()
	{
		return getCollection(ReadConcern.MAJORITY);
	}
}
