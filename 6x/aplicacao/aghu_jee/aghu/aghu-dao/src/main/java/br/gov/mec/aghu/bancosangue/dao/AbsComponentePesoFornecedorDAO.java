package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;

public class AbsComponentePesoFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsComponentePesoFornecedor> {

	private static final long serialVersionUID = 7758116693829003422L;
	
	//count da lista de paginaçao.
	public Long pesquisarComponentePesoFornecedorCount(AbsComponentePesoFornecedor componentePesoFornecedor){

		DetachedCriteria criteria = montaCriteriaAbsComponentePesoFornecedor(componentePesoFornecedor);
		
		return this.executeCriteriaCount(criteria);
	}
	
	//Lista recuperada para paginaçao. 
	public List<AbsComponentePesoFornecedor> pesquisarComponentePesoFornecedor(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AbsComponentePesoFornecedor componentePesoFornecedor) {
		DetachedCriteria criteria = montaCriteriaAbsComponentePesoFornecedor(componentePesoFornecedor);

		criteria.createAlias(AbsComponentePesoFornecedor.Fields.FORNECEDOR_BOLSA.toString(), "FB", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.desc(AbsComponentePesoFornecedor.Fields.IND_SUGESTAO.toString()));
		criteria.addOrder(Order.asc(AbsComponentePesoFornecedor.Fields.FBO_SEQ.toString()));
		criteria.addOrder(Order.asc(AbsComponentePesoFornecedor.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc(AbsComponentePesoFornecedor.Fields.PESO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult,orderProperty, asc);
	}
	
	//Criteria para paginação. Segundo parametros.
	public DetachedCriteria montaCriteriaAbsComponentePesoFornecedor(AbsComponentePesoFornecedor componentePesoFornecedor){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponentePesoFornecedor.class);
		
		if(componentePesoFornecedor.getComponenteSanguineo()!= null){
			criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.COMPONENTE_SANGUINEO.toString(), componentePesoFornecedor.getComponenteSanguineo()));
		}
		if(componentePesoFornecedor.getFornecedorBolsas()!=null){
			criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.FORNECEDOR_BOLSA.toString(), componentePesoFornecedor.getFornecedorBolsas()));
		}
		if(componentePesoFornecedor.getPeso()!=null){
			criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.PESO.toString(), componentePesoFornecedor.getPeso()));
		}
		
		return criteria;
	}
	
	public Short pesquisarMaxSeqp(String csaCodigo, Integer fboSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponentePesoFornecedor.class);
		criteria.setProjection(Projections.projectionList().add(Projections.max(AbsComponentePesoFornecedor.Fields.SEQP.toString())));
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.CSA_CODIGO.toString(), csaCodigo));
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.FBO_SEQ.toString(), fboSeq));
		return (Short) executeCriteriaUniqueResult(criteria);
	}	

	public Boolean existeSugestaoParaComponenteSanguineo(String csaCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponentePesoFornecedor.class);
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.CSA_CODIGO.toString(),csaCodigo));
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.IND_SUGESTAO.toString(),Boolean.TRUE));
		
		return executeCriteriaExists(criteria);
	}
	
	public AbsComponentePesoFornecedor obterSugestaoParaComponenteSanguineo(String csaCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponentePesoFornecedor.class);
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.CSA_CODIGO.toString(),csaCodigo));
		criteria.add(Restrictions.eq(AbsComponentePesoFornecedor.Fields.IND_SUGESTAO.toString(),Boolean.TRUE));

		return (AbsComponentePesoFornecedor) executeCriteriaUniqueResult(criteria);
	}
	
}
