package app.client.ui.menus;

public abstract class SelectMenu<T> extends PagedMenu
{
	private T selection;

	public SelectMenu()
	{
		super();
	}

	public SelectMenu(int perPage)
	{
		super(perPage);
	}

	public SelectMenu(String prompt)
	{
		super(prompt);
	}

	public SelectMenu(String prompt, int perPage)
	{
		super(prompt, perPage);
	}

	public T getSelection()
	{
		return selection;
	}

	protected void setSelection(T selection)
	{
		this.selection = selection;
	}
}
