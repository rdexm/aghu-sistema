package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Atributo indSituacao da entidade MpaPopVersoes.
 * 
 * @author rcorvalao
 *
 */
public enum DominioSituacaoPopVersao implements Dominio {
	E, L, I
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
	
}
