package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelLoteExame;


public class AelLoteExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelLoteExame> {		
	

	private static final long serialVersionUID = -1215749761485769673L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLoteExame.class);
		return criteria;
    }

	public List<AelLoteExame> pesquisaLotesExamesPorLoteExameUsual(Short leuSeq) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelLoteExame.Fields.EMA.toString(), "ema", JoinType.INNER_JOIN);
		criteria.createCriteria("ema."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria("ema."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "ema_ma", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelLoteExame.Fields.LEUSEQ.toString(), leuSeq));

		criteria.addOrder(Order.asc("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()));

		return executeCriteria(criteria);
	}
}