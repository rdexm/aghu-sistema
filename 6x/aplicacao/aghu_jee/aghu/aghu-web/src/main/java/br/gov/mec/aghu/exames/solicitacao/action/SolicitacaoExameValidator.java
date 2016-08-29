package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class SolicitacaoExameValidator extends ISECamposObrigatoriosValidator {
	

	private static final Log LOG = LogFactory.getLog(SolicitacaoExameValidator.class);

	private SolicitacaoExameVO solicitacaoExameVO;
	
	public SolicitacaoExameValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	public SolicitacaoExameValidator(SolicitacaoExameVO solicitacaoExameVO, ResourceBundle messages) {
		super(null, messages);
		this.solicitacaoExameVO = solicitacaoExameVO;
	}
	
	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		
		boolean validou = true;
		
		SolicitacaoExameVO solicitacaoExameVOAvaliar;
		if(getItemSolicitacaoExameVO() != null) {
			solicitacaoExameVOAvaliar = getItemSolicitacaoExameVO().getSolicitacaoExameVO();
		} else {
			solicitacaoExameVOAvaliar = solicitacaoExameVO;
		}
		
		if(solicitacaoExameVOAvaliar.getUnidadeFuncional() == null) {
			statusMessagesAddToControlFromResourceBundle("sbUnidadeFuncional",Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_UNIDADE_FUNCIONAL_SOLICITACAO_EXAME"));
			validou = false;
		}
		
		if (solicitacaoExameVOAvaliar.getOrigem() != DominioOrigemAtendimento.X && solicitacaoExameVOAvaliar.getResponsavel() == null) {
			statusMessagesAddToControlFromResourceBundle("sbResponsavel", Severity.ERROR, "CAMPO_OBRIGATORIO",
					getMessagesBundle("LABEL_RESPONSAVEL_SOLICITACAO_EXAME"));
			validou = false;
		}
		
		return validou;
	}

}
