package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAfpPublicado implements Dominio {
	
	/**
	 * Efetivado
	 */
	E,
	/**
	 * Não Publicada
	 */
	N,
	/**
	 * Publicada
	 */
	S,
	/**
	 * Pendente de Publicação
	 */
	P;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente de Publicação";
		case S:
			return "Publicada";
		case N:
			return "Não Publicada";
		case E:
			return "Efetivado";
		default:
			return "";
		}
	}

}
