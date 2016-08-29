package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEventoComunicacao implements Dominio {
	
	/**
	 * Aviso Geral
	 */
	AG,
	/**
	 * Processamento OK
	 */
	PF,
	/**
	 * Processamento Erro 
	 */
	PE,	
	/**
	 * Cadastro de Exames 
	 */
	CE,
	/**
	 * Análise do Processamento
	 */
	AP
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AG:
			return "Aviso Geral";
		case PF:
			return "Processamento OK";
		case PE:
			return "Processamento Erro";
		case CE:
			return "Cadastro de Exames";
		case AP:
			return "Análise do Processamento";
		default:
			return "";
		}
	}
}
