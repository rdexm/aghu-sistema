package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaTipoTransporteValidator extends ISECamposObrigatoriosValidator {
	

	private static final Log LOG = LogFactory.getLog(AbaTipoTransporteValidator.class);

	public AbaTipoTransporteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}

	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		boolean validou = true;
		if(getItemSolicitacaoExameVO().getTipoTransporte() == null) {
			statusMessagesAddToControlFromResourceBundle(getTipoTransporteId(),Severity.ERROR, "CAMPO_OBRIGATORIO_TRANSPORTE", getMessagesBundle("LABEL_TRANSPORTE_PACIENTE"));
			validou = false;
		}
		if(getItemSolicitacaoExameVO().getOxigenioTransporte() == null) {
			statusMessagesAddToControlFromResourceBundle(getUsoO2Id(),Severity.ERROR, "CAMPO_OBRIGATORIO_TRANSPORTE", getMessagesBundle("LABEL_OXIGENIO_TRANSPORTE"));
			validou = false;
		}
		return validou;
	}
	
	protected String getTipoTransporteId() {
		return "tipoTransporte";
	}
	protected String getUsoO2Id() {
		return "usoO2";
	}
}
