package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;

public class RecemNascidoFilterVO implements Serializable {

	private static final long serialVersionUID = 8253568371304563303L;
	private String nomeGestante;
	private Integer pacCodigo;
	private Short seqp;

	public String getNomeGestante() {
		return nomeGestante;
	}

	public void setNomeGestante(String nomeGestante) {
		this.nomeGestante = nomeGestante;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

}
