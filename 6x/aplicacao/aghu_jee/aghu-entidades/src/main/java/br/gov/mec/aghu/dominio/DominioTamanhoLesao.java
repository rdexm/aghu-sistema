package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTamanhoLesao implements Dominio{
	
	MENOR_2CM(1),
	
	DE_2_A_5CM(2),
	
	MAIOR_5_A_10CM(3),
	
	MAIOR_10CM(4),
	
	NAO_PALPAVEL(5);
	
	private Integer value;
	
	private DominioTamanhoLesao(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case MENOR_2CM: 
				return "<2 cm";
			case DE_2_A_5CM:
				return "2 - 5 cm";
			case MAIOR_5_A_10CM:
				return ">5 a 10 cm";
			case MAIOR_10CM:
				return ">10 cm";
			case NAO_PALPAVEL:
				return "Não palpável";
			default:
				return "";
		}
	}
	
	public static DominioTamanhoLesao getInstance(Integer value) {
		switch (value) {
		case 1:
			return MENOR_2CM;
		case 2:
			return DE_2_A_5CM;
		case 3:
			return MAIOR_5_A_10CM;
		case 4:
			return MAIOR_10CM;
		case 5:
			return NAO_PALPAVEL;
		default:
			return null;
		}
	}
}
