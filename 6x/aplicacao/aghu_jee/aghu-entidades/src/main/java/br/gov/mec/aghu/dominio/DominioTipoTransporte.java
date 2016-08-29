package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Tipo Transporte da entidade AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 *
 */
public enum DominioTipoTransporte implements Dominio {
	D, C, L, M, A, O
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	
	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Deambulando";
		case C:
			return "Cadeira";
		case L:
			return "Leito";
		case M:
			return "Maca";
		case A:
			return "Cama";
		case O:
			return "Colo";
		default:
			return "";
		}
	}

}
