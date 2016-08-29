package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lucas
 *
 */
@Stateless
public class AelExtratoItemCartasON extends BaseBusiness {


@EJB
private AelExtratoItemCartasRN aelExtratoItemCartasRN;

private static final Log LOG = LogFactory.getLog(AelExtratoItemCartasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4638478728346747524L;

	public void persistirAelExtratoItemCartas(AelExtratoItemCartas aelExtratoItemCartas, Boolean flush) throws ApplicationBusinessException{
		
		if (aelExtratoItemCartas.getId() == null) {
		
			inserirAelExtratoItemCartas(aelExtratoItemCartas, flush);
		
		}
		// TODO update
	
	}
	
	private void inserirAelExtratoItemCartas(AelExtratoItemCartas aelExtratoItemCartas, Boolean flush) throws ApplicationBusinessException {
		getAelExtratoItemCartasRN().inserirAelExtratoItemCartas(aelExtratoItemCartas, flush);
		
	}
	
	protected AelExtratoItemCartasRN getAelExtratoItemCartasRN() {
		return aelExtratoItemCartasRN;
	}
}
