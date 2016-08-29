package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTamanhoNoduloMamografia implements Dominio {
	
	
	/*0 – Default (default)
	1 - <10 mm
	2 - 11-20 mm
	3 - 21-50 mm
	4 - >=50 mm*/
	
	DEFAULT(0),
	MENOR_QUE_DEZ_MM(1),
	ONZE_A_VINTE_MM(2),
	VINTE_E_UM_A_CINQUENTA_MM(3),
	MAIOR_OU_IGUAL_50_MM(4);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioTamanhoNoduloMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case DEFAULT:
				return "Default";
			case MENOR_QUE_DEZ_MM:
				return "<10 mm";	
			case ONZE_A_VINTE_MM:
				return "11-20 mm";
			case VINTE_E_UM_A_CINQUENTA_MM:
				return "21-50 mm";				
			case MAIOR_OU_IGUAL_50_MM:
				return ">=50 mm";				
			default:
				return "";
		}
	}

	public static DominioTamanhoNoduloMamografia getDominioPorCodigo(int codigo) {
		for (DominioTamanhoNoduloMamografia dominio : DominioTamanhoNoduloMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioTamanhoNoduloMamografia.DEFAULT;
	}	
}
