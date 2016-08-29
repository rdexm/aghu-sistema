package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasId;
import br.gov.mec.aghu.model.AelTxtDescMats;

public class AelGrpDescMatsLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpDescMatLacunas>  {
	
	private static final long serialVersionUID = -1507671964274782779L;

	public Short obterProximaSequence(Short gtmSeq, Short ldaSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpDescMatLacunas.class);		
		
		criteria.add(Restrictions.eq(AelGrpDescMatLacunas.Fields.GTM_SEQ.toString(), gtmSeq));
		criteria.add(Restrictions.eq(AelGrpDescMatLacunas.Fields.LDA_SEQ.toString(), ldaSeq));
		
		criteria.setProjection(Projections.max(AelGrpDescMatLacunas.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}

		return ++seq;
		
	}
	
	public List<AelGrpDescMatLacunas> pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(final Short aelTextoDescMatsGtmSeq, final Short aelTextoDescMatsSeqp, final DominioSituacao indSituacao) {

		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelGrpDescMatLacunas.class);

		if (aelTextoDescMatsGtmSeq != null) {
			criteria.add(Restrictions.eq(AelGrpDescMatLacunas.Fields.AEL_TXT_DESC_MATS_GTM_SEQ.toString(), aelTextoDescMatsGtmSeq));
		}
		
		if (aelTextoDescMatsSeqp != null) {
			criteria.add(Restrictions.eq(AelGrpDescMatLacunas.Fields.AEL_TXT_DESC_MATS_SEQP.toString(), aelTextoDescMatsSeqp));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelGrpDescMatLacunas.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		
		criteria.addOrder(Order.asc(AelGrpDescMatLacunas.Fields.LACUNA.toString()));
		
		return executeCriteria(criteria);
	}

	public AelGrpDescMatLacunas obterPorChavePrimariaAlias(AelGrpDescMatLacunasId aelGrpDescMatLacunasId) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.createAlias(AelGrpDescMatLacunas.Fields.AEL_TXT_DESC_MATS.toString(), "txtDesc", JoinType.INNER_JOIN);
		criteria.createAlias("txtDesc." + AelTxtDescMats.Fields.AEL_GRP_TXT_DESC_MATS.toString(), "grpTxtDesc", JoinType.INNER_JOIN);
		return (AelGrpDescMatLacunas)executeCriteriaUniqueResult(criteria);
	}
}