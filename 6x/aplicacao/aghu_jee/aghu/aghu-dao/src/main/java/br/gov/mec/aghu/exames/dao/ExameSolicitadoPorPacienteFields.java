package br.gov.mec.aghu.exames.dao;

import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;

class ExameSolicitadoPorPacienteFields {
	
	private boolean historico;
	
	public ExameSolicitadoPorPacienteFields(boolean isBuscaHistorico) {
		historico = isBuscaHistorico;
	}
	
	
	public String obterSchema() {
		if (this.historico) {
			return "hist";
		} else {
			return "agh";
		}		
	}
	
	
	
	
	public String obterAelExames() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.AEL_EXAMES.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString();			
		}
	}

	public String obterSolicitacaoExame() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString();			
		}
	}


	public String obterUnidadeFuncional() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.UNIDADE_FUNCIONAL.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString();			
		}
	}

	public String obterAelUnfExecutaExames() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.AEL_UNF_EXECUTA_EXAMES.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString();			
		}
	}
	
	public String obterExtratoItemSolic() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.EXTRATO_ITEM_SOLIC.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString();			
		}
	}
	
	public String obterSituacaoItemSolicitacao() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString();			
		}
	}
	
	public String obterSoeSeq() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.SOE_SEQ.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.SOE_SEQ.toString();			
		}
	}

	public String obterSituacaoItemSolicitacaoCodigo() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString();			
		}
	}

	public String obterSeqp() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.SEQP.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.SEQP.toString();			
		}
	}
	
	public String obterTipoColeta() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.TIPO_COLETA.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString();			
		}
	}
	
	public String obterEtiologia() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.ETIOLOGIA.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.ETIOLOGIA.toString();			
		}
	}
	
	public String obterDthrProgramada() {
		if (this.historico) {
			return AelItemSolicExameHist.Fields.DTHR_PROGRAMADA.toString();
		} else {
			return AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString();			
		}
	}

	public String obterSolicitacaoAtendimento() {
		if (this.historico) {
			return AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString();
		} else {
			return AelSolicitacaoExames.Fields.ATENDIMENTO.toString();			
		}
	}
	
	public String obterSolicitacaoSeq() {
		if (this.historico) {
			return AelSolicitacaoExamesHist.Fields.SEQ.toString(); 
		} else {
			return AelSolicitacaoExames.Fields.SEQ.toString();			
		}
	}
	
}
