package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de uma movimentação de
 * prontuário.
 * 
 * @author gmneto
 * 
 */
public enum DominioSituacaoMovimentoProntuario implements Dominio {
	/**
	 * Retirado
	 */
	R,
	/**
	 * Separado
	 */
	S,
	/**
	 * Não Localizado
	 */
	N,
	/**
	 * Devolvido
	 */
	D,
	/**
	 * Solicitado
	 */
	P,

	/**
	 * Requerido
	 */
	Q;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Retirado";
		case S:
			return "Separado";
		case N:
			return "Não Localizado";
		case D:
			return "Devolvido";
		case P:
			return "Solicitado";
		case Q:
			return "Requerido";
		default:
			return "";
		}

	}

}
