package app.client.ui.menus;

import java.util.SortedSet;

import app.client.net.Protocol;
import app.client.ui.Console;

public abstract class Menu
{
	protected String prompt;
	protected abstract SortedSet<MenuEntry> getMenu();
	protected static int currentPage = 0;
	protected static Protocol protocol = Protocol.getInstance();

	protected MenuEntry printMenu()
	{
		Console.newLine();
		MenuEntry selection = Console.printMenu(prompt, getMenu());
		selection.triggerHandler();
		return selection;
	}

	protected Menu()
	{
		this("Select an action");
	}

	protected Menu(String prompt)
	{
		this.prompt = prompt;
	}

	public void show()
	{
		while (!printMenu().isExit());
	}

	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}
}
