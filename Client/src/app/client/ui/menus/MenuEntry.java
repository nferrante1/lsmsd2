package app.client.ui.menus;

import java.util.function.Consumer;

public class MenuEntry
{
	private final int key;
	private final String text;
	private final boolean exit;
	private final Object handlerData;
	private final Consumer<MenuEntry> handler;

	MenuEntry(int key, String text, boolean exit, Consumer<MenuEntry> handler, Object handlerData)
	{
		if (key < 0)
			this.key = 0;
		else
			this.key = key;
		this.text = text;
		this.exit = exit;
		this.handler = handler;
		this.handlerData = handlerData;
	}

	MenuEntry(int key, String text, boolean exit, Consumer<MenuEntry> handler)
	{
		this(key, text, exit, handler, null);
	}

	MenuEntry(int key, String text, Consumer<MenuEntry> handler, Object handlerData)
	{
		this(key, text, false, handler, handlerData);
	}

	MenuEntry(int key, String text, Consumer<MenuEntry> handler)
	{
		this(key, text, false, handler);
	}

	MenuEntry(int key, String text, boolean exit)
	{
		this(key, text, exit, null);
	}

	public int getKey()
	{
		return this.key;
	}

	public String getText()
	{
		return this.text;
	}

	public boolean isExit()
	{
		return this.exit;
	}

	protected void triggerHandler()
	{
		if (this.handler != null)
			this.handler.accept(this);
	}

	public Object getHandlerData()
	{
		return handlerData;
	}
}
