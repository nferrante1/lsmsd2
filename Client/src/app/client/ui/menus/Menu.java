package app.client.ui.menus;

import java.util.List;

import app.client.net.Protocol;
import app.client.ui.Console;

public abstract class Menu
{
	protected String prompt;
	protected abstract List<MenuEntry> getMenu();
	protected static Protocol protocol = Protocol.getInstance();

	protected MenuEntry printMenu()
	{
		Console.newLine();
		List<MenuEntry> menus = getMenu();
		if (menus == null || menus.isEmpty())
			return new MenuEntry(0, "dummy", true);
		MenuEntry selection = Console.printMenu(prompt, menus);
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
