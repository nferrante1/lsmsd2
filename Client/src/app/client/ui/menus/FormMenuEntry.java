package app.client.ui.menus;

import java.util.function.Function;

public class FormMenuEntry<T, R> extends MenuEntry {
	
	protected Function<T, R> formHandler;
	
	public FormMenuEntry(int key, String text, Function<T,R> handler, Object handlerData) {
		super(key, text, null, null);
		formHandler = handler;
	}
	
	public R triggerFormHandler()
	{
		if (this.formHandler != null) {
			R r = this.formHandler.apply((T)this);
			return r;
		}
		return null;
	}
}
