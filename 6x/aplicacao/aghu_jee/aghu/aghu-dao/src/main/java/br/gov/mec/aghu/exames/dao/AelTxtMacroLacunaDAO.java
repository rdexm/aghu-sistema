package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;


public class AelTxtMacroLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTxtMacroLacuna>  {
	
	private static final long serialVersionUID = 6869199505394025167L;

	public Short obterProximaSequence(Short lo3LufLubSeq, Short lo3LufSeqp, Short lo3Seqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTxtMacroLacuna.class);		
		
		criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_LUF_LUB_SEQ.toString(), lo3LufLubSeq));
		criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_LUF_SEQP.toString(), lo3LufSeqp));
		criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_SEQP.toString(), lo3Seqp));

		criteria.setProjection(Projections.max(AelTxtMacroLacuna.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}

		return ++seq;
		
	}
	
	public List<AelTxtMacroLacuna> pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTxtMacroLacuna.class);

		if (aelGrpMacroLacuna != null && aelGrpMacroLacuna.getId() != null) {
			criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_LUF_LUB_SEQ.toString(), aelGrpMacroLacuna.getId().getLufLubSeq()));
			criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_LUF_SEQP.toString(), aelGrpMacroLacuna.getId().getLufSeqp()));
			criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.LO3_SEQP.toString(), aelGrpMacroLacuna.getId().getSeqp()));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelTxtMacroLacuna.Fields.IND_SITUACAO.toString(), indSituacao));
		}		
		
		criteria.addOrder(Order.asc(AelTxtMacroLacuna.Fields.TEXTO_LACUNA.toString()));
		
		return executeCriteria(criteria);
	}
}
