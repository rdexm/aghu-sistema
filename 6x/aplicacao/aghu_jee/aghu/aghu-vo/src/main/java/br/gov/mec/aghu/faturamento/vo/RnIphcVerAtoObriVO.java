package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class RnIphcVerAtoObriVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1194690916459248997L;
	private Short phoCobradoSeq;
	private Integer iphCobradoSeq;
	private Integer tivCodSus;
	private Byte taoCodSus;
	private Short qtd;

	public RnIphcVerAtoObriVO(final Short phoCobradoSeq,
			final Integer iphCobradoSeq, final Integer tivCodSus,
			final Byte taoCodSus, final Short qtd) {

		super();

		this.phoCobradoSeq = phoCobradoSeq;
		this.iphCobradoSeq = iphCobradoSeq;
		this.tivCodSus = tivCodSus;
		this.taoCodSus = taoCodSus;
		this.qtd = qtd;
	}

	public RnIphcVerAtoObriVO(final Short phoCobradoSeq,
			final Integer iphCobradoSeq) {
		this(phoCobradoSeq, iphCobradoSeq, null, null, null);
	}

	public Short getPhoCobradoSeq() {
		return phoCobradoSeq;
	}

	public void setPhoCobradoSeq(Short phoCobradoSeq) {
		this.phoCobradoSeq = phoCobradoSeq;
	}

	public Integer getIphCobradoSeq() {
		return iphCobradoSeq;
	}

	public void setIphCobradoSeq(Integer iphCobradoSeq) {
		this.iphCobradoSeq = iphCobradoSeq;
	}

	public Integer getTivCodSus() {
		return tivCodSus;
	}

	public void setTivCodSus(Integer tivCodSus) {
		this.tivCodSus = tivCodSus;
	}

	public Byte getTaoCodSus() {
		return taoCodSus;
	}

	public void setTaoCodSus(Byte taoCodSus) {
		this.taoCodSus = taoCodSus;
	}

	public Short getQtd() {
		return qtd;
	}

	public void setQtd(Short qtd) {
		this.qtd = qtd;
	}
}