package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseTipoEntrada implements Dominio {
	//

	/**
	 * Caso Novo
	 */
	CASO_NOVO(1),

	/**
	 * Recidiva
	 */
	RECIDIVA(2),

	/**
	 * Reingresso Após Abandono
	 */
	REINGRESSO_APOS_ABANDONO(3),

	/**
	 * Não Sabe
	 */
	NAO_SABE(4),

	/**
	 * Transferência
	 */
	TRANSFERENCIA(5);

	private int value;

	private DominioNotificacaoTuberculoseTipoEntrada(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CASO_NOVO:
			return "Caso Novo";
		case RECIDIVA:
			return "Recidiva";
		case REINGRESSO_APOS_ABANDONO:
			return "Reingresso Após Abandono";
		case NAO_SABE:
			return "Não Sabe";
		case TRANSFERENCIA:
			return "Transferência";
		default:
			return "";
		}
	}

}