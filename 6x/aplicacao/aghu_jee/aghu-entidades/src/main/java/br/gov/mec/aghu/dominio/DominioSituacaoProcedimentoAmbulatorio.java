package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica os valores possível para procedimentos ambulatoriais.
 */
public enum DominioSituacaoProcedimentoAmbulatorio implements DominioString {
	/**
	 * Aberto
	 */
	ABERTO("A"),
	/**
	 * Cancelado
	 */
	CANCELADO("C"),
	/**
	 * Apresentado
	 */
	APRESENTADO("P"),
	/**
	 * Encerrado
	 */
	ENCERRADO("E"),
	/**
	 * Transferido para APAC
	 */
	TRANSFERIDO("T"),
	/**
	 * Consultas grupos
	 */
	CONSULTAS_GRUPO("G"),	
	/**
	 * Transferido APAP
	 */
	TRANSFERIDO_APAP("5");

	private String value;

	private DominioSituacaoProcedimentoAmbulatorio(String value) {
		this.value = value;
	}

	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ABERTO: 		   return "Aberto";
		case CANCELADO: 	   return "Cancelado";
		case APRESENTADO: 	   return "Apresentado";
		case ENCERRADO: 	   return "Encerrado";
		case TRANSFERIDO: 	   return "Transferido";
		case CONSULTAS_GRUPO:  return "Consultas grupo";
		case TRANSFERIDO_APAP: return "Transf. APAP";
		default: 			  return "";
		}
	}

}
