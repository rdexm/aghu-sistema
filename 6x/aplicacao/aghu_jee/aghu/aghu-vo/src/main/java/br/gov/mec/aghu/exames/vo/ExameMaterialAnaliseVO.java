package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Representa os dados de AEL_EXAMES_MATERIAL_ANALISE
 * 
 * @author luismoura
 * 
 */
public class ExameMaterialAnaliseVO implements BaseBean {
	private static final long serialVersionUID = 894294505163534800L;

	private String sigla;
	private Integer mamSeq;
	private String descricao;
	private String material;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getMamSeq() {
		return mamSeq;
	}

	public void setMamSeq(Integer mamSeq) {
		this.mamSeq = mamSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mamSeq == null) ? 0 : mamSeq.hashCode());
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
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
		ExameMaterialAnaliseVO other = (ExameMaterialAnaliseVO) obj;
		if (mamSeq == null) {
			if (other.mamSeq != null) {
				return false;
			}
		} else if (!mamSeq.equals(other.mamSeq)) {
			return false;
		}
		if (sigla == null) {
			if (other.sigla != null) {
				return false;
			}
		} else if (!sigla.equals(other.sigla)) {
			return false;
		}
		return true;
	}
}
