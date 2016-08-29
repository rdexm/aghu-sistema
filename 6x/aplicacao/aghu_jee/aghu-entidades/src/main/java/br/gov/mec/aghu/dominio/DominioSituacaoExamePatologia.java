package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoExamePatologia implements Dominio {
	
	/**
	 * Diagnóstico Concluído
	 */
	DC,

	/**
	 * Exame em Andamento
	 */
	EA,

	/**
	 * Macro Concluída
	 */
	MC,

	/**
	 * Liberar Laudo
	 */
	LA,

	/**
	 * Recebido
	 */
	RE,

	/**
	 * Técnica Concluída
	 */
	TC

	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DC:
			return "Diagnóstico Concluído";
		case EA:
			return "Exame em Andamento";
		case LA:
			return "Laudo Liberado";
		case MC:
			return "Macro Concluída";
		case RE:
			return "Recebido";
		case TC:
			return "Técnica Concluída";
		default:
			return "";
		}
	}

	public String getAcao() {
		switch (this) {
		case DC:
			return "Concluir Diagnóstico";
		case EA:
			return "Exame em Andamento";
		case LA:
			return "Liberar Laudo";
		case MC:
			return "Liberar para Técnica";
		case RE:
			return "Receber";
		case TC:
			return "Concluir Técnica";
		default:
			return "";
		}
	}
}