package app.client.ui.menus;

import java.util.List;

public abstract class PagedMenu extends Menu
{
	private int page = 1;
	private int perPage = 20;

	public PagedMenu()
	{
		super();
	}

	public PagedMenu(String prompt)
	{
		super(prompt);
	}

	@Override
	public void show()
	{
		List<MenuEntry> menu = getMenu();
		int nextIndex = menu.size();
		if (nextIndex < perPage) {
			menu.add(new MenuEntry(nextIndex, "Next Page", this::nextPage));
			nextIndex++;
		}
		if (page > 1)
			menu.add(new MenuEntry(nextIndex, "Previous Page", this::nextPage));
		while (!printMenu(menu).isExit());
	}

	protected void nextPage(MenuEntry entry)
	{
		page++;
	}
	
	protected void previousPage(MenuEntry entry)
	{
		page--;
	}

}
