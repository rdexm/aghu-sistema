package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtCidDescON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PdtCidDescON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	private static final long serialVersionUID = -5384146520497723953L;

	public enum PdtCidDescONExceptionCode implements BusinessExceptionCode {
		PDT_00172, PDT_00173;
	}

	public void validarResultadoNormalOuCid(final Boolean resultadoNormal, final AghCid cid) throws BaseException {
		if(Boolean.FALSE.equals(resultadoNormal)) {
			if(cid == null) {
				//-- Na pasta Resultado, clique em resultado Normal ou Informe um CID
				throw new ApplicationBusinessException(PdtCidDescONExceptionCode.PDT_00172);
			}
		}
		else {
			if(cid == null) {
				//-- Na pasta Resultado,Informe que o resultado é Normal OU informe um CID
				throw new ApplicationBusinessException(PdtCidDescONExceptionCode.PDT_00173);
			}
		}
	}
}
