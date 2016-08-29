package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica os status de uma solicitação de consultoria com relação a
 * sua conclusão.
 * 
 * @author gmneto
 * 
 */
public enum DominioIndConcluidaSolicitacaoConsultoria implements Dominio {
	/**
	 * Concluída
	 */
	S,
	/**
	 * Pendente
	 */
	N,
	/**
	 * Acompanhamento
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Concluída";
		case N:
			return "Pendente";
		case A:
			return "Acompanhamento";
		default:
			return "";
		}
	}

}
