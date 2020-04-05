package app.server;



import java.util.List;

import app.datamodel.AuthTokenManager;
import app.datamodel.PojoCursor;
import app.datamodel.UsersManager;
import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.AuthToken;
import app.datamodel.pojos.User;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();
		AuthTokenManager manager = new AuthTokenManager();
		AuthToken token = new AuthToken("user1", false);
		System.out.println(token.getId());
		
		
		
	}
	
	public static void setupDBManager()
	{
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		 DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}
	

}
