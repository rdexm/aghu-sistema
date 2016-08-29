package br.gov.mec.aghu.registrocolaborador.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapRamaisCentroCusto;
import br.gov.mec.aghu.registrocolaborador.dao.RapRamaisCentroCustoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RapRamaisCentroCustoRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1390628740799061663L;
	
	private static final Log LOG = LogFactory.getLog(RapRamaisCentroCustoRN.class);
	
	@Inject
	private RapRamaisCentroCustoDAO rapRamaisCentroCustoDAO;
		
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public RapRamaisCentroCustoDAO getRapRamaisCentroCustoDAO() {
		return rapRamaisCentroCustoDAO;
	}
	
	
	public RapRamaisCentroCusto consultarRamalChefia2e3(Integer codigoCC, boolean restricao) {
		List<RapRamaisCentroCusto> ramais = getRapRamaisCentroCustoDAO().consultarRamalChefia2e3(codigoCC, restricao);
		if (ramais == null || ramais.isEmpty()) {
			return null;
		}
		return ramais.get(0); 
	}

}
