package app.server;



import app.datamodel.UsersManager;
import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.User;

public class Server {

	public static void main(String[] args)
	{
		setupDBManager();
		
		User u = new User("username", "password");
		UsersManager manager = new UsersManager();
		manager.insert(u);
		
		manager.find(u.getName());
		//manager.delete(u);
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
