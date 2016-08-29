package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para: <br/>
 * ORADB: <code>FAT_CIDS_CONTA_HOSPITALAR</code>
 */
@Stateless
public class CidContaHospitalarPersist extends AbstractAGHUCrudPersist<FatCidContaHospitalar> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8447396492316924634L;
	

	private static final Log LOG = LogFactory.getLog(CidContaHospitalarPersist.class);

	@Inject
	private FatCidContaHospitalarDAO fatCidContaHospitalarDAO;
	
	@EJB
	private CidContaHospitalarRN cidContaHospitalarRN;
	
	public CidContaHospitalarPersist() {
		super();
	}

	public CidContaHospitalarPersist(boolean comFlush) {
		super(comFlush);
	}

	@Override
	public Object getChavePrimariaEntidade(final FatCidContaHospitalar entidade) {

		return entidade.getId();
	}

	@Override
	public BaseDao<FatCidContaHospitalar> getEntidadeDAO() {

		return fatCidContaHospitalarDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatCidContaHospitalar> getRegraNegocio() {
		return cidContaHospitalarRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
