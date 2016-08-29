package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo do encerramento de um crachá.
 */
public enum DominioMotivoEncerramento implements Dominio {

	/**
	 * Alta
	 */
	A,

	/**
	 * Perda
	 */
	P,

	/**
	 * Troca
	 */
	T,

	/**
	 * Outros
	 */
	O;

	private int value;

	private DominioMotivoEncerramento() {
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Alta";
		case P:
			return "Perda";
		case T:
			return "Troca";
		case O:
			return "Outros";
		default:
			return "";
		}
	}

}
