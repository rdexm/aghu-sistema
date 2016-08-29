package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEscalaGlasgowAberturaOcular implements Dominio {

	NAO_ABRE(1),
	ESTIMULO_DOLOROSO(2),
	COMANDO_VERBAL(3),
	ESPONTANEA(4),
	;
	
	private int value;
	
	private DominioEscalaGlasgowAberturaOcular(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_ABRE:
			return "Não abre";
		case ESTIMULO_DOLOROSO:
			return "Estímulo doloroso";
		case COMANDO_VERBAL:
			return "Comando verbal";
		case ESPONTANEA:
			return "Espontânea";
		default:
			return "";
		}
	}

}