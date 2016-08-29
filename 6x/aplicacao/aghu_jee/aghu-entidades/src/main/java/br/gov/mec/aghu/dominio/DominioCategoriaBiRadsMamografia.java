package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCategoriaBiRadsMamografia implements Dominio {
	
	/*1 – Categoria 0
	2 – Categoria 1
	3 – Categoria 2
	4 – Categoria 3
	5 – Categoria 4
	6 – Categoria 5
	7 – Categoria 6*/
	
	CATEGORIA_0(1),
	CATEGORIA_1(2),
	CATEGORIA_2(3),
	CATEGORIA_3(4),
	CATEGORIA_4(5),
	CATEGORIA_5(6),
	CATEGORIA_6(7);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioCategoriaBiRadsMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case CATEGORIA_0:
				return "Categoria 0";	
			case CATEGORIA_1:
				return "Categoria 1";
			case CATEGORIA_2:
				return "Categoria 2";				
			case CATEGORIA_3:
				return "Categoria 3";	
			case CATEGORIA_4:
				return "Categoria 4";
			case CATEGORIA_5:
				return "Categoria 5";
			case CATEGORIA_6:
				return "Categoria 6";	
			default:
				return "";
		}
	}
	
	public static DominioCategoriaBiRadsMamografia getDominioPorCodigo(int codigo) {
		for (DominioCategoriaBiRadsMamografia dominio : DominioCategoriaBiRadsMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioCategoriaBiRadsMamografia.CATEGORIA_0;		
	}
}
