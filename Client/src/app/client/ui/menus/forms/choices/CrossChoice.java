package app.client.ui.menus.forms.choices;

public enum CrossChoice
{
	DIRECT,
	INVERTED;

	@Override
	public String toString()
	{
		switch (this) {
		case DIRECT:
		case INVERTED:
			return this.name();
		default:
			return "Unknown";
		}
	}

	public boolean isInverseCross()
	{
		return this == INVERTED;
	}

}
