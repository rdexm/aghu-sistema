package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author gmneto
 * 
 */
public enum DominioTipoQualificacao implements Dominio {
	/**
	 * Curso com conselho
	 */
	CCC,

	/**
	 * Curso sem conselho
	 */
	CSC, 
	/**
	 * Eventos
	 */
	EVE, 
	/**
	 * Publicações
	 */
	PUB;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CCC:
			return "Curso com conselho";
		case CSC:
			return "Curso sem conselho";
		case EVE:
			return "Eventos";
		case PUB:
			return "Publicações";
		default:
			return "";
		}
	}
	
	


}
