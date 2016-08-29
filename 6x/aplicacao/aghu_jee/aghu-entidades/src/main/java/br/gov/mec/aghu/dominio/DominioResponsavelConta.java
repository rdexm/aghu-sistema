package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o responsavel da conta
 * 
 * @author frutkowski
 * 
 */
public enum DominioResponsavelConta implements Dominio {
	/**
	 * paciente
	 */
	P,

	/**
	 * Outro
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Paciente";
		case O:
			return "Outro";
		default:
			return "";
		}
	}

}
