package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a confirmação de CCI.
 * 
 * @author evschneider
 * 
 */
public enum DominioConfirmacaoCCI implements Dominio {
	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N,
	/**
	 * Provável
	 */
	P;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case P:
			return "Provável";
		default:
			return "";
		}
	}

}
