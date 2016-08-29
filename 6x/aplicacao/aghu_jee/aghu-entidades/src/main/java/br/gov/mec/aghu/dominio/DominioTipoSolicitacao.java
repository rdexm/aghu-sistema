package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica o tipo de uma solicitacao.
 * 
 *  
 * @author dzboeira
 * 
 */
public enum DominioTipoSolicitacao implements Dominio {
	SC("Solicitação de Compra"),
	SS("Solicitação de Serviço");
	
	private String descricao;
	
	private DominioTipoSolicitacao(String descricao) {
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
