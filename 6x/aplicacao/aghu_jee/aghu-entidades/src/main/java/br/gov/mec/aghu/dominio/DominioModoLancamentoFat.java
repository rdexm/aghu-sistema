package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio para MODO LANCAMENTO FAT especificado no Oracle Design.
 */
public enum DominioModoLancamentoFat implements Dominio {
	/**
	 * Cadastro APAC
	 */
	A,
	/**
	 * Consultas Marcadas
	 */
	C,
	/**
	 * Sistema Origem
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Cadastro APAC";
		case C:
			return "Consultas Marcadas";
		case O:
			return "Sistema Origem";
		default:
			return "";
		}
	}

}
