package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiagAltoRisco implements Dominio {

	NENHUM(0,"Nenhum"),
	PARADA_PRECENDENDO_UTI(1,"Parada cardíaca precedendo admissão na UTI"),
	IMUNODEFICIENCIA_COMBINADA(2,"Imunodeficiência grave combinada"),
	LEUCEMIA_LINFOMA_APOS_INDUCAO(3,"Leucemia ou linfoma após a primeira indução"),
	HEMORRAGIA_CEREBRAL(4,"Hemorragia Cerebral espontânea"),
	MIOCARDIOPATIA_MIOCARDITE(5,"Miocardiopatia ou miocardite"),
	SINDROME_CORACAO_HIPOPLASTICO(6,"Síndrome do coração esquerdo hipoplásico"),
	INFECCAO_HIV(7,"Infecção por HIV"),
	INSUFICIENCIA_HEPATICA(8,"Insuficiência hepática é a principal razão para admissão na UTI"),
	DOENCA_NEURODEGENERATIVA(9,"Doença neurodegenerativa");
	
	private int codigo;
	private String descricao;
	
	DominioDiagAltoRisco(int cod, String desc){
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
