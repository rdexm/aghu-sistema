package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;

/**
 * 
 * @author diego.pacheco
 *
 */
public class DiagnosticoEtiologiaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5381658175246030381L;
	
	private Integer prcAtdSeq;
	private Short cdgFdgDgnSnbGnbSeq; 
	private Short cdgFdgDgnSnbSequencia;
	private Short cdgFdgDgnSequencia;
	private Short cdgFdgFreSeq;
	private String descricaoDiagnostico;
	private String descricaoEtiologia;
	private Boolean selecionado;

	public Integer getPrcAtdSeq() {
		return prcAtdSeq;
	}

	public void setPrcAtdSeq(Integer prcAtdSeq) {
		this.prcAtdSeq = prcAtdSeq;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	public String getDescricaoDiagnostico() {
		return descricaoDiagnostico;
	}
	
	public void setDescricaoDiagnostico(String descricaoDiagnostico) {
		this.descricaoDiagnostico = descricaoDiagnostico;
	}
	
	public String getDescricaoEtiologia() {
		return descricaoEtiologia;
	}
	
	public void setDescricaoEtiologia(String descricaoEtiologia) {
		this.descricaoEtiologia = descricaoEtiologia;
	}

	public Short getCdgFdgDgnSnbGnbSeq() {
		return cdgFdgDgnSnbGnbSeq;
	}

	public void setCdgFdgDgnSnbGnbSeq(Short cdgFdgDgnSnbGnbSeq) {
		this.cdgFdgDgnSnbGnbSeq = cdgFdgDgnSnbGnbSeq;
	}

	public Short getCdgFdgDgnSnbSequencia() {
		return cdgFdgDgnSnbSequencia;
	}

	public void setCdgFdgDgnSnbSequencia(Short cdgFdgDgnSnbSequencia) {
		this.cdgFdgDgnSnbSequencia = cdgFdgDgnSnbSequencia;
	}

	public Short getCdgFdgDgnSequencia() {
		return cdgFdgDgnSequencia;
	}

	public void setCdgFdgDgnSequencia(Short cdgFdgDgnSequencia) {
		this.cdgFdgDgnSequencia = cdgFdgDgnSequencia;
	}

	public Short getCdgFdgFreSeq() {
		return cdgFdgFreSeq;
	}

	public void setCdgFdgFreSeq(Short cdgFdgFreSeq) {
		this.cdgFdgFreSeq = cdgFdgFreSeq;
	}

}
