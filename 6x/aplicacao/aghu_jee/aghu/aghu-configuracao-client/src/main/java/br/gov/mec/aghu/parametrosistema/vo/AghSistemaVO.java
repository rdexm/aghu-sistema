package br.gov.mec.aghu.parametrosistema.vo;

import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AghSistemaVO implements BaseBean {
	
	private static final long serialVersionUID = -5009554047047419479L;
	
	private String sigla;
	private String nome;
	
	public AghSistemaVO() {
	}
	
	public AghSistemaVO(AghSistemas aghSistemas) {
		this();
		this.sigla = aghSistemas.getSigla();
		this.nome = aghSistemas.getNome();
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSigla() == null) ? 0 : getSigla().hashCode());
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
		if (!(obj instanceof AghSistemaVO)) {
			return false;
		}
		AghSistemaVO other = (AghSistemaVO) obj;
		if (getSigla() == null) {
			if (other.getSigla() != null) {
				return false;
			}
		} else if (!getSigla().equals(other.getSigla())) {
			return false;
		}
		return true;
	}

	
}
