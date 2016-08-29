package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class LeitoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3409376640653995394L;

	private String leitoID;
	private String leito;
	private String indSituacao;
	private Short unfSeq;

	public LeitoVO(String leitoID, String leito, String indSituacao,Short unfSeq) {
		super();
		this.leitoID = leitoID;
		this.leito = leito;
		this.indSituacao = indSituacao;
		this.unfSeq = unfSeq;
	}

	public LeitoVO() {
	}

	public String getLeitoID() {
		return leitoID;
	}

	public String getLeito() {
		return leito;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	public String getDescricao() {
		return leitoID + " - " + leito + " - " + unfSeq + " - " + indSituacao;
	}

}
