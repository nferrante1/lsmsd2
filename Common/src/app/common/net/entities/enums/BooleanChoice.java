package app.common.net.entities.enums;

import java.util.logging.Logger;

public enum BooleanChoice {
	
	TRUE,
	FALSE;
	
	public String toString()
	{
		switch (this) {
		case TRUE:
			return "TRUE";
		case FALSE:
			return "FALSE";
		default:
			Logger.getLogger(BooleanChoice.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}
	
	public boolean toBooelan()
	{
		switch (this) {
		case TRUE:
			return true;
		case FALSE:
			return false;
		default:
			return false;
		}
		
	}

}
