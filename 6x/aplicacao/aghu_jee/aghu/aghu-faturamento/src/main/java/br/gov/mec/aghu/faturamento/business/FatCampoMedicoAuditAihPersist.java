package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para <code>FAT_CAMPOS_MEDICO_AUDIT_AIH</code>
 */
@Stateless
public class FatCampoMedicoAuditAihPersist extends AbstractAGHUCrudPersist<FatCampoMedicoAuditAih> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3672272615590448502L;

	private static final Log LOG = LogFactory.getLog(FatCampoMedicoAuditAihPersist.class);
	
	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;
	
	@EJB
	private FatCampoMedicoAuditAihRN fatCampoMedicoAuditAihRN;
	
	
	public FatCampoMedicoAuditAihPersist() {
		super();
	}

	public FatCampoMedicoAuditAihPersist(boolean comFlush) {
		super(comFlush);
	}

	@Override
	public Object getChavePrimariaEntidade(final FatCampoMedicoAuditAih entidade) {
		return entidade.getId();
	}

	@Override
	public BaseDao<FatCampoMedicoAuditAih> getEntidadeDAO() {
		return fatCampoMedicoAuditAihDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatCampoMedicoAuditAih> getRegraNegocio() {
		return fatCampoMedicoAuditAihRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
