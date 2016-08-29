package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para valores Sim e Não - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaSimNao implements Dominio {
	
	/**
	 * Sim
	 */
	SIM(3),
	
	/**
	 * Não
	 */
	NAO(1);

	
	private Integer value;
	
	private DominioSismamaSimNao(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO:
			return "Não";
		case SIM:
			return "Sim";
		default:
			return "";
		}
	}
	
	public static DominioSismamaSimNao getInstance(Integer value) {
		switch (value) {
		case 1:
			return NAO;
		case 3:
			return SIM;
		default:
			return null;
		}
	}
}
