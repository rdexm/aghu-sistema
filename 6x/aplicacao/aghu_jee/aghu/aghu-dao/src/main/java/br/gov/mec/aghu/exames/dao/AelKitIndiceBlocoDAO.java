package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelKitIndiceBlocoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelKitIndiceBloco> {
	
	private static final long serialVersionUID = 7698516971673396298L;

	public List<AelKitIndiceBloco> pesquisarAelKitIndiceBloco(final Integer seq, final String filtro, final DominioSituacao situacao){
		final DetachedCriteria criteria = obterCriteria(seq, filtro,situacao);
		criteria.addOrder(Order.asc(AelKitIndiceBloco.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria); 
	}

	public Long pesquisarAelKitIndiceBlocoCount(final Integer seq, final String filtro, final DominioSituacao situacao){
		return executeCriteriaCount(obterCriteria(seq, filtro, situacao));
	}
	
	private DetachedCriteria obterCriteria(final Integer seq, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelKitIndiceBloco.class);
		
		if(seq != null){
			criteria.add(Restrictions.eq(AelKitIndiceBloco.Fields.SEQ.toString(), seq));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(AelKitIndiceBloco.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelKitIndiceBloco.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelKitIndiceBloco.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}