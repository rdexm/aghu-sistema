package br.gov.mec.aghu.configuracao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


/**
 * Filtro para busca de Especialidade através do web service.
 * 
 * @author Flavio Rutkowski
 * 
 */
public class Especialidade  implements BaseBean {
	
	private static final long serialVersionUID = 306286560403432454L;
	
	private Short seq;	
	private String sigla;	
	private String nomeEspecialidade;
	
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
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
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
		if (!(obj instanceof Especialidade)) {
			return false;
		}
		Especialidade other = (Especialidade) obj;
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
