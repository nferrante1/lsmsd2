package app.client.ui.menus.forms;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Files;

import app.client.ui.Console;

public abstract class TextForm
{
	protected String prompt;
	private List<FormField> fields;

	protected abstract List<FormField> createFields();

	protected TextForm()
	{
		this("Fill the following form");
	}

	protected TextForm(String prompt)
	{
		this.prompt = prompt;
	}

	public HashMap<String, String> show()
	{
		if (!prompt.isBlank())
			Console.println(prompt + ":");
		Console.newLine();
		fields = createFields();
		HashMap<String, String> hm = new HashMap<String, String>();
		for (FormField field: fields) {
			field.show();
			hm.put(field.getName(), field.getValue());
		}
		return hm;
	}

	protected boolean validateUsername(String username)
	{
		if (! (username != null && username.matches("^[A-Za-z0-9]{3,32}$"))) {
			Console.println("Invalid username.");
			return false;
		}
		return true;
	}

	protected boolean validatePassword(String password)
	{
		if (!(password != null && password.length() > 7)) {
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
	
	protected boolean validateDate(String date)
	{
		if(date == null || date.isBlank())
			return true;
		try {
			Instant converted = Instant.parse(date);
		} catch (DateTimeParseException ex) {
			Console.println("Invalid date (use format YYYY-MM-DD).");
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
	
	protected boolean validatePathJava(String filepath) 
	{
		if(!Files.getFileExtension(filepath).equals("java")) {
			Console.println("Invalid file format (must be .java).");
			return false;
		}
		return true;
	}
}
