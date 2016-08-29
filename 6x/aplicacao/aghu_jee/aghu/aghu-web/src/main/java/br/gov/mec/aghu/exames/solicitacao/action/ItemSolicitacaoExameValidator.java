package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class ItemSolicitacaoExameValidator extends ISECamposObrigatoriosValidator {

	

	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExameValidator.class);

	public ItemSolicitacaoExameValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		
		boolean validou = true;
		
		if(getItemSolicitacaoExameVO().getUnfExecutaExame() == null) {
			statusMessagesAddToControlFromResourceBundle(getSbExamesId(),Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_SB_EXAME"));
			validou = false;
		}

		if(getItemSolicitacaoExameVO().getDataProgramada() == null) {
			String idTela = getComboDataProgramadaId();
			if (getItemSolicitacaoExameVO().getCalendar()) {
				idTela = getDataProgramadaId();
			}
			
			statusMessagesAddToControlFromResourceBundle(idTela,Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_DATA_HORA_EXAME"));
			validou = false;
		}
		
		if(getItemSolicitacaoExameVO().getSituacaoCodigo() == null) {
			statusMessagesAddToControlFromResourceBundle(getComboSituacaoItemId(),Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_SITUACAO_EXAME"));
			validou = false;
		}
		
		return validou;
	}
	
	protected String getSbExamesId() {
		return "sbExames";
	}
	protected String getComboDataProgramadaId() {
		return "comboDataProgramada";
	}
	protected String getDataProgramadaId() {
		return "dataProgramada";
	}
	protected String getComboSituacaoItemId() {
		return "comboSituacaoItem";
	}

}
