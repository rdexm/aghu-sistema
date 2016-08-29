package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLinfonodosAxilaresMamografia implements Dominio {
	
	/*1 – Normal
	5 - Não Visibilizado*/
	
	NORMAL(1),
	NAO_VISIBILIZADO(5);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioLinfonodosAxilaresMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case NORMAL:
				return "Normal";	
			case NAO_VISIBILIZADO:
				return "Não Visibilizado";
			default:
				return "";
		}
	}
	
	public static DominioLinfonodosAxilaresMamografia getDominioPorCodigo(int codigo) {
		for (DominioLinfonodosAxilaresMamografia dominio : DominioLinfonodosAxilaresMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return null;
	}		
}
