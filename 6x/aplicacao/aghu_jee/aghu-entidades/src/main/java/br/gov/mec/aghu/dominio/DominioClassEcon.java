package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioClassEcon implements Dominio {
	
	LTDA(null,"Ltda","Ltda"),
	SA(null,"SA","Soc. Anônima"),
	ME(null,"ME","Micro Empresa"),
	FI(null,"FI","Firma Individual");
	
	private String highValue;
	private String abbreviation;
	private String meaning;
	
	private DominioClassEcon(String highValue, String abbreviation, String meaning){
		this.highValue = highValue;
		this.abbreviation = abbreviation;
		this.meaning = meaning;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public String getHighValue() {
		return highValue;
	}

	public void setHighValue(String highValue) {
		this.highValue = highValue;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	@Override
	public String getDescricao() {
		return abbreviation;
	}

}
