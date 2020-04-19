package app.client.ui.menus.forms;

import java.util.LinkedHashSet;

public class SearchByNameForm extends TextForm{
	
	protected String searchBy;
	
	public SearchByNameForm(String searchBy) {
		super("Search by name (press Enter to find all entities):");
		this.searchBy = searchBy;
	}
	
	public SearchByNameForm(String searchBy, String prompt) {
		super(prompt);
		this.searchBy = searchBy;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField(searchBy,"*", this::validateUsername));
		return fields;
	}

}
