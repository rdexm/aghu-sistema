package br.gov.mec.aghu.perinatologia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoEscalaLeitoRns;

public class McoEscalaLeitoRnsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoEscalaLeitoRns> {

	private static final long serialVersionUID = 5568057670260672234L;

	public McoEscalaLeitoRns obterMcoEscalaLeitoRnsPorLeito(String leitoID) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoEscalaLeitoRns.class);
		criteria.add(Restrictions.eq(McoEscalaLeitoRns.Fields.LEITOID.toString(), leitoID));
		return (McoEscalaLeitoRns) executeCriteriaUniqueResult(criteria);
	}
}
