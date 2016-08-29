package br.gov.mec.aghu.configuracao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que retorna os dados de AGH_UNIDADES_FUNCIONAIS
 * 
 * @author luismoura
 * 
 */
public class UnidadeFuncionalVO implements BaseBean {
	private static final long serialVersionUID = 3127459941378965942L;

	private Short seq;
	private String descricao;

	public UnidadeFuncionalVO() {

	}

	public UnidadeFuncionalVO(Short seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
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
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		UnidadeFuncionalVO other = (UnidadeFuncionalVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
