package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author israel.haas
 */
public enum DominioSituacaoConsultoria implements Dominio {
	
	/**
	 * Pendente
	 */
	P,
	/**
	 * Acompanhamento
	 */
	A,
	/**
	 * Pendente + Acompanhamento
	 */
	PA,
	/**
	 * Concluída
	 */
	CO,
	/**
	 * Cancelada
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
			return "Acompanhamento";
		case PA:
			return "Pendente + Acompanhamento";
		case CO:
			return "Concluída";
		case C:
			return "Cancelada";
		default:
			return "";
		}

	}
	
}
