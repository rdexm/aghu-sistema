package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmJustificativaUsoMdtoON extends BaseBusiness {

	private static final long serialVersionUID = 3758371706419780834L;

	private static final Log LOG = LogFactory.getLog(MpmJustificativaUsoMdtoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private MpmJustificativaUsoMdtoRN mpmJustificativaUsoMdtoRN;
	
	public enum MpmJustificativaUsoMdtoONExceptionCode implements BusinessExceptionCode {
		MPM_JUM_CK20;
	}
	
	public MpmJustificativaUsoMdto persistirMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa, 
		List<JustificativaMedicamentoUsoGeralVO> medicamentos) throws ApplicationBusinessException {
		verificaTratamentoQuimio(justificativa);
		return mpmJustificativaUsoMdtoRN.persistirMpmJustificativaUsoMdto(justificativa, medicamentos);
	}
	
	public void verificaTratamentoQuimio(MpmJustificativaUsoMdto justificativa) throws ApplicationBusinessException {
		if ((justificativa.getTratAntQuimio() != null && justificativa.getMesAnoUltCiclo() == null) || 
			(justificativa.getTratAntQuimio() == null && justificativa.getMesAnoUltCiclo() != null)) {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoONExceptionCode.MPM_JUM_CK20);
		}
	}
}
