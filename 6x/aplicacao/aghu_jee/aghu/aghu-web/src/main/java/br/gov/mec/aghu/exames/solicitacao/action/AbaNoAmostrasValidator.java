package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaNoAmostrasValidator extends ISECamposObrigatoriosValidator {

	

	private static final Log LOG = LogFactory.getLog(AbaNoAmostrasValidator.class);

	public AbaNoAmostrasValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	
	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		boolean validou = true;
		if(getNumeroAmostraValue() == null) {
			statusMessagesAddToControlFromResourceBundle(getNumeroAmostraId(),Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_NUMERO_AMOSTRA"));
			validou = false;
		} else {
			validou = verificaIntervalo(getNumeroAmostraValue(), getNumeroAmostraId());
		}
		
		if(getNumeroAmostraValue() == 0){
			setNumeroAmostraValue(1);
		}
		
		if(getItemSolicitacaoExameVO().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getUnidTempoColetaAmostras() == DominioUnidTempo.H 
				&& getIntervaloHorasValue() == null && getNumeroAmostraValue() > 1) {
				//String mensagem = getMessagesBundle("CAMPO_OBRIGATORIO") + " " + getMessagesBundle("ERROR_INTERVALO_HORA_VALIDATOR");
				statusMessagesAddToControlFromResourceBundle(getIntervaloHoraObrigId(),Severity.ERROR, getMessagesBundle("ERROR_INTERVALO_HORA_VALIDATOR"));
				validou = false;
		}
		if(getItemSolicitacaoExameVO().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getUnidTempoColetaAmostras() != DominioUnidTempo.H) {
			if (getIntervaloDiasValue() == null && getNumeroAmostraValue() > 1) {
				statusMessagesAddToControlFromResourceBundle(getIntervaloDiasObrigId(),Severity.ERROR, "CAMPO_OBRIGATORIO", getMessagesBundle("LABEL_INTERVALO_DIAS"));
				validou = false;
			} 
		}
		
		return validou;
	}
	
	protected String getNumeroAmostraId() {
		return "numeroAmostra";
	}

	protected String getIntervaloHoraObrigId() {
		return "intervaloHoraObrig";
	}

	protected String getIntervaloDiasObrigId() {
		return "intervaloDiasObrig";
	}

	protected Integer getNumeroAmostraValue() {
		return getItemSolicitacaoExameVO().getNumeroAmostra();
	}

	protected void setNumeroAmostraValue(int numAmostra) {
		getItemSolicitacaoExameVO().setNumeroAmostra(numAmostra);
	}

	
	protected Date getIntervaloHorasValue() {
		return getItemSolicitacaoExameVO().getIntervaloHoras();
	}

	protected Integer getIntervaloDiasValue() {
		return getItemSolicitacaoExameVO().getIntervaloDias();
	}
}
