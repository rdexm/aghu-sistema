package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoJustificativa implements Dominio{
	/**
	 * Empenho
	 */
	E,
	
	R, 
	
	BLQ,
	/**
	 * INTERCORRÊNCIAS
	 */
	INT,
	/**
	 * BLOQUEIO DE SALA
	 */
	BLO,

	/**
	 * CANCELAMENTO DE AGENDA
	 */
	CAN,
	/**
	 * SUSPENDER SESSÃO
	 */
	SUS;
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Empenho";
		case INT:
			return "Intercorrências";
		case BLO:
			return "Bloqueio de Sala";
		case CAN:
			return "Cancelamento de Agenda";
		case SUS:
			return "Suspender Sessão";
		default:
			return "";
		}
	}

	public static String getDescricao(String tipoJustificativa) {
		if(tipoJustificativa.equals(E.getDescricao())){
			return E.getDescricao();
		}else if(tipoJustificativa.equals(BLQ.getDescricao())){
			return BLQ.getDescricao();
		}else if(tipoJustificativa.equals(INT.getDescricao())){
			return INT.getDescricao();
		}else if(tipoJustificativa.equals(BLO.getDescricao())){
			return BLO.getDescricao();
		}else if(tipoJustificativa.equals(CAN.getDescricao())){
			return CAN.getDescricao();
		}else if(tipoJustificativa.equals(SUS.getDescricao())){
			return SUS.getDescricao();
		}
		return null;
	}

}
