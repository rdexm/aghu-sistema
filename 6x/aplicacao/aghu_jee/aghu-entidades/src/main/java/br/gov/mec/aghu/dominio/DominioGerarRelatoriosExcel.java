package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGerarRelatoriosExcel implements Dominio {

	ESCC("ES – Entradas de Serviços/Materiais por Centro de Custo"),
	ESMP("ES – Entradas de Serviços/Materiais no Período"),
	ESSL("SL – Entradas/Saídas Sem Licitação"),
	MDAF("MD - Entrega de Materiais com Marca Divergente da AF"),
	NRGM("NR - Notas de Recebimento Geradas no Mês")
	;
	
	private String descricao;
	
	private DominioGerarRelatoriosExcel(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return ordinal();
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}
}
