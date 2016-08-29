package br.gov.mec.aghu.casca.menu.portal.rss;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class FormataDataPortal {

    private static SimpleDateFormat dateFormat;

	public static String formataData(String dataPortal) {

		if (!StringUtils.isBlank(dataPortal)) {
			return getSimpleDateFormat().format(new Date(dataPortal));
		}
		return StringUtils.EMPTY;
	}

	public static SimpleDateFormat getSimpleDateFormat() {

		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		}
		return dateFormat;
	}
}