package app.client.ui.menus;

abstract class SelectMenu<T> extends PagedMenu
{
	private T selection;

	SelectMenu()
	{
		super();
	}

	SelectMenu(int perPage)
	{
		super(perPage);
	}

	SelectMenu(String prompt)
	{
		super(prompt);
	}

	SelectMenu(String prompt, int perPage)
	{
		super(prompt, perPage);
	}

	T getSelection()
	{
		return selection;
	}

	protected void setSelection(T selection)
	{
		this.selection = selection;
	}
}
