package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioMotivoEncerramentoNotificacao implements Dominio {	

	/**
	 * 1 Curado
	 */
	CURADO(1),
	
	/**
	 * 2 Alta
	 */
	ALTA(2),
	
	/**
	 * 3 Óbito
	 */
	OBITO(3),
	
	/**
	 * 4 Mudança
	 */
	MUDANCA(4);
	
	private Integer value;
		
	private DominioMotivoEncerramentoNotificacao(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case CURADO:
			return "Curado";
		case ALTA:
			return "Alta";
		case OBITO:
			return "Óbito";			
		case MUDANCA:
			return "Mudança";
		default:
			return "";
		}
	}

}
