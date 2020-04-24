package app.client.ui.menus.forms;

import java.util.LinkedHashSet;

public class SearchAmountForm extends TextForm {
	
	protected String amount;
	
	public SearchAmountForm(String amount) {
		super("Define an amount:");
		this.amount = amount;
	}
	
	public SearchAmountForm(String amount, String prompt) {
		super(prompt);
		this.amount = amount;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField(amount,"", /*this::validateString(searchBy)*/ null));
		return fields;
	}

}
