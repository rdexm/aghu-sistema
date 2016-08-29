package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Representa os dados de AEL_EXAMES_MATERIAL_ANALISE
 * 
 * @author luismoura
 * 
 */
public class DadosExameMaterialAnaliseVO implements BaseBean {
	private static final long serialVersionUID = 894294505163534800L;

	private String sigla;
	private String descricao;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		DadosExameMaterialAnaliseVO other = (DadosExameMaterialAnaliseVO) obj;
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
