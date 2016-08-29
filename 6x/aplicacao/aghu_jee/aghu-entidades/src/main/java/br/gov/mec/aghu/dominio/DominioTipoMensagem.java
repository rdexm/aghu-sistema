package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Possíveis tipos de mensagem da entidade ael_exigencia_exames.
 *
 */
public enum DominioTipoMensagem implements Dominio {
	
	/**
	 * Informativa
	 */
	I, 
	/**
	 * Restritiva
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Informativa";
		case R:
			return "Restritiva";
		default:
			return "";
		}
	}

}