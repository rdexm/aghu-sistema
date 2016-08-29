package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpaUsoOrdNutricao;

public class MpaUsoOrdNutricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaUsoOrdNutricao>{

	private static final long serialVersionUID = 2018237336640241666L;

	public List<MpaUsoOrdNutricao> pesquisarUsoOrdNutricoesPorPrescricaoDieta(Long pdtSeqAnt, Integer pmeAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaUsoOrdNutricao.class);
		
		criteria.add(Restrictions.eq(MpaUsoOrdNutricao.Fields.PDT_ATD_SEQ.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(MpaUsoOrdNutricao.Fields.PDT_SEQ.toString(), pdtSeqAnt));
			
		return executeCriteria(criteria);

	}


}
