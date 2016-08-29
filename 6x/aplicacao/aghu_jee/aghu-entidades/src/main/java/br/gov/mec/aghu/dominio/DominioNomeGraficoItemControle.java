package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNomeGraficoItemControle implements Dominio {
	GP // Gráfico Pressão
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GP:
			return "Gráfico Pressão";
		default:
			return "";
		}
	}
}
