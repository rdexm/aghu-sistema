package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;

public class AfaSinonimoMedicamentoJnDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaSinonimoMedicamentoJn> {

	
	
	private static final long serialVersionUID = -1497004707052534792L;

	/**
	 * Pesquisa histórico de Sinônimos de Medicamento
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return List<AfaSinonimoMedicamentoJn>
	 */
	public List<AfaSinonimoMedicamentoJn> pesquisarSinonimoMedicamentoJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		
		DetachedCriteria criteria = obterCriteriaSinonimoMedicamentoJn(medicamento);
		
		criteria.addOrder(Order.asc(AfaSinonimoMedicamentoJn.Fields.DATA_ALTERACAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param medicamento
	 * @return
	 */
	public Long pesquisarSinonimoMedicamentoJnCount(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaSinonimoMedicamentoJn(medicamento);
		
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaSinonimoMedicamentoJn(AfaMedicamento medicamento){

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaSinonimoMedicamentoJn.class);

		criteria.add(Restrictions.eq(AfaSinonimoMedicamentoJn.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		
		return criteria;
	}

}
