package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a ação para parâmetros de regras orçamentárias.
 * 
 * @author mlcruz
 */
public enum DominioAcaoParametrosOrcamento implements Dominio {
	/** Obriga */
	O("Obriga"),
	
	/** Sugere */
	S("Sugere"),
	
	/** Restring */
	R("Restringe");
	
	private String descricao;
	
	private DominioAcaoParametrosOrcamento(String descricao) {
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