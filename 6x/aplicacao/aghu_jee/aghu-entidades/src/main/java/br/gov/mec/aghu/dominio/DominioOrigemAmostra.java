package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemAmostra implements Dominio {

	N("Não Humano", 1),
	H("Humano", 2);
	
	private int codigo;
	private String descricao;

	DominioOrigemAmostra(final String descricao, int codigo) {
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

	public static DominioOrigemAmostra getInstance(String valor) {
		if ("N".equals(valor)) {
			return DominioOrigemAmostra.N;
		} else if ("H".equals(valor)) {
			return DominioOrigemAmostra.H;
		} else {
			return null;
		}
	}
}