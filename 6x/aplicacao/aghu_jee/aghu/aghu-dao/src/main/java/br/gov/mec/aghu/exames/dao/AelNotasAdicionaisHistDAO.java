package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelNotasAdicionaisHist;
import br.gov.mec.aghu.model.RapServidores;

public class AelNotasAdicionaisHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNotasAdicionaisHist> {

	private static final long serialVersionUID = 8760618483429963567L;

	private DetachedCriteria preparaCriteriaNotasAdicionaisItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelNotasAdicionaisHist.class,"AelNotasAdicionaisHist");
		criteria.add(Restrictions.eq(AelNotasAdicionaisHist.Fields.ISE_SOE_SEQ.toString(),soeSeq));
		criteria.add(Restrictions.eq(AelNotasAdicionaisHist.Fields.ISE_SEQP.toString(),seqp));
		return criteria;
	}
	
	/**
	 * História #18999 consulta C1
	 */
	public boolean possuiNotasAdicionaisItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = preparaCriteriaNotasAdicionaisItemSolicitacaoExame(soeSeq,seqp);
		boolean possuiNotasAdicionais = false;
		Long count = executeCriteriaCount(criteria);
		if (count != null && count > 0) {
			possuiNotasAdicionais = true;
		}
		return possuiNotasAdicionais;
	}
	
	/**
	 * História #18999 consulta C2
	 */
	public List<AelNotasAdicionaisHist> pesquisarNotasAdicionaisItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = preparaCriteriaNotasAdicionaisItemSolicitacaoExame(soeSeq,seqp);

		criteria.createAlias("AelNotasAdicionaisHist."+AelNotasAdicionaisHist.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(AelNotasAdicionaisHist.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
}