package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLogAplicacao implements Dominio {

	TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return toString();
	}
}
