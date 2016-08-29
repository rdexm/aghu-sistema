package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoIngressoCtiDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class MotivoIngressoCtiRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MotivoIngressoCtiRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmMotivoIngressoCtiDAO mpmMotivoIngressoCtiDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7407363604152354906L;

	private enum MotivoIngressoCtiRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_MOTIVO_INGRESSO_CTI, ERRO_PERSISTIR_MOTIVO_INGRESSO_CTI_SEM_ATD;
	}

	/**
	 * ORADB Trigger MPMT_MIG_BRI
	 * 
	 * @param motivoIngressoCti
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirMotivoIngressoCti(MpmMotivoIngressoCti motivoIngressoCti)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		

		if (motivoIngressoCti == null) {
			throw new ApplicationBusinessException(
					MotivoIngressoCtiRNExceptionCode.ERRO_PERSISTIR_MOTIVO_INGRESSO_CTI);
		} else if(motivoIngressoCti.getAtendimento() == null){
			throw new ApplicationBusinessException(
					MotivoIngressoCtiRNExceptionCode.ERRO_PERSISTIR_MOTIVO_INGRESSO_CTI_SEM_ATD);
		} else {
			motivoIngressoCti.setServidor(servidorLogado);
			motivoIngressoCti.setCriadoEm(new Date());
			this.getMotivoIngressoCtiDAO().persistir(motivoIngressoCti);
			this.getMotivoIngressoCtiDAO().flush();
		}
	}
	
	/**
	 * Retorna o DAO de motivo de ingresso CTI
	 * 
	 * @return
	 */
	protected MpmMotivoIngressoCtiDAO getMotivoIngressoCtiDAO() {
		return mpmMotivoIngressoCtiDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
