package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio com tipos de notificação de CIDs.
 * 
 * @author evschneider
 * 
 */
public enum DominioTipoNotificacaoCID implements Dominio {
	/**
	 * Infecção
	 */
	I,
	/**
	 * Condição de Risco
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Infecção";
		case C:
			return "Condição de Risco";
		default:
			return "";
		}
	}

}
