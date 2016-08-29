package br.gov.mec.aghu.farmacia.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @ORADB AFAK_GMD_RN
 */
@Stateless
public class GrupoMedicamentoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GrupoMedicamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -9179488109091832407L;

	public enum GrupoMedicamentoRNExceptionCode implements
			BusinessExceptionCode {
		AFA_00431
	}

	/**
	 * @ORADB AFAK_GMD_RN.RN_GMDP_VER_DESCRICA
	 * 
	 * A descrição de um grupo de medicamento não pode ser alterada.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDescricao() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				GrupoMedicamentoRNExceptionCode.AFA_00431);
	}

}
