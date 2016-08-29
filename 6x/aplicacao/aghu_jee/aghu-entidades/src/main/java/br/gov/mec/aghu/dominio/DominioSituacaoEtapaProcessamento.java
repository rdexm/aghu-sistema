package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoEtapaProcessamento implements Dominio{
	
	//Em processamento
	A,
	
	//Concluído
	P,
	
	//Erro
	E,
	
	//Fechado
	F
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Em processamento";
		case P:
			return "Concluído";
		case E:
			return "Erro";
		case F:
			return "Fechado";
		default:
			return "";
		}
	}

}
