package br.gov.mec.aghu.controlepaciente.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * @author fpalma
 * 
 */
public class EcpItemControleVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7729341439174130068L;
	
	private Short seq;
	private String sigla;
	private String descricaoUnidadeMedicaGrupo;
	
	public EcpItemControleVO() {

	}

	public EcpItemControleVO(Short seq, String sigla,
			String descricaoUnidadeMedicaGrupo) {
		super();
		this.seq = seq;
		this.sigla = sigla;
		this.descricaoUnidadeMedicaGrupo = descricaoUnidadeMedicaGrupo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricaoUnidadeMedicaGrupo() {
		return descricaoUnidadeMedicaGrupo;
	}

	public void setDescricaoUnidadeMedicaGrupo(String descricaoUnidadeMedicaGrupo) {
		this.descricaoUnidadeMedicaGrupo = descricaoUnidadeMedicaGrupo;
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
		EcpItemControleVO other = (EcpItemControleVO) obj;
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
