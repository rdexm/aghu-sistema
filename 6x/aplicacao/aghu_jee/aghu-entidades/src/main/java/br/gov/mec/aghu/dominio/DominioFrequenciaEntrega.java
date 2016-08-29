package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Dominio que indica a frequencia de entregas de compras/serviços
 * 
 */
public enum DominioFrequenciaEntrega implements Dominio {
	TOTAL(0),
	DIARIA(1),
	SEMANAL(2),
	MENSAL(3),
	TRIMESTRAL(4),
	SEMESTRAL(5),
	ANUAL(6);
	
	private int value;
	
	private DominioFrequenciaEntrega(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {

		switch (this) {
			case TOTAL:
				return "Total";
			case DIARIA:
				return "Diária";
			case SEMANAL:
				return "Semanal";
			case MENSAL:
				return "Mensal";
			case TRIMESTRAL:
				return "Trimestral";
			case SEMESTRAL:
				return "Semestral";
			case ANUAL:
				return "Anual";
			default:
				return "";
		}
	}
}
