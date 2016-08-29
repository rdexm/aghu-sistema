package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica as dimensões de um tumor dominante - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaDimensMaxTumor implements Dominio {
	
	/**
	 * <2 cm
	 */
	MENOR_QUE_DOIS_CM(1),
	
	/**
	 * 2 - 5 cm
	 */
	DE_DOIS_A_CINCO_CM(2),
	
	/**
	 * >5 cm
	 */
	MAIS_QUE_CINCO_CM(3),
	
	/**
	 * Não Avaliável
	 */
	NAO_AVALIAVEL(4);
	
	
	private Integer value;
	
	private DominioSismamaDimensMaxTumor(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case MENOR_QUE_DOIS_CM: 
				return "<2 cm";
			case DE_DOIS_A_CINCO_CM:
				return "2 - 5 cm";
			case MAIS_QUE_CINCO_CM:
				return ">5 cm";
			case NAO_AVALIAVEL:
				return "Não Avaliável";
			default:
				return "";
		}
	}
	
	public static DominioSismamaDimensMaxTumor getInstance(Integer value) {
		switch (value) {
		case 1:
			return MENOR_QUE_DOIS_CM;
		case 2:
			return DE_DOIS_A_CINCO_CM;
		case 3:
			return MAIS_QUE_CINCO_CM;
		case 4:
			return NAO_AVALIAVEL;
		default:
			return null;
		}
	}

}
