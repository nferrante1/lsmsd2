package app.client.ui.animations;

import app.client.ui.Console;

public class ProgressBar extends AnimatedText
{
	protected int progress;

	public ProgressBar(String text)
	{
		super(text);
	}

	public synchronized void setProgress(int progress)
	{
		this.progress = progress;
	}

	@Override
	public void run()
	{
		super.run();
		if (!isRunning()) {
			setProgress(100);
			animate();
		}
	}

	@Override
	protected void animate()
	{
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		StringBuilder bar = new StringBuilder(50);
		int i = 0;
		synchronized(this) {
			for (; i < progress/2; i++)
				bar.append('#');
			for(; i < 50; i++)
				bar.append('-');

			Console.printf("\r%s\t %-52s %-4s", text, "[" + bar + "]", progress + "%");
		}
	}
}
