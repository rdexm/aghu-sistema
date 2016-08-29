package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioMamaLesao implements Dominio {

	
	MAMA_DIREITA(1),
	
	MAMA_ESQUERDA(2);
	
	
	private Integer value;
	
	private DominioMamaLesao(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case MAMA_DIREITA: 
				return "Mama Direita";
			case MAMA_ESQUERDA:
				return "Mama Esquerda";
			default:
				return "";
		}
	}
	
	public static DominioMamaLesao getInstance(Integer value) {
		switch (value) {
		case 1:
			return MAMA_DIREITA;
		case 2:
			return MAMA_ESQUERDA;
		default:
			return null;
		}
	}
}
