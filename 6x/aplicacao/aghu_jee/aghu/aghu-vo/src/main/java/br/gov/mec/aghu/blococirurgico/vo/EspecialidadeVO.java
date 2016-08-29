package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class EspecialidadeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4823942811842874824L;
	private String sigla;
	private String nomeEspecialidade;
	private Short seq;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((nomeEspecialidade == null) ? 0 : nomeEspecialidade
						.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof EspecialidadeVO)) {
			return false;
		}
		EspecialidadeVO other = (EspecialidadeVO) obj;
		if (nomeEspecialidade == null) {
			if (other.nomeEspecialidade != null) {
				return false;
			}
		} else if (!nomeEspecialidade.equals(other.nomeEspecialidade)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
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
