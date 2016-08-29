package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioStatusAghControleExecucao implements Dominio {
	/**
	 * Execução concluída com sucesso
	 */
	CONCLUIDO,
	
	/**
	 * Executando operação 
	 */
	EXECUTANDO,
	
	/**
	 * Execução concluída com erro
	 */
	CONCLUIDO_COM_ERRO;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {		
			case EXECUTANDO: 
				return "Executando";			
			case CONCLUIDO: 
				return "Concluído";
			case CONCLUIDO_COM_ERRO: 
				return "Concluído com erro";
			default: 
				return "";
		}
	}
}