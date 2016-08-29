package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;

public class AelTxtDescMatsLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDescMatLacunas> {

	private static final long serialVersionUID = 6869199505394025167L;

	public Short obterProximaSequence(Short gtmSeq, Short ldaSeq, Short gmlSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDescMatLacunas.class);

		criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.GTM_SEQ.toString(), gtmSeq));
		criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.LDA_SEQ.toString(), ldaSeq));
		criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.GML_SEQ.toString(), gmlSeq));

		criteria.setProjection(Projections.max(AelDescMatLacunas.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);

		if (seq == null) {
			return 1;
		}

		return ++seq;

	}

	public List<AelDescMatLacunas> pesquisarAelDescMatLacunasPorAelGrpDescMatsLacuna(final AelGrpDescMatLacunas aelGrpDescMatLacunas, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelDescMatLacunas.class);

		if (aelGrpDescMatLacunas != null && aelGrpDescMatLacunas.getId() != null) {
			criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.GTM_SEQ.toString(), aelGrpDescMatLacunas.getId().getGtmSeq()));
			criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.LDA_SEQ.toString(), aelGrpDescMatLacunas.getId().getLdaSeq()));
			criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.GML_SEQ.toString(), aelGrpDescMatLacunas.getId().getSeqp()));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelDescMatLacunas.Fields.IND_SITUACAO.toString(), indSituacao));
		}		

		criteria.addOrder(Order.asc(AelDescMatLacunas.Fields.TEXTO_LACUNA.toString()));

		return executeCriteria(criteria);
	}
}
