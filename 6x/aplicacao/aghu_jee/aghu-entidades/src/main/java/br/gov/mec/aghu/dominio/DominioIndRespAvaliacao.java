package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o responsável por uma avaliação.
 * 
 * @author lcmoura
 * 
 */
public enum DominioIndRespAvaliacao implements Dominio {
	/**
	 * Comedi
	 */
	C,

	/**
	 * Pneumologia
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "COMEDI";
		case P:
			return "PNEUMOLOGIA";
		default:
			return "";
		}
	}
}
