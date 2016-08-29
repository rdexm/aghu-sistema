package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunasId;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;

public class AelTxtDiagLacunasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTxtDiagLacunas> {

	private static final long serialVersionUID = 2119478791433439970L;

	public List<AelTxtDiagLacunas> pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTxtDiagLacunas.class);

		if (aelGrpDiagLacunas != null && aelGrpDiagLacunas.getId() != null) {
			criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_LUJ_LUH_SEQ.toString(), aelGrpDiagLacunas.getId().getLujLuhSeq()));
			criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_LUJ_SEQP.toString(), aelGrpDiagLacunas.getId().getLujSeqp()));
			criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_SEQP.toString(), aelGrpDiagLacunas.getId().getSeqp()));
		}
		
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.IND_SITUACAO.toString(), indSituacao));
		}		
		
		criteria.addOrder(Order.asc(AelTxtMacroLacuna.Fields.TEXTO_LACUNA.toString()));
		
		return executeCriteria(criteria);
	}

	public AelTxtDiagLacunasId gerarId(final AelGrpDiagLacunas aelGrpDiagLacunas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTxtDiagLacunas.class);		
		
		if(aelGrpDiagLacunas == null || aelGrpDiagLacunas.getId() == null){
			throw new IllegalArgumentException("Grupo Diagnóstico Lacunas deve ser informado.");
		}
		criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_LUJ_LUH_SEQ.toString(), aelGrpDiagLacunas.getId().getLujLuhSeq()));
		criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_LUJ_SEQP.toString(), aelGrpDiagLacunas.getId().getLujSeqp()));
		criteria.add(Restrictions.eq(AelTxtDiagLacunas.Fields.LO1_SEQP.toString(), aelGrpDiagLacunas.getId().getSeqp()));
		
		criteria.setProjection(Projections.max(AelTxtDiagLacunas.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			seq = 1;
		} else {
			seq ++;
		}
		return new AelTxtDiagLacunasId(aelGrpDiagLacunas.getId().getLujLuhSeq(), aelGrpDiagLacunas.getId().getLujSeqp(),aelGrpDiagLacunas.getId().getSeqp(), seq);
	}
}
