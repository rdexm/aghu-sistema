package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.persistence.BaseEntity;

public abstract class AbstractMedicamentoDAO<E extends BaseEntity> extends br.gov.mec.aghu.core.persistence.dao.BaseDao<E> {

	private static final long serialVersionUID = 5342377366104382268L;
	/**
	 * Retornar o criterio para pesquisar todas as entidades que referenciam o medicamento solicitado.
	 * @param medicamento
	 * @return
	 */
	protected abstract DetachedCriteria pesquisarCriteria(
			AfaMedicamento medicamento);
	
	/**
	 * Encapsula chamada de {@link GenericDAO#executeCriteria(DetachedCriteria)} com verificacao de argumentos.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return
	 * @see {@link #pesquisarCriteria(AfaMedicamento)}
	 * @see {@link #executeCriteria(DetachedCriteria)}
	 */
	public List<E> pesquisar(AfaMedicamento medicamento) {
		
		List<E> result = null;
		DetachedCriteria criteria = null;
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		criteria = this.pesquisarCriteria(medicamento);
		result = this.executeCriteria(criteria);
	
		return result;		
	}
	
	/**
	 * Encapsula chamada de {@link GenericDAO#executeCriteria(DetachedCriteria, int, int, String, boolean)} com verificacao de argumentos.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param medicamento
	 * @return
	 * @see {@link #pesquisarCriteria(AfaMedicamento)}
	 * @see {@link #executeCriteria(DetachedCriteria, int, int, String, boolean)}
	 */
	public List<E> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		
		List<E> result = null;
		DetachedCriteria criteria = null;
		
		if (firstResult == null) {
			throw new IllegalArgumentException();
		}
		if (maxResult == null) {
			throw new IllegalArgumentException();
		}		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		criteria = this.pesquisarCriteria(medicamento);
		result = this.executeCriteria(criteria, firstResult.intValue(), maxResult.intValue(), orderProperty,
				asc);
	
		return result;
	}	

	/**
	 * @param medicamento
	 * @return
	 * @see #pesquisarCriteria(AfaMedicamento)
	 * @see #executeCriteriaCount(DetachedCriteria)
	 */
	public Long pesquisarCount(AfaMedicamento medicamento) {
		
		Long result = null;
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		result = this.executeCriteriaCount(this.pesquisarCriteria(medicamento));
		
		return result;
	}
	/*
	 * TODO Método inserido para funcionar a visibilidade da classe de test, rever a necessidade deste método 
	 * 
	 */
	@Override
	protected <T> List<T> executeCriteria(DetachedCriteria criteria,
			int firstResult, int maxResults, String orderProperty, boolean asc) {
		// TODO Auto-generated method stub
		return super.executeCriteria(criteria, firstResult, maxResults, orderProperty,
				asc);
	}


}
