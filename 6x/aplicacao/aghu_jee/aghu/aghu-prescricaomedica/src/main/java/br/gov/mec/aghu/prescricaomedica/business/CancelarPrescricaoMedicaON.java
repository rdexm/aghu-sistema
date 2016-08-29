package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class CancelarPrescricaoMedicaON extends BaseBusiness {


@EJB
private CancelarPrescricaoMedicaRN cancelarPrescricaoMedicaRN;

private static final Log LOG = LogFactory.getLog(CancelarPrescricaoMedicaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6209499408757366488L;

	/**
	 * Cancela uma prescrição fazendo o devido tratamento para todos os seus
	 * ítens
	 * @param prescricao
	 * @throws BaseException 
	 */
	public void cancelarPrescricao(Integer idAtendimento, Integer seqPrescricao, String nomeMicrocomputador)
			throws BaseException {
		
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(idAtendimento);
		id.setSeq(seqPrescricao);
		MpmPrescricaoMedica prescricaoMedica = getPrescricaoMedicaDAO()
				.obterOriginal(id);
		
		getCancelarPrescricaoMedicaRN().cancelarPrescricao(prescricaoMedica, nomeMicrocomputador);
	}
	
	public void verificarPrescricaoCancelada(MpmPrescricaoMedica prescricao) 
			throws ApplicationBusinessException {
		getCancelarPrescricaoMedicaRN().verificarPrescricaoCancelada(prescricao);
	}
	
	protected CancelarPrescricaoMedicaRN getCancelarPrescricaoMedicaRN() {
		return cancelarPrescricaoMedicaRN;
	}
	
	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}
}
