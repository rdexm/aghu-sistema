package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de coleta da classe AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 */
public enum DominioTipoColeta implements Dominio {
	/**
	 * Normal
	 */
	N,
	/**
	 * Urgente
	 */
	U
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Normal";
		case U:
			return "Urgente";
		default:
			return "";
		}
	}

	public static DominioTipoColeta getInstance(String valor) {
		if (valor!=null && valor.trim().equalsIgnoreCase("N")) {
			return DominioTipoColeta.N;
		} else {
			return DominioTipoColeta.U;
		}
	}
}