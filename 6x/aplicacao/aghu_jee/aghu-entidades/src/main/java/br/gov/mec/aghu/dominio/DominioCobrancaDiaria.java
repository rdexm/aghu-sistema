package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio referente a COBRANCA DIARIAS, criado com valores encontrados no
 * Designer (abrir "Application System" = FAT)
 */
public enum DominioCobrancaDiaria implements Dominio {

	/**
	 * Diárias
	 */
	S,

	/**
	 * Normal
	 */
	N,
	
	/**
	 * Diárias ou Dias
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Diárias";
		case N:
			return "Normal";
		case D:
			return "Diárias ou Dias";
		default:
			return "";
		}
	}
}
