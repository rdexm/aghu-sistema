package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * TODO
 * 
 * @author fgka
 * 
 */
public class AfaFormaDosagemJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaFormaDosagemJn> {

	

	private static final long serialVersionUID = -8164742934487163725L;

	/**
	 * Pesquisa histórico de Forma Dosagem por medicamento
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return List<AfaFormaDosagemJn>
	 */
	public List<AfaFormaDosagemJn> pesquisarFormaDosagemJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		
		DetachedCriteria criteria = obterCriteriaFormaDosagemJnPorMedicamento(medicamento);
		
		criteria.addOrder(Order.desc(BaseJournal.Fields.DATA_ALTERACAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param medicamento
	 * @return
	 */
	public Long pesquisarFormaDosagemJnCount(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaFormaDosagemJnPorMedicamento(medicamento);
		
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaFormaDosagemJnPorMedicamento(AfaMedicamento medicamento){

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormaDosagemJn.class);

		criteria.add(Restrictions.eq(AfaFormaDosagemJn.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));

		return criteria;
	}
	
}
