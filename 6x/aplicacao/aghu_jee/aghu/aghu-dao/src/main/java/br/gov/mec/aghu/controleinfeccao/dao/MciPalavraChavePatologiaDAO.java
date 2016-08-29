package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;

public class MciPalavraChavePatologiaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciPalavraChavePatologia> {

	private static final long serialVersionUID = -7284523728274911615L;

	
	public List<MciPalavraChavePatologia> listarPalavraChavePatologia(Integer codigoPatologia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPalavraChavePatologia.class, "PCP");
		criteria.createAlias("PCP." + MciPalavraChavePatologia.Fields.MCI_PATOLOGIA_INFECCAO.toString(), "MPI", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("MPI." + MciPatologiaInfeccao.Fields.SEQ.toString(), codigoPatologia));
		
		return executeCriteria(criteria);
	}
	
	public Short obterMaxSeqpPalavraChavePatologia(Integer codigoPatologia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPalavraChavePatologia.class);
		criteria.add(Restrictions.eq(MciPalavraChavePatologia.Fields.PAI_SEQ.toString(), codigoPatologia));
		
		criteria.setProjection(Projections.max(MciPalavraChavePatologia.Fields.SEQP.toString()));
		
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
}
