package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.CacheMode;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;

public class SigProcessamentoPassosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProcessamentoPassos> {

	private static final long serialVersionUID = 2450525027539609471L;

	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(50);
		sql.append(" DELETE " ).append( SigProcessamentoPassos.class.getSimpleName().toString() ).append( " ca ");
		sql.append(" WHERE ca." ).append( SigProcessamentoPassos.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}

	public List<SigProcessamentoPassos> pesquisarSigProcessamentoPassos(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoPassos.class);
		criteria.createAlias(SigProcessamentoPassos.Fields.PASSOS.toString(), "passo", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SigProcessamentoPassos.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		criteria.addOrder(Order.asc("passo."+SigPassos.Fields.ORDEM_EXECUCAO.toString()));
		return this.executeCriteria(criteria, false, CacheMode.IGNORE);
	}
	
	public SigProcessamentoPassos obterSigProcessamentoPassos(SigProcessamentoCusto processamentoCusto, SigPassos passo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoPassos.class);
		criteria.setFetchMode(SigProcessamentoPassos.Fields.PASSOS.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigProcessamentoPassos.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		criteria.add(Restrictions.eq(SigProcessamentoPassos.Fields.PASSOS.toString(), passo));
		return (SigProcessamentoPassos) this.executeCriteriaUniqueResult(criteria);
	}
}