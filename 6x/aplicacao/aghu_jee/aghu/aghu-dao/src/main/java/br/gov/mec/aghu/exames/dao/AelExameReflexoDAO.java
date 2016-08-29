package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.model.AelExameReflexoId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;

public class AelExameReflexoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameReflexo> {

	private static final long serialVersionUID = -3270125139840576827L;

	public List<AelExameReflexo> buscarAelExamesReflexo(final String exaSigla, final Integer manSeq) {
		return executeCriteria(obterCriteriabuscarAelExamesReflexo(exaSigla, manSeq));
	}

	public List<AelExameReflexo> buscarAelExamesReflexoAtivos(final String exaSigla, final Integer manSeq) {
		final DetachedCriteria criteria = obterCriteriabuscarAelExamesReflexo(exaSigla, manSeq);
		criteria.add(Restrictions.eq(AelExameReflexo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriabuscarAelExamesReflexo(final String exaSigla, final Integer manSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameReflexo.class);

		criteria.createAlias(AelExameReflexo.Fields.AEL_RESULTADO_CODIFICADO.toString(), "RCO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameReflexo.Fields.AEL_CAMPO_LAUDO.toString(), "CAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameReflexo.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameReflexo.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameReflexo.Fields.AEL_EXAMES_MATERIAL_ANALISE_REFLEXO.toString(), "EMR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMR.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMR.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "MTX", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelExameReflexo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelExameReflexo.Fields.EMA_MAN_SEQ.toString(), manSeq));
		return criteria;
	}

	public Short getNextSeqp(AelExameReflexoId id) {
		final DetachedCriteria criteria = obterCriteriabuscarAelExamesReflexo(id.getEmaExaSigla(), id.getEmaManSeq());
		criteria.setProjection(Projections.max(AelExameReflexo.Fields.SEQP.toString()));
		Object res = executeCriteriaUniqueResult(criteria);
		if (res == null) {
			return 1;
		}
//		return Integer.valueOf((((Number) res).intValue() + 1)).shortValue();
		return (short) (((Number)res).intValue() + 1);
	}
}