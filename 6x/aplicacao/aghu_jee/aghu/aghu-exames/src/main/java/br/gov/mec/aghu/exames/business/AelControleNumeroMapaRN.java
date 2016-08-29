package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelControleNumeroMapaDAO;
import br.gov.mec.aghu.model.AelControleNumeroMapa;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;


@Stateless
public class AelControleNumeroMapaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelControleNumeroMapaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelControleNumeroMapaDAO aelControleNumeroMapaDAO;

	private static final long serialVersionUID = 7238404483544295604L;

	public void persistir(AelControleNumeroMapa aelControleNumeroMapa) throws BaseException{
		AelControleNumeroMapaDAO dao = getAelControleNumeroMapaDAO();
		dao.persistir(aelControleNumeroMapa);
		dao.flush();
	} 
	
	protected AelControleNumeroMapaDAO getAelControleNumeroMapaDAO() {
		return aelControleNumeroMapaDAO;
	}
}