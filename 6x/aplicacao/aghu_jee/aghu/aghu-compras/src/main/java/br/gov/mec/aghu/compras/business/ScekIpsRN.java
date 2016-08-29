package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * ORADB SCEK_IPS_RN
 *
 */
@Stateless
public class ScekIpsRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScekIpsRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IEstoqueFacade estoqueFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7962041432178435080L;

	public enum ScekIpsRNExceptionCode implements BusinessExceptionCode {
		SCE_00597
	}

	
	/**
	 * ORADB RN_IPSP_VER_ULT_ITEM
	 * 
	 * @param rmpSeq
	 * @throws BaseException
	 */
	 
	public void ipspVerUltItem(Integer rmpSeq) throws BaseException {		
		Long count = this.getEstoqueFacade().countSceRmrPaciente(rmpSeq);
		
		if (count == null || count == 0) {
			throw new ApplicationBusinessException(ScekIpsRNExceptionCode.SCE_00597);
		}
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
}
