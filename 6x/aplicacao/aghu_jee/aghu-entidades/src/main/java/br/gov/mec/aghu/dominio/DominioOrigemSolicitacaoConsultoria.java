package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica a origem de uma solicitação de consultoria.
 * 
 * @author gmneto
 * 
 */
public enum DominioOrigemSolicitacaoConsultoria implements Dominio {
	/**
	 * Médico
	 */
	M,
	/**
	 * Enfermeiro
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Médico";
		case E:
			return "Enfermeiro";

		default:
			return "";
		}
	}

}
