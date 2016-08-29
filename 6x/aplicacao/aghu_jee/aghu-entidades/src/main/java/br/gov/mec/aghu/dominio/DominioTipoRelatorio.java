package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoRelatorio implements Dominio {

	/**
	 * Impressão normal
	 */
	N,
	/**
	 * Impressora Não fiscal bematech
	 */
	F;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Ticket por Consulta Contínuo";
		case N:
			return "Ticket por Consulta";
		default:
			return "";
		}
	}
}