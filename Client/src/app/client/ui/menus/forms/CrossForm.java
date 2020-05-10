package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.common.net.entities.enums.BooleanChoice;

public class CrossForm extends TextForm
{
	public CrossForm()
	{
		super("Do you want an inverse cross?");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new ChoiceFormField<BooleanChoice>("Inverse Cross", BooleanChoice.FALSE, BooleanChoice.class));
		return fields;
	}
}
