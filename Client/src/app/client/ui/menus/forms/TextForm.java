package app.client.ui.menus.forms;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashSet;

import app.client.ui.Console;

public abstract class TextForm
{
	protected String prompt;
	private LinkedHashSet<FormField> fields;

	protected abstract LinkedHashSet<FormField> createFields();

	protected TextForm()
	{
		this("Fill the following form");
	}

	protected TextForm(String prompt)
	{
		this.prompt = prompt;
	}

	public HashMap<Integer, String> show()
	{
		if (!prompt.isBlank())
			Console.println(prompt + ":");
		Console.newLine();
		fields = createFields();
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		int i = 0;
		for (FormField field: fields) {
			field.show();
			hm.put(i, field.getValue());
			i++;
		}
		return hm;
	}

	protected boolean validateUsername(String username)
	{
		if (!User.validateUsername(username)) {
			Console.println("Invalid username.");
			return false;
		}
		return true;
	}

	protected boolean validatePassword(String password)
	{
		if (!User.validatePassword(password)) {
			Console.println("Invalid password.");
			return false;
		}
		return true;
	}

	protected boolean validatePositiveInteger(String value)
	{
		try {
			int converted = Integer.parseInt(value);
			if (converted <= 0)
				return false;
		} catch (NumberFormatException ex) {
			Console.println("Invalid number.");
			return false;
		}
		return true;
	}

	protected boolean validateFutureDate(String date)
	{
		try {
			LocalDate converted = LocalDate.parse(date);
			if (converted.compareTo(LocalDate.now()) < 0) {
				Console.println("Invalid date. Must be a date placed in future.");
				return false;
			}
		} catch (DateTimeParseException ex) {
			Console.println("Invalid date (use format YYYY-MM-DD).");
			return false;
		}
		return true;
	}
}
