package app.client.ui.menus.forms;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Predicate;

public class ChoiceFormField<T extends Enum<T>> extends FormField
{
	protected LinkedHashMap<Integer, String> values = new LinkedHashMap<>();
	protected int defaultSelection;
	protected Class<T> enumClass;
	protected Predicate<T> validator;

	public ChoiceFormField(String name, Class<T> e)
	{
		super(name);
		enumClass = e;
		int i = 1;
		String[] values = getEnumNames(e);
		for (String value: values) {
			this.values.put(i, value);
			i++;
		}
	}

	public ChoiceFormField(String name, Class<T> e, Predicate<T> validator)
	{
		this(name, e);
		this.validator = validator;
	}

	public ChoiceFormField(String name, T defaultValue, Class<T> e)
	{
		super(name, defaultValue.toString());
		enumClass = e;
		int i = 1;
		String[] values = getEnumNames(e);
		for (String value: values) {
			this.values.put(i, value);
			if (value.equals(defaultValue.name()))
				defaultSelection = i;
			i++;
		}
	}

	public ChoiceFormField(String name, T defaultValue, Class<T> e, Predicate<T> validator)
	{
		this(name, defaultValue, e);
		this.validator = validator;
	}

	@Override
	protected boolean isValid()
	{
		if (!values.containsValue(value))
			return false;
		if (validator == null)
			return true;
		return validator.test(Enum.valueOf(enumClass, value));
	}

	@Override
	public void setValue(String value)
	{
		if (value == null || value.isBlank())
			value = Integer.toString(defaultSelection);
		this.value = values.get(Integer.parseInt(value));
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(name + ":\n");
		values.forEach((key, value) -> {
			sb.append("\t" + key + ") " + Enum.valueOf(enumClass, value).toString() + "\n");
		});
		sb.append("Enter selection" + (defaultSelection > 0 ? " [" + defaultSelection + "]" : ""));
		return sb.toString();
	}

	protected static String[] getEnumNames(Class<? extends Enum<?>> e)
	{
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
}
