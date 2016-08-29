/**
 * 
 */
package br.gov.mec.aghu.centrocusto.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author marcelofilho
 *
 */
public class FcuGrupoCentroCustosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FcuGrupoCentroCustos> {

	private static final long serialVersionUID = 9131224545389037302L;

	public List<FcuGrupoCentroCustos> pesquisarGruposCentroCustos(String filtro) {
		final DetachedCriteria criteria = getCriteriaSuggestionGruposCentroCustos(filtro);
		
		criteria.addOrder(Order.asc(FcuGrupoCentroCustos.Fields.DESCRICAO.toString()));
		List<FcuGrupoCentroCustos> list = executeCriteria(criteria, 0, 25, null, false);
		
		return list;
	}
	
	public Long pesquisarGruposCentroCustosCount(String filtro) {
		return executeCriteriaCount(getCriteriaSuggestionGruposCentroCustos(filtro));
	}

	private DetachedCriteria getCriteriaSuggestionGruposCentroCustos(String filtro) {
		final DetachedCriteria cri = DetachedCriteria.forClass(FcuGrupoCentroCustos.class);
		
		if(filtro != null && !StringUtils.isEmpty(filtro)){
			if (CoreUtil.isNumeroShort(filtro)) {
				cri.add(Restrictions.idEq(Short.parseShort(filtro)));
			} else {
				cri.add(Restrictions.ilike(FcuGrupoCentroCustos.Fields.DESCRICAO.toString(), filtro,MatchMode.ANYWHERE));
			}
		}
		
		return cri;
	}

	public 	FcuGrupoCentroCustos obterGrupoCentroCusto(Short seq) {
		DetachedCriteria cri = DetachedCriteria.forClass(FcuGrupoCentroCustos.class);
		cri.add(Restrictions.idEq(seq));

		return (FcuGrupoCentroCustos) executeCriteriaUniqueResult(cri);
	}
}