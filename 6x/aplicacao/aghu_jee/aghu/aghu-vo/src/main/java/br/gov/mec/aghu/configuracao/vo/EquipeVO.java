package br.gov.mec.aghu.configuracao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que retorna os dados de AGH_EQUIPE
 * 
 * @author luismoura
 * 
 */
public class EquipeVO implements BaseBean {
	private static final long serialVersionUID = 5901661506980881413L;

	private Integer seq;
	private String nome;
	private Boolean indAtivo;
	private Boolean indPlacarCo;
	private Integer serMatricula;
	private Short serVinCodigo;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}

	public Boolean getIndPlacarCo() {
		return indPlacarCo;
	}

	public void setIndPlacarCo(Boolean indPlacarCo) {
		this.indPlacarCo = indPlacarCo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
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
		EquipeVO other = (EquipeVO) obj;
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
