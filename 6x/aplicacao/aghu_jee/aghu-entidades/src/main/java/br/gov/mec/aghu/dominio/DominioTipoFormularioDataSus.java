package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author twickert
 * 
 * São os tipos de formulário utilizados pelo sistema da DATASUS (SISbpa/I)
 *
 */
public enum DominioTipoFormularioDataSus implements Dominio {
	/**
	 * Equivale ao Boletim de Produção Ambulatorial = BPA
	 */
	C,
	/**
	 * Equivale ao Boletim de Produção Individual = BPI
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}
}
