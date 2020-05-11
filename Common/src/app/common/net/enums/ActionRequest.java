package app.common.net.enums;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	BROWSE_MARKETS,
	BROWSE_STRATEGIES,
	VIEW_STRATEGY,//TODO: needed?
	RUN_STRATEGY,
	ADD_STRATEGY,
	DOWNLOAD_STRATEGY,
	DELETE_STRATEGY,
	BROWSE_REPORTS,//TODO
	VIEW_REPORT,//TODO
	DELETE_REPORT,//TODO
	BROWSE_USERS,
	ADD_USER,
	DELETE_USER,
	BROWSE_DATA_SOURCES,
	EDIT_DATA_SOURCE,
	EDIT_MARKET,
	DELETE_DATA;

	public String toCamelCaseString()
	{
		char[] name = this.name().toLowerCase().toCharArray();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(name[0]));
		for (int i = 1; i < name.length; i++) {
			char c = name[i];
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				sb.append(c);
				continue;
			}
			c = name[i + 1];
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				sb.append(Character.toUpperCase(c));
				i++;
			}
		}
		return sb.toString();
	}
}
