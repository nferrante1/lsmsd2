package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

import app.client.ui.menus.forms.choices.CrossChoice;

public class CrossForm extends TextForm
{
	private String baseCurrency;
	private String quoteCurrency;

	public CrossForm(String baseCurrency, String quoteCurrency)
	{
		super("Do you want an inverse cross?");
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new ChoiceFormField<CrossChoice>("Inverse Cross", CrossChoice.INVERTED, CrossChoice.class, this::getFieldText));
		return fields;
	}

	private String getFieldText(CrossChoice choice)
	{
		switch (choice) {
		case INVERTED:
			return baseCurrency + "/" + quoteCurrency + " (direct)";
		case DIRECT:
			return quoteCurrency + "/" + baseCurrency + " (inverted)";
		default:
			return "?";
		}
	}
}
