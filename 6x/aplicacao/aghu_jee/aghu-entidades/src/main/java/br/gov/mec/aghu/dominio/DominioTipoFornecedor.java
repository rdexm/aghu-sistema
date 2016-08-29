package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica uma categoria profissional
 * 
 * @author lcmoura
 * 
 */
public enum DominioTipoFornecedor implements Dominio {
	FNE("Fornecedor Estrangeiro"), FNA("Fornecedor Nacional");
	private final String descricao;

	DominioTipoFornecedor(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return descricao;
	}
}
