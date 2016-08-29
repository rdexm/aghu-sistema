
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author guilherme.finotti
 *
 */
public enum DominioImpressaoRequisicaoMaterial implements Dominio {
	
	/**
	 * Local
	 */
	L,
	/**
	 * Remota
	 */
	R,
	/**
	 * Não Impressa
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	/*
	 * Foi alterado o label das constantes para atender a estória 0595.
	 * */	
	@Override
	public String getDescricao() {
		switch (this) {
		case L:
			return "Local";
		case R:
			return "Remota";
		case N:
			return "Não Impressa";		
		default:
			return "";
		}
	}

}
