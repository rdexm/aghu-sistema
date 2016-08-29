package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGrupoCFOP implements Dominio {
	
	/**
	 * ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DO ESTADO
	 */
	ENTRADA_AQUISICOES_SERVICOS_ESTADO(1000),
	
	/**
	 * ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DE OUTROS ESTADOS
	 */
	ENTRADA_AQUISICOES_SERVICOS_OUTROS_ESTADOS(2000),
	
	/**
	 * ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DO EXTERIOR
	 */
	ENTRADA_AQUISICOES_SERVICOS_EXTERIOR(3000),
	
	/**
	 * SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA O ESTADO
	 */
	SAIDAS_PRESTACOES_SERVICOS_PARA_ESTADO(5000),
	
	/**
	 * SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA OUTROS ESTADOS
	 */
	SAIDAS_PRESTACOES_SERVICOS_PARA_OUTROS_ESTADOS(6000),
	
	/**
	 * SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA O EXTERIOR
	 */
	SAIDAS_PRESTACOES_SERVICOS_PARA_EXTERIOR(7000),
	;

	private int value;

	private DominioGrupoCFOP(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		
		case ENTRADA_AQUISICOES_SERVICOS_ESTADO:
			return "ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DO ESTADO";
		
		case ENTRADA_AQUISICOES_SERVICOS_OUTROS_ESTADOS:
			return "ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DE OUTROS ESTADOS";
			
		case ENTRADA_AQUISICOES_SERVICOS_EXTERIOR:
			return "ENTRADA E/OU AQUISIÇÕES DE SERVIÇOS DO EXTERIOR";
		
		case SAIDAS_PRESTACOES_SERVICOS_PARA_ESTADO:
			return "SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA O ESTADO";
		
		case SAIDAS_PRESTACOES_SERVICOS_PARA_OUTROS_ESTADOS:
			return "SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA OUTROS ESTADOS";
			
		case SAIDAS_PRESTACOES_SERVICOS_PARA_EXTERIOR:
			return "SAÍDAS OU PRESTAÇÕES DE SERVIÇOS PARA O EXTERIOR";
		
		default:
			return "";
		}
	}
	
	
	public static DominioGrupoCFOP getDominioPorCodigo(int codigo){
		for (DominioGrupoCFOP dominio : DominioGrupoCFOP.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return null;
	}	

}
