package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamConsultorAmbulatorioDAO;
import br.gov.mec.aghu.model.MamConsultorAmbulatorio;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamConsultorAmbulatorioRN extends BaseBusiness{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3704439267833741239L;
	
	protected Log getLogger() {
		return LogFactory.getLog(MamConsultorAmbulatorioRN.class);
	}
	@Inject
	private MamConsultorAmbulatorioDAO mamConsultorAmbulatorioDAO;
	
	public enum MamConsultorAmbulatorioRNExceptionCode implements BusinessExceptionCode {
		ERRO_MAM_CONS_AMB_DUPLICADO;
	}
	public void persistirConsultorAmbulatorio(MamConsultorAmbulatorio mamConsultorAmbulatorio) throws ApplicationBusinessException {
		if (mamConsultorAmbulatorio.getSeq() == null) {
			inserirConsultorAmbulatorio(mamConsultorAmbulatorio);
		} else {
			atualizarConsultorAmbulatorio(mamConsultorAmbulatorio);
		}
	}
	private void atualizarConsultorAmbulatorio(MamConsultorAmbulatorio mamConsultorAmbulatorio) {
		preAtualizar(mamConsultorAmbulatorio);
		mamConsultorAmbulatorioDAO.merge(mamConsultorAmbulatorio);
	}
	/**
	 * @ORADB MAMT_CAB_BRU
	 * @param mamConsultorAmbulatorio
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizar(MamConsultorAmbulatorio mamConsultorAmbulatorio) {
		mamConsultorAmbulatorio.setAlteradoEm(new Date());
	}
	private void inserirConsultorAmbulatorio(MamConsultorAmbulatorio mamConsultorAmbulatorio) throws ApplicationBusinessException {
		preInserir(mamConsultorAmbulatorio);
		mamConsultorAmbulatorioDAO.persistir(mamConsultorAmbulatorio);
	}
	/**
	 * @ORADB MAMT_CAB_BRI
	 * @param mamConsultorAmbulatorio
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MamConsultorAmbulatorio mamConsultorAmbulatorio) throws ApplicationBusinessException {
		//MAM_CAB_UK1
		Integer eqpSeq = mamConsultorAmbulatorio.getAghEquipes() == null ? null : mamConsultorAmbulatorio.getAghEquipes().getSeq();
		List<MamConsultorAmbulatorio> consultorAmbulatorioList = mamConsultorAmbulatorioDAO.pesquisarMamConsultorAmbulatorioPorServidorEspSeq(mamConsultorAmbulatorio.getRapServidores(), mamConsultorAmbulatorio.getAghEspecialidadesByEspSeq().getSeq(), eqpSeq);
		if (consultorAmbulatorioList != null && !consultorAmbulatorioList.isEmpty()) {
			throw new ApplicationBusinessException(MamConsultorAmbulatorioRNExceptionCode.ERRO_MAM_CONS_AMB_DUPLICADO);
		}
		mamConsultorAmbulatorio.setCriadoEm(new Date());
	}
}

