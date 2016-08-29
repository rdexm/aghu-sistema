package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacTipoConsultaSisreg;

public class AacTipoConsultaSisregDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultas> {

	private static final long serialVersionUID = -4805709007387887480L;

	public AacTipoConsultaSisreg obterTipoConsultaSisregPorSeq(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoConsultaSisreg.class);
		criteria.add(Restrictions.eq(AacTipoConsultaSisreg.Fields.SEQ.toString(), seq));
		return (AacTipoConsultaSisreg) executeCriteriaUniqueResult(criteria);
	}
}
