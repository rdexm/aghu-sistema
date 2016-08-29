package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio Fideps. 
 * Não foi possível encontrar o mesmo no Designer. Os valores
 * são baseados nas checkConstraints da tabela FAT_ITENS_PROCED_HOSPITALAR.
 */
public enum DominioFideps implements Dominio {

	/**
	 * S
	 */
	S,
	/**
	 * F
	 */
	F,
	/**
	 * N
	 */
	N,
	/**
	 * V
	 */
	V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim - Todos";
		case F:
			return "Sim - Fora RS";
		case N:
			return "Não";
		case V:
			return "Sem Valor";
		default:
			return "";
		}
	}
}
