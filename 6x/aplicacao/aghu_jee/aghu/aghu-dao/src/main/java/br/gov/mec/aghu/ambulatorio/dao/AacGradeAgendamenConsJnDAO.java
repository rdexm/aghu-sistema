package br.gov.mec.aghu.ambulatorio.dao;

import br.gov.mec.aghu.model.AacGradeAgendamenConsJn;

public class AacGradeAgendamenConsJnDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacGradeAgendamenConsJn> {

	private static final long serialVersionUID = -2328712107127718258L;


	public void persistirGradeAgendamenConsJn(
			AacGradeAgendamenConsJn gradeAgendamenCons) {

		if (gradeAgendamenCons != null && gradeAgendamenCons.getSeqJn() == null) {
			this.persistir(gradeAgendamenCons);
			this.flush();
		}
	}
}
