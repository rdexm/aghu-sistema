package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ViaAdministracaoMedicamentoON extends AbstractCrudMedicamento<AfaViaAdministracaoMedicamento> {


@EJB
private ViaAdministracaoMedicamentoRN viaAdministracaoMedicamentoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6170100455377839243L;

	private static final Log LOG = LogFactory.getLog(ViaAdministracaoMedicamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}		
	
	@Inject
	private AfaViaAdministracaoMedicamentoDAO afaViaAdministracaoMedicamentoDAO;
	
	@Override
	public AbstractAGHUCrudRn<AfaViaAdministracaoMedicamento> getRegraNegocio() {
		
		return viaAdministracaoMedicamentoRN;
	}

	@Override
	public AbstractMedicamentoDAO<AfaViaAdministracaoMedicamento> getEntidadeDAO() {
		return afaViaAdministracaoMedicamentoDAO;  
	}

	@Override
	public Object getChavePrimariaEntidade(
			AfaViaAdministracaoMedicamento entidade) {

		return (entidade != null ? entidade.getId() : null);
	}
}
