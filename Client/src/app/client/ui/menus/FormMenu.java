package app.client.ui.menus;

import java.util.SortedSet;

import app.client.net.Protocol;
import app.client.ui.Console;

public abstract class FormMenu<T>
{
	protected String prompt;
	protected abstract SortedSet<MenuEntry> getMenu();
	protected static int currentPage = 0;
	protected static Protocol protocol = Protocol.getInstance();
	protected static User loggedUser;

	protected T printFormMenu()
	{
		Console.newLine();
		FormMenuEntry selection = (FormMenuEntry)Console.printMenu(prompt, getMenu());
		T t = (T)selection.triggerFormHandler();
		return t;
	}

	protected FormMenu()
	{
		this("Select an action");
	}

	protected FormMenu(String prompt)
	{
		this.prompt = prompt;
	}

	public void show()
	{
		printFormMenu();
	}
}
