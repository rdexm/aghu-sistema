package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum TipoArquivoRelatorio implements Dominio {

	CSV("csv"), PDF("pdf");

	private final String descricao;
	
	private TipoArquivoRelatorio(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}

	// Método utilizado antes do refactoring, deixado para evitar problemas de
	// incompatibilidade e evitar um refactoring maior.
	public String getExtensao() {
		return this.getDescricao();
	}

}
