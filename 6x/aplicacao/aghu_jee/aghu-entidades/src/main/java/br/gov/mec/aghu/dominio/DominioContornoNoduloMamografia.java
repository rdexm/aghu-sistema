package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioContornoNoduloMamografia implements Dominio {
	
	/*0 – Default (default)
	1 – Regular
	2 – Lobulado
	3 – Irregular
	4 – Espiculado*/
	DEFAULT(0),
	REGULAR(1),
	LOBULADO(2),
	IRREGULAR(3),
	ESPICULADO(4);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioContornoNoduloMamografia(final int valor) {
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
			case REGULAR:
				return "Regular";	
			case LOBULADO:
				return "Lobulado";
			case IRREGULAR:
				return "Irregular";				
			case ESPICULADO:
				return "Espiculado";				
			default:
				return "";
		}
	}
	
	public static DominioContornoNoduloMamografia getDominioPorCodigo(int codigo){
		for (DominioContornoNoduloMamografia dominio : DominioContornoNoduloMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioContornoNoduloMamografia.DEFAULT;
	}
}
