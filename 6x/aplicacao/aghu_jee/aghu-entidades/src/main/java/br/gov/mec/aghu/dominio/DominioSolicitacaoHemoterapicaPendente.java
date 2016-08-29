package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 *  Dominio que indica os status do indPendente de um <code>AbsSolicitacoesHemoterapicas</code>.
 * 
 */
@Deprecated
public enum DominioSolicitacaoHemoterapicaPendente implements Dominio {
	/**
	 * Validado
	 */
	N,
	/**
	 * Não validado
	 */
	P,
	/**
	 * Exclusão não validada
	 */
	E,
	/**
	 * Alteração não validada
	 */
	A,
	/**
	 * Não validado - Modelo Básico
	 */
	B,
	/**
	 * Rascunho
	 */
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Validado";
		case P:
			return "Não validado";
		case E:
			return "Exclusão não validada";
		case A:
			return "Alteração não validada";
		case B:
			return "Não validado - Modelo Básico";
		case X:
			return "Rascunho";
		default:
			return "";
		}
	}

}
