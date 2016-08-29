package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.VAelGradeHrAgenda;


public class VAelGradeHrAgendaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelGradeHrAgenda> {
	
	private static final long serialVersionUID = -5723780728437375164L;

	public List<VAelGradeHrAgenda> pesquisarGradeHorarioAgendaPorItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelGradeHrAgenda.class);
		criteria.add(Restrictions.eq(VAelGradeHrAgenda.Fields.GRADE.toString(), itemHorarioAgendado.getId().getHedGaeUnfSeq()));
		criteria.add(Restrictions.eq(VAelGradeHrAgenda.Fields.SEQ_GRADE.toString(), itemHorarioAgendado.getId().getHedGaeSeqp()));
		criteria.add(Restrictions.eq(VAelGradeHrAgenda.Fields.DTHR_AGENDA.toString(), itemHorarioAgendado.getId().getHedDthrAgenda()));
		return executeCriteria(criteria);
	}
}
