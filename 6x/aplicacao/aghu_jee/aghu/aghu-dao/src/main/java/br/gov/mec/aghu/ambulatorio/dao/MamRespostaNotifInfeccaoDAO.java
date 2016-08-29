package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccao;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccaoId;

public class MamRespostaNotifInfeccaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRespostaNotifInfeccao> {

	private static final long serialVersionUID = -931668994330394927L;

	public MamRespostaNotifInfeccao obterAtualizaLocalNotificacaoOrigem(final MamRespostaNotifInfeccaoId id) {

		if (id == null) {
			throw new IllegalArgumentException();
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaNotifInfeccao.class);

		criteria.createAlias(MamRespostaNotifInfeccao.Fields.CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias(MamRespostaNotifInfeccao.Fields.MAM_PISTA_NOTIF_INFECCOES.toString(), "PNN", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(MamRespostaNotifInfeccao.Fields.ID.toString(), id));

		return (MamRespostaNotifInfeccao) executeCriteriaUniqueResult(criteria);
	}

}
