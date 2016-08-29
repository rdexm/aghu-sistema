package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioIrritabilidade implements DominioString {
	SEM_RESPOSTA("0"),
	ALGUM_MOVIMENTO("1"),
	CHORO("2");
	
	private String value;
	
	private DominioIrritabilidade(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		return this.value;
	}
	
	public String getDescricaoCompleta() {

		switch (this) {
			case SEM_RESPOSTA:
				return "Sem Resposta";
			case ALGUM_MOVIMENTO:
				return "Algum Movimento";
			case CHORO:
				return "Choro";
			default:
				return "";
		}
	}
}
