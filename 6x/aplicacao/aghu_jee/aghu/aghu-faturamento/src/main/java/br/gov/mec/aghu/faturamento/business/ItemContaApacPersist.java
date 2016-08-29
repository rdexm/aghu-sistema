package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_ITENS_CONTA_APAC</code>
 * @author gandriotti
 *
 */
@Stateless
public class ItemContaApacPersist extends AbstractAGHUCrudPersist<FatItemContaApac> {
	
	private static final Log LOG = LogFactory.getLog(ItemContaApacPersist.class);
	
	@Inject
	private FatItemContaApacDAO fatItemContaApacDAO;
	
	@EJB
	private ItemContaApacRN itemContaApacRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7718186565185205524L;

	@Override
	public Object getChavePrimariaEntidade(FatItemContaApac entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatItemContaApac> getEntidadeDAO() {

		return fatItemContaApacDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatItemContaApac> getRegraNegocio() {
		return itemContaApacRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
