package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a adequabilidade do material de exame - SISMAMA.
 * 
 * @author dpacheco
 * 
 */
public enum DominioSismamaAdequabilidadeMaterial implements Dominio {
	
	/**
	 * Satisfatório
	 */
	SATISFATORIO(3),

	/**
	 * Insatisfatório
	 */
	INSATISFATORIO(1);
	
	
	private Integer value;
	
	private DominioSismamaAdequabilidadeMaterial(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SATISFATORIO:
			return "Satisfatório";
		case INSATISFATORIO:
			return "Insatisfatório";
		default:
			return "";
		}
	}
	
	public static DominioSismamaAdequabilidadeMaterial getInstance(Integer value) {
		switch (value) {
		case 3:
			return SATISFATORIO;
		case 1:
			return INSATISFATORIO;
		default:
			return null;
		}
	}

}
