package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaIntervColetaValidator extends ISECamposObrigatoriosValidator {

	
	private static final Log LOG = LogFactory.getLog(AbaIntervColetaValidator.class);

	public AbaIntervColetaValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	
	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		boolean validou = true;
		if(getItemSolicitacaoExameVO().getTmpIntervaloColeta() == null) {
			statusMessagesAddToControlFromResourceBundle(getSbConvenioId(),Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_INTERVALO_COLETA_CADASTRADO"));
			validou = false;
		}
		return validou;
	}
	
	protected String getSbConvenioId() {
		return "sbConvenio";
	}
}
