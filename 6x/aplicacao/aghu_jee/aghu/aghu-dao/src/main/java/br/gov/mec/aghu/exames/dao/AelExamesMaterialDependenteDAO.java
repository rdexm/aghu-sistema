package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AelExamesMaterialDependenteDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesDependentes> {
		
	private static final long serialVersionUID = 3998948778149512345L;

	public List<AelExamesDependentes> obterExamesDependentes(String sigla, Integer manSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesDependentes.class);
		
		criteria.createAlias(AelExamesDependentes.Fields.EXAME_DEPENDENTE.toString(), "EXP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExamesDependentes.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(), manSeq));

		return executeCriteria(criteria);
	}
	
	public AelExamesDependentes buscarAelExamesDependenteById(AelExamesDependentesId id) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesDependentes.class);

		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_EXA_SIGLA_DEPENDENTE.toString(), id.getEmaExaSiglaEhDependente()));
		criteria.add(Restrictions.eq(AelExamesDependentes.Fields.EMA_MAN_SEQ_DEPENDENTE.toString(), id.getEmaManSeqEhDependente()));

		return (AelExamesDependentes) executeCriteriaUniqueResult(criteria);
	}
	
}