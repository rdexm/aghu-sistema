package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica a situação de autorização do fornecedor
 * 
 * @author agerling
 * 
 */
public enum DominioSituacaoAutorizacaoFornecimento implements DominioString {
	
	/**
	 * A Efetivar
	 */
	AE,
	
	/**
	 * Efetivada
	 */
	EF,

	/**
	 * Efetivada Parcialmente
	 */
	EP,
	
	/**
	 * Estorno
	 */
	ES,
	
	/**
	 * Excluída
	 */
	EX,
	
	/**
	 * Parcialmente Atendida
	 */
	PA;
	
	public String getCodigo() {
		return this.toString();
	}

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
	
	public String getCodigoEDescricao() {
		switch (this) {
		case EF:
			return "EF - Efetivada";
		case EP:
			return "EP - Efetivada Parcialmente";
		case AE:
			return "AE - A Efetivar";
		case PA:
			return "PA - Parcialmente Atendida";
		case EX:
			return "EX - Excluída";
		case ES:
			return "ES - Estorno";
		default:
			return "";
		}
	}
	
	public static DominioSituacaoAutorizacaoFornecimento getInstance(String valor) {
		if ("EF".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.EF;
		} else if ("EP".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.EP;
		} else if ("AE".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.AE;
		} else if ("PA".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.PA;
		} else if ("EX".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.EX;
		} else if ("ES".equals(valor)) {
			return DominioSituacaoAutorizacaoFornecimento.ES;
		} else {
			return null;
		}
	}

}
