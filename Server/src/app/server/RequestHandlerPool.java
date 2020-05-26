package app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class RequestHandlerPool implements Runnable
{
	private final ServerSocket serverSocket;
	private final ExecutorService pool;

	RequestHandlerPool(int port) throws IOException
	{
		pool = Executors.newCachedThreadPool();
		serverSocket = new ServerSocket(port);
		Logger.getLogger(RequestHandlerPool.class.getName()).info("Server listening on port " + port + ".");
	}

	@Override
	public void run()
	{
		try {
			while (!pool.isShutdown())
				pool.execute(new RequestHandler(serverSocket.accept()));
		} catch (IOException ex) {
			Logger.getLogger(RequestHandlerPool.class.getName()).log(Level.SEVERE, null, ex);
			shutdown();
		}
	}

	void shutdown()
	{
		Logger.getLogger(RequestHandlerPool.class.getName()).info("Shutting down...");
		pool.shutdown();
		try {
			pool.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			Logger.getLogger(RequestHandlerPool.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			pool.shutdownNow();
		}
	}
}
