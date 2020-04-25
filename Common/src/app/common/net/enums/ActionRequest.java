package app.common.net.enums;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	BROWSE_STRATEGY,
	VIEW_STRATEGY,
	RUN_STRATEGY,
	ADD_STRATEGY,
	DELETE_STRATEGY,
	DOWNLOAD_STRATEGY,
	BROWSE_REPORT,
	VIEW_REPORT,
	DELETE_REPORT,
	BROWSE_MARKET,
	BROWSE_DATA_SOURCE,
	CHANGE_DATA_SOURCE,
	BROWSE_USERS,
	ADD_USER,
	DELETE_USER,
	DELETE_DATA,
	CONFIG_MARKET;

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
