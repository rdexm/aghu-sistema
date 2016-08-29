package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ExamesPerinatologiaVO implements BaseBean {
	private static final long serialVersionUID = -4644643044154714261L;

	private String chave;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer eexSeq;
	private String descricao;
	private String origem;

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getEexSeq() {
		return eexSeq;
	}

	public void setEexSeq(Integer eexSeq) {
		this.eexSeq = eexSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
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
		ExamesPerinatologiaVO other = (ExamesPerinatologiaVO) obj;
		if (chave == null) {
			if (other.chave != null) {
				return false;
			}
		} else if (!chave.equals(other.chave)) {
			return false;
		}
		return true;
	}
}
