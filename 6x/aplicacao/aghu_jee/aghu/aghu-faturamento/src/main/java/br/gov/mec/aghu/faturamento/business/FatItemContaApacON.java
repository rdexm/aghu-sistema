package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FatItemContaApacON extends BaseBusiness {


@EJB
private FatItemContaApacRN fatItemContaApacRN;

private static final Log LOG = LogFactory.getLog(FatItemContaApacON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	private static final long serialVersionUID = 4536055331127280184L;

	/**
	 * Método para persistir um item de conta apac.
	 * 
	 * @param newItemCtaHosp
	 * @param oldItemCtaHosp
	 * @throws BaseException
	 */
	public void persistirItemContaApac(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac, final Boolean flush)
			throws BaseException {
		getFatItemContaApacRN().persistir(newItemCtaApac, oldItemCtaApac, flush);
	}

	protected FatItemContaApacRN getFatItemContaApacRN() {
		return fatItemContaApacRN;
	}

	public FatItemContaApac cloneFatItemContaApac(FatItemContaApac fatItemContaApac) throws BaseException {
		try {
			return (FatItemContaApac) BeanUtils.cloneBean(fatItemContaApac);
		} catch (Exception e) {
			throw new ApplicationBusinessException(null);
		}
	}
}
