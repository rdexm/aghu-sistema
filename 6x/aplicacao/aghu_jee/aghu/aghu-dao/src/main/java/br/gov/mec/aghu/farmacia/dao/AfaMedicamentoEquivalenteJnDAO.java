package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.core.model.BaseJournal;

public class AfaMedicamentoEquivalenteJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaMedicamentoEquivalenteJn> {

		
	private static final long serialVersionUID = 6878756207537754227L;

	/**
	 * Pesquisa histórico de Medicamento Equivalente por medicamento
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return List<AfaMedicamentoEquivalenteJn>
	 */
	public List<AfaMedicamentoEquivalenteJn> pesquisarMedicamentoEquivalenteJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		
		DetachedCriteria criteria = obterCriteriaMedicamentoEquivalenteJnPorMedicamento(medicamento);
		
		criteria.addOrder(Order.desc(BaseJournal.Fields.DATA_ALTERACAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param medicamento
	 * @return
	 */
	public Long pesquisarMedicamentoEquivalenteJnCount(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaMedicamentoEquivalenteJnPorMedicamento(medicamento);
		
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaMedicamentoEquivalenteJnPorMedicamento(AfaMedicamento medicamento){

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamentoEquivalenteJn.class);

		criteria.add(Restrictions.eq(AfaMedicamentoEquivalenteJn.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));

		return criteria;
	}

}
