package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o limite de valor de um patrimônio.
 * 
 * @author mlcruz
 */
public enum DominioLimiteValorPatrimonio implements Dominio {
	/**
	 * Até
	 */
	A("Até"),
	
	/**
	 * Maior que
	 */
	M("Maior que");
	
	private String descricao;
	
	private DominioLimiteValorPatrimonio(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return ordinal();
	}

	@Override
	public String getDescricao() {
		return descricao;
	}
}