package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;

public class AelGrpDiagLacunasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpDiagLacunas> {

	private static final long serialVersionUID = 3472475242073009935L;

	public List<AelGrpDiagLacunas> pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(final Short aelTextoPadraoDiagsLuhSeq, final Short aelTextoPadraoDiagsSeqp, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelGrpDiagLacunas.class);

		if (aelTextoPadraoDiagsLuhSeq != null) {
			criteria.add(Restrictions.eq(AelGrpDiagLacunas.Fields.AEL_TEXTO_PADRAO_DIAGS_LUH_SEQ.toString(), aelTextoPadraoDiagsLuhSeq));
		}
		
		if (aelTextoPadraoDiagsSeqp != null) {
			criteria.add(Restrictions.eq(AelGrpDiagLacunas.Fields.AEL_TEXTO_PADRAO_DIAGS_SEQP.toString(), aelTextoPadraoDiagsSeqp));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelGrpDiagLacunas.Fields.IND_SITUACAO.toString(), indSituacao));
		}		
		
		criteria.addOrder(Order.asc(AelGrpDiagLacunas.Fields.LACUNA.toString()));
		
		return executeCriteria(criteria);
	}

	public AelGrpDiagLacunasId gerarId(final AelTextoPadraoDiags aelGrpDiagLacunas ) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpDiagLacunas.class);		
		
		if(aelGrpDiagLacunas == null || aelGrpDiagLacunas.getId() == null){
			throw new IllegalArgumentException("Texto Padrão Diagnóstico deve ser informado.");
		}
		criteria.add(Restrictions.eq(AelGrpDiagLacunas.Fields.AEL_TEXTO_PADRAO_DIAGS_LUH_SEQ.toString(), aelGrpDiagLacunas.getId().getLuhSeq()));
		criteria.add(Restrictions.eq(AelGrpDiagLacunas.Fields.AEL_TEXTO_PADRAO_DIAGS_SEQP.toString(), aelGrpDiagLacunas.getId().getSeqp()));
		
		criteria.setProjection(Projections.max(AelGrpDiagLacunas.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			seq = 1;
		} else {
			seq ++;
		}
		return new AelGrpDiagLacunasId(aelGrpDiagLacunas.getId().getLuhSeq(), aelGrpDiagLacunas.getId().getSeqp(), seq);
		
	}
}
