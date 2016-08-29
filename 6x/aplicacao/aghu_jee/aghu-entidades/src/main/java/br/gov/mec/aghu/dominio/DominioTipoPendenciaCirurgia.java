package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoPendenciaCirurgia implements Dominio {
	
	/**
	 * Produção
	 */
	PRODUCAO,

	/**
	 * Nota de Consumo
	 */
	NOTA_CONSUMO, 
	
	/**
	 * Descrição Cirúrgica / PDT
	 */
	DESC_CIR_PDT;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
			case PRODUCAO:
				return "Produção";
			case NOTA_CONSUMO:
				return "Nota de Consumo";
			case DESC_CIR_PDT:
				return "Descrição Cirúrgica / PDT";			
			default:
				return "";
		}
	}
	
}
