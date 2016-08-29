package br.gov.mec.aghu.estoque.dao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceItemDfHistorico;

public class SceItemDfHistoricoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemDfHistorico> {

	private static final long serialVersionUID = 6064647222897373624L;

	public Integer somarQuantidadePorHistoricoItemDevolucaoFornecedor(Integer dfsSeq, Integer nroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemDfHistorico.class,"IDH");
		criteria.setProjection(Projections.sum("IDH."+SceItemDfHistorico.Fields.QUANTIDADE.toString()));
		criteria.add(Restrictions.eq("IDH."+SceItemDfHistorico.Fields.DFS_SEQ.toString(), dfsSeq));
		criteria.add(Restrictions.eq("IDH."+SceItemDfHistorico.Fields.IDF_NUMERO.toString(), Short.valueOf(nroItem.toString())));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
}