package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemGrupoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ItemGrupoMedicamentoON extends AbstractCrudMedicamento<AfaItemGrupoMedicamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7458241651655953623L;

	private static final Log LOG = LogFactory.getLog(ItemGrupoMedicamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO;
	
	@Override
	public AbstractMedicamentoDAO<AfaItemGrupoMedicamento> getEntidadeDAO() {
		return afaItemGrupoMedicamentoDAO;
	}

	@Override
	public AbstractAGHUCrudRn<AfaItemGrupoMedicamento> getRegraNegocio() {
		return null;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaItemGrupoMedicamento entidade) {
		
		return entidade.getId();
	}

}
