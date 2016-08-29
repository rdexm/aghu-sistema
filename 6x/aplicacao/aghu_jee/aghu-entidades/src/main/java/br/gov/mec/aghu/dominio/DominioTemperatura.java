package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTemperatura implements Dominio {
	MAIOR_QUE_TRINTA_CINCO(0),
	ENTRE_TRINTA_CINCO(8),
	MENOR_QUE_TRINTA_CINCO(15);
	
	private int value;
	
	private DominioTemperatura(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MAIOR_QUE_TRINTA_CINCO:
				return "> 35,6º C";
			case ENTRE_TRINTA_CINCO:
				return "35 - 35,6º C";
			case MENOR_QUE_TRINTA_CINCO:
				return "< 35º C”";
			default:
				return "";
		}
	}
}
