package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação da notificação - CCIH
 * 
 * @author fbraganca
 * 
 */
public enum DominioSituacaoNotificacao implements Dominio {
	/**
	 * Aberta
	 */
	A,
	/**
	 * Encerrada
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
			return "Aberta";
		case E:
			return "Encerrada";
		default:
			return "";
		}
	}

}
