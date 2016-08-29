package br.gov.mec.aghu.core.utils;

import org.apache.commons.lang3.StringUtils;

public class SqlUtils {
	
	
	public static String makeOrderHQL(String alias, String orderProperty, boolean asc) {
		if (StringUtils.isBlank(orderProperty)) {
			return "";
		}
		StringBuilder order = new StringBuilder();
		
		order.append(" order by ");
		if (StringUtils.isBlank(alias)) {
			order.append(alias).append(".");
		}
		order.append(orderProperty);
		
		order.append(asc ? " asc" : " desc");
		
		return order.toString();
	}

}
