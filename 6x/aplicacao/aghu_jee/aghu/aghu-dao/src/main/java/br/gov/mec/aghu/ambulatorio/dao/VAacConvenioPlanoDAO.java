package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAacConvenioPlano;

public class VAacConvenioPlanoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAacConvenioPlano>{
	

	private static final long serialVersionUID = -4823844846142484102L;

	public VAacConvenioPlano obterVAacConvenioPlanoAtivoPorId(Short cnvCodigo, Byte plano) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAacConvenioPlano.class);
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.CNV_CODIGO.toString(), cnvCodigo));
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.PLANO.toString(), plano));
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.IND_SITUACAO.toString(), "A"));
		return (VAacConvenioPlano)this.executeCriteriaUniqueResult(criteria);
	}
	
	public VAacConvenioPlano obterVAacConvenioPlanoPorId(Short cnvCodigo, Byte plano) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAacConvenioPlano.class);
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.CNV_CODIGO.toString(), cnvCodigo));
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.PLANO.toString(), plano));
		return (VAacConvenioPlano)this.executeCriteriaUniqueResult(criteria);
	}	
	
	public List<VAacConvenioPlano> pesquisarConvenios(String parametro){
		DetachedCriteria criteria = obterPesquisarConveniosCriteria(parametro);
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.CONVENIO_PLANO.toString()));
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.CNV_CODIGO.toString()));
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.PLANO.toString()));
	    return this.executeCriteria(criteria,0 , 100, null, false);		
	}
	
	public Long pesquisarConveniosCount(String parametro){
		DetachedCriteria criteria = obterPesquisarConveniosCriteria(parametro);
		return this.executeCriteriaCount(criteria);		
	}
	
	public DetachedCriteria obterPesquisarConveniosCriteria(String parametro){
		
		String desc = StringUtils.trimToNull(parametro);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacConvenioPlano.class);
		if (StringUtils.isNotEmpty(desc)) {
			criteria.add(Restrictions.ilike(VAacConvenioPlano.Fields.DESC_CONV.toString(),
					desc, MatchMode.ANYWHERE));
		}
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.IND_SITUACAO.toString(), "A"));
	    
	    criteria.add(Restrictions.or(Restrictions.isNull(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString()), 
	    		Restrictions.eq(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString(), "A")));
	    
	    return criteria;
	}
	
	public List<VAacConvenioPlano> obterListaConvenios(){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacConvenioPlano.class);
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.IND_SITUACAO.toString(), "A"));
	    criteria.add(Restrictions.or(Restrictions.isNull(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString()), 
	    		Restrictions.eq(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString(), "A")));
	    
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.CONVENIO_PLANO.toString()));
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.CNV_CODIGO.toString()));
	    criteria.addOrder(Order.asc(VAacConvenioPlano.Fields.PLANO.toString()));
	   
	    return this.executeCriteria(criteria);
	}
	
	public List<VAacConvenioPlano> pesquisarCovenioPlanoSGB(String pesquisa){
		DetachedCriteria criteria = criteriaSGB(pesquisa);
		this.addOrder(criteria, VAacConvenioPlano.Fields.CONVENIO_PLANO.toString(), true);
	return executeCriteria(criteria, 0, 100, null, true);
	} 
	
	public Long pesquisarCovenioPlanoSGBCount(String pesquisa){
		DetachedCriteria criteria = criteriaSGB(pesquisa);
	return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criteriaSGB(String pesquisa){ 
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacConvenioPlano.class);
	    criteria.add(Restrictions.eq(VAacConvenioPlano.Fields.IND_SITUACAO.toString(), "A"));
	    criteria.add(Restrictions.or(Restrictions.isNull(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString()), 
	    		Restrictions.eq(VAacConvenioPlano.Fields.CSP_IND_SITUACAO.toString(), "A")));
	    
	    		criteria.add(Restrictions.ilike(VAacConvenioPlano.Fields.CONVENIO_PLANO.toString(), pesquisa, MatchMode.ANYWHERE));
	    		
		return criteria; 
	}
	
	/**
	 * Adicionar ordem a uma Detached Criteria.
	 * 
	 * @param criteria
	 * @param orderProperty
	 * @param asc
	 */
	private void addOrder(final DetachedCriteria criteria, String orderProperty, boolean asc) {

		if (orderProperty != null && StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}
	}
	
}
