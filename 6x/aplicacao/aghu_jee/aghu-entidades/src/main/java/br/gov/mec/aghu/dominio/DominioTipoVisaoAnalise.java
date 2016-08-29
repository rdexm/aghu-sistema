package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoVisaoAnalise implements Dominio {
	
	OBJETO_CUSTO,
	CENTRO_CUSTO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OBJETO_CUSTO:
			return "Objeto de Custo";
		case CENTRO_CUSTO:
			return "Centro de Custo";
		default:
			return "";
		}
	}

}
