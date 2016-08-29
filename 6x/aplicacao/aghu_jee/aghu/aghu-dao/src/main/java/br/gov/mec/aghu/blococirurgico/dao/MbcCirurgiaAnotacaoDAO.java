package br.gov.mec.aghu.blococirurgico.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcCirurgiaAnotacao;
import br.gov.mec.aghu.model.RapServidores;

public class MbcCirurgiaAnotacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCirurgiaAnotacao> {

	private static final long serialVersionUID = -6520779999200201260L;

	public List<MbcCirurgiaAnotacao> obterCirurgiaAnotacaoPorSeqCirurgia(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgiaAnotacao.class);
		criteria.createAlias(MbcCirurgiaAnotacao.Fields.RAP_SERVIDORES.toString(), "RAP");
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS");
		criteria.createAlias(MbcCirurgiaAnotacao.Fields.FCC_CENTRO_CUSTOS.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MbcCirurgiaAnotacao.Fields.MBC_CIRURGIAS_SEQ.toString(), seq));
		criteria.addOrder(Order.desc(MbcCirurgiaAnotacao.Fields.CRIADO_EM.toString()));

		return this.executeCriteria(criteria);
	}
	
	public Double obterCirurgiaAnotacaoMaxSeqp(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgiaAnotacao.class);
		
		criteria.setProjection(Projections.max(MbcCirurgiaAnotacao.Fields.ID_SEQP.toString()));
		
		criteria.add(Restrictions.eq(MbcCirurgiaAnotacao.Fields.ID_CRG_SEQ.toString(), seq));

		return (Double) this.executeCriteriaUniqueResult(criteria);
	}
}