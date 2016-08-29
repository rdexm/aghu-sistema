package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudOn;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ComponenteNptON extends AbstractAGHUCrudOn<AfaComponenteNpt> {


@EJB
private ComponenteNptRN componenteNptRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6555422694877493493L;

	private static final Log LOG = LogFactory.getLog(ComponenteNptON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Override
	public BaseDao<AfaComponenteNpt> getEntidadeDAO() {

		return afaComponenteNptDAO;
	}

	@Override
	public AbstractAGHUCrudRn<AfaComponenteNpt> getRegraNegocio() {

		return componenteNptRN;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaComponenteNpt entidade) {

		return (entidade != null ? entidade.getMedMatCodigo() : null);
	}

}
