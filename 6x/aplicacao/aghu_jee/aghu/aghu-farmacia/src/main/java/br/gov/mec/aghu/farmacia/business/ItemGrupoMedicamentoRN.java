package br.gov.mec.aghu.farmacia.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @ORADB AFAK_IGM_RN
 */
@Stateless
public class ItemGrupoMedicamentoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ItemGrupoMedicamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -8230859420533200673L;

	public enum ItemGrupoMedicamentoRNExceptionCode implements
			BusinessExceptionCode {
		AFA_00433, AFA_00434
	}

	/**
	 * @ORADB AFAK_IGM_RN.RN_IGMP_VER_MEDICAM
	 * 
	 * Ao incluir um medicamento para um grupo, ambos devem estar ativos.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaMedicamento(AfaGrupoMedicamento grupoMedicamento,
			AfaMedicamento medicamento) throws ApplicationBusinessException {
		if (grupoMedicamento == null
				|| !DominioSituacao.A.equals(grupoMedicamento.getSituacao())) {
			throw new ApplicationBusinessException(
					ItemGrupoMedicamentoRNExceptionCode.AFA_00433);
		}
		
		if (medicamento == null
				|| (!DominioSituacaoMedicamento.A.equals(medicamento
						.getIndSituacao()) && !DominioSituacaoMedicamento.I
						.equals(medicamento.getIndSituacao()))) {
			throw new ApplicationBusinessException(
					ItemGrupoMedicamentoRNExceptionCode.AFA_00434);
		}
	}

}
