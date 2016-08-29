package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um documento fiscal de entrada
 * 
 * @author lsamberg
 * 
 */
public enum DominioSituacaoDocumentoFiscalEntrada implements Dominio {
	
	C,
	G,
	L,
	P,
	R,
	V,
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		return null;
	}

}
