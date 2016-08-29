package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoProcedimentoEspecial implements Dominio {
	
	ESPECIAIS_DIVERSOS,
	PROCEDIMENTOS_REALIZADOS_NO_LEITO,
	ORTESES_PROTESES;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ESPECIAIS_DIVERSOS:
			return "Especiais Diversos";
		case PROCEDIMENTOS_REALIZADOS_NO_LEITO:
			return "Cirurgias Realizadas no Leito";
		case ORTESES_PROTESES:
			return "Órteses / Próteses";
		default:
			return "";
		}
	}

}
