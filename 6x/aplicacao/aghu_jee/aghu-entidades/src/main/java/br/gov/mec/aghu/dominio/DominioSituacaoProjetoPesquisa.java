package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioSituacaoProjetoPesquisa implements DominioString {
	
	/**
	 * Aprovado.
	 */
	APROVADO("01"),
	
	/**
	 * Arquivado.
	 */
	ARQUIVADO("02"),
	
	/**
	 * Diligência.
	 */
	DILIGENCIA("03"),
	
	/**
	 * Diligência comparecer GPPG.
	 */
	DILIGENCIA_COMPARECER_GPPG("04"),
	
	/**
	 * Falta Material.
	 */
	FALTA_MATERIAL("05"),
	
	/**
	 * Não Aprovado.
	 */
	NAO_APROVADO("06"),
	
	/**
	 * Encerrado.
	 */
	ENCERRADO("07"),
	
	/**
	 * Aprovado Pend Origem.
	 */
	APROVADO_PEND_ORIGEM("08"),
	
	/**
	 * Não Executado.
	 */
	NAO_EXECUTADO("09"),
	
	/**
	 * Encerrado Prazo.
	 */
	ENCERRADO_PRAZO("10"),
	
	/**
	 * Reaprovado.
	 */
	REAPROVADO("11"),
	
	/**
	 * Aprovado resolução 340/2004.
	 */
	APROVADO_RES_340_2004("12"),
	
	/**
	 * Pré-Análise.
	 */
	PRE_ANALISE("13"),
	
	/**
	 * Aprovado Guarda CONEP.
	 */
	APROVADO_GUARDA_CONEP("14"),
	
	/**
	 * Aprovado resolução 346/2005.
	 */
	APROVADO_RES_346_2005("99")

	;
	
	private String value;
	
	private DominioSituacaoProjetoPesquisa(String value) {
		this.value = value;
	}

	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APROVADO:
			return "Aprovado";
		case ARQUIVADO:
			return "Arquivado";
		case DILIGENCIA:
			return "Diligência";
		case DILIGENCIA_COMPARECER_GPPG:
			return "Diligência comparecer GPPG";
		case FALTA_MATERIAL:
			return "Falta Material";
		case NAO_APROVADO:
			return "Não Aprovado";
		case ENCERRADO:
			return "Encerrado";
		case APROVADO_PEND_ORIGEM:
			return "Aprovado Pend Origem";
		case NAO_EXECUTADO:
			return "Não Executado";
		case ENCERRADO_PRAZO:
			return "Encerrado Prazo";
		case REAPROVADO:
			return "Reaprovado";
		case APROVADO_RES_340_2004:
			return "Aprovado resolução 340/2004";
		case PRE_ANALISE:
			return "Pré-Análise";
		case APROVADO_GUARDA_CONEP:
			return "Aprovado Guarda CONEP";
		case APROVADO_RES_346_2005:
			return "Aprovado resolução 346/2005";
		default:
			return "";
		}
	}
	
}
