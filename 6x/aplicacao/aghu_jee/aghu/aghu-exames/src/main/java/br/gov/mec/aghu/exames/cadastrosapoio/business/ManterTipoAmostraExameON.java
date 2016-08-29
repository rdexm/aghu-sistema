package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ManterTipoAmostraExameON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterTipoAmostraExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3055321133518846951L;


	/**
	 * Verifica se o recipiente de coleta<br>
	 * esta relacionado a exames.
	 * 
	 * @param codigo
	 * @return
	 */
	public Boolean temTipoAmostraExame(AelRecipienteColeta elemento) {
		return getAelTipoAmostraExameDAO().obterTipoAmostraExame(elemento.getSeq());
	}

	
	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}
	
}
