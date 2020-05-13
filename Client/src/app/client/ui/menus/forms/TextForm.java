package app.client.ui.menus.forms;

import java.util.HashMap;
import java.util.List;

import app.client.ui.Console;

public abstract class TextForm
{
	protected String prompt;
	private List<FormField> fields;

	protected abstract List<FormField> createFields();

	protected TextForm()
	{
		this("Fill the following form");
	}

	protected TextForm(String prompt)
	{
		this.prompt = prompt;
	}

	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}

	public HashMap<String, String> show()
	{
		if (!prompt.isBlank()) {
			Console.println(prompt + ":");
			Console.newLine();
		}
		fields = createFields();
		HashMap<String, String> hm = new HashMap<String, String>();
		for (FormField field : fields) {
			field.show();
			hm.put(field.getName(), field.getValue());
		}
		return hm;
	}
}
