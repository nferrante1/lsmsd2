package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class DateForm extends TextForm {

	public DateForm()
	{
		super("Select the date until you want to delete candles:");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Date", this::validateDate));
		return fields;
	}
}
