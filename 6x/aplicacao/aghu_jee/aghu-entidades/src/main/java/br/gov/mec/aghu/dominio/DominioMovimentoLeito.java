package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para reforçar retrições de tipo em campos com comportamento boleano.
 * 
 * @author gmneto
 * 
 */
public enum DominioMovimentoLeito implements Dominio {
	/**
	 * Desocupado.
	 */
	L,
	/**
	 * Ocupado.
	 */
	O,
	
	/**
	 * Desativado .
	 */
	D,
	
	/**
	 * Bloqueio Limpeza.
	 */
	BL,
	
	/**
	 * Bloqueio.
	 */
	B,
	
	/**
	 * Bloqueio Infecção.
	 */
	BI,
	
	/**
	 * Informativo. 
	 */
	I,
	
	/**
	 * Reserva. 
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Desocupado";
		case O:
			return "Ocupado";
		case D:
			return "Desativado";
		case BL:
			return "Bloqueio Limpeza";
		case B:
			return "Bloqueado";
		case BI:
			return "Bloqueio Infecção";
		case I:
			return "Informativo";
		case R:
			return "Reserva";

		default:
			return "";
		}
	}
	

	

}
