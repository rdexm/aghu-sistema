package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelOrdExameMatAnaliseON extends BaseBusiness {


	@EJB
	private AelOrdExameMatAnaliseRN aelOrdExameMatAnaliseRN;
	
	private static final Log LOG = LogFactory.getLog(AelOrdExameMatAnaliseON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -7161955851240165230L;
	
	public void atualizar(AelOrdExameMatAnalise aelOrdExameMatAnalise) throws BaseException{
		getAelValorNormalidCampoRN().atualizarAelOrdExameMatAnalise(aelOrdExameMatAnalise);
	}
	
	protected AelOrdExameMatAnaliseRN getAelValorNormalidCampoRN() {
		return aelOrdExameMatAnaliseRN;
	}
}
