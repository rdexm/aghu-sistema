package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoPesquisaFilaTransplante implements Dominio {
	permanencia,
	nome,
	dataIngresso;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case permanencia:
			return "Permanência";
		case nome:
			return "Nome";
		case dataIngresso:
			return "Data Ingresso";
		default:
			return "";
		}
	}
}
