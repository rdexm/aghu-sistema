package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaGrandePorte;

public class MbcFichaGrandePorteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaGrandePorte> {

	private static final long serialVersionUID = -5452717167623741423L;

	public Long getCountMbcFichaGrandePorteByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaGrandePorte.class);
		criteria.add(Restrictions.eq(MbcFichaGrandePorte.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		return executeCriteriaCount(criteria);
	}

	public List<MbcFichaGrandePorte> pesquisarMbcFichaGrandePorteByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaGrandePorte.class);
		criteria.add(Restrictions.eq(MbcFichaGrandePorte.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		return executeCriteria(criteria);
	}




}
