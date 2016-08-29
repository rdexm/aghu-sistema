package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoNomeComercial;

/**
 * 
 * @modulo compras
 *
 */
public class ScoNomeComercialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoNomeComercial> {

	private static final long serialVersionUID = -8946090693886299254L;

	public ScoNomeComercial obterNomeComercialPorMcmCodigoNumero(Integer mcmCodigo, Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoNomeComercial.class);
		criteria.add(Restrictions.eq(ScoNomeComercial.Fields.MARCA_COMERCIAL_ID.toString(), mcmCodigo));
		criteria.add(Restrictions.eq(ScoNomeComercial.Fields.NUMERO.toString(), numero));
		
		return (ScoNomeComercial)executeCriteriaUniqueResult(criteria);
		
		
	}	

	public Integer getNextNumero(Integer marcaCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoNomeComercial.class);
		criteria.setProjection(Projections.max(ScoNomeComercial.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoNomeComercial.Fields.MARCA_COMERCIAL_ID.toString(), marcaCodigo));
		if(executeCriteriaUniqueResult(criteria) == null){
			return 1;
		}else{
			return (Integer) executeCriteriaUniqueResult(criteria) +1;
		}
	}

	public List<ScoNomeComercial> buscaMarcasComeriais(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc, ScoMarcaComercial marcaComercial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoNomeComercial.class, "NC");
		criteria.createAlias("NC."+ScoNomeComercial.Fields.MARCA_COMERCIAL.toString(), "MCM",JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MCM."+ScoMarcaComercial.Fields.CODIGO.toString(), marcaComercial.getCodigo()));
		criteria.addOrder(Order.asc("NC."+ScoNomeComercial.Fields.NUMERO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}	
	
	public Long buscaMarcasComeriaisCount(ScoMarcaComercial marcaComercial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoNomeComercial.class, "NC");
		criteria.createAlias("NC."+ScoNomeComercial.Fields.MARCA_COMERCIAL.toString(), "MCM",JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MCM."+ScoMarcaComercial.Fields.CODIGO.toString(), marcaComercial.getCodigo()));
		return executeCriteriaCount(criteria);
	}

	public boolean isValidoParaCadastrar(ScoNomeComercial entity, String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoNomeComercial.class, "NC");
		criteria.add(Restrictions.eq("NC."+ScoNomeComercial.Fields.MARCA_COMERCIAL_ID.toString(), entity.getId().getMcmCodigo()));
		criteria.add(Restrictions.eq("NC."+ScoNomeComercial.Fields.NOME.toString(), nome));
		return !executeCriteriaExists(criteria);
	}
}
