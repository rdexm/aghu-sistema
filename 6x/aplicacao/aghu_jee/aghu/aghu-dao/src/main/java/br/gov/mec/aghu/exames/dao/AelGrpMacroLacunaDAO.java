package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;

public class AelGrpMacroLacunaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpMacroLacuna>  {
	
	private static final long serialVersionUID = -1507671964274782779L;

	public Short obterProximaSequence(Short lufLubSeq, Short lufSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpMacroLacuna.class);		
		
		criteria.add(Restrictions.eq(AelGrpMacroLacuna.Fields.LUF_LUB_SEQ.toString(), lufLubSeq));
		criteria.add(Restrictions.eq(AelGrpMacroLacuna.Fields.LUF_SEQP.toString(), lufSeqp));
		
		criteria.setProjection(Projections.max(AelGrpMacroLacuna.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}

		return ++seq;
		
	}
	
	public List<AelGrpMacroLacuna> pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(final Short aelTextoPadraoMacroLubSeq, final Short aelTextoPadraoMacroSeqp, final DominioSituacao indSituacao) {

		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelGrpMacroLacuna.class);

		if (aelTextoPadraoMacroLubSeq != null) {
			criteria.add(Restrictions.eq(AelGrpMacroLacuna.Fields.AEL_TEXTO_PADRAO_MACROS_LUB_SEQ.toString(), aelTextoPadraoMacroLubSeq));
		}
		
		if (aelTextoPadraoMacroSeqp != null) {
			criteria.add(Restrictions.eq(AelGrpMacroLacuna.Fields.AEL_TEXTO_PADRAO_MACROS_SEQP.toString(), aelTextoPadraoMacroSeqp));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelGrpMacroLacuna.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		
		criteria.addOrder(Order.asc(AelGrpMacroLacuna.Fields.LACUNA.toString()));
		
		return executeCriteria(criteria);
	}
}