package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSigTipoAlerta implements Dominio{
	
	/**
	 * Quantidade ajustada
	 */
	QA,
		
	/**
	 * Calculo somente para rateio
	*/
	CR,
	
	/**
	 * Centro de custo sem objetos de custos ativos
	 */
	NC,
	
	/**
	 * Cliente sem peso na coleta de direcionadores 
	 */
	CP
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case QA:
			return "Quantidade ajustada";
		case CR:
			return "Calculo somente para rateio";
		case NC:
			return "Centro de custo sem objetos de custos ativos";
		case CP:
			return "Cliente sem peso na coleta de direcionadores";
		default:
			return "";
		}
		
	}
}
