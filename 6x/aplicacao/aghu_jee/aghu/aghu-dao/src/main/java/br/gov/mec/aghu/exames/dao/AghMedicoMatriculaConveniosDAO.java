package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghMedicoMatriculaConvenios;

public class AghMedicoMatriculaConveniosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghMedicoMatriculaConvenios> {
	
	private static final long serialVersionUID = 6268099177895188020L;

	public Long countMatriculaConvenioPorMedicoExterno(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoMatriculaConvenios.class);
		criteria.add(Restrictions.eq(AghMedicoMatriculaConvenios.Fields.MEX_SEQ.toString(), seq));
		
		return executeCriteriaCount(criteria);
	}
}
