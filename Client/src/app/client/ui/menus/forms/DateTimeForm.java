package app.client.ui.menus.forms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;

public class DateTimeForm extends TextForm
{
	public DateTimeForm()
	{
		super("Insert the date until you want to delete candles (Format: yyyy-M-d H:m) or leave blank to delete all");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Date", this::validateDateTime));
		return fields;
	}

	private boolean validateDateTime(String value)
	{
		if (value == null || value.isBlank())
			return true;
		try {
			Instant converted = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-M-d H:m")).atZone(ZoneId.of("UTC")).toInstant();
			if (converted.isAfter(Instant.now())) {
				Console.println("Can not use a future date.");
				return false;
			}
		} catch (DateTimeParseException ex) {
			Console.println("Invalid date (use format yyyy-M-d H:m).");
			return false;
		}
		return true;
	}
}
