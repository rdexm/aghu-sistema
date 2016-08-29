package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;

public class ItemContaHospitalarVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1315683043042465202L;
	
	private Integer cthSeq;
	private DominioSituacaoItenConta indSituacao;
	private Integer iseSoeSeq;
	private Short iseSeqp;

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public DominioSituacaoItenConta getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoItenConta indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

}
