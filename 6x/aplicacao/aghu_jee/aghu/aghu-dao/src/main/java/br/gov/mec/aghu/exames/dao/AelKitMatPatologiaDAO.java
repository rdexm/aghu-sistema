package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelKitMatPatologiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelKitMatPatologia> {
	
	private static final long serialVersionUID = 3595241824430604389L;

	public List<AelKitMatPatologia> pesquisarAelKitMatPatologia(final Integer seq, final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(seq, filtro,situacao);
		criteria.addOrder(Order.asc(AelKitMatPatologia.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	private DetachedCriteria obterCriteria(final Integer seq, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelKitMatPatologia.class);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AelKitMatPatologia.Fields.SEQ.toString(), seq));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(AelKitMatPatologia.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelKitMatPatologia.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelKitMatPatologia.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}