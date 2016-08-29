package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Camada de persistencia para <code>FAT_CONTAS_INTERNACAO</code>
 */
@Stateless
public class FatContaInternacaoPersist extends AbstractAGHUCrudPersist<FatContasInternacao> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1387114049569989192L;

	private static final Log LOG = LogFactory.getLog(FatContaInternacaoPersist.class);

	@Inject
	private FatContasInternacaoDAO fatContasInternacaoDAO;
	
	@EJB
	private FatContaInternacaoRN fatContaInternacaoRN;
	
	
	
	
	public FatContaInternacaoPersist() {
		super();
	}

	public FatContaInternacaoPersist(boolean comFlush) {
		super(comFlush);
	}
	
	
	
	
	@Override
	public Object getChavePrimariaEntidade(final FatContasInternacao entidade) {
		return entidade.getSeq();
	}

	@Override
	public BaseDao<FatContasInternacao> getEntidadeDAO() {
		return fatContasInternacaoDAO;
	}

	@Override
	public AbstractAGHUCrudRn<FatContasInternacao> getRegraNegocio() {
		return fatContaInternacaoRN;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
