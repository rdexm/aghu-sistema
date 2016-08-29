package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpaUsoOrdCuidado;


public class MpaUsoOrdCuidadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaUsoOrdCuidado>{

	private static final long serialVersionUID = 1877725585556289711L;

	public List<MpaUsoOrdCuidado> pesquisarUsoOrdCuidadosPorPrescricaoCuidado(Long pcuSeqAnt, Integer pmeAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaUsoOrdCuidado.class);
		
		criteria.add(Restrictions.eq(MpaUsoOrdCuidado.Fields.PCU_ATD_SEQ.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(MpaUsoOrdCuidado.Fields.PCU_SEQ.toString(), pcuSeqAnt));
			
		return executeCriteria(criteria);

	}

}
