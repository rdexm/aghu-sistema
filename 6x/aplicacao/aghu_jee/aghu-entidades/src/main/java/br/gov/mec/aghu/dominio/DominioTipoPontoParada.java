package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoPontoParada implements Dominio{
	/**
	 *	CH - Chefia
	 *  CP – Compra
	 *  EN – Engenharia
	 *  LI – Licitação
	 *  PL – Planejamento
	 *  PT – Parecer Técnico
	 *  SL – Solicitante	
	 *  GP - Gppg
	 *  OR - Orcamento
	 *  CO - Comprador Orcamento
	 */
	CH,
	CP,
	EN,
	LI,
	PL,
	PT,
	SL,
	GP,
	OC,
	CO;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CH:
			return "Chefia";
		case CP:
			return "Comprador";
		case EN:
			return "Engenharia";
		case LI:
			return "Licitação";
		case PL:
			return "Planejamento";
		case PT:
			return "Parecer Técnico";
		case SL:
			return "Solicitante";
		case GP:
			return "Gppg";
		case OC:
			return "Orcamento";
		case CO:
			return "Comprador Orcamento";
		default:
			return "";
		}
	}

}
