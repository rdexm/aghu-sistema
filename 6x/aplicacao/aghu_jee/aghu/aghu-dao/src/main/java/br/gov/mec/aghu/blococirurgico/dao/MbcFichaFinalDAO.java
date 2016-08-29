package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaFinal;

public class MbcFichaFinalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaFinal> {

	private static final long serialVersionUID = -4998233864142679565L;

	public List<MbcFichaFinal> pesquisarMbcFichasFinais(Long seqMbcFichaAnestesia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFinal.class);
		criteria.add(Restrictions.eq(MbcFichaFinal.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}

	public List<MbcFichaFinal> pesquisarMbcFichasFinais(
			Long seqMbcFichaAnestesia, Boolean nenhumEventoAdverso) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaFinal.class);
		criteria.add(Restrictions.eq(MbcFichaFinal.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.add(Restrictions.eq(MbcFichaFinal.Fields.NENHUM_EVENTO_ADVERSO.toString(), nenhumEventoAdverso));
		
		return executeCriteria(criteria);
	}


}
