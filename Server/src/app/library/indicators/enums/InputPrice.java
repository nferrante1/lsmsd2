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
}
