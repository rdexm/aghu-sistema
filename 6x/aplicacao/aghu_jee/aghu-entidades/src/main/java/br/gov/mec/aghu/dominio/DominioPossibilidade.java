package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.persistence.BaseEntity;

public class DominioPossibilidade implements BaseEntity {

	private static final long serialVersionUID = -2970074035187412571L;

	private int codigo;
	
	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return String.valueOf(codigo);
	}

}
