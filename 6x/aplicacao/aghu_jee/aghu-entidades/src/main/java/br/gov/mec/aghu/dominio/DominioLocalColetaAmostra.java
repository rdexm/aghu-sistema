package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o local de coleta da amostra de exame.
 * 
 * @author diego.pacheco
 * 
 */
public enum DominioLocalColetaAmostra implements Dominio {
	
	/**
	 * Unidade de Coleta
	 */
	C,
	
	/**
	 * Enfermagem
	 */
	E,
	
	/**
	 * Solicitante
	 */
	S,
	
	/**
	 * Unidade Básica Saúde
	 */
	U,
	
	/**
	 * Terceiros
	 */
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Unidade de Coleta";
		case E:
			return "Enfermagem";
		case S:
			return "Solicitante";
		case U:
			return "Unidade Básica Saúde";
		case T: 
			return "Terceiros";
		default:
			return "";
		}
	}

}
