package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.PdtInstrDesc;

public class PdtInstrDescDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtInstrDesc> {

	private static final long serialVersionUID = 7808042636808204163L;
	
	public List<PdtInstrDesc> pesquisarPdtInstrDescPorDdtSeq(final Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtInstrDesc.class);
		criteria.createAlias(PdtInstrDesc.Fields.PDT_INSTRUMENTAIS.toString(), "INST", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(PdtInstrDesc.Fields.DDT_SEQ.toString(), ddtSeq));
		return executeCriteria(criteria);
	}

}
