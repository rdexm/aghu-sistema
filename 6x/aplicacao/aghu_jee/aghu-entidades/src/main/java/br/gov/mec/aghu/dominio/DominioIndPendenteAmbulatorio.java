package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica os status do indPendente de uma interconsulta.
 * 
 */
public enum DominioIndPendenteAmbulatorio implements Dominio {
	/**
	 * Pendente
	 */
	P,
	/**
	 * Alteração não validada
	 */
	A,
	/**
	 * Exclusão não validada
	 */
	E,
	/**
	 * Validado
	 */
	V,
	/**
	 * Rascunho
	 */
	R,
	/**
	 * Item não utilizado
	 */
	X,
	/**
	 * Excluído após validação
	 */
	C;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Pendente";
		case A:
			return "Alteração não validada";
		case E:
			return "Exclusão não validada";
		case V:
			return "Validado";
		case R:
			return "Rascunho";
		case X:
			return "Item não utilizado";
		case C:
			return "Excluído após validação";
		default:
			return "";
		}
	}

}
