package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmDesmembramentoDAO extends BaseDao<PtmDesmembramento> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6710913849412799745L;
	
	public List<PtmDesmembramento> pesquisarDesmembramentoPorAvtSeq(Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmDesmembramento.class, "D");
		criteria.createAlias("D." + PtmDesmembramento.Fields.SERVIDOR.toString(), "FCC", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("D." + PtmDesmembramento.Fields.AVT_SEQ.toString(), avtSeq));
		criteria.addOrder(Order.asc("D."+PtmDesmembramento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

}
