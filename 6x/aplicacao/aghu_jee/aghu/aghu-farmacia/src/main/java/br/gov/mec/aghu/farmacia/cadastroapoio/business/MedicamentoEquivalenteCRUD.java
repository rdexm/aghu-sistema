package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoEquivalenteDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteId;

@Stateless
public class MedicamentoEquivalenteCRUD  extends AbstractCrudMedicamento<AfaMedicamentoEquivalente> {


@EJB
private MedicamentoEquivalenteRN medicamentoEquivalenteRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6771520206138488695L;

	private static final Log LOG = LogFactory.getLog(MedicamentoEquivalenteCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Inject
	private AfaMedicamentoEquivalenteDAO afaMedicamentoEquivalenteDAO;
	
	@Override
	public AbstractAGHUCrudRn<AfaMedicamentoEquivalente> getRegraNegocio() {
		return medicamentoEquivalenteRN;
	}
	
	@Override
	public AbstractMedicamentoDAO<AfaMedicamentoEquivalente> getEntidadeDAO() {
		return afaMedicamentoEquivalenteDAO;  
	}
	
	@Override
	public Object getChavePrimariaEntidade(
			AfaMedicamentoEquivalente entidade) {

		return (entidade != null ? entidade.getId() : null);
	}  
	
	public AfaMedicamentoEquivalente obterPorChavePrimaria(Integer medMatCodigo, Integer medMatCodigoEquivalente) {
		
		
		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(medMatCodigo);
		
		AfaMedicamento medicamentoEquivalente = new AfaMedicamento();
		medicamentoEquivalente.setMatCodigo(medMatCodigoEquivalente);
		
		AfaMedicamentoEquivalenteId chavePrimaria = new AfaMedicamentoEquivalenteId();
		chavePrimaria.setMedMatCodigo(medMatCodigo);
		chavePrimaria.setMedMatCodigoEquivalente(medMatCodigoEquivalente);
		
		AfaMedicamentoEquivalente result = null;
		
		result = this.getEntidadeDAO().obterPorChavePrimaria(chavePrimaria);
		
		return result;
	}
	
	/***
	 * Retorna lista com todos os medicamentos de acordo com a pesquisa
	 * @param strPesquisa
	 * @return
	 */
	public List<AfaMedicamento> pesquisarTodosMedicamentos(Object strPesquisa) {
		return getAfaMedicamentoDAO().pesquisarTodosMedicamentos(strPesquisa);
	}

	public Long pesquisarTodosMedicamentosCount(Object strPesquisa) {
		return getAfaMedicamentoDAO().pesquisarTodosMedicamentosCount(strPesquisa);
	}
	
}

