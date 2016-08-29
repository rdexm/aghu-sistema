package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAmostra implements Dominio {

	D("Doador", 1),
	R("Receptor", 2);
	
	private int codigo;
	private String descricao;

	DominioTipoAmostra(final String descricao, int codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}

	public static DominioTipoAmostra getInstance(String valor) {
		if ("D".equals(valor)) {
			return DominioTipoAmostra.D;
		} else if ("R".equals(valor)) {
			return DominioTipoAmostra.R;
		} else {
			return null;
		}
	}
	
}
