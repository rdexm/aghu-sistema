package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o local de coleta da classe AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 */
public enum DominioLocalColeta implements Dominio {
	/**
	 * Nenhum
	 */
	NENHUM,
	/**
	 * Urgente
	 */
	CTI,	
	/**
	 * Normal
	 */
	NORMAL;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NENHUM:
			return "Nenhum";
		case NORMAL:
			return "Coleta Normal";
		case CTI:
			return "Coleta CTI";
		default:
			return "";
		}
	}
}