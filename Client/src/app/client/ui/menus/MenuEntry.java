package app.client.ui.menus;

import java.util.function.Consumer;

public class MenuEntry implements Comparable<MenuEntry>
{
	protected int key;
	protected String text;
	protected boolean exit;
	protected Object handlerData;

	protected Consumer<MenuEntry> handler;

	public MenuEntry(int key, String text, boolean exit, Consumer<MenuEntry> handler, Object handlerData)
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

	public MenuEntry(int key, String text, boolean exit, Consumer<MenuEntry> handler)
	{
		this(key, text, exit, handler, null);
	}

	public MenuEntry(int key, String text, Consumer<MenuEntry> handler, Object handlerData)
	{
		this(key, text, false, handler, handlerData);
	}

	public MenuEntry(int key, String text, Consumer<MenuEntry> handler)
	{
		this(key, text, false, handler);
	}

	public MenuEntry(int key, String text, boolean exit)
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

	public void setHandler(Consumer<MenuEntry> handler)
	{
		this.handler = handler;
	}

	public void triggerHandler()
	{
		if (this.handler != null)
			this.handler.accept(this);
	}

	public Object getHandlerData()
	{
		return handlerData;
	}

	@Override
	public int compareTo(MenuEntry me)
	{
		if (key == 0)
			return Integer.MAX_VALUE;
		if (me.key == 0)
			return Integer.MIN_VALUE;
		int diff = key - me.key;
		if (diff == 0 && (exit ^ me.exit))
			return exit ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		return diff;
	}
}
