package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoFiltroAutorizacaoFornecimento implements Dominio {

	MATERIAL,
	SERVICO,
	SC,
	SS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case MATERIAL:
			return "Material";
		case SERVICO:
			return "Serviço";
		case SC:
			return "Solicitação de Compras";
		case SS:
			return "Solicitação de Serviço";
		default:
			return this.toString();	
		}
	}
	
}
