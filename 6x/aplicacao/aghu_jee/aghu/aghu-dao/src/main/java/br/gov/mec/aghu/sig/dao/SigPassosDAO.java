package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import br.gov.mec.aghu.model.SigPassos;

public class SigPassosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigPassos>{
	
	private static final long serialVersionUID = -5581871010320848244L;

	public List<SigPassos> listarTodosOrdenadosPelaDescricao(){
		DetachedCriteria criteria = DetachedCriteria.forClass( SigPassos.class);
		criteria.addOrder(Order.asc(SigPassos.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria);
	}

	public List<SigPassos> listarTodosOrdenadosPeloCampoOrdem() {
		DetachedCriteria criteria = DetachedCriteria.forClass( SigPassos.class);
		criteria.addOrder(Order.asc(SigPassos.Fields.ORDEM_EXECUCAO.toString()));
		return this.executeCriteria(criteria);
	}
	
}
