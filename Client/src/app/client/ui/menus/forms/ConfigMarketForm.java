package app.client.ui.menus.forms;

import java.util.LinkedHashSet;

public class ConfigMarketForm extends TextForm {
	
	public ConfigMarketForm() {
		super("");
	}
	
	public ConfigMarketForm(String prompt) {
		super(prompt);

	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Granularity: ", this::validatePositiveInteger));
		fields.add(new FormField("Change Selectable: ", "false", this::validateBinary));
		fields.add(new FormField("Change Sync: ", "false", this::validateBinary));
		return fields;
	}
	
	protected boolean validateBinary(String input) 
	{
		return (input.equals("true") || input.equals("false"));
	}
	

}
