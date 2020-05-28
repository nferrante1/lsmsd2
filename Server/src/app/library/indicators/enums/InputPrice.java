package app.library.indicators.enums;

public enum InputPrice
{
	OPEN,
	HIGH,
	LOW,
	CLOSE,
	TYPICAL,
	TRUE_RANGE,
	INCREMENT,
	DECREMENT,
	VOLUME;

	public String getShortName()
	{
		switch (this) {
		case DECREMENT:
			return "d";
		case INCREMENT:
			return "u";
		case TRUE_RANGE:
			return "tr";
		case TYPICAL:
			return "tp";
		case OPEN:
			return "o";
		case HIGH:
			return "h";
		case LOW:
			return "l";
		case VOLUME:
			return "v";
		case CLOSE:
		default:
			return "";
		}
	}

	public static InputPrice fromShortName(String name)
	{
		if (name == null)
			return null;
		if (name.equalsIgnoreCase("d"))
			return DECREMENT;
		if (name.equalsIgnoreCase("u"))
			return INCREMENT;
		if (name.equalsIgnoreCase("tr"))
			return TRUE_RANGE;
		if (name.equalsIgnoreCase("tp"))
			return TYPICAL;
		if (name.equalsIgnoreCase("o"))
			return OPEN;
		if (name.equalsIgnoreCase("h"))
			return HIGH;
		if (name.equalsIgnoreCase("l"))
			return LOW;
		if (name.equalsIgnoreCase("v"))
			return VOLUME;
		return CLOSE;
	}
}
