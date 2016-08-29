package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o procedimento cirúrgico - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaProcedimentoCirurgico implements Dominio {
	
	/**
	 * Biópsia estereotáxica
	 */
	BIOPSIA_ESTEREOTAXICA(4),
	
	/**
	 * Biópsia excisional
	 */
	BIOPSIA_EXCISIONAL(2),
	
	/**
	 * Biópsia incisional
	 */
	BIOPSIA_INCISIONAL(1),
	
	/**
	 * Biópsia por agulha grossa
	 */
	BIOPSIA_POR_AGULHA_GROSSA(3),
	
	/**
	 * Excisão de ductos principais
	 */
	EXCISAO_DE_DUCTOS_PRINCIPAIS(6),
	
	/**
	 * Mastectomia glandular
	 */
	MASTECTOMIA_GLANDULAR(7),
	
	/**
	 * Mastectomia radical e radical modificada
	 */
	MASTECTOMIA_RADICAL_E_RADICAL_MODIFICADA(10),
	
	/**
	 * Mastectomia simples
	 */
	MASTECTOMIA_SIMPLES(9),
	
	/**
	 * Ressecção segmentar
	 */
	RESSECCAO_SEGMENTAR(5),
			
	/**
	 * Ressecção segmentar com esvaziamento axilar	
	 */
	RESSECCAO_SEGMENTAR_COM_ESVAZIAMENTO_AXILAR(8);
	
	
	private Integer value;
	
	private DominioSismamaProcedimentoCirurgico(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case BIOPSIA_ESTEREOTAXICA:
				return  "Biópsia estereotáxica";
			case BIOPSIA_EXCISIONAL:
				return "Biópsia excisional";
			case BIOPSIA_INCISIONAL:
				return "Biópsia incisional";
			case BIOPSIA_POR_AGULHA_GROSSA:
				return "Biópsia por agulha grossa (core biopsy)";
			case EXCISAO_DE_DUCTOS_PRINCIPAIS:
				return "Excisão de ductos principais";
			case MASTECTOMIA_GLANDULAR:
				return "Mastectomia glandular";
			case MASTECTOMIA_RADICAL_E_RADICAL_MODIFICADA:
				return "Mastectomia radical e radical modificada";
			case MASTECTOMIA_SIMPLES:
				return "Mastectomia simples";
			case RESSECCAO_SEGMENTAR:
				return "Ressecção segmentar";
			case RESSECCAO_SEGMENTAR_COM_ESVAZIAMENTO_AXILAR:
				return "Ressecção segmentar com esvaziamento axilar";
			default: 
				return "";
		}
	}
	
	public static DominioSismamaProcedimentoCirurgico getInstance(Integer value) {
		switch (value) {
		case 4:
			return BIOPSIA_ESTEREOTAXICA;
		case 2:
			return BIOPSIA_EXCISIONAL;
		case 1:
			return BIOPSIA_INCISIONAL;
		case 3:
			return BIOPSIA_POR_AGULHA_GROSSA;
		case 6:
			return EXCISAO_DE_DUCTOS_PRINCIPAIS;
		case 7:
			return MASTECTOMIA_GLANDULAR;
		case 10:
			return MASTECTOMIA_RADICAL_E_RADICAL_MODIFICADA;
		case 9:
			return MASTECTOMIA_SIMPLES;
		case 5:
			return RESSECCAO_SEGMENTAR;
		case 8:
			return RESSECCAO_SEGMENTAR_COM_ESVAZIAMENTO_AXILAR;
		default:
			return null;
		}
	}

}
