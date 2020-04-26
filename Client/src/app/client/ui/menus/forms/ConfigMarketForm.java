package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class ConfigMarketForm extends TextForm
{
	protected int previousGranularity;

	public ConfigMarketForm(int previousGranularity)
	{
		super("Set granularity (minutes)");
		this.previousGranularity = previousGranularity;
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Granularity", Integer.toString(previousGranularity), this::validatePositiveInteger));
		return fields;
	}
}
