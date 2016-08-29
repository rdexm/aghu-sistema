package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinMedicosAssistentes;

public class AinMedicosAssistentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinMedicosAssistentes> {

	private static final long serialVersionUID = -659440086787361644L;

	public List<AinMedicosAssistentes> pesquisarAinMedicosAssistentesPorInternacao(Integer intSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMedicosAssistentes.class);
		criteria.add(Restrictions.eq(AinMedicosAssistentes.Fields.INT_SEQ.toString(), intSeq));
		return executeCriteria(criteria);
	}
}
