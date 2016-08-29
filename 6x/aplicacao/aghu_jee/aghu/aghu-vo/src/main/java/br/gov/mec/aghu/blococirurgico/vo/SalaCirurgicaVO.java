package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * VO que representa uma sala cirurgica
 * 
 * @author luismoura
 * 
 */
public class SalaCirurgicaVO implements Serializable {
	private static final long serialVersionUID = 8870974327137845556L;

	private Short unfSeq;
	private Short seqp;
	private String nome;

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}