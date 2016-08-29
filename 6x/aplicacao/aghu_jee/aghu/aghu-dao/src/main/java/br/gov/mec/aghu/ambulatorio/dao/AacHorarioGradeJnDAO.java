package br.gov.mec.aghu.ambulatorio.dao;

import br.gov.mec.aghu.model.AacHorarioGradeJn;

public class AacHorarioGradeJnDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacHorarioGradeJn> {

	private static final long serialVersionUID = 7305431028745931970L;

	public void persistirHorarioGradeJn(
			AacHorarioGradeJn horarioGradeJn) {

		if (horarioGradeJn != null && horarioGradeJn.getSeqJn() == null) {
			this.persistir(horarioGradeJn);
			this.flush();
		}
	}
}
