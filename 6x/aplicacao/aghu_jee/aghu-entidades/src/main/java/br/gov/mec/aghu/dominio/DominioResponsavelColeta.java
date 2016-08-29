package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o responsável por uma coleta. 
 * 
 * @author mtocchetto
 * 
 */
public enum DominioResponsavelColeta implements Dominio {
	
	S,
	C,
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.name();
	}

}
