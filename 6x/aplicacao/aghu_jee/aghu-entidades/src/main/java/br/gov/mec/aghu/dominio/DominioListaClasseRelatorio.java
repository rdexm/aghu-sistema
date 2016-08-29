package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;



public enum DominioListaClasseRelatorio implements DominioString {
	
	BIOQUÍMICA("RelMapaTrabalhoBioqController"),
	BIOQUÍMICA_SORO("RelMapaTrabalhoSoroController"),
	EXAME_PARASITOLÓGICO_DE_FEZES("RelMapaTrabalhoEPFController"),
	UROCULTURA("RelMapaTrabalhoUroController"),
	BIOEQUIVALÊNCIA("RelMapaTrabBioEquController"),
	CD4("RelMapaTrabHemoCD4Controller"),
	HEMOCULTURA("RelMapaTrabalhoHemoController"),
	TRABALHO_HEMATOLOGIA("RelMapaTrabalhoHemaController")
	;
	
	private String value;
	
	private DominioListaClasseRelatorio(String nome) {
		this.value = nome;
	}
	
	@Override
	public String getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		return this.name(); //toString();	
	}

}