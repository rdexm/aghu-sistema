package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtSolicTemp;

public class PdtSolicTempDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtSolicTemp>{

	private static final long serialVersionUID = -4646006165021182610L;

	public PdtSolicTemp obterSolicTempPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtSolicTemp.class, "pdt");
		criteria.add(Restrictions.eq("pdt." + PdtSolicTemp.Fields.DDT_SEQ.toString(), seq));		

		return (PdtSolicTemp) executeCriteriaUniqueResult(criteria);
	}

	public List<PdtSolicTemp> pesquisarPdtSolicTempPorDdtSeq(Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtSolicTemp.class);
		criteria.add(Restrictions.eq(PdtSolicTemp.Fields.DDT_SEQ.toString(), seq));		
		return executeCriteria(criteria);
	}
}
