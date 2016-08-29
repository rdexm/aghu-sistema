package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDeteccaoLesao implements Dominio {
	
	EXAME_CLINICO_MAMA(1),
	
	IMAGEM(2);
	
	private Integer value;
	
	private DominioDeteccaoLesao(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case EXAME_CLINICO_MAMA: 
				return "Exame Clínico Mama";
			case IMAGEM:
				return "Imagem";
			default:
				return "";
		}
	}
	
	public static DominioDeteccaoLesao getInstance(Integer value) {
		switch (value) {
		case 1:
			return EXAME_CLINICO_MAMA;
		case 2:
			return IMAGEM;
		default:
			return null;
		}
	}
}
