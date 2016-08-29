package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatBancoCapacidadeDAO;
import br.gov.mec.aghu.model.FatBancoCapacidade;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_BANCO_CAPACIDADES</code>
 * @author gandriotti
 */
@Stateless
public class BancoCapacidadePersist
		extends AbstractAGHUCrudPersist<FatBancoCapacidade> {
	
	
	private static final Log LOG = LogFactory.getLog(BancoCapacidadePersist.class);
	
	@Inject
	private FatBancoCapacidadeDAO fatBancoCapacidadeDAO;
	
	@EJB
	private BancoCapacidadeRN bancoCapacidadeRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5053450318211303846L;

	@Override
	public Object getChavePrimariaEntidade(final FatBancoCapacidade entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatBancoCapacidade> getEntidadeDAO() {
		return fatBancoCapacidadeDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatBancoCapacidade> getRegraNegocio() {
		return bancoCapacidadeRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
