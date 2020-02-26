package app.client.ui.menus.forms;

import java.util.function.Predicate;

import app.client.ui.Console;

public class FormField
{
	protected String name;
	protected String defaultValue;
	protected String value;
	protected boolean inputHidden;
	protected Predicate<String> validator;

	public FormField(String name)
	{
		this(name, false);
	}

	public FormField(String name, boolean inputHidden)
	{
		this(name, inputHidden, null);
	}

	public FormField(String name, String defaultValue)
	{
		this(name, defaultValue, null);
	}

	public FormField(String name, Predicate<String> validator)
	{
		this(name, false, validator);
	}

	public FormField(String name, boolean inputHidden, Predicate<String> validator)
	{
		this(name, inputHidden, null, validator);
	}

	public FormField(String name, String defaultValue, Predicate<String> validator)
	{
		this(name, false, defaultValue, validator);
	}

	protected FormField(String name, boolean inputHidden, String defaultValue, Predicate<String> validator)
	{
		this.name = name;
		this.inputHidden = inputHidden;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	public String getValue()
	{
		if (value == null || value.isBlank())
			return defaultValue;
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	protected boolean isValid()
	{
		if (validator == null)
			return true;
		return validator.test(getValue());
	}

	public void show()
	{
		while (true) {
			if (inputHidden)
				setValue(Console.askPassword(this.toString()));
			else
				setValue(Console.askString(this.toString()));
			if (isValid())
				return;
			if (this.validator == null)
				Console.println("Invalid value.");
		}
	}

	@Override
	public String toString()
	{
		return name + (defaultValue != null ? " [" + defaultValue + "]" : "");
	}
}
