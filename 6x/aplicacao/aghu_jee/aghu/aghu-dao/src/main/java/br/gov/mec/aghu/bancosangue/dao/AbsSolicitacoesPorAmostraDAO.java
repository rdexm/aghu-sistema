package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;

public class AbsSolicitacoesPorAmostraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsSolicitacoesPorAmostra> {

	private static final long serialVersionUID = -5678559621320364071L;

	public List<AbsSolicitacoesPorAmostra> pesquisarAbsSolicitacoesPorAmostra(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesPorAmostra.class);
		
		criteria.add(Restrictions.eq(AbsSolicitacoesPorAmostra.Fields.PACIENTE_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

}
