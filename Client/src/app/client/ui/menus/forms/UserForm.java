package app.client.ui.menus.forms;

import java.util.LinkedHashSet;

public class UserForm extends TextForm
{
	public UserForm()
	{
		super("Please, log in");
	}
	
	public UserForm(String prompt) {
		super(prompt);
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
