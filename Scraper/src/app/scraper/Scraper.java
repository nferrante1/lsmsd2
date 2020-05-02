package app.scraper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.datamodel.DataSourceManager;
import app.datamodel.StorablePojoCursor;
import app.datamodel.StorablePojoManager;
import app.datamodel.mongo.DBManager;
import app.datamodel.pojos.DataSource;
import app.scraper.net.BinanceConnector;
import app.scraper.net.CoinbaseConnector;
import app.scraper.net.SourceConnector;

public final class Scraper
{
	
	private static Map<String, Class<? extends SourceConnector>> sourceConnectorMap = Map.ofEntries(
			Map.entry("COINBASE", CoinbaseConnector.class),
			Map.entry("BINANCE", BinanceConnector.class)
		);
	private static List<Worker> workers = new ArrayList<Worker>();
	private static int stopCount = 1;

	public static void main(String[] args)
	{
		setupDBManager();

		start();
		listenForSync();
		System.err.println("Unknown error. Stopping...");
		stop();
	}

	private static void start() {
		stopCount--;
		if(stopCount >= 1)
			return;
		System.out.println("Starting Workers...");
		createWorkers();
		for (Worker worker: workers)
			worker.start();
	}
	private static void stop() 
	{
		stopCount++;
		if(stopCount > 1)
			return;
		System.out.println("Stopping workers...");
		for (Worker worker: workers)
			worker.interrupt();
		for (Worker worker: workers)
			while (worker.isAlive())
				try {
					worker.join();
				} catch (InterruptedException e) {
				}
		workers.clear();
		System.out.println("All threads stopped.");		
	}
	
	private static void listenForSync()
	{
		ServerSocket listeningSocket = null;
		try {
			listeningSocket = new ServerSocket(5656);
			while (true) {
				Socket socket = listeningSocket.accept();
				InputStream is = socket.getInputStream();
				DataInputStream dis = new DataInputStream(is);

				String msg = dis.readUTF();
				if(msg.equals("START")) {
					start();
				}
				else if (msg.equals("STOP")) {
					stop();
				}

				

				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.writeUTF("ACK");
				dos.flush();

				dos.close();
				dis.close();
				socket.close();
				
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			if (listeningSocket != null);
				try {
					listeningSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static void setupDBManager()
	{
	}

	private static void createWorkers()
	{
		DataSourceManager manager = new DataSourceManager();
		StorablePojoCursor<DataSource> cursor = (StorablePojoCursor<DataSource>)manager.find();
		List<DataSource> sources = cursor.toList();
		cursor.close();

		for (Map.Entry<String, Class<? extends SourceConnector>> sourceConnector: sourceConnectorMap.entrySet())
		{
			DataSource source = null;
			for (DataSource curSource: sources)
				if (curSource.getName().equals(sourceConnector.getKey()))
					source = curSource;
			if (source == null)
				source = new DataSource(sourceConnector.getKey());
			SourceConnector connector;
			try {
				connector = sourceConnector.getValue().getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
			workers.add(new Worker(source, connector));
		}
	}

}
