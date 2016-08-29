package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 
 */
public enum DominioTipoCampoCampoLaudo implements Dominio {

	/**
	 * Texto fixo
	 */
	T,
	
	/**
	 * Numérico
	 */
	N,
	
	/**
	 * Alfanumérico
	 */
	A,
	
	/**
	 * Expressão
	 */
	E,
	
	/**
	 * Codificado
	 */
	C,
	
	/**
	 * Método
	 */
	M,
	
	/**
	 * Equipamento
	 */
	Q,
	
	/**
	 * Recebimento
	 */
	R,
	
	/**
	 * Valor de referência
	 */
	V,
	
	/**
	 * Histórico
	 */
	H;
	
	
	private DominioTipoCampoCampoLaudo(){
		
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Texto fixo";
		case N:
			return "Numérico";
		case A:
			return "Alfanumérico";
		case E:
			return "Expressão";
		case C:
			return "Codificado";
		case M:
			return "Método";
		case Q:
			return "Equipamento";
		case R:
			return "Recebimento";
		case V:
			return "Valor de referência";
		case H:
			return "Histórico";
		
		default:
			return "";
		}
	}

	
	

}
