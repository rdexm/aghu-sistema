package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelControleNumeroUnicoDAO;
import br.gov.mec.aghu.model.AelControleNumeroUnico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelControleNumeroUnicoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelControleNumeroUnicoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelControleNumeroUnicoDAO aelControleNumeroUnicoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5290968519601628817L;

	/**
	 * Atualiza objeto AelControleNumeroUnico
	 * @param {AelControleNumeroUnico} controleNumeroUnico
	 * @return {AelControleNumeroUnico}
	 * @throws BaseException
	 */
	public AelControleNumeroUnico atualizarSemFlush(AelControleNumeroUnico controleNumeroUnico) throws BaseException {
		
		controleNumeroUnico = this.getAelControleNumeroUnicoDAO().merge(controleNumeroUnico);
		
		return controleNumeroUnico;
		
	}
	
	public AelControleNumeroUnicoDAO getAelControleNumeroUnicoDAO(){
		return aelControleNumeroUnicoDAO;
	}	

}
