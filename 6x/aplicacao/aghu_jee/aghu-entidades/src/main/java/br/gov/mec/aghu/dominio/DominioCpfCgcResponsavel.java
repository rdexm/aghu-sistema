package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o responsavel da conta
 * 
 * @author frutkowski
 * 
 */
public enum DominioCpfCgcResponsavel implements Dominio {
	/**
	 * Pessoa Fisica
	 */
	F,

	/**
	 * PEssoa Juridica
	 */
	J;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "CPF";
		case J:
			return "CGC";		
		default:
			return "";
		}
	}

}
