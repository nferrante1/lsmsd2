package app.client.ui.menus.forms;

import java.util.ArrayList;
import java.util.List;

public class PathForm extends TextForm {

	public PathForm(String path)
	{
		super("Select a strategy to upload (the file must be .java):");
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("Strategy Name"));
		fields.add(new FormField("Path", "...", this::validatePathJava));
		return fields;
	}

}
