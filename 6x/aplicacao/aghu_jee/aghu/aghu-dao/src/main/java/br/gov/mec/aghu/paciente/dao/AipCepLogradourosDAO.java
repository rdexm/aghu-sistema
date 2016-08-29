package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipCepLogradourosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipCepLogradouros> {

	private static final long serialVersionUID = -1321931349474368745L;

	public List<AipCepLogradouros> listarCepLogradourosPorCEP(Integer cep) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipCepLogradouros.class);

		criteria.add(Restrictions.eq(AipCepLogradouros.Fields.CEP.toString(),
				cep));

		return executeCriteria(criteria);
	}
	
	public List<AipCepLogradouros> obterAipCepLogradourosPorCEP(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipCepLogradourosPorCEPCount(filtro);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipCepLogradouros.Fields.ID.toString()), AipCepLogradouros.Fields.ID.toString())
				);
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipCepLogradouros.class));
		criteria.addOrder(Order.asc(AipCepLogradouros.Fields.CEP.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long obterAipCepLogradourosPorCEPCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipCepLogradourosPorCEPCount(filtro);
		return executeCriteriaCount(criteria);
	}

	public DetachedCriteria obterCriteriaAipCepLogradourosPorCEPCount(Object filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipCepLogradouros.class);
		criteria.add(Restrictions.eq(AipCepLogradouros.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
				
		String filtroPesquisa = filtro.toString();
		
		if(CoreUtil.isNumeroInteger(filtroPesquisa)){
			criteria.add(Restrictions.eq(AipCepLogradouros.Fields.CEP.toString(), Integer.valueOf(filtroPesquisa)));
		}
		
		return criteria;
	}
}
