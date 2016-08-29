package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelCestoPatologiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCestoPatologia> {

	private static final long serialVersionUID = -973596334132866153L;

	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(filtro, situacao);
		criteria.addOrder(Order.asc(AelCestoPatologia.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}
	
	public Long pesquisarAelCestoPatologiaCount(final String filtro, final DominioSituacao situacao){
		return executeCriteriaCount(obterCriteria(filtro, situacao));
	}
	
	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final Integer seq, final String descricao, final String sigla, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(seq, descricao, sigla, situacao);
		criteria.addOrder(Order.asc(AelCestoPatologia.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	private DetachedCriteria obterCriteria(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCestoPatologia.class);
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelCestoPatologia.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelCestoPatologia.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.or(
											  Restrictions.ilike(AelCestoPatologia.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE), 
											  Restrictions.ilike(AelCestoPatologia.Fields.SIGLA.toString(), filtro, MatchMode.ANYWHERE)
											 )
							);
			}
		}
		return criteria;
	}
	
	private DetachedCriteria obterCriteria(final Integer seq, final String descricao, final String sigla, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCestoPatologia.class);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AelCestoPatologia.Fields.SEQ.toString(), seq));
		}

		if(!StringUtils.isEmpty(sigla)){
			criteria.add(Restrictions.ilike(AelCestoPatologia.Fields.SIGLA.toString(), sigla, MatchMode.ANYWHERE));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(AelCestoPatologia.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(!StringUtils.isEmpty(descricao)){
			if(CoreUtil.isNumeroInteger(descricao)){
				criteria.add(Restrictions.eq(AelCestoPatologia.Fields.SEQ.toString(), Integer.valueOf(descricao)));
			} else {
				criteria.add(Restrictions.ilike(AelCestoPatologia.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}