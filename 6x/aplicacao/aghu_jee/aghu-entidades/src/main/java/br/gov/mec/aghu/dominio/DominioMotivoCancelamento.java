package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo do cancelamento de uma cirurgia
 */
public enum DominioMotivoCancelamento implements Dominio {
	
	/**
	 * Geral
	 */
	G,
	
	/**
	 * Causas hospitalares
	 */
	H,

	/**
	 * Causas dos Centros Cirúrgicos
	 */
	C;

	private int value;

	private DominioMotivoCancelamento() {
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Geral";
		case H:
			return "Causas hospitalares";
		case C:
			return "Causas dos Centros Cirúrgicos";
		default:
			return "";
		}
	}

}
