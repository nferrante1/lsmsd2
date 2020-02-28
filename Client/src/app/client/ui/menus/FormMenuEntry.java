package app.client.ui.menus;

import java.util.function.Function;

public class FormMenuEntry<T, R> extends MenuEntry {
	
	protected Function<MenuEntry, Market> formHandler;
	
	public FormMenuEntry(int key, String text, Function<MenuEntry,Market> handler, Object handlerData) {
		super(key, text, null, null);
		formHandler = handler;
	}
	
	public R triggerFormHandler()
	{
		if (this.formHandler != null)
			this.formHandler.apply(this);
	}
}
