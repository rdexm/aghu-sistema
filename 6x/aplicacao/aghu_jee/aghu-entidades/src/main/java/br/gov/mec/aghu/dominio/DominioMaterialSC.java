package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica se e material ou SC
 * 
 *  
 * @author frutkowski
 * 
 */
public enum DominioMaterialSC implements Dominio {
	MT("Material"),
	SC("Solicitação de Compra");
	
	private String descricao;
	
	private DominioMaterialSC(String descricao) {
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
