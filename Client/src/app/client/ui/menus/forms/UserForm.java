package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class UserForm extends TextForm
{
	public UserForm()
	{
		this("Please, log-in");
	}

	public UserForm(String prompt)
	{
		super(prompt);
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("USERNAME", this::validateUsername));
		fields.add(new FormField("PASSWORD", true, this::validatePassword));
		return fields;
	}
}
