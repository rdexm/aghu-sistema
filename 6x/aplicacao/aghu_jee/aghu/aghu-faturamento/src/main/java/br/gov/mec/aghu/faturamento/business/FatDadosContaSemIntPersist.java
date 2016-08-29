package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatDadosContaSemIntDAO;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para <code>FAT_CONTAS_INTERNACAO</code>
 */
@Stateless
public class FatDadosContaSemIntPersist extends AbstractAGHUCrudPersist<FatDadosContaSemInt> {
	
	private static final Log LOG = LogFactory.getLog(FatDadosContaSemIntPersist.class);
	
	@Inject
	private FatDadosContaSemIntDAO fatDadosContaSemIntDAO;
	
	@EJB
	private FatDadosContaSemIntRN fatDadosContaSemIntRN;
	

	@Override
		protected Log getLogger() {
			return LOG;
		}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8078610961991631177L;

	public FatDadosContaSemIntPersist() {
		super();
	}

	@Override
	public Object getChavePrimariaEntidade(final FatDadosContaSemInt entidade) {
		return entidade.getSeq();
	}

	@Override
	public BaseDao<FatDadosContaSemInt> getEntidadeDAO() {
		return fatDadosContaSemIntDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatDadosContaSemInt> getRegraNegocio() {
		return fatDadosContaSemIntRN;
	}
}