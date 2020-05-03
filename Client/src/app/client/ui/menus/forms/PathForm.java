package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class PathForm extends TextForm {

	public PathForm(String prompt)
	{
		super(prompt);
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Path", "Strategy.java", this::validatePathJava));
		//TODO add defaultPath 
		return fields;
	}

}
