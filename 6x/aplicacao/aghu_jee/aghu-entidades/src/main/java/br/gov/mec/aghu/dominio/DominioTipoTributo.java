package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoTributo implements Dominio {
	

	/**
	 * Federal
	 */
	F,
	
	/**
	 * Municipal
	 */
	M,
	
	/**
	 * Previdenciário
	 */
	P,
	
	/**
	 * Contribuições
	 */
	C,
	
	/**
	 * Retenções Folha
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Federal";
		case M:
			return "Municipal";
		case P:
			return "Previdenciário";
		case C:
			return "Contribuições";
		case R:
			return "Retenções Folha";
		default:
			return "";
		}
	}
	
	public String getSigla(){
		switch (this) {
		case F:
			return "F";
		case M:
			return "M";
		case P:
			return "P";
		case C:
			return "C";
		case R:
			return "R";
		default:
			return "";
		}
	}
	
	public static DominioTipoTributo getInstance(String valor){
		if (valor != null && valor.equals("F")){
			return DominioTipoTributo.F;
		}
		if (valor != null && valor.equals("M")){
			return DominioTipoTributo.M;
		}
		if (valor != null && valor.equals("P")){
			return DominioTipoTributo.P;
		}
		if (valor != null && valor.equals("C")){
			return DominioTipoTributo.C;
		}
		if (valor != null && valor.equals("R")){
			return DominioTipoTributo.R;
		}
		return null;
		
	}
}