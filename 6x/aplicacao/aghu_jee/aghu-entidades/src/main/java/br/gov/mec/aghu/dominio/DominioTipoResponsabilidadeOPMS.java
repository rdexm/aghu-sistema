package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoResponsabilidadeOPMS implements
		Dominio {

	APROVADOR,SUBSTITUTO,OBSERVADOR;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APROVADOR:
			return "Aprovador da alçada";
		case SUBSTITUTO:
			return "Aprovador Substituto da alçada";
		case OBSERVADOR:
			return "Observador da alçada. Recebe notificações do andamento do processo.";
		default:
			return "";
		}
	}
}