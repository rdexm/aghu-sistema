package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceLoteDAO;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SceLoteRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceLoteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceLoteDAO sceLoteDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9135311539462101689L;

	public void remover(SceLote lote) throws ApplicationBusinessException{
		getSceLoteDAO().remover(lote);
		getSceLoteDAO().flush();
	}
	
	public void inserir(SceLote lote) throws ApplicationBusinessException{
		getSceLoteDAO().persistir(lote);
		getSceLoteDAO().flush();
	}

	public SceLoteDAO getSceLoteDAO(){
		return sceLoteDAO;
	}
}