package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AghMedicoExternoDAO;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class MedicoAtendimentoExternoON extends BaseBusiness {


@EJB
private MedicoAtendimentoExternoRN medicoAtendimentoExternoRN;

private static final Log LOG = LogFactory.getLog(MedicoAtendimentoExternoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghMedicoExternoDAO aghMedicoExternoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6589945113632033802L;

	public Long countMedicoAtendimentoExterno(Map<Object, Object> filtersMap) {
		return this.getAghMedicoExternoDAO().countMedicoExterno(filtersMap);
	}
	
	public List<AghMedicoExterno> pesquisaMedicoExternoPaginado(Map<Object, Object> filtersMap, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.getAghMedicoExternoDAO().pesquisaMedicoExternoPaginado(filtersMap, firstResult, 
				maxResult, orderProperty, asc);
	}
	
	public void saveOrUpdateMedicoExterno(AghMedicoExterno medicoExterno) throws BaseException{
		if(medicoExterno != null && medicoExterno.getSeq() == null) {
			this.getMedicoAtendimentoExternoRN().inserir(medicoExterno);
		} else {
			this.getMedicoAtendimentoExternoRN().atualizar(medicoExterno);
		}
	}
	
	//getter do DAO
	protected AghMedicoExternoDAO getAghMedicoExternoDAO() {
		return aghMedicoExternoDAO;
	}
	
	protected MedicoAtendimentoExternoRN getMedicoAtendimentoExternoRN() {
		return medicoAtendimentoExternoRN;
	}
}
