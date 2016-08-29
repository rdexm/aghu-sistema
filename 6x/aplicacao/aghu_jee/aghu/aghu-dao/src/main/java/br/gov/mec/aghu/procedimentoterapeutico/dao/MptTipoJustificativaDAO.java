package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptTipoJustificativa;


public class MptTipoJustificativaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptTipoJustificativa> {

	private static final long serialVersionUID = -6198070852508193226L;

	
	
		
	
	//Consulta suggestion
	public Long listarMptTipoJustificativaCount(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoJustificativa.class);
		if(param != null && StringUtils.isNotBlank(param)){
			criteria.add(
					Restrictions.or(
							Restrictions.eq(MptTipoJustificativa.Fields.SIGLA.toString(), param),
							Restrictions.ilike(MptTipoJustificativa.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE)));
		}
		
		criteria.addOrder(Order.asc(MptTipoJustificativa.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}
	
	
	public List<MptTipoJustificativa> listarMptTipoJustificativa(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoJustificativa.class);
			if(param != null && StringUtils.isNotBlank(param)){
				criteria.add(
						Restrictions.or(
								Restrictions.eq(MptTipoJustificativa.Fields.SIGLA.toString(), param),
								Restrictions.ilike(MptTipoJustificativa.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE)));
			}
		criteria.addOrder(Order.asc(MptTipoJustificativa.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

}
