package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoJn;

/**
 * DAO de AfaMedicamentoJn
 * 
 * @author lcmoura
 * 
 */
public class AfaMedicamentoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaMedicamentoJn> {

	

	private static final long serialVersionUID = -5448822177654786427L;

	/**
	 * Pesquisa histórico de cadastro de Medicamento
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return List<AfaMedicamentoJn>
	 */
	public List<AfaMedicamentoJn> pesquisarHistoricoCadastroMedicamento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		
		DetachedCriteria criteria = obterCriteriaHistoricoCadastroMedicamento(medicamento);
		
		criteria.addOrder(Order.desc(AfaMedicamentoJn.Fields.DATA_ALTERACAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param medicamento
	 * @return
	 */
	public Long pesquisarHistoricoCadastroMedicamentoCount(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaHistoricoCadastroMedicamento(medicamento);
		
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaHistoricoCadastroMedicamento(AfaMedicamento medicamento){

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamentoJn.class);

		criteria.add(Restrictions.eq(AfaMedicamentoJn.Fields.MAT_CODIGO.toString(), medicamento.getMatCodigo()));

		return criteria;
	}
	
	/**
	 * Obtém o histórico do medicamento selecionado
	 * @param seqJn
	 * @return
	 */
	
	public AfaMedicamentoJn obterAfaMedicamentoJn(Integer seqJn) {

		DetachedCriteria cri = DetachedCriteria.forClass(AfaMedicamentoJn.class);
		cri.add(Restrictions.eq(AfaMedicamentoJn.Fields.SEQ_JN.toString(), seqJn));

		return (AfaMedicamentoJn) executeCriteriaUniqueResult(cri);
	}



}
