package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelExamesCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelExamesCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade iParametroFacade;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1327168396428933766L;

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	/**
	 * oradb aelk_exa_rn.rn_exap_ver_delecao
	 * @param data
	 * @param aghuParametrosEnum
	 * @param exceptionForaPeriodoPermitido
	 * @param erroRecuperacaoAghuParametro
	 * @throws BaseException
	 */
	public void verificaDataCriacao(final Date data, final AghuParametrosEnum aghuParametrosEnum, BusinessExceptionCode exceptionForaPeriodoPermitido, BusinessExceptionCode erroRecuperacaoAghuParametro) throws BaseException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(aghuParametrosEnum);
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(exceptionForaPeriodoPermitido);
			}
		} else {
			throw new ApplicationBusinessException(erroRecuperacaoAghuParametro);
		}
	}
	
}
