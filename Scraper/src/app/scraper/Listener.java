package app.scraper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Listener extends Thread {
	private static Listener instance;
	private static AtomicBoolean running;
	private static List<Worker> workers = new ArrayList<Worker>();
	private static int portNumber;
	private static String scraperAddress;
	private static List<String> allowedAddresses = new ArrayList<String>();
	
	private Listener() 
	{
		
	}
	
	public static List<String> getAllowedAddresses()
	{
		return allowedAddresses;
	}

	public static void setAllowedAddresses(List<String> allowedAddresses)
	{
		allowedAddresses.addAll(allowedAddresses);
	}
	
	public static void addAllowedAddress(String allowedAddress) 
	{
		allowedAddresses.add(allowedAddress);
	}

	public int getPortNumber()
	{
		return portNumber;
	}

	public static Listener getListener() 
	{
		if(instance == null)
			instance = new Listener();
			setRunning(true);
		return instance;
	}
	
	public static void setPortNumber(int port)
	{
		portNumber = port;
	} 
		
	public String getScraperAddress()
	{
		return scraperAddress;
	}

	public static void setScraperAddress(String scraper)
	{
		scraperAddress = scraper;
	}

	public static void addWorkers(List<Worker> workers) 
	{
		workers.addAll(workers);
	}
	
	public static boolean getRunning()
	{
		return running.get();
	}

	public static void setRunning(boolean run)
	{
		running.set(run);
	}
	
	@Override
	public void run() {
		
		try (ServerSocket serverSocket = new ServerSocket(portNumber, 10, InetAddress.getByName("127.0.0.1"))) {
			while (getRunning()) {
				Socket newSocket = serverSocket.accept();
				if(allowedAddresses.contains(newSocket.getRemoteSocketAddress().toString())) {
					for (Worker worker : workers) {
						if (!worker.isInterrupted() && worker.isAlive())
							worker.interrupt();
					}
					setRunning(false);
				}
				yield();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
