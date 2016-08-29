package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoRelatorioTransferenciaMaterial implements Dominio {
	/*
	 * Endereco origem
	 */
	ENDERECO_ORIGEM,
	
	/*
	 * Codigo
	 */
	CODIGO,
	/*
	 * Endereco destino
	 */
	ENDERECO_DESTINO,
	/*
	 * Descricao
	 */
	DESCRICAO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	//Endereço Origem, Endereço Destino e Descrição
	
	
	@Override
	public String getDescricao() {
		switch (this) {
		case ENDERECO_ORIGEM:
			return "Endereço origem";
		case CODIGO:
			return "Código";	
		case ENDERECO_DESTINO:
			return "Endereço destino";
		case DESCRICAO:
			return "Descrição";
		default:
			return "";
		}	
	}

}
