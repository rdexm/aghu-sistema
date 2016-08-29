package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatTipoTransplante;

public class FatTipoTransplanteDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoTransplante> {

	private static final long serialVersionUID = -8555561836065332309L;
	
	private DetachedCriteria obterDetachedCriteriaFatTipoTransplante(){
		return DetachedCriteria.forClass(FatTipoTransplante.class);
	}
	
	/**
	 * Lista todos os tipos de transplante (sao poucos registros. Atualmente, 6).
	 * @return
	 */
	public List<FatTipoTransplante> listarTodosOsTiposTransplante() {
		return executeCriteria(obterDetachedCriteriaFatTipoTransplante().addOrder(Order.asc(FatTipoTransplante.Fields.CODIGO.toString())));
	}
	
	/**
	 * #41082 - consulta suggestionBox 
	 */
	public List<FatTipoTransplante> pesquisarProcedimentosTransplante(
			Object filtro) {
		DetachedCriteria criteria = montarQueryProcedimentosTransplante(filtro, Boolean.TRUE);
		
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarProcedimentosTransplanteCount(Object filtro) {
		return executeCriteriaCount(montarQueryProcedimentosTransplante(filtro, Boolean.FALSE));
	}
	
	private DetachedCriteria montarQueryProcedimentosTransplante(Object tipoTransplante, boolean ordem){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatTipoTransplante.class);

		String stParametro = (String) tipoTransplante;

		if (StringUtils.isNotBlank(stParametro)) {
			criteria.add(Restrictions.or((Restrictions.ilike(FatTipoTransplante.Fields.CODIGO.toString(), stParametro, MatchMode.START)), 
					(Restrictions.ilike(FatTipoTransplante.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE))));
		}
		
		if(ordem){
			criteria.addOrder(Order.asc(FatTipoTransplante.Fields.DESCRICAO.toString()));
		}
		
		return criteria;
	}
}
