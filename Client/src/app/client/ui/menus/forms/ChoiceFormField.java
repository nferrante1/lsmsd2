package app.client.ui.menus.forms;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class ChoiceFormField<T extends Enum<T>> extends FormField
{
	protected LinkedHashMap<Integer, String> values = new LinkedHashMap<>();
	protected int defaultSelection;
	protected Class<T> enumClass;
	protected Predicate<T> validator;
	protected Function<T, String> converter;

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

	public ChoiceFormField(String name, Class<T> e, Function<T, String> converter)
	{
		this(name, e);
		this.converter = converter;
	}

	public ChoiceFormField(String name, Class<T> e, Predicate<T> validator)
	{
		this(name, e);
		this.validator = validator;
	}

	public ChoiceFormField(String name, Class<T> e, Predicate<T> validator, Function<T, String> converter)
	{
		this(name, e, validator);
		this.converter = converter;
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

	public ChoiceFormField(String name, T defaultValue, Class<T> e, Function<T, String> converter)
	{
		this(name, defaultValue, e);
		this.converter = converter;
	}

	public ChoiceFormField(String name, T defaultValue, Class<T> e, Predicate<T> validator)
	{
		this(name, defaultValue, e);
		this.validator = validator;
	}

	public ChoiceFormField(String name, T defaultValue, Class<T> e, Predicate<T> validator, Function<T, String> converter)
	{
		this(name, defaultValue, e, validator);
		this.converter = converter;
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
			sb.append("\t" + key + ") " + converter == null ? Enum.valueOf(enumClass, value).toString() : converter.apply(Enum.valueOf(enumClass, value)) + "\n");
		});
		sb.append("Enter selection" + (defaultSelection > 0 ? " [" + defaultSelection + "]" : ""));
		return sb.toString();
	}

	protected static String[] getEnumNames(Class<? extends Enum<?>> e)
	{
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
}
