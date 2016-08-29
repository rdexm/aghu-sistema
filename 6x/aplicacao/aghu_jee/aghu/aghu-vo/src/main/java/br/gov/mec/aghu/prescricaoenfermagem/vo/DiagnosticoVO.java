package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

public class DiagnosticoVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -704436150870907274L;

	private Short sequencia;
	private String descricao;
	private Short snbGnbSeq;
	private Short snbSequencia;
	private Boolean selecionado;
	
	public DiagnosticoVO(){
		
	}
	
	public Short getSequencia() {
		return sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getSnbGnbSeq() {
		return snbGnbSeq;
	}

	public void setSnbGnbSeq(Short snbGnbSeq) {
		this.snbGnbSeq = snbGnbSeq;
	}

	public Short getSnbSequencia() {
		return snbSequencia;
	}

	public void setSnbSequencia(Short snbSequencia) {
		this.snbSequencia = snbSequencia;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	

}
