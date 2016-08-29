package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Esse dominio é usado na tabela FAT_PROCED_HOSP_INTERNOS no campo TIPO_OPER_CONVERSAO 
 * 
 */
public enum DominioTipoOperacaoConversao implements Dominio {
	/**
	 * Multiplicação
	 */
	M,
	/**
	 * Divisão
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Multiplicação";
		case D:
			return "Divisão";
		default:
			return "";
		}
	}
	
	
	

}
