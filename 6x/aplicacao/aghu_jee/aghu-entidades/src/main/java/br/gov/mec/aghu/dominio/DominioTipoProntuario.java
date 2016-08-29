package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um prontuário.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioTipoProntuario implements Dominio {
	
	/**
	 * Ativo
	 */
	A,

	/**
	 * Passivo
	 */
	P,
	
	/**
	 * Recadastro
	 */
	R,

	/**
	 * Histórico
	 */
	H,
	
	/**
	 * Exclusão
	 */
	E;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ativo";
		case P:
			return "Passivo";
		case R:
			return "Recadastro";
		case H:
			return "Histórico";
		case E:
			return "Excluído";
		default:
			return "";
		}
	}

}
