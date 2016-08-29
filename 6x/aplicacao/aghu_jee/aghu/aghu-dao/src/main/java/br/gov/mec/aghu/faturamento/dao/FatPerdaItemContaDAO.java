package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatPerdaItemConta;

public class FatPerdaItemContaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPerdaItemConta>{

	private static final long serialVersionUID = -6420754433223679213L;

	public Short obterProximoSeqp(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPerdaItemConta.class);

		criteria.setProjection(Projections.max(FatPerdaItemConta.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(FatPerdaItemConta.Fields.CTH_SEQ.toString(), cthSeq));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);

		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}

		return seqp;
	}

	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {
		Query query = createQuery("delete " + FatPerdaItemConta.class.getName() + 
				 " where " + FatPerdaItemConta.Fields.CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameter("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
}
