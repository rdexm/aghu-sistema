package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica um tipo de atuação profissional
 * 
 * @author lcmoura
 *
 */
public enum DominioTipoAtendimento implements Dominio {
	
	/**
	 * Atendimento
	 */
	ATV, 
	
	/**
	 * Atendimento Diversos
	 */
	ATD,
	;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {	
		case ATV:
			return "Atendimento";
		case ATD:
			return "Atendimento Diversos";
		default:
			return "";
		}
	}
}
