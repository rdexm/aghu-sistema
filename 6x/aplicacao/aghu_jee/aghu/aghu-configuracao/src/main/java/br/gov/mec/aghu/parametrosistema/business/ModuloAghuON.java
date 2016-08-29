package br.gov.mec.aghu.parametrosistema.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.parametrosistema.dao.AghModuloAghuDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ModuloAghuON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ModuloAghuON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghModuloAghuDAO aghModuloAghuDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1516776406433793018L;

	protected AghModuloAghuDAO getAghModuloAghuDAO(){
		return aghModuloAghuDAO;
	}
	
	public List<AghModuloAghu> pesquisarModulosParametroSistemas() {
		return getAghModuloAghuDAO().pesquisarModulosParametroSistemas();
	}
}
