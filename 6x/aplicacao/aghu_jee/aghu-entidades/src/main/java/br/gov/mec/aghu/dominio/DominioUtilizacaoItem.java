package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUtilizacaoItem implements Dominio {

	/**
	 * Ambos (Proced. pode ir tanto no SSM Solicitado quanto no Realizado).
	 */
	A,
	/**
	 * Realizado (Procedimento somente pode aparecer no SSM Realizado).
	 */
	R,
	/**
	 * Solicitado (Procedimento somente pode aparecer no SSM Solicitado).
	 */
	S,
	/**
	 * Nenhum (Proced. não pode ir nem no SSM Solicitado nem no Realizado).
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambos";
		case R:
			return "Realizado";
		case S:
			return "Solicitado";
		case N:
			return "Nenhum";
		default:
			return "";
		}
	}
}
