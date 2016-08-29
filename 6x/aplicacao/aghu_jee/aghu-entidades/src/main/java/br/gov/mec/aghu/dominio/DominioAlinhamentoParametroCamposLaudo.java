package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAlinhamentoParametroCamposLaudo implements Dominio {

	/**
	 * Esquerda
	 */
	E, 

	/**
	 * Direita
	 */
	D,

	/**
	 * Centralizado
	 */
	C,
	
	/**
	 * Justificado
	 */
	J;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "left";
		case D:
			return "right";
		case C:
			return "center";
		case J:
			return "justify";
		default:
			return "";
		}
	}
	
	
	public static DominioAlinhamentoParametroCamposLaudo getInstance(String alinhamento) {
		if ("left".equalsIgnoreCase(alinhamento)) {
			return DominioAlinhamentoParametroCamposLaudo.E;
		} else if ("right".equalsIgnoreCase(alinhamento)) {
			return DominioAlinhamentoParametroCamposLaudo.D;
		} else if ("center".equalsIgnoreCase(alinhamento)) {
			return DominioAlinhamentoParametroCamposLaudo.C;
		} else if ("justify".equalsIgnoreCase(alinhamento)) {
			return DominioAlinhamentoParametroCamposLaudo.J;
		}else{
			return DominioAlinhamentoParametroCamposLaudo.E;
		}
	}

}
