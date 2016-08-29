package br.gov.mec.aghu.internacao.business.vo;

import java.io.Serializable;


public class RegistrarMovimentoInternacoesVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6016982849724548238L;

	private Short numeroQuarto;
	
	private Short seqUnidadeFuncional;
	
	private String leitoID;

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

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}
	
}
