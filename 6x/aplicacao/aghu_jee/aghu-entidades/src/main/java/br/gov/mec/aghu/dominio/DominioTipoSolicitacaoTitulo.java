package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author rafael.silvestre
 */
public enum DominioTipoSolicitacaoTitulo implements Dominio {
	
	SS("Serviço"),
	SC("Compras");
	
	private String descricao;
	
	private DominioTipoSolicitacaoTitulo(String descricao) {
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
