package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_ESPELHOS_AIH</code>
 * @author gandriotti
 */
@Stateless
public class EspelhoAihPersist	extends AbstractAGHUCrudPersist<FatEspelhoAih> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3161638070282036784L;
	
	private static final Log LOG = LogFactory.getLog(EspelhoAihPersist.class);

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@EJB
	private EspelhoAihRN espelhoAihRN;
	
	
	public EspelhoAihPersist() {
		super();
	}

	public EspelhoAihPersist(boolean comFlush) {
		super(comFlush);
	}

	@Override
	public Object getChavePrimariaEntidade(final FatEspelhoAih entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatEspelhoAih> getEntidadeDAO() {

		return fatEspelhoAihDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatEspelhoAih> getRegraNegocio() {
		return espelhoAihRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
