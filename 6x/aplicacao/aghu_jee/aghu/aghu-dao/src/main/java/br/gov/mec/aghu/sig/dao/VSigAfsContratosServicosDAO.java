package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.view.VSigAfsContratosServicos;

public class VSigAfsContratosServicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VSigAfsContratosServicos> {

	
	private static final long serialVersionUID = 5412355912400033045L;

	public List<VSigAfsContratosServicos> obterAfPorContrato(ScoContrato contrato) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigAfsContratosServicos.class);
		criteria.add(Restrictions.eq(VSigAfsContratosServicos.Fields.CONTRATO.toString(), contrato.getSeq()));
		return executeCriteria(criteria);
	}
	
	public VSigAfsContratosServicos obterAfPorId(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigAfsContratosServicos.class);
		criteria.add(Restrictions.eq(VSigAfsContratosServicos.Fields.SEQ.toString(), seq));
		List<VSigAfsContratosServicos> list = this.executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
