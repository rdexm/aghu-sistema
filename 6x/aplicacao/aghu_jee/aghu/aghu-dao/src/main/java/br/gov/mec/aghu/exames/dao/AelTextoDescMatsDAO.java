package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTextoDescMatsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTxtDescMats>  {
	
	private static final long serialVersionUID = -3378722239135311653L;

	public List<AelTxtDescMats> pesquisarTextoPadraoDescMatsPorAelGrpTxtPadraoDescMats(final Short seqAelGrpTxtPadraoDescMats) {
		
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTxtDescMats.class);
		
		if (seqAelGrpTxtPadraoDescMats != null) {
			criteria.add(Restrictions.eq(AelTxtDescMats.Fields.AEL_GRP_TXT_DESC_MATS_SEQ.toString(), seqAelGrpTxtPadraoDescMats));
		}
		
		criteria.addOrder(Order.asc(AelTxtDescMats.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public List<AelTxtDescMats> pesquisarTextoPadraoDescMats(final AelGrpTxtDescMats aelGrpTxtDescMats, final String filtro, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria = obterCriteria(aelGrpTxtDescMats,filtro, indSituacao);
		criteria.addOrder(Order.asc(AelTxtDescMats.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarTextoPadraoDescMatsCount(final AelGrpTxtDescMats aelGrpTxtDescMats, final String filtro, final DominioSituacao indSituacao) {
		return executeCriteriaCount(obterCriteria(aelGrpTxtDescMats,filtro, indSituacao));
	}

	private DetachedCriteria obterCriteria(final AelGrpTxtDescMats aelGrpTxtDescMats,final String filtro, final DominioSituacao indSituacao) {
		
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTxtDescMats.class);
		
		if(aelGrpTxtDescMats != null){
			criteria.add(Restrictions.eq(AelTxtDescMats.Fields.AEL_GRP_TXT_DESC_MATS.toString(), aelGrpTxtDescMats));
		}
		
		if(indSituacao != null){
			criteria.add(Restrictions.eq(AelTxtDescMats.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		if(StringUtils.isNotBlank(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelTxtDescMats.Fields.SEQP.toString(), Short.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTxtDescMats.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}	
	
	public Short obterProximaSequence(Short gtmSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTxtDescMats.class);
		
		criteria.add(Restrictions.eq(AelTxtDescMats.Fields.GTM_SEQ.toString(), gtmSeq));
		criteria.setProjection(Projections.max(AelTxtDescMats.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}
		return ++seq;
	}
	
}
