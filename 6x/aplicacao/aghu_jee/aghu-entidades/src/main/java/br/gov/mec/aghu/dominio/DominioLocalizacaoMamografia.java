package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


public enum DominioLocalizacaoMamografia implements DominioString {
	
	/*
	00 – Default 
	01 - QSL(Quadrante superior lateral)
	02 - QIL(Quadrante inferior lateral)
	03 - QSM(Quadrante superior medial)
	04 - QIM(Quadrante inferior medial)
	05 - UQlat(União dos quadrantes laterais)
	06 - UQsup(União dos quadrantes superiores)
	07 - UQmed(União dos quadrantes mediais)
	08 - UQinf(União dos quadrantes inferiores)
	09 - RRA(Região retroareolar)
	10 - RC(Região central (união de todos os quadrantes))
	11 - PA(Prolongamento axilar)*/
	DEFAULT("00"),
	QSL("01"),
	QIL("02"),
	QSM("03"),
	QIM("04"),
	UQLAT("05"),
	UQSUP("06"),
	UQMED("07"),
	UQINF("08"),
	RRA("09"),
	RC("10"),
	PA("11");
	
	private final String valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioLocalizacaoMamografia(final String valor) {
		this.valor = valor;
	}

	@Override
	public String getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case DEFAULT:
				return "Default";
			case QSL:
				return "QSL(Quadrante superior lateral)";	
			case QIL:
				return "QIL(Quadrante inferior lateral)";
			case QSM:
				return "QSM(Quadrante superior medial)";				
			case QIM:
				return "QIM(Quadrante inferior medial)";	
			case UQLAT:
				return "UQlat(União dos quadrantes laterais)";
			case UQSUP:
				return "UQsup(União dos quadrantes superiores)";
			case UQMED:
				return "UQmed(União dos quadrantes mediais)";	
			case UQINF:
				return "UQinf(União dos quadrantes inferiores)";
			case RRA:
				return "RRA(Região retroareolar)";
			case RC:
				return "RC(Região central (união de todos os quadrantes))";	
			case PA:
				return "PA(Prolongamento axilar)";
			default:
				return "";
		}
	}

	public static DominioLocalizacaoMamografia getDominioPorCodigo(String codigo){
		for (DominioLocalizacaoMamografia dominio : DominioLocalizacaoMamografia.values()) {
			if (codigo.equals(dominio.getCodigo())) {
				return dominio;
			}
		}
		return null;
	}
}
