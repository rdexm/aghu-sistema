package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Pais
 * 
 * @author frutkowski
 * 
 */
public enum DominioPaisResponsavelConta implements Dominio {
	/**
	 * paciente
	 */
	B,

	/**
	 * Outro
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "Brasil";
		case E:
			return "Exterior";
		default:
			return "";
		}
	}

}
