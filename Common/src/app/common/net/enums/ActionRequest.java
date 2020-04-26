package app.common.net.enums;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN, // req={loginInfo} res={authToken}
	LOGOUT,
	BROWSE_MARKETS, // req={(browseInfo)page, perPage; (filter)nameFilter} res={marketInfoList}
	BROWSE_STRATEGIES, // req={(browseInfo)page, perPage; (filter)nameFilter} res={strategyInfoList}
	VIEW_STRATEGY, // TODO: req={(filter)strategyName} res={(ParameterInfoList)name, enum:type}
	RUN_STRATEGY, // req={(filter)strategyName; kvParameterList} res={(progressInfo)percentage}/{reportInfo} //TODO: add type to KVParameter
	ADD_STRATEGY, // req={(filter)strategyName; file}
	DOWNLOAD_STRATEGY, // req={(filter)strategyName}, res={file}
	DELETE_STRATEGY, // req={(filter)strategyName}
	BROWSE_REPORTS, // req={(browseReportsInfo)page, perPage[from browseInfo], strategyName, marketName} res={baseReportInfoList}
	VIEW_REPORT, // req={(filter)reportId} res={reportInfo; KVParameterList}
	DELETE_REPORT, // req={(filter)reportId}
	BROWSE_USERS, // req={(browseInfo)page, perPage, (filter)nameFilter} res={userInfoList}
	ADD_USER, // req={loginInfo}
	DELETE_USER, // req={(filter)username}
	BROWSE_DATA_SOURCES, // res={sourceInfoList}
	EDIT_DATA_SOURCE, // req={sourceInfo}
	EDIT_MARKET, // req={marketInfo}
	DELETE_DATA; // req={(deleteDataFilter)sourceId, marketId, date}

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
