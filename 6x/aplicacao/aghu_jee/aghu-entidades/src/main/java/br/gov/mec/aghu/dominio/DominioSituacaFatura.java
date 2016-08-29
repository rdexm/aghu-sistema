package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

//select * from sce_ref_codes where rv_domain = 'SIT_FATURA_CUM'
public enum DominioSituacaFatura implements Dominio {
	/**
	 * Gerado
	 */
	G,
	/**
	 * Pendente
	 */
	P,
	/**
	 * Cancelado
	 */
	C,
	/**
	 * Liberado
	 */
	L,
	/**
	 * Assinado
	 */
	A,
	/**
	 * Enviado
	 */
	E,
	/**
	 * Recebido
	 */
	R,
	/**
	 * Com NR
	 */
	N,
	/**
	 * Faturado
	 */
	F,
	/**
	 * Histórico
	 */
	H,
	/**
	 * Exclusão
	 */
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Gerado";
		case P:
			return "Pendente";
		case C:
			return "Cancelado";
		case L:
			return "Liberado";
		case A:
			return "Assinado";
		case E:
			return "Enviado";
		case R:
			return "Recebido";
		case N:
			return "Com NR";
		case F:
			return "Faturado";
		case H:
			return "Histórico";
		case X:
			return "Exclusão";
		default:
			return "";
		}
	}

}
