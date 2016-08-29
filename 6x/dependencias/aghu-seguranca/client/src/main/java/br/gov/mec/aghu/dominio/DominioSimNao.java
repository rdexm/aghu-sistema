package br.gov.mec.aghu.dominio;

import br.gov.mec.dominio.Dominio;

/**
 * Domínio para reforçar retrições de tipo em campos com comportamento boleano.
 * 
 * @author gmneto
 * 
 */
public enum DominioSimNao implements Dominio {
	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N;

	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";

		default:
			return "";
		}
	}

	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * 
	 * @return
	 */
	public boolean isSim() {
		switch (this) {
		case S:
			return Boolean.TRUE;
		case N:
		default:
			return Boolean.FALSE;
		}
	}

	public static DominioSimNao getInstance(boolean valor) {
		if (valor) {
			return DominioSimNao.S;
		} else {
			return DominioSimNao.N;
		}
	}
	
	

}
