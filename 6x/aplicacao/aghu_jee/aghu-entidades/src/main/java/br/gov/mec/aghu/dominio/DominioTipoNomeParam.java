package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de nota adicional
 * 
 * @author tfelini
 * 
 */
public enum DominioTipoNomeParam implements Dominio {
	
	G,
	
	P;
	


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
}