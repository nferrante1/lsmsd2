package app.client.ui.menus.forms;

import app.client.ui.Console;

public class BooleanFormField extends FormField
{
	public BooleanFormField(String name)
	{
		super(name);
	}

	@Override
	public void show()
	{
		setValue(Boolean.toString(Console.askChoice(this.toString(), "y", "n")));
	}

	@Override
	public String toString()
	{
		return name;
	}
}
