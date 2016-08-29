package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio referente a CATEGORIA DE TABELAS para o POJO AGHTabelasSistema
 */
public enum DominioCategoriaTabela implements Dominio {
	
	/**
	 * Processo
	 */
	P,

	/**
	 * Cadastro
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Processo";
		case C:
			return "Cadastro";
		default:
			return "";
		}
	}

}
