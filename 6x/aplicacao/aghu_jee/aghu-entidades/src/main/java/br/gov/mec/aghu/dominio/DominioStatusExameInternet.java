package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Classe de Domínio para controle status do exame na Internet
 * @author dcastro
 *
 */
public enum DominioStatusExameInternet implements Dominio {

	/**
	 * NOVO
	 */
	NO,
	
	/**
	 * Exame liberado
	 */
	LI,

	/**
	 * Exame na fila de geração
	 */
	FG,
	
	/**
	 * Exame na fila de envio
	 */
	FE,
	
	/**
	 * Envio Concluído
	 */
	EC,

	/**
	 * Reenvio
	 */
	RE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case NO:
			return "Novo";		
			case LI:
				return "Liberado";
			case FG:
				return "Fila Geração";
			case FE:
				return "Fila Envio";
			case EC:
				return "Envio Concluído";
			case RE:
				return "Reenvio";				
			default:
				return "";
		}
	}
}
