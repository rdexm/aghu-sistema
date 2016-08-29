package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;


public class ConfirmacaoRecebimentoFiltroVO implements Serializable {
	private static final long serialVersionUID = 8919020356375252000L;

	private Integer nrpSeq;
	private Integer lctNumero;
	private Short nroComplemento;
	private Integer numeroFornecedor;
	private Integer seqNota;
	private Integer eslSeq;

	public Integer getNrpSeq() {
		return nrpSeq;
	}

	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getSeqNota() {
		return seqNota;
	}

	public void setSeqNota(Integer seqNota) {
		this.seqNota = seqNota;
	}

	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	
}
