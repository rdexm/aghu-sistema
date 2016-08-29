package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaViaAerea;
import br.gov.mec.aghu.model.MbcViaAerea;

public class MbcFichaViaAereaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaViaAerea> {

	private static final long serialVersionUID = -2802442312771170946L;

	public Long getCountMbcFichaViaAereaByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaViaAerea.class);
		criteria.createAlias(MbcFichaViaAerea.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteriaCount(criteria);
	}

	public List<MbcFichaViaAerea> pesquisarMbcFichaViaAereaByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaViaAerea.class);
		criteria.createAlias(MbcFichaViaAerea.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaViaAerea.Fields.MBC_VIA_AEREAS.toString(), "via");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc("via." + MbcViaAerea.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
}