package br.gov.mec.aghu.internacao.business.vo;

import java.io.Serializable;


public class BuscarLocalInternacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6729164903321338500L;

	private String leitoID;
	
	private Short numeroQuarto;
	
	private Short seqUnidadeFuncional;

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}
	
}
