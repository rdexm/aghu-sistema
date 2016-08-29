package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;


public class DiluentesVO implements Serializable {

	private static final long serialVersionUID = 704221847659025883L;
	
	private String seqMedicamento;
	private String tipoUsoMdto;
	private Integer seqDiluente;
	private String descricao;
	private Boolean usualPrescricao;
	private String situacao;
	
	
	
	public String getSeqMedicamento() {
		return seqMedicamento;
	}
	public void setSeqMedicamento(String seqMedicamento) {
		this.seqMedicamento = seqMedicamento;
	}
	public String getTipoUsoMdto() {
		return tipoUsoMdto;
	}
	public void setTipoUsoMdto(String tipoUsoMdto) {
		this.tipoUsoMdto = tipoUsoMdto;
	}
	public Integer getSeqDiluente() {
		return seqDiluente;
	}
	public void setSeqDiluente(Integer seqDiluente) {
		this.seqDiluente = seqDiluente;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Boolean getUsualPrescricao() {
		return usualPrescricao;
	}
	public void setUsualPrescricao(Boolean usualPrescricao) {
		this.usualPrescricao = usualPrescricao;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqDiluente == null) ? 0 : seqDiluente.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DiluentesVO other = (DiluentesVO) obj;
		if (seqDiluente == null) {
			if (other.seqDiluente != null) {
				return false;
			}
		} else {
			if (!seqDiluente.equals(other.seqDiluente)) {
			return false;
			}
		}
		return true;
	}

		
}
