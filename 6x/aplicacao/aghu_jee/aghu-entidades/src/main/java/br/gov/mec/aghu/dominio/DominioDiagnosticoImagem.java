package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiagnosticoImagem implements Dominio {
	
	MICROCALCIFICACAO(1),
	
	DISTORCAO(2),
	
	NODULO(3),
	
	ASSIMETRIA(4);
	
	private Integer value;
	
	private DominioDiagnosticoImagem(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case MICROCALCIFICACAO: 
				return "Microcalcificação";
			case DISTORCAO:
				return "Distorção";
			case NODULO:
				return "Nódulo";
			case ASSIMETRIA:
				return "Assimetria";
			default:
				return "";
		}
	}
	
	public static DominioDiagnosticoImagem getInstance(Integer value) {
		switch (value) {
		case 1:
			return MICROCALCIFICACAO;
		case 2:
			return DISTORCAO;
		case 3:
			return NODULO;
		case 4:
			return ASSIMETRIA;
		default:
			return null;
		}
	}
}
