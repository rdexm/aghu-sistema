package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica as aplicações de parâmetros de regras orçamentárias.
 * 
 * @author mlcruz
 */
public enum DominioIndicadorParametrosOrcamento implements Dominio {
	/**
	 * Patrimônio
	 */
	P("Patrimônio"),
	
	/**
	 * Engenharia
	 */
	E("Engenharia"),
	
	/**
	 * Nutrição
	 */
	N("Nutrição");
	
	private String descricao;
	
	private DominioIndicadorParametrosOrcamento(String descricao) {
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