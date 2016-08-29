package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsSolicitacoesDoacoes;

public class AbsSolicitacoesDoacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsSolicitacoesDoacoes> {

	private static final long serialVersionUID = 3683922266599938438L;

	public List<AbsSolicitacoesDoacoes> pesquisarAbsSolicitacoesDoacoes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesDoacoes.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesDoacoes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(AbsSolicitacoesDoacoes.Fields.SEQUENCIA.toString()));

		return executeCriteria(criteria);
	}

	public Short obterMaxSeqAbsSolicitacoesDoacoes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesDoacoes.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesDoacoes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.max(AbsSolicitacoesDoacoes.Fields.SEQUENCIA.toString()));

		Short max = (Short) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			max = Short.valueOf((short) 0);
		}

		return ++max;
	}

	public Short obterSequenciaSolicitacoesDoacoes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesDoacoes.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesDoacoes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.max(AbsSolicitacoesDoacoes.Fields.SEQUENCIA.toString()));
		Short maxSequencia = (Short) executeCriteriaUniqueResult(criteria);
		if (maxSequencia == null) {
			return 1;
		} else {
			return maxSequencia++;
		}
	}
	
	public List<AbsSolicitacoesDoacoes> listarSolicitacoesDoacoes(Integer pacCodigo, Date dthrSolicitacao, LockMode lockMode) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesDoacoes.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesDoacoes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AbsSolicitacoesDoacoes.Fields.DTHR_SOLICITACAO.toString(), dthrSolicitacao));

		if (lockMode != null) {
			criteria.setLockMode(lockMode);
		}

		return executeCriteria(criteria);
	}
	
}
