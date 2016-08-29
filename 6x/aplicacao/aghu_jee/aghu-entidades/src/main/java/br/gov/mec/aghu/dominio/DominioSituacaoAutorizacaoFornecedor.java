package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de autorização do fornecedor
 * 
 * @author agerling
 * 
 */
public enum DominioSituacaoAutorizacaoFornecedor implements Dominio {
	
	/**
	 * Efetivada
	 */
	EF,

	/**
	 * Efetivada Parcialmente
	 */
	EP,
	
	/**
	 * A Efetivar
	 */
	AE,

	/**
	 * Parcialmente Atendida
	 */
	PA,
	
	/**
	 * Excluída
	 */
	EX,
	
	/**
	 * Estorno
	 */
	ES;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case EF:
			return "Efetivada";
		case EP:
			return "Efetivada Parcialmente";
		case AE:
			return "A Efetivar";
		case PA:
			return "Parcialmente Atendida";
		case EX:
			return "Excluída";
		case ES:
			return "Estorno";
		default:
			return "";
		}
	}

}
