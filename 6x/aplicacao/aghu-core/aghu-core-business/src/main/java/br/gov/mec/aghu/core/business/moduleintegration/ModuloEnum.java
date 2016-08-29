package br.gov.mec.aghu.core.business.moduleintegration;



public enum ModuloEnum {
	PACIENTES("pacientes"), 
	PRESCRICAO_MEDICA("prescricaomedica"), 
	EXAMES("exames"), 
	FATURAMENTO("faturamento"), 
	CASCA("casca"), 
	REGISTRO_COLABORADOR("registrocolaborador"), 
	INTERNACAO("internacao"),
	FINANCEIRO("financeiro"),
	PATRIMONIO("patrimonio"),
	CONFIGURACAO("configuracao"), 
	CERTIFICACAO_DIGITAL("certificacaodigital"),
	AMBULATORIO("ambulatorio"),
	BANCO_SANGUE("bancosangue"),
	BLOCO_CIRURGICO("blococirurgico"),
	CHECAGEM_ELETRONICA("checagemEletronica"),
	CONTROLE_INFECCAO("controleinfeccao"),
	CONTROLE_PACIENTE("controlepaciente"),
	FARMACIA("farmacia"),
	INDICADORES("indicadores"),
	NUTRICAO("nutricao"),
	PERINATOLOGIA("perinatologia"),
	SICON("sicon"),
	CENTRO_CUSTO("centrocusto"),
	COMPRAS("compras"),
	ESTOQUE("estoque"),
	EXAMES_LAUDOS("exameslaudos"),
	PRESCRICAO_ENFERMAGEM("prescricaoEnfermagem"),
	PROCEDIMENTO_TERAPEUTICO("procedterapeuticos"),
	PROTOCOLO("protocolo"),
	SUPRIMENTOS("suprimentos"),
	SIG_CUSTOS_ATIVIDADE("sigcustosatividade"),
	TRANSPLANTE("transplante"),
	ENGENHARIA("engenharia"),
	INVESTIMENTOS("investimentos")
	;
	
	private final String descricao; 
	
	private ModuloEnum(String descricao) {

		this.descricao = descricao;
	}

	public String getCodigo() {

		return this.descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}
	
	public static ModuloEnum obterPorValorBanco(String valor) {
		for(ModuloEnum item : values()) {
			if(item.getCodigo().equals(valor)) {
				return item;
			}
		}
		
		return null;
	}

}