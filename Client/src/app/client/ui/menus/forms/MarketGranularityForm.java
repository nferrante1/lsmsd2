package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;

public class MarketGranularityForm extends TextForm
{
	protected int previousGranularity;

	public MarketGranularityForm(int previousGranularity)
	{
		super("Set granularity (minutes)");
		this.previousGranularity = previousGranularity;
	}
	
	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(
			new FormField("Granularity", Integer.toString(previousGranularity), this::validateGranularity));
		return fields;
	}

	private boolean validateGranularity(String value)
	{
		try {
			int converted = Integer.parseUnsignedInt(value);
			if (converted == 0) {
				Console.println("Granularity can not be zero.");
				return false;
			}
		} catch (NumberFormatException ex) {
			Console.println("Invalid granularity.");
			return false;
		}
		return true;
	}
}
