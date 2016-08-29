package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoMensagemLog implements Dominio {
	/**
	 * Não Encerrado
	 */
	NAOENC, 
	
	/**
	 * Não Cobrado
	 */
	NAOCOBR, 
	
	/**
	 * Incosistente
	 */
	INCONS,
	/**
	 * Perda
	 */
	PERDA,
	/**
	 * NAOATUA
	 */
	NAOATUA	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAOENC: 
			return "Não Encerrado";
		case NAOCOBR: 
			return "Não Cobrado";
		case INCONS:
			return "Inconsistente";
		case PERDA:
			return "Perda";
		case NAOATUA:
			return "NAOATUA";
		default:
			return "";
		}
	}
}
