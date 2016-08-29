package br.gov.mec.aghu.exames.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAelHrGradeDispTr;


public class VAelHrGradeDispTrDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelHrGradeDispTr> {

	
	private static final long serialVersionUID = -4355970597193963842L;

	public Long listarGradeDispPorHorarioExameDisp(Date hedDthrAgenda, Integer hedGaeSeqp, Short hedGaeUnfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelHrGradeDispTr.class);

		criteria.add(Restrictions.eq(VAelHrGradeDispTr.Fields.UNF_GRADE.toString(), hedGaeUnfSeq));
		criteria.add(Restrictions.eq(VAelHrGradeDispTr.Fields.SEQ_GRADE.toString(), hedGaeSeqp));
		criteria.add(Restrictions.eq(VAelHrGradeDispTr.Fields.DTHR_AGENDA.toString(), hedDthrAgenda));

		return executeCriteriaCount(criteria);
	}
}
