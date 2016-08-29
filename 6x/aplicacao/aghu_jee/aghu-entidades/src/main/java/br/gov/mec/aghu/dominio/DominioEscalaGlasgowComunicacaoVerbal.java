package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEscalaGlasgowComunicacaoVerbal implements Dominio {

	NENHUMA(1),
	SOM_INCOMPREENSIVEL(2),
	PALAVRAS_NAO_APROPRIADAS(3),
	FALA_CONFUSA(4),
	FALA_NORMAL(5),
	;
	
	private int value;
	
	private DominioEscalaGlasgowComunicacaoVerbal(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NENHUMA:
			return "Nenhuma";
		case SOM_INCOMPREENSIVEL:
			return "Sons incompreensíveis";
		case PALAVRAS_NAO_APROPRIADAS:
			return "Palavras não apropriadas";
		case FALA_CONFUSA:
			return "Fala confusa";
		case FALA_NORMAL:
			return "Fala normal";
		default:
			return "";
		}
	}

}