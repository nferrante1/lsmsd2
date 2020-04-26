package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class AmountForm extends TextForm
{
	public AmountForm()
	{
		super("Define an amount");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Amount", "100000", /*this::validateString(searchBy)*/ null));
		//TODO: validate positive double
		return fields;
	}

}
