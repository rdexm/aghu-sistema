package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatSaldoUtiDAO;
import br.gov.mec.aghu.model.FatSaldoUti;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_SALDOS_UTI</code>
 * @author gandriotti
 */
@Stateless
public class SaldoUtiPersist
		extends AbstractAGHUCrudPersist<FatSaldoUti> {
	
	private static final Log LOG = LogFactory.getLog(SaldoUtiPersist.class);
	
	@Inject
	private FatSaldoUtiDAO fatSaltoUtiDAO;
	
	@EJB
	private SaldoUtiRN saldoUtiRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6296475451995123785L;

	@Override
	public Object getChavePrimariaEntidade(final FatSaldoUti entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatSaldoUti> getEntidadeDAO() {

		return fatSaltoUtiDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatSaldoUti> getRegraNegocio() {

		return saldoUtiRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
