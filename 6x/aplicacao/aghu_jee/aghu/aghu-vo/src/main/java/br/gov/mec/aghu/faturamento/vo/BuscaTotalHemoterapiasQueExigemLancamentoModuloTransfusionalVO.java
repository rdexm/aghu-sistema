package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654795854941483664L;

	private Long iphCodSus;

	private Short iphPhoSeq;

	private Integer iphSeq;

	private Integer quantidade;

	public BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO() {
	}

	public BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO(Long iphCodSus, Short iphPhoSeq, Integer iphSeq,
			Integer quantidade) {
		this.iphCodSus = iphCodSus;
		this.iphPhoSeq = iphPhoSeq;
		this.iphSeq = iphSeq;
		this.quantidade = quantidade;
	}

	public Long getIphCodSus() {
		return iphCodSus;
	}

	public void setIphCodSus(Long iphCodSus) {
		this.iphCodSus = iphCodSus;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
