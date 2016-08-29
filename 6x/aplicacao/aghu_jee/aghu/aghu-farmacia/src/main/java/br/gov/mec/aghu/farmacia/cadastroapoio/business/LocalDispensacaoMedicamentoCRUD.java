package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;

@Stateless
public class LocalDispensacaoMedicamentoCRUD extends AbstractCrudMedicamento<AfaLocalDispensacaoMdtos> {


@EJB
private LocalDispensacaoMedicamentoRN localDispensacaoMedicamentoRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2466340052815766307L;

	private static final Log LOG = LogFactory.getLog(LocalDispensacaoMedicamentoCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	@Override
	public AbstractMedicamentoDAO<AfaLocalDispensacaoMdtos> getEntidadeDAO() {

		return afaLocalDispensacaoMdtosDAO;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaLocalDispensacaoMdtos entidade) {

		return (entidade != null ? entidade.getId() : null);
	}

	@Override
	public AbstractAGHUCrudRn<AfaLocalDispensacaoMdtos> getRegraNegocio() {
		return localDispensacaoMedicamentoRN;
	}

}
