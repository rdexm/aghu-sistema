package br.gov.mec.aghu.core.dominio;

public enum DominioDiaSemana implements Dominio {

	DOMINGO(1),
	SEGUNDA(2),
	TERCA(3),
	QUARTA(4),
	QUINTA(5),
	SEXTA(6),
	SABADO(7);
	
	private Integer value;
	
	private DominioDiaSemana(Integer value) {
		this.value = value;
	}
	
	//@Override
	public int getCodigo() {
		return this.value.intValue();
	}

	
	//@Override
	public String getDescricao() {

		switch (this) {
		case DOMINGO:
			return "DOMINGO";
		case SEGUNDA:
			return "SEGUNDA";
		case TERCA:
			return "TERÇA";
		case QUARTA:
			return "QUARTA";
		case QUINTA:
			return "QUINTA";
		case SEXTA:
			return "SEXTA";
		case SABADO:
			return "SABADO";
		default:
			return "";
		}
	}
	
}
