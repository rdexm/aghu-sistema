package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo da pendencia do paciente
 */
public enum DominioMotivoPendencia implements Dominio {
	
	/**
	 * Paciente ausente realizando exames
	 */
	EXA,
	
	/**
	 * O caso será discutido com o preceptor
	 */
	PRE,

	/**
	 * O registro será realizado posteriormente
	 */
	POS,

	/**
	 * Outros
	 */
	OUT;

	private int value;

	private DominioMotivoPendencia() {
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case EXA:
			return "Paciente ausente realizando exames";
		case PRE:
			return "O caso será discutido com o preceptor";
		case POS:
			return "O registro será realizado posteriormente";
		case OUT:
			return "Outros";
		default:
			return "";
		}
	}

}
