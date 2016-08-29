package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaNeonatologia;

public class MbcFichaNeonatologiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaNeonatologia> {

	private static final long serialVersionUID = -2115140027519145879L;

	public Long getCountMbcFichaNeonatologiaByMbcFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaNeonatologia.class);
		criteria.add(Restrictions.eq(MbcFichaNeonatologia.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		return executeCriteriaCount(criteria);
	}

	public List<MbcFichaNeonatologia> pesquisarMbcNeonatologiasByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaNeonatologia.class);
		
		criteria.add(Restrictions.eq(MbcFichaNeonatologia.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}	



}
