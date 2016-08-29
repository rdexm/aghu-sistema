package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;

/**
 * 
 * @author dansantos
 *
 */
public class OrigemUnidadeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -600689938679083787L;
	private DominioOrigemAtendimento origem;
	private Short unfSeq;
	
	public OrigemUnidadeVO() {
	}

	public OrigemUnidadeVO(DominioOrigemAtendimento origem, Short unfSeq) {
		this.origem = origem;
		this.unfSeq = unfSeq;
	}
	
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	
	
}
	
