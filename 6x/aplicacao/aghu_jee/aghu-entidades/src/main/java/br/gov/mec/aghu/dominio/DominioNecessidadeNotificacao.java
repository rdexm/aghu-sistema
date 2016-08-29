package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio de necessidades de notificação.
 * 
 * @author evschneider
 * 
 */
public enum DominioNecessidadeNotificacao implements Dominio {

	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N,
	/**
	 * Se necessário
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case C:
			return "Se necessário";
		default:
			return "";
		}
	}
}
