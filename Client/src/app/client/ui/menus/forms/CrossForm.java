package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.common.net.entities.enums.BooleanChoice;

public class CrossForm extends TextForm
{
	public CrossForm()
	{
		super("Select direct or inverse cross");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new ChoiceFormField<BooleanChoice>("Cross", BooleanChoice.FALSE, BooleanChoice.class));
		return fields;
	}
}
