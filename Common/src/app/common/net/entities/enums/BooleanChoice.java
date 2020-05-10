package app.common.net.entities.enums;

import java.util.logging.Logger;

public enum BooleanChoice
{
	TRUE,
	FALSE;

	public String toString()
	{
		switch (this) {
		case TRUE:
		case FALSE:
			return this.name();
		default:
			Logger.getLogger(BooleanChoice.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}

	public boolean toBoolean()
	{
		switch (this) {
		case TRUE:
			return true;
		case FALSE:
		default:
			return false;
		}
	}

}
