package br.gov.mec.aghu.registrocolaborador.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.registrocolaborador.dao.RapServidorUnimedDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RapServidorUnimedRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6600502360741707516L;
	

	@Inject
	private RapServidorUnimedDAO rapServidorUnimedDAO;
	
	private static final Log LOG = LogFactory.getLog(RapServidorUnimedRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	

	public String pesquisarCarteirasUnimedFuncionarios(Integer codStarh){
		return getRapServidorUnimedDAO().obterCarteiraUnimed(codStarh);		
	}


	public RapServidorUnimedDAO getRapServidorUnimedDAO() {
		return rapServidorUnimedDAO;
	}


	public void setRapServidorUnimedDAO(RapServidorUnimedDAO rapServidorUnimedDAO) {
		this.rapServidorUnimedDAO = rapServidorUnimedDAO;
	}
	

}
