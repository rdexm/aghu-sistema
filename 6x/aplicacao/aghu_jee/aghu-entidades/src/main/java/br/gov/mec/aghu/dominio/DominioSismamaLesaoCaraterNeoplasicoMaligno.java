package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a lesão de caráter neoplásico maligno - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaLesaoCaraterNeoplasicoMaligno implements Dominio {
	
	/**
	 * Carcinoma ductal com componente intraductal predominante
	 */
	CARCINOMA_DUCTAL_COM_COMPONENTE_INTRADUCTAL_PREDOMINANTE(6),
	
	/**
	 * Carcinoma ductal infiltrante
	 */
	CARCINOMA_DUCTAL_INFILTRANTE(5),
	
	/**
	 * Carcinoma intraductal (in situ) de alto grau histológico
	 */
	CARCINOMA_INTRADUCTAL_IN_SITU_DE_ALTO_GRAU_HISTOLOGICO(3), 
	
	/**
	 * Carcinoma intraductal (in situ) de baixo grau histológico
	 */
	CARCINOMA_INTRADUCTAL_IN_SITU_DE_BAIXO_GRAU_HISTOLOGICO(1),
	
	/**
	 * Carcinoma intraductal (in situ) de grau intermediário
	 */
	CARCINOMA_INTRADUCTAL_IN_SITU_DE_GRAU_INTERMEDIARIO(2),	
	
	/**
	 * Carcinoma lobular  (in situ)
	 */
	CARCINOMA_LOBULAR_IN_SITU(4),
	
	/**
	 * Carcinoma lobular invasivo
	 */
	CARCINOMA_LOBULAR_INVASIVO(7),
	
	/**
	 * Carcinoma medular
	 */
	CARCINOMA_MEDULAR(10),
	
	/**
	 * Carcinoma mucinoso
	 */
	CARCINOMA_MUCINOSO(9),
	
	/**
	 * Carcinoma tubular
	 */
	CARCINOMA_TUBULAR(8),
	
	/**
	 * Outros
	 */
	OUTROS(11),
	
	/**
	 * Doença de Paget do mamilo sem tumor
	 */
	DOENCA_DE_PAGET(12);
	
	
	
	private Integer value;
	
	private DominioSismamaLesaoCaraterNeoplasicoMaligno(Integer value) {
		this.value = value;
	}
	
	
	@Override
	public int getCodigo() {
		return this.value;
	}
	
	@Override
	public String getDescricao() {
		switch(this) {
			case CARCINOMA_DUCTAL_COM_COMPONENTE_INTRADUCTAL_PREDOMINANTE:
				return "Carcinoma ductal com componente intraductal predominante";
			case CARCINOMA_DUCTAL_INFILTRANTE:
				return "Carcinoma ductal infiltrante";
			case CARCINOMA_INTRADUCTAL_IN_SITU_DE_ALTO_GRAU_HISTOLOGICO:
				return "Carcinoma intraductal (in situ) de alto grau histológico";
			case CARCINOMA_INTRADUCTAL_IN_SITU_DE_BAIXO_GRAU_HISTOLOGICO:
				return "Carcinoma intraductal (in situ) de baixo grau histológico";
			case CARCINOMA_INTRADUCTAL_IN_SITU_DE_GRAU_INTERMEDIARIO:
				return "Carcinoma intraductal (in situ) de grau intermediário";
			case CARCINOMA_LOBULAR_IN_SITU:
				return "Carcinoma lobular  (in situ)";
			case CARCINOMA_LOBULAR_INVASIVO:
				return "Carcinoma lobular invasivo";
			case CARCINOMA_MEDULAR:
				return "Carcinoma medular";
			case CARCINOMA_MUCINOSO:
				return "Carcinoma mucinoso";
			case CARCINOMA_TUBULAR:
				return "Carcinoma tubular";
			case OUTROS:
				return "Outros";
			case DOENCA_DE_PAGET:
				return "Doença de Paget do mamilo sem tumor";
			default:
				return "";
		}
	}
	
	public static DominioSismamaLesaoCaraterNeoplasicoMaligno getInstance(
			Integer value) {
		switch (value) {
		case 6:
			return CARCINOMA_DUCTAL_COM_COMPONENTE_INTRADUCTAL_PREDOMINANTE;
		case 5:
			return CARCINOMA_DUCTAL_INFILTRANTE;
		case 3:
			return CARCINOMA_INTRADUCTAL_IN_SITU_DE_ALTO_GRAU_HISTOLOGICO;
		case 1:
			return CARCINOMA_INTRADUCTAL_IN_SITU_DE_BAIXO_GRAU_HISTOLOGICO;
		case 2:
			return CARCINOMA_INTRADUCTAL_IN_SITU_DE_GRAU_INTERMEDIARIO;
		case 4:
			return CARCINOMA_LOBULAR_IN_SITU;
		case 7:
			return CARCINOMA_LOBULAR_INVASIVO;
		case 10:
			return CARCINOMA_MEDULAR;
		case 9:
			return CARCINOMA_MUCINOSO;
		case 8:
			return CARCINOMA_TUBULAR;
		case 11:
			return OUTROS;
		default:
			return null;
		}
	}

}
