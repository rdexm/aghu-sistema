package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioSituacaoRequisicao implements DominioString {
	
	CONCLUIDA("CONCLUIDA"),
	INCOMPATIVEL("INCOMPATIVEL"),
	COMPATIVEL("COMPATIVEL"),
	CANCELADA("CANCELADA"),
	AUTORIZADA("AUTORIZADA"),
	NAO_AUTORIZADA("NAO_AUTORIZADA"),
	TODAS("TODAS"),
	EM_ANDAMENTO("EM_ANDAMENTO"),
	EM_AUTORIZACAO_01("EM_AUTORIZACAO_01"),
	EM_AUTORIZACAO_02("EM_AUTORIZACAO_02"),
	EM_AUTORIZACAO_03("EM_AUTORIZACAO_03"),
	EM_AUTORIZACAO_04("EM_AUTORIZACAO_04"),
	EM_ORCAMENTO_01("EM_ORCAMENTO_01"),
	PREPARAR_OPM("PREPARAR_OPM");

	private String valor;
	
	private DominioSituacaoRequisicao(String valor) {
		this.valor = valor;
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case INCOMPATIVEL:
			return "Incompatível";
		case COMPATIVEL:
			return "Compatível";
		case CONCLUIDA:
			return "Concluída";
		case CANCELADA:
			return "Cancelada";
		case AUTORIZADA:
			return "Autorizada";
		case NAO_AUTORIZADA:
			return "Não Autorizada";
		case TODAS:
			return "Todas";
		case EM_ANDAMENTO:
			return "Em Andamento";
		case EM_AUTORIZACAO_01:
			return "Em Autorização 01";
		case EM_AUTORIZACAO_02:
			return "Em Autorização 02";
		case EM_AUTORIZACAO_03:
			return "Em Autorização 03";
		case EM_AUTORIZACAO_04:
			return "Em Autorização 04";
		case EM_ORCAMENTO_01:
			return "Em Orçamento 01";
		case PREPARAR_OPM:
			return "Preparar Opmes";
		default:
			return "";
		}
	}
	
	
	public static DominioSituacaoRequisicao getInstance(String codigo) {
		if("TODAS".equals(codigo)) {
			return TODAS;
		} else if ("ANDAMENTO".equals(codigo)) {
			return EM_ANDAMENTO;
			
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


	@Override
	public String getCodigo() {
		return valor;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
}
