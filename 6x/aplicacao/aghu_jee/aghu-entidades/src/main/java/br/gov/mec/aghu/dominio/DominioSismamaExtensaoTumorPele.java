package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a extensão do tumor de pele - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaExtensaoTumorPele implements Dominio {
	
	/**
	 * Não
	 */
	NAO(1),
	
	/**
	 * Não Avaliável
	 */
	NAO_AVALIAVEL(2),
	
	/**
	 * Sim, com ulceração
	 */
	SIM_COM_ULCERACAO(3),
	
	/**
	 * Sim, sem ulceração
	 */
	SIM_SEM_ULCERACAO(4);
	
	
	private Integer value;
	
	private DominioSismamaExtensaoTumorPele(Integer value) {
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
		case NAO_AVALIAVEL:
			return "Não Avaliável";
		case SIM_COM_ULCERACAO:
			return "Sim, com ulceração";
		case SIM_SEM_ULCERACAO:
			return "Sim, sem ulceração";
		default:
			return "";
		}
	}
	
	public static DominioSismamaExtensaoTumorPele getInstance(Integer value) {
		switch (value) {
		case 1:
			return NAO;
		case 2:
			return NAO_AVALIAVEL;
		case 3:
			return SIM_COM_ULCERACAO;
		case 4:
			return SIM_SEM_ULCERACAO;
		default:
			return null;
		}
	}
}
