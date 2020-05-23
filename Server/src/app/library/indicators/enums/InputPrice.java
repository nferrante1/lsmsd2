package app.library.indicators.enums;

public enum InputPrice
{
	CLOSE,
	TYPICAL,
	INCREMENT,
	DECREMENT,
	TRUE_RANGE;

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
		return CLOSE;
	}
}
