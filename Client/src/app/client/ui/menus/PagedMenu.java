package app.client.ui.menus;

import java.util.List;

abstract class PagedMenu extends Menu
{
	private int page = 1;
	private int perPage = 20;

	PagedMenu()
	{
		super();
	}

	PagedMenu(int perPage)
	{
		this();
		this.perPage = perPage;
	}

	PagedMenu(String prompt)
	{
		super(prompt);
	}

	PagedMenu(String prompt, int perPage)
	{
		this(prompt);
		this.perPage = perPage;
	}

	protected abstract List<MenuEntry> getEntries();

	protected int getPerPage()
	{
		return perPage;
	}

	protected int getPage()
	{
		return page;
	}

	@Override
	protected List<MenuEntry> getMenu()
	{
		List<MenuEntry> entries = getEntries();
		if (entries == null)
			return null;
		int count = entries.size();
		int nextIndex = ((count / 10) * 10) + 10;
		MenuEntry previous = new MenuEntry(nextIndex, "<< Previous Page <<", (MenuEntry e) -> page--);
		MenuEntry next = new MenuEntry(nextIndex + (page > 1 ? 1 : 0), ">> Next Page >>", (MenuEntry e) -> page++);
		MenuEntry back = new MenuEntry(0, "Go Back", true);
		if (page > 1)
			entries.add(previous);
		if (count >= perPage)
			entries.add(next);
		entries.add(back);
		return entries;
	}
}
