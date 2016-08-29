package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de envio do email
 * 
 * @author israel.haas
 * 
 */
public enum DominioSituacaoEnvioEmail implements Dominio {
	
	/**
	 * E-mail enviado
	 */
	OK,

	/**
	 * Sem e-mail cadastrado
	 */
	SE;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OK:
			return "E-mail enviado";
		case SE:
			return "Sem e-mail cadastrado";
		default:
			return "";
		}
	}

}
