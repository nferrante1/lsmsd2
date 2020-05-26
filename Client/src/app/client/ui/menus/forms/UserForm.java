package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;

public final class UserForm extends TextForm
{
	private boolean validate;

	public UserForm(String prompt)
	{
		super(prompt);
	}

	public UserForm()
	{
		this("Please, log-in");
	}

	public UserForm(String prompt, boolean validate)
	{
		this(prompt);
		this.validate = validate;
	}

	public UserForm(boolean validate)
	{
		this();
		this.validate = validate;
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("USERNAME", this::validateUsername));
		fields.add(new FormField("PASSWORD", true, this::validatePassword));
		return fields;
	}

	private boolean validateUsername(String username)
	{
		if (username == null || !username.matches("^[A-Za-z0-9]{3,32}$")) {
			Console.println("Invalid username.");
			return false;
		}
		return true;
	}

	private boolean validatePassword(String password)
	{
		if (password == null || password.length() < 8) {
			Console.println("Invalid password.");
			return false;
		}
		if (validate) {
			FormField confirmField = new FormField("CONFIRM PASSWORD", true);
			confirmField.show();
			if (!confirmField.getValue().equals(password)) {
				Console.println("Passwords does not match.");
				return false;
			}
		}
		return true;
	}
}
