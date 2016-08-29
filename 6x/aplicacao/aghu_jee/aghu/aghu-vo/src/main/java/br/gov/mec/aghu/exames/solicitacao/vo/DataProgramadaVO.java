package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataProgramadaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6338972216817649583L;
	private String pattern = "EEE dd/MM/yyyy HH:mm";
	private Date date;
	
	public DataProgramadaVO(Date data) {
		date = data;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFormattedDate() {
		if (getDate() == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("pt", "BR"));
		return sdf.format(getDate());
	}
}
