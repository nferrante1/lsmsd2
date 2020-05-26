package app.client.ui.menus.forms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.client.ui.Console;

public final class StrategyFileForm extends TextForm
{
	private final boolean mustExists;

	public StrategyFileForm(boolean mustExists)
	{
		this("Insert file name", mustExists);
	}

	public StrategyFileForm()
	{
		this(false);
	}

	public StrategyFileForm(String prompt)
	{
		this(prompt, false);
	}

	public StrategyFileForm(String prompt, boolean mustExists)
	{
		super(prompt);
		this.mustExists = mustExists;
	}

	@Override
	protected List<FormField> createFields()
	{
		List<FormField> fields = new ArrayList<FormField>();
		fields.add(new FormField("File", "strategy.java", this::validateJavaFile));
		return fields;
	}

	private boolean validateJavaFile(String filepath)
	{
		if (!filepath.endsWith(".java")) {
			Console.println("Invalid file extension (must be a .java file).");
			return false;
		}
		if (!mustExists)
			return true;
		try {
			File file = new File(filepath);
			if (!file.exists() || !file.isFile()) {
				Console.println("Can not find file '" + file.getCanonicalPath() + "'.");
				return false;
			}
			if (!file.canRead()) {
				Console.println("Can not read file '" + file.getCanonicalPath() + "'.");
				return false;
			}
		} catch (IOException e) {
			Console.println("Error while trying to access file '" + filepath + "'.");
			return false;
		}
		return true;
	}
}
