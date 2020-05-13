package app.client.ui.animations;

import app.client.ui.Console;

public class Spinner extends AnimatedText
{
	private int i = 0;

	public Spinner(String text)
	{
		super(text);
	}

	@Override
	public void run()
	{
		super.run();
		if (!isRunning())
			Console.print("\r" + text + "done!");
	}

	@Override
	protected void animate()
	{
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		String anim = "|/-\\";
		Console.print("\r" + anim.charAt(i) + " " + text);
		i = (i + 1) % anim.length();
	}
}
