package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.Severity;

public abstract class ISECamposObrigatoriosValidator {

	private static final Log LOG = LogFactory.getLog(ISECamposObrigatoriosValidator.class);
	
	
	private ItemSolicitacaoExameVO itemSolicitacaoExameVO;
	private ResourceBundle bundle;
	
	public ISECamposObrigatoriosValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle pBundle) {
		LOG.info("Class: " + getClass().getSimpleName());
		this.itemSolicitacaoExameVO = itemSolicitacaoExameVO;
		this.bundle = pBundle;
	}
	
	
	public ItemSolicitacaoExameVO getItemSolicitacaoExameVO() {
		return itemSolicitacaoExameVO;
	}
	
	public String getMessagesBundle(String key) {
		return bundle.getString(key);
	}
	
	protected boolean verificaIntervalo(Integer intervalo, String id) {
		if(!CoreUtil.isBetweenRange(intervalo, -99, 99)) {
			this.statusMessagesAddToControlFromResourceBundle(id, Severity.ERROR, "MESSAGE_INTERVALO", -99, 99);
			return false;
		}
		return true;
	}
	
	protected void statusMessagesAddToControlFromResourceBundle(String componentID, Severity s, String codeBundle, Object... params) {
		javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(s);
		
		FacesContext context = FacesContext.getCurrentInstance();
		String message =  WebUtil.initLocalizedMessage(codeBundle, null, params);
		
		String clientId = "Messages";
		if (StringUtils.isNotBlank(componentID)) {
			clientId = componentID;
		}
		
		context.addMessage(clientId, new FacesMessage(severity, message, message));
	}

	abstract boolean validate();
}
