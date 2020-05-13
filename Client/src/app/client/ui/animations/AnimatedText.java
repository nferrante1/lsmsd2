package app.client.ui.animations;

import app.client.ui.Console;

public abstract class AnimatedText extends Thread
{
	protected String text;
	protected boolean running;
	private Object lock;

	public AnimatedText(String text)
	{
		this.text = text;
		lock = new Object();
	}

	@Override
	public void run()
	{
		synchronized(lock) {
			running = true;
		}
		while (isRunning())
			animate();
	}

	public boolean isRunning()
	{
		synchronized(lock) {
			return running;
		}
	}

	protected abstract void animate();

	public void stopShowing()
	{
		if (!isAlive())
			return;
		synchronized(lock) {
			running = false;
		}
		try {
			this.join();
		} catch (InterruptedException e) {
		}
		Console.newLine();
	}
}
