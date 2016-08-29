package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o grau de instrução de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioGrauInstrucao implements Dominio {
	

	/**
	 * 1° Grau Completo
	 */
	PRIMEIRO_GRAU_COMPLETO(1),

	/**
	 * 2° Grau Completo
	 */
	SEGUNDO_GRAU_COMPLETO(2),
	
	/**
	 * Superior
	 */
	SUPERIOR(3),

	/**
	 * Nenhum
	 */
	NENHUM(4),
	
	/**
	 * Ignorado
	 */
	IGNORADO(5),

	/**
	 * 1° Grau Incompleto
	 */
	PRIMEIRO_GRAU_INCOMPLETO(6),
	
	/**
	 * 2° Grau Incompleto
	 */
	SEGUNDO_GRAU_INCOMPLETO(7),

	/**
	 * Superior Incompleto
	 */
	SUPERIOR_INCOMPLETO(8);

	private int value;
	
	private DominioGrauInstrucao(int value) {
		this.value = value;
	}
	
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRIMEIRO_GRAU_COMPLETO:
			return "1° Grau Completo";
		case SEGUNDO_GRAU_COMPLETO:
			return "2° Grau Completo";
		case SUPERIOR:
			return "Superior";
		case NENHUM:
			return "Nenhum";
		case IGNORADO:
			return "Ignorado";
		case PRIMEIRO_GRAU_INCOMPLETO:
			return "1° Grau Incompleto";
		case SEGUNDO_GRAU_INCOMPLETO:
			return "2° Grau Incompleto";
		case SUPERIOR_INCOMPLETO:
			return "Superior Incompleto";
		default:
			return "";
		}
	}

}
