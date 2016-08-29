package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para o campo tipoConvenio.
 * @author gfmenezes
 *
 */
public enum DominioConvenioExameSituacao implements Dominio {
	/**
	 * SUS
	 */
	S, 
	/**
	 * Não SUS
	 */
	NS;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this) {
		case S:
			return "SUS";
		case NS:
			return "Não SUS";
		default:
			return "";
		}
	}
}
