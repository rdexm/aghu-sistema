package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que representa um tipo de anestesia
 * 
 * @author luismoura
 * 
 */
public class TipoAnestesiaVO implements BaseBean {
	private static final long serialVersionUID = 7545688761800625846L;

	public TipoAnestesiaVO() {

	}

	public TipoAnestesiaVO(Short seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}

	private Short seq;
	private String descricao;

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
		TipoAnestesiaVO other = (TipoAnestesiaVO) obj;
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