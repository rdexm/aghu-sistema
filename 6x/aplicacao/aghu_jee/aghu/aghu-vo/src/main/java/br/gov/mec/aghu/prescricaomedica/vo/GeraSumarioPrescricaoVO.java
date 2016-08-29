package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;


public class GeraSumarioPrescricaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4769291336734081897L;

	private Date dataInicial;

	private Date dataFinal;

	private Boolean possuiDados;

	public GeraSumarioPrescricaoVO() {
	}

	public GeraSumarioPrescricaoVO(Date dataInicial, Date dataFinal,
			Boolean possuiDados) {
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.possuiDados = possuiDados;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Boolean getPossuiDados() {
		return possuiDados;
	}

	public void setPossuiDados(Boolean possuiDados) {
		this.possuiDados = possuiDados;
	}

}
