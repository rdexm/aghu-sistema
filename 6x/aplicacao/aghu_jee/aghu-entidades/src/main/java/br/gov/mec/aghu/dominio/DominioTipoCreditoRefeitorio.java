package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica tipo do credito do refeitório
 */
public enum DominioTipoCreditoRefeitorio implements Dominio {
	
	/**
	 * café
	 */
	VALOR_1(1),

	/**
	 * almoço
	 */
	VALOR_2(2),

	/**
	 * janta
	 */
	VALOR_3(3),
	
	/**
	 * café, almoço e janta
	 */
	VALOR_4(4),
	
	/**
	 *  Acompanhantes
	 */
	VALOR_5(5),
	
	/**
	 * café, almoço, café e janta
	 */
	//VALOR_6(6),
	
	/**
	 * Almoço/Janta
	 */
	VALOR_7(7),
	
	/**
	 * sem crédito
	 */
	VALOR_0(0);


	private int value;

	private DominioTipoCreditoRefeitorio(int value) {
		this.value = value;
	}
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VALOR_0:
			return "Sem Crédito";
		case VALOR_1:
			return "Café";
		case VALOR_2:
			return "Almoço";
		case VALOR_3:
			return "Janta";
		case VALOR_4:
			return "Café/Almoço/Janta";
		case VALOR_5:
			return "Acompanhantes";
		//case VALOR_6:
		//  return "Café/Almoço/Café/Janta";
		case VALOR_7:
			return "Almoço/Janta";
		default:
			return "";
		}
	}
}
