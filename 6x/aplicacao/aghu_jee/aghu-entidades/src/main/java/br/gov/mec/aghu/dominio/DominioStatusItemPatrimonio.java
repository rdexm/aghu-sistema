package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para o campo status da entidade PtmItemRecebProvisorios.
 * 
 */
public enum DominioStatusItemPatrimonio implements Dominio {

	/**
	 * Um
	 */
	UM(1),

	/**
	 * Dois
	 */
	DOIS(2),
	
	/**
	 * Sete
	 */
	SETE(7),
	
	/**
	 * Oito
	 */
	OITO(8);

	private Integer codigo;

	private DominioStatusItemPatrimonio(Integer codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case UM:
			return "1";
		case DOIS:
			return "2";
		case SETE:
			return "7";
		case OITO:
			return "8";
		default:
			return "0";
		}
	}

}
