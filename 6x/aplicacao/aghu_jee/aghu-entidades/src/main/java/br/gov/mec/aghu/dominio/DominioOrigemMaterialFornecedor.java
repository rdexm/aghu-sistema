package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * Dominio que Indica a origem da inclusão do Material do Fornecedor.
 * @author mesias
 *
 */
public enum DominioOrigemMaterialFornecedor implements Dominio {
	/**
	 * 
	 */
	P("Proposta"),
	
	/**
	 * 
	 */
	C("Cadastro"),
	
	/**
	 * 
	 */
	R("Recebimento"),
	
	/**
	 * 
	 */
	T("Portal");

	
	private String descricao;
	
	private DominioOrigemMaterialFornecedor(String descricao){
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
}
