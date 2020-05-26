package app.client.ui.menus.forms;

import java.util.function.Predicate;

import app.client.ui.Console;

class FormField
{
	protected final String name;
	protected final String defaultValue;
	protected String value;
	protected final boolean inputHidden;
	protected final Predicate<String> validator;

	protected FormField(String name)
	{
		this(name, false);
	}

	protected FormField(String name, boolean inputHidden)
	{
		this(name, inputHidden, null);
	}

	protected FormField(String name, String defaultValue)
	{
		this(name, defaultValue, null);
	}

	protected FormField(String name, Predicate<String> validator)
	{
		this(name, false, validator);
	}

	protected FormField(String name, boolean inputHidden, Predicate<String> validator)
	{
		this(name, inputHidden, null, validator);
	}

	protected FormField(String name, String defaultValue, Predicate<String> validator)
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

	public String getName()
	{
		return name;
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
