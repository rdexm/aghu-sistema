package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class EspecialidadeEmergenciaUnidadeVO  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2735809472015318675L;
	private Short espSeq;
	private String espSigla;

	public EspecialidadeEmergenciaUnidadeVO(Short espSeq, String espSigla) {
		super();
		this.espSeq = espSeq;
		this.espSigla = espSigla;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

}
