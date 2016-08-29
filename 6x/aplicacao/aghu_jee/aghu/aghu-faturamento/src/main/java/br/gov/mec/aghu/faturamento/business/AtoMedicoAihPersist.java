package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_ATOS_MEDICOS_AIH</code>
 * @author gandriotti
 */
@Stateless
public class AtoMedicoAihPersist extends AbstractAGHUCrudPersist<FatAtoMedicoAih> {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -374700201202849531L;

	private static final Log LOG = LogFactory.getLog(AtoMedicoAihPersist.class);
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;
	
	@EJB
	private AtoMedicoAihRN atoMedicoAihRN;
	

	public AtoMedicoAihPersist() {
		super();
	}

	public AtoMedicoAihPersist(boolean comFlush) {
		super(comFlush);
	}

	@Override
	public Object getChavePrimariaEntidade(FatAtoMedicoAih entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatAtoMedicoAih> getEntidadeDAO() {
		return fatAtoMedicoAihDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatAtoMedicoAih> getRegraNegocio() {
		return atoMedicoAihRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
