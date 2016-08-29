package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelLoteExameON extends BaseBusiness{


@EJB
private AelLoteExameRN aelLoteExameRN;

private static final Log LOG = LogFactory.getLog(AelLoteExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -580401958497714856L;

	public void persistirAelLoteExame(AelLoteExame loteExame, Boolean flush) throws BaseException {
		
		if (loteExame.getId() == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}else{
			this.inserirAelLoteExame(loteExame, flush);
		}	
	}
	
	private void inserirAelLoteExame(AelLoteExame loteExame, boolean flush) throws BaseException {
		getAelLoteExameRN().inserirAelLoteExame(loteExame);
	}
	
	/***
	 * remover lote de exame usual
	 * @param loteExame
	 * @throws BaseException 
	 */
	public void removerAelLoteExame(AelLoteExameId loteExameId) throws ApplicationBusinessException {
		getAelLoteExameRN().removerAelLoteExame(loteExameId);
	}
	
	public AelLoteExameRN getAelLoteExameRN(){
		return aelLoteExameRN;
	}

}
