package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiagBaixoRisco implements Dominio {
	NENHUM(0,"Nenhum"),
	ASMA(1,"Asma é a principal razão para admissão na UTI"),
	BRONQUIOLITE(2,"Bronquiolite é a principal razão para admissão na UTI"),
	LARINGITE(3,"Laringite é a principal razão para admissão na UTI"),
	APNEIA_OBSTRUTIVA(4,"Apnéia obstrutiva do sono é a principal razão para admissão na UTI"),
	CETACIDOSE_DIABETICA(5,"Cetacidose diabédica é a principal razão para admissão na UTI")
	;

	
	private int codigo;
	private String descricao;
	
	DominioDiagBaixoRisco(int cod, String desc){
		this.codigo = cod;
		this.descricao = desc;
	}
	
	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		return descricao;
	}

}
