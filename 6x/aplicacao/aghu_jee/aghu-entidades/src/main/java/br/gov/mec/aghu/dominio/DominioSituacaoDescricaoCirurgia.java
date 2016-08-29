package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoDescricaoCirurgia implements Dominio {
	/**
	 * 
	 */
	PEN, 
	/**
	 * 
	 */
	CON;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PEN:
			return "Pendente";
		case CON:
			return "Concluída";
		default:
			return "";
		}
	}

}
