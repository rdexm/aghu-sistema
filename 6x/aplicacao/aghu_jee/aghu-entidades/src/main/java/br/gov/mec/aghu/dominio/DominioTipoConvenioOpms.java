package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a cor de uma pessoa.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoConvenioOpms implements Dominio {

	
	/**
	 * SUS
	 */
	SUS("SUS", "Convênio é somente o SUS"),

	/**
	 * Outros Convênios
	 */
	OUT("Outros Convênios", "São todos os convênios cadastrados, menos o SUS");

	private String descricao;
	private String significado;

	//TODO verificar o uso do significado
	DominioTipoConvenioOpms(String descricao, String significado) {
		this.descricao = descricao;
		this.significado = significado;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}
	public String getSignificado() {
		return significado;
	}

	public static DominioTipoConvenioOpms getInstance(String valor) {
		if ("SUS".equals(valor)) {
			return DominioTipoConvenioOpms.SUS;
		} else if ("OUT".equals(valor)) {
			return DominioTipoConvenioOpms.OUT;
		} else {
			return null;
		}
	}
	
}
