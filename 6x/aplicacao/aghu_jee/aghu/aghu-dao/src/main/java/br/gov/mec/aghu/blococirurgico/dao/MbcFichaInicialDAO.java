package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaInicial;

public class MbcFichaInicialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaInicial> {

	private static final long serialVersionUID = 8902185289571588393L;

	public List<MbcFichaInicial> pesquisarMbcFichasIniciais(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaInicial.class);
		criteria.add(Restrictions.eq(MbcFichaInicial.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}	



}
