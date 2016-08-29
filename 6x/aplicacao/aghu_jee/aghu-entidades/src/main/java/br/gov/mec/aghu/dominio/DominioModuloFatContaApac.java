package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioModuloFatContaApac implements Dominio {

	APAC("Apac Quimio"),
	APEX("Apac de Exames"),
	APAP("Apac de Acomp. Pós-transplante"),
	APAF("Apac Fotocoagulação"),
	APAR("Apac de Radioterapia"),
	APAT("Apac de Otorrino"),
	APAN("Apac Nefro"),
	APRE("Apac de Pré-Transplante"),
	;
	
	private final String descricao;
	
	private DominioModuloFatContaApac(String descricao) {
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