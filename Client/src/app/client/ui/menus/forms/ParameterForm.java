package app.client.ui.menus.forms;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;
import app.common.net.entities.ParameterInfo;

public final class ParameterForm extends TextForm
{
	private final List<ParameterInfo> parameters;

	public ParameterForm(List<ParameterInfo> parameters)
	{
		super("");
		this.parameters = parameters;
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		for (ParameterInfo parameter: parameters) {
			switch (parameter.getType()) {
			case BOOLEAN:
				fields.add(new BooleanFormField(parameter.getName()));
				continue;
			case DOUBLE:
				fields.add(new FormField(parameter.getName(), this::validateDouble));
				continue;
			case INSTANT:
				fields.add(new FormField(parameter.getName(), this::validateInstant));
				continue;
			case INTEGER:
				fields.add(new FormField(parameter.getName(), this::validateInteger));
				continue;
			case STRING:
			default:
				fields.add(new FormField(parameter.getName(), v -> v != null && !v.isBlank()));
				continue;
			}
		}
		return fields;
	}

	private boolean validateDouble(String value)
	{
		try {
			double converted = Double.parseDouble(value);
			if (!Double.isFinite(converted))
				throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			Console.println("Invalid value (double).");
			return false;
		}
		return true;
	}

	private boolean validateInstant(String value)
	{
		try {
			LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-M-d H:m")).atZone(ZoneId.of("UTC")).toInstant();
		} catch (DateTimeParseException ex) {
			Console.println("Invalid date (use format yyyy-M-d H:m).");
			return false;
		}
		return true;
	}

	private boolean validateInteger(String value)
	{
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			Console.println("Invalid value (integer).");
			return false;
		}
		return true;
	}
}
