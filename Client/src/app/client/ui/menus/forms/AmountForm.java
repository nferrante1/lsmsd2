package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;

public final class AmountForm extends TextForm
{
	public AmountForm()
	{
		super("Define an amount");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Amount", "100000.00", this::validateAmount));
		return fields;
	}

	private boolean validateAmount(String value)
	{
		try {
			double converted = Double.parseDouble(value);
			if (!Double.isFinite(converted))
				throw new NumberFormatException();
			if (converted <= 0) {
				Console.println("Amount must be greater than zero.");
				return false;
			}
		} catch (NumberFormatException ex) {
			Console.println("Invalid amount.");
			return false;
		}
		return true;
	}

}
