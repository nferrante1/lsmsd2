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

	public static void main(String[] args)
	{
		setupDBManager();

		createWorkers();
		for (Worker worker: workers)
			worker.start();
		listenForSync();
		System.err.println("Unknown error. Stopping...");
		for (Worker worker: workers)
			if (worker.isAlive())
				worker.interrupt();
		for (Worker worker: workers)
			try {
				worker.join();
			} catch (InterruptedException e) {
				System.exit(1);
			}
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
				if (!msg.equals("STOP")) {
					System.err.println("Received invalid SYNC command from server: " + msg + ".");
					dis.close();
					socket.close();
					continue;
				}

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

				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				dos.writeUTF("ACK");
				dos.flush();

				msg = dis.readUTF();
				if (!msg.equals("START")) {
					System.err.println("Received invalid SYNC command from server: " + msg + ".");
					dos.close();
					dis.close();
					socket.close();
					throw new RuntimeException("Invalid message from server.");
				}
				dos.close();
				dis.close();
				socket.close();
				System.out.println("Restarting...");
				createWorkers();
				for (Worker worker: workers)
					worker.start();
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
		DBManager.setHostname("127.0.0.1");
		DBManager.setPort(27017);
		DBManager.setUsername("root");
		DBManager.setPassword("rootpass");
		DBManager.setDatabase("mydb");
	}

	private static void createWorkers()
	{
		StorablePojoManager<DataSource> manager = new StorablePojoManager<DataSource>(DataSource.class);
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
