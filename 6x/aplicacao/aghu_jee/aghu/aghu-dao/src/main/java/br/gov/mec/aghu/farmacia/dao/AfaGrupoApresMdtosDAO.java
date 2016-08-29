package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AfaGrupoApresMdtosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoApresMdtos> {


	private static final long serialVersionUID = -863582484420046905L;

	public List<AfaGrupoApresMdtos> pesquisarGrupoApresMdtos(Object strPesquisa) {
		
		DetachedCriteria criteria = criteriaPesquisarGrupoApresMdtos(strPesquisa);
		criteria.addOrder(Order.asc(AfaGrupoApresMdtos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarGrupoApresMdtosCount(Object strPesquisa) {
		
		DetachedCriteria criteria = criteriaPesquisarGrupoApresMdtos(strPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criteriaPesquisarGrupoApresMdtos(Object strPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoApresMdtos.class);
		
		if(strPesquisa != null && !strPesquisa.toString().isEmpty()){
			if (CoreUtil.isNumeroInteger(strPesquisa)) {			
				criteria.add(Restrictions.eq(AfaGrupoApresMdtos.Fields.SEQ.toString(), Short.valueOf(strPesquisa.toString())));
			}else{
				criteria.add(Restrictions.ilike(AfaGrupoApresMdtos.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
}
