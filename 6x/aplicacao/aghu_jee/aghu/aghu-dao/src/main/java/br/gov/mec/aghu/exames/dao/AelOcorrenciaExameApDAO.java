package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.RapServidores;

public class AelOcorrenciaExameApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelOcorrenciaExameAp> {
	
	private static final long serialVersionUID = -182780011775487181L;

	public Short geraSeqp(final Long luxSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelOcorrenciaExameAp.class);
		criteria.setProjection(Projections.max(AelOcorrenciaExameAp.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelOcorrenciaExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return Short.valueOf("1");
		}
		else {
			Short retorno = (Short) obj;
			return ++retorno;
		}
	}
	
	public List<AelOcorrenciaExameAp> buscarAelOcorrenciaExameApPorSeqExameAp(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelOcorrenciaExameAp.class);
		criteria.add(Restrictions.eq(AelOcorrenciaExameAp.Fields.LUX_SEQ.toString(), luxSeq));
		criteria.createAlias(AelOcorrenciaExameAp.Fields.RAP_SERVIDORES.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.desc(AelOcorrenciaExameAp.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
}