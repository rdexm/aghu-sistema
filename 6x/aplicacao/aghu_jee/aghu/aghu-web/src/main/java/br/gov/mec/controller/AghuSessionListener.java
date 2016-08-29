package br.gov.mec.controller;

import java.util.Calendar;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.sistema.bussiness.UserSessions;

/**
 * Classe usada para registrar que a sessão do usuário expirou.
 * 
 * @author geraldo
 * 
 */
@WebListener
public class AghuSessionListener implements HttpSessionListener {

	private static final Log LOG = LogFactory.getLog(AghuSessionListener.class);   
	
	@Inject
	private UserSessions sessions;
    
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		LOG.info("Iniciou uma sessão");
		
		HttpSession session = se.getSession();
		session.setAttribute(VariaveisSessaoEnum.MPMK_IME_RN_AUTO_EXCLUIR_PROCEDIMENTO_MEDICAMENTO_SOL.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.ABSK_ISH_RN_AUTO_EXCLUIR_ITEM_HEMOTERAPICA.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.ABSK_ISJ_RN_AUTO_EXCLUIR_JUSTIFICATIVA_ITEM_HEMOTERAPICA.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.FATK_CTH2_RN_UN_V_MAIOR_VALOR.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.FATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.FATK_DCS_RN_V_SUBST_PRONT_DCS.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.SCEK_IPS_V_EXCLUIR_RMP.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.MCOK_BSR_RN_V_VER_DT_ROMP.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_BAL_RN_V_VEIO_SUBS_GESTA_BAL.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_EFI_RN_V_VEIO_TROCA_PAC.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.MCOK_IDC_RN_V_VEIO_SUBS_GESTA_IDC.toString(), Boolean.FALSE);
		session.setAttribute(VariaveisSessaoEnum.MCOK_INN_RN_V_VEIO_TROCA_PAC.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_NAS_RN_V_VEIO_UPD_NAS.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_NAS_RN_V_VEIO_TROCA_PAC.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_RNA_RN_V_VEIO_UPD_RNA.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_RNA_RN_V_VEIO_TROCA_PAC.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.MCOK_SNA_RN_V_VEIO_SUBS_GESTA.toString(), Boolean.FALSE);		
		session.setAttribute(VariaveisSessaoEnum.AIPF_PRNTOL_ADMIN.toString(), null);		
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		LOG.info("Encerrando sessão");

		HttpSession sessao = arg0.getSession();

		// é preciso se certificar que a sessão está mesmo expirada, pois o
		// método sessionDestroyde é chamado inclusive quando a sessão é
		// invalidada no logout acionado pelo usuário.
		if (verificarSessaoExpirada(sessao)) {
			LOG.info("Sessão expirada");
		}
		sessions.removeSession(sessao.getId());
	}

	private boolean verificarSessaoExpirada(HttpSession sessao) {
		long agora = Calendar.getInstance().getTimeInMillis();
		return ((agora - sessao.getLastAccessedTime()) / 1000) > sessao.getMaxInactiveInterval();
	}
}