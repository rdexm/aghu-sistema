package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica os status do indPendente de um diagnostico.
 * 
 */
public enum DominioIndPendenteDiagnosticos implements Dominio {
	/**
	 * Exclusão após validação
	 */
	C,
	/**
	 * Exclusão não validada
	 */
	E,
	/**
	 * Item não utilizado
	 */
	X,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Rascunho
	 */
	R,
	/**
	 * Alteração não validada
	 */
	A,
	/**
	 * Item Validado
	 */
	V;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Exclusão após validação";
		case E:
			return "Exclusão não validada";
		case X:
			return "Item não utilizado";
		case A:
			return "Alteração não validada";
		case P:
			return "Pendente";
		case R:
			return "Rascunho";
		case V:
			return "Item Validado";
		default:
			return "";
		}
	}

}
