package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioComposicaoMama implements Dominio {
	
	/*1 – Mama Densa (default)
	2 – Mama Adiposa
	3 – Mama Predominantemente Densa
	4 – Mama Predominantemente Adiposa*/
	
	MAMA_DENSA(1),
	MAMA_ADIPOSA(2),
	MAMA_PREDOMINANTEMENTE_DENSA(3),
	MAMA_PREDOMINANTEMENTE_ADIPOSA(4);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioComposicaoMama(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MAMA_DENSA:
				return "Mama Densa";	
			case MAMA_ADIPOSA:
				return "Mama Adiposa";
			case MAMA_PREDOMINANTEMENTE_DENSA:
				return "Mama Predominantemente Densa";
			case MAMA_PREDOMINANTEMENTE_ADIPOSA:
				return "Mama Predominantemente Adiposa";	
			default:
				return "";
		}
	}
	
	public static DominioComposicaoMama getDominioPorCodigo(int codigo) {
		for (DominioComposicaoMama dominio : DominioComposicaoMama.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return DominioComposicaoMama.MAMA_DENSA;
	}
}
