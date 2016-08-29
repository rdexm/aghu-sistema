package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;



public enum DominioCaracteristicaCentroCusto implements DominioString {
	AUTORIZAR_SS("AUTORIZAR SS"), GERAR_SC("GERAR SC"), GERAR_SS("GERAR SS"), AUTORIZAR_SC("AUTORIZAR SC"), CCUSTO_EXIGE_PROJETO("CCUSTO EXIGE PROJETO"), GERAR_RM(
			"GERAR RM"), NAO_SUGERIR_CC_APLICACAO_SC("NAO SUGERIR CC APLICACAO SC"),
			NAO_SUGERIR_CC_APLICACAO_SS("NAO SUGERIR CC APLICACAO SS"),
			NAO_SUGERIR_CC_APLICACAO_RM("NAO SUGERIR CC APLICACAO RM"),
			AUTORIZAR_GPPG("AUTORIZAR GPPG"), GERAR_GPPG("GERAR GPPG"),
			ALTERAR_RM_GPPG("ALTERAR RM GPPG"),PROTOCOLO_PAC("PROTOCOLO PAC"),
			PREENCHER_CC_SOLIC_SC("PREENCHER CC SOLIC SC"), PREENCHER_CC_APLIC_SC("PREENCHER CC APLIC SC"),
			PREENCHER_CC_SOLIC_SS("PREENCHER CC SOLIC SS"), PREENCHER_CC_APLIC_SS("PREENCHER CC APLIC SS"),
			PREENCHER_CC_SOLIC_RM("PREENCHER CC SOLIC RM"), PREENCHER_CC_APLIC_RM("PREENCHER CC APLIC RM"),
			MANTER_CC_DUPLIC_SC("MANTER CC DUPLIC SC"),MANTER_CC_DUPLIC_SS("MANTER CC DUPLIC SS"),
			GERAR_SC_MAT_ESTOCAVEL("GERAR SC MAT ESTOCAVEL");
	
	
	private final String descricao;
	private final String caracteristica;
	
	private DominioCaracteristicaCentroCusto(final String caracteristica) {
		this.caracteristica = caracteristica;
		this.descricao = null;
	}
	
	private DominioCaracteristicaCentroCusto(final String caracteristica, final String descricao) {
		this.descricao = descricao;
		this.caracteristica = caracteristica;
	}

	@Override
	public String getCodigo() {
		return this.caracteristica;
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}

}
