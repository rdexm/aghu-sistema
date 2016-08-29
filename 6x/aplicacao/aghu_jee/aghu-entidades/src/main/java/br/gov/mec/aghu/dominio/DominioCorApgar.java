package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioCorApgar implements DominioString {
	PALIDO_CIANOTICO("0"),
	CIANOSE_EXTREMIDADE("1"),
	TODO_ROSADO("2");
	
	private String value;
	
	private DominioCorApgar(String value) {
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
			case PALIDO_CIANOTICO:
				return "Pálido Cianótico";
			case CIANOSE_EXTREMIDADE:
				return "Cianose Extremidade";
			case TODO_ROSADO:
				return "Todo Rosado";
			default:
				return "";
		}
	}
}
