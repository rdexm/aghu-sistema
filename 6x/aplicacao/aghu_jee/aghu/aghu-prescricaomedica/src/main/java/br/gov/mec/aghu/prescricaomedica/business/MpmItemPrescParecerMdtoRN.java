package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParecerUsoMdtosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmItemPrescParecerMdtoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1546396356068509930L;
	
	private static final Log LOG = LogFactory.getLog(MpmItemPrescParecerMdtoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmParecerUsoMdtosDAO mpmParecerUsoMdtosDAO;
	
	public enum MpmItemPrescParecerMdtoRNExceptionCode implements BusinessExceptionCode {
		MPM_01156, MPM_01163, MPM_01164;
	}

	public void persistirItemPrescParecerMdto(MpmItemPrescParecerMdto itemPrescr) throws ApplicationBusinessException {
		this.preInserir(itemPrescr);
		this.mpmItemPrescParecerMdtoDAO.persistir(itemPrescr);
		this.mpmItemPrescParecerMdtoDAO.flush();
	}

	/**
	 * Before Insert MPMT_IPR_BRI de MPM_ITEM_PRESC_PARECER_MDTOS
	 * 
	 * @ORADB MPMT_IPR_BRI
	 */
	public void preInserir(MpmItemPrescParecerMdto itemPrescr) throws ApplicationBusinessException {
		this.verificarItemPrecricaoMedicamento(itemPrescr);
	}
	
	/**
	 * Verificação de existencia de medicamentos prescritos
	 * 
	 * @ORADB MPMK_IPR_RN.RN_IPRP_VER_IT_PR_MD
	 */
	public void verificarItemPrecricaoMedicamento(MpmItemPrescParecerMdto itemPrescr) throws ApplicationBusinessException {
		
		MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId(itemPrescr.getId().getImePmdAtdSeq(), itemPrescr.getId().getImePmdSeq(), 
				itemPrescr.getId().getImeMedMatCodigo(), itemPrescr.getId().getImeSeqp());
		MpmItemPrescricaoMdto ime = this.mpmItemPrescricaoMdtoDAO.obterPorChavePrimaria(id, MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO);
		
		if (ime != null) {
			if (ime.getJustificativaUsoMedicamento() != null && ime.getJustificativaUsoMedicamento().getSeq() != null) {
				Long count = this.mpmParecerUsoMdtosDAO.obterCountParecerPorSeqJustificativa(
						itemPrescr.getMpmParecerUsoMdtos().getSeq(), ime.getJustificativaUsoMedicamento().getSeq());
				if (count == null || count == 0l) {
					throw new ApplicationBusinessException(MpmItemPrescParecerMdtoRNExceptionCode.MPM_01163);
				}
			} else {
				throw new ApplicationBusinessException(MpmItemPrescParecerMdtoRNExceptionCode.MPM_01156);
			}
		} else {
			throw new ApplicationBusinessException(MpmItemPrescParecerMdtoRNExceptionCode.MPM_01164);
		}
	}

}
