package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCtrlDispensario implements Dominio {
	/**
	 * Inclusão para integrar com dispensário
	 */
	I,
	/**
	 * Alteração para integrar com dispensário
	 */
	A,
	/**
	 * Integração com dispensário processada
	 */
	P,
	/**
	 * Exclusão para integrar com dispensário
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inclusão para integrar com dispensário";
		case A:
			return "Alteração para integrar com dispensário";
		case P:
			return "Integração com dispensário processada";
		case E:
			return "Exclusão para integrar com dispensário";
		default:
			return "";
		}
	}

}
