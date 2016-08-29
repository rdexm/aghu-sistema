package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica as situações que uma Licitação pode assumir
 * 
 * @author agerling
 * 
 */
public enum DominioSituacaoLicitacao implements Dominio {
	
	/**
	 * Em AF
	 */
	AF,
	
	/**
	 * Em Comissão de Licitação
	 */
	CL,
	
	/**
	 * Efetivada
	 */
	EF,
	
	/**
	 * Gerada
	 */
	GR,

	/**
	 * Julgada
	 */
	JU,
	
	/**
	 * Em Parecer Técnico
	 */
	PT;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AF:
			return "AF - Em AF";
		case CL:
			return "CL - Comissão de Licitação";
		case EF:
			return "EF - Efetivada";
		case GR:
			return "GR - Gerada";
		case JU:
			return "JU - Julgada";
		case PT:
			return "PT - Parecer Técnico";
		default:
			return "";
		}
	}
	
	public String getDescricaoParaHint() {
		switch (this) {
		case AF:
			return "Em AF";
		case CL:
			return "Comissão de Licitação";
		case EF:
			return "Efetivada";
		case GR:
			return "Gerada";
		case JU:
			return "Julgada";
		case PT:
			return "Parecer Técnico";	

		default:
			return "";
		}
	}

}
