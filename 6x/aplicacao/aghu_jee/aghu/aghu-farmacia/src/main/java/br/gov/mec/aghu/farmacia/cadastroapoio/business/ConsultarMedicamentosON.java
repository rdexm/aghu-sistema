package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultarMedicamentosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConsultarMedicamentosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaMedicamentoDAO afaMedicamentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3541888226620056512L;

	public List<AfaMedicamento> pesquisar(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento) {
		if (medicamento == null) {
			throw new IllegalArgumentException("medicamento não pode ser nulo.");
		}
		return this.getEntidadeDAO().pesquisarMedicamentos(firstResult, 
				maxResult, orderProperty, asc, medicamento);
	}
	
	public Long pesquisarCount(AfaMedicamento medicamento) {		
		if (medicamento == null) {
			throw new IllegalArgumentException("medicamento não pode ser nulo.");
		}
		return this.getEntidadeDAO().pesquisarMedicamentosCount(medicamento);		
	}
	
	protected AfaMedicamentoDAO getEntidadeDAO() {
		return afaMedicamentoDAO;
	}
}
