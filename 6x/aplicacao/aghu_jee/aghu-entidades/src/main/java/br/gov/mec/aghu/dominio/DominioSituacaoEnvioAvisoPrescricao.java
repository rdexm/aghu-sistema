package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoEnvioAvisoPrescricao implements Dominio {

	/**
	 * Aviso Inicial
	 */
	I,
	/**
	 * A cada acesso prescrição
	 */
	P,
	/**
	 * Repetir aviso inicial
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
			return "Aviso Inicial";
		case P:
			return "A cada acesso prescrição";
		case R:
			return "Repetir aviso inicial";
		default:
			return "";
		}
	}

}
