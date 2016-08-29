package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioLimiteNoduloMamografia implements Dominio {
	
	/*0 – Default (default)
	1 – Definido
	2 - Parcialmente Definido
	3 - Pouco Definido*/
	
	DEFAULT(0),
	DEFINIDO(1),
	PARCIALMENTE_DEFINIDO(2),
	POUCO_DEFINIDO(3);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioLimiteNoduloMamografia(final int valor) {
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
			case DEFINIDO:
				return "Definido";	
			case PARCIALMENTE_DEFINIDO:
				return "Parcialmente Definido";
			case POUCO_DEFINIDO:
				return "Pouco Definido";							
			default:
				return "";
		}
	}

	public static DominioLimiteNoduloMamografia getDominioPorCodigo(int codigo) {
		for (DominioLimiteNoduloMamografia dominio : DominioLimiteNoduloMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioLimiteNoduloMamografia.DEFAULT;
	}	
}
