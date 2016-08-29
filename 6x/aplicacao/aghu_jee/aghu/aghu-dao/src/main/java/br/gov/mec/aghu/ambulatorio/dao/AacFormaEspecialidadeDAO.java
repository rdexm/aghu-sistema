package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaEspecialidade;

public class AacFormaEspecialidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacFormaEspecialidade> {

	private static final long serialVersionUID = -2339912023736865464L;

	public Long listarFormasEspecialidadeCount(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaEspecialidade.class);

		criteria.add(Restrictions.eq(AacFormaEspecialidade.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));

		return executeCriteriaCount(criteria);
	}
	
	public List<AacFormaEspecialidade> listaFormasEspecialidadePorFormaAgendaneto(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaEspecialidade.class);

		criteria.createAlias(AacFormaEspecialidade.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AacFormaEspecialidade.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));

		return executeCriteria(criteria);
	}

}
