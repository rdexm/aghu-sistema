package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoManipulacao implements Dominio {
	
	/**
	 * SEM MANIPULACAO
	 */
	MSE, 
	/**
	 * NÃO REVISADA
	 */
	MNR, 
	/**
	 * REJEITADA FAMARCIA
	 */
	MRE, 
	/**
	 * APTA PARA PREPARO
	 */
	MAP, 
	/**
	 * RESERVANDO INSUMOS
	 */
	MRI, 
	/**
	 * AGUARDANDO AUTORIZACAO
	 */
	MAA, 
	/**
	 * AGUARDANDO MANIPULACAO
	 */
	MAM, 
	/**
	 * PREPARO CONCLUÍDO
	 */
	MPC,
	/**
	 * ENTREGUE
	 */
	MPE,
	/**
	 * DEVOLVIDA
	 */
	MDE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case MSE:
			return "Sem manipulação";
		case MNR:
			return "Não revisada";
		case MRE:
			return "Rejeitada famárcia";
		case MAP:
			return "Apta para preparo";
		case MRI:
			return "Reservando insumos ";
		case MAA:
			return "Aguardando autorização";
		case MAM:
			return "Aguardando manipulação";
		case MPC:
			return "Preparo concluído";		
		case MPE:
			return "Entregue";		
		case MDE:
			return "Devolvida";			
		default:
			return "";
		}
	}
}
