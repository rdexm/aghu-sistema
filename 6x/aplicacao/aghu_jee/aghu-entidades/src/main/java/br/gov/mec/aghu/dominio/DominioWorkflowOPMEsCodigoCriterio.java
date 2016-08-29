package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioWorkflowOPMEsCodigoCriterio implements Dominio {
	
	ALCADA_1("ALCADA_1","O valor do material pertence à alçada 1"),
	ALCADA_2 ("ALCADA_2","O valor do material pertence à alçada 2"),
	ALCADA_3("ALCADA_3","O valor do material pertence à alçada 3"),
	MAT_NOVO("MAT_NOVO","Não se sabe o valor do material"),
	NAO_HA ("NAO_HA", "Não há critério a ser testado"),
	REQ_AUT ("REQ_AUT", "A REQUISIÇÃO DE OPMEs FOI AUTORIZADA");
		
	private String codigo;
	private String descricao;

	private DominioWorkflowOPMEsCodigoCriterio(String codigo, String descricao) {
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}
	
	@Override
	public String toString(){
		return codigo;
	}

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}
}
