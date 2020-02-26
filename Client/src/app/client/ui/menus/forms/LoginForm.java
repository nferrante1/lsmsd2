package app.client.ui.menus.forms;

import java.util.LinkedHashSet;

public class LoginForm extends TextForm
{
	public LoginForm()
	{
		super("Please, log in");
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("USERNAME", this::validateUsername));
		fields.add(new FormField("PASSWORD", true, this::validatePassword));
		return fields;
	}
}
