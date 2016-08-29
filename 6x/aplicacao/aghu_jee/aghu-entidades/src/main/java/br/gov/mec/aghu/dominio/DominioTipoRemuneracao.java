package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de remuneração do servidor.
 * 
 * @author gmneto
 * 
 */
public enum DominioTipoRemuneracao implements Dominio {
	/**
	 * Mensalista
	 */
	M,

	/**
	 * Horista
	 */
	H;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Mensalista";
		case H:
			return "Horista";
		default:
			return "";
		}
	}

}
