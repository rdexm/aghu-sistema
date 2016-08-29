
package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioWorkflowOPMEsCodigoTemplateEtapa implements Dominio {
	
	TODAS("TODAS","(Todas)"),
	ANDAMENTO("ANDAMENTO","(Em Andamento)"),
	INCOMPATIVEL("INCOMPATIVEL","Procedimento Incompatível"),
	COMPATIVEL("COMPATIVEL", "Procedimento Compatível"),
	EM_AUTORIZACAO_01("EM_AUTORIZACAO_01", "Avaliação Assistencial"),
	EM_AUTORIZACAO_02("EM_AUTORIZACAO_02", "Avaliação Administrativa Inicial"),
	EM_ORCAMENTO_01("EM_ORCAMENTO_01", "Em Orçamento Materiais"),
	EM_AUTORIZACAO_03("EM_AUTORIZACAO_03", "Avaliação Administrativa Final"),
	EM_AUTORIZACAO_04("EM_AUTORIZACAO_04", "Avaliação da Administração Central"),
	AUTORIZADA("AUTORIZADA", "Requisição Autorizada"),
	NAO_AUTORIZADA("NAO_AUTORIZADA", "Requisição Não Autorizada"),
	CONCLUIDA("CONCLUIDA", "Requisição Concluída"),
	CANCELADA("CANCELADA", "Requisição Cancelada"),
	PREPARAR_OPM("PREPARAR_OPM", "Preparar OPMs");
		
	private String codigo;
	private String descricao;

	private DominioWorkflowOPMEsCodigoTemplateEtapa(String codigo, String descricao) {
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}
	
	@Override
	public String toString() {
		return codigo;
	}
	
	public static DominioWorkflowOPMEsCodigoTemplateEtapa getInstance(String codigo) {
		if("TODAS".equals(codigo)) {
			return TODAS;
		} else if ("ANDAMENTO".equals(codigo)) {
			return ANDAMENTO;
			
		} else if ("INCOMPATIVEL".equals(codigo)) {
			return INCOMPATIVEL;
			
		} else if ("COMPATIVEL".equals(codigo)) {
			return COMPATIVEL;
			
		}  else if ("EM_AUTORIZACAO_01".equals(codigo)) {
			return EM_AUTORIZACAO_01;
			
		} else if ("EM_AUTORIZACAO_02".equals(codigo)) {
			return EM_AUTORIZACAO_02;
			
		} else if ("EM_ORCAMENTO_01".equals(codigo)) {
			return EM_ORCAMENTO_01;
			
		}  else if ("EM_AUTORIZACAO_03".equals(codigo)) {
			return EM_AUTORIZACAO_03;
			
		} else if ("EM_AUTORIZACAO_04".equals(codigo)) {
			return EM_AUTORIZACAO_04;
			
		}  else if ("AUTORIZADA".equals(codigo)) {
			return AUTORIZADA;
			
		} else if ("NAO_AUTORIZADA".equals(codigo)) {
			return NAO_AUTORIZADA;
			
		} else if ("CONCLUIDA".equals(codigo)) {
			return CONCLUIDA;
			
		} else if ("CANCELADA".equals(codigo)) {
			return CANCELADA;
		
		} else if ("PREPARAR_OPM".equals(codigo)) {
			return PREPARAR_OPM;
		}
		
		return null;
	}
}
