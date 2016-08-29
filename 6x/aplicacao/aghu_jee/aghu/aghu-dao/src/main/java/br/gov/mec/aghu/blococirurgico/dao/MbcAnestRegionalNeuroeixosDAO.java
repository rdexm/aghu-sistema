package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAnestRegionalNeuroeixos;
import br.gov.mec.aghu.model.MbcFichaAnestesias;

public class MbcAnestRegionalNeuroeixosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAnestRegionalNeuroeixos> {

	private static final long serialVersionUID = 7725426353585116124L;

	public List<MbcAnestRegionalNeuroeixos> pesquisarMbcAnestRegNeuroeixoByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAnestRegionalNeuroeixos.class);
		criteria.createAlias(MbcAnestRegionalNeuroeixos.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcAnestRegionalNeuroeixos.Fields.MBC_NIVEL_BLOQUEIOS_BY_NBL_SEQ_INICIAL.toString(), "ini", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcAnestRegionalNeuroeixos.Fields.MBC_NIVEL_BLOQUEIOS_BY_NBL_SEQ_FINAL.toString(), "fim", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc(MbcAnestRegionalNeuroeixos.Fields.CRIADO_EM.toString()));
		
		return  executeCriteria(criteria);
	}
}
