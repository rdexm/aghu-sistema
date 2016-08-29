package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de nota adicional
 * 
 * @author tfelini
 * 
 */
public enum DominioTipoNotaAdicional implements Dominio {
	
	/**
	 * Relatórios do ambulatório
	 */
	N1,
	
	/**
	 * Telas do ambulatório
	 */
	N2,

	/**
	 * Guia da Unimed
	 */
	N3;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N1:
			return "Relatórios do ambulatório";
		case N2:
			return "Telas do ambulatório";
		case N3:
			return "Guia da Unimed";
		default:
			return "";
		}
	}
}