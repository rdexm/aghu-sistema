package br.gov.mec.aghu.configuracao.dao;


import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.RapServidores;

public class AghProfissionaisEquipeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghProfissionaisEquipe> {

	private static final long serialVersionUID = 6808304953782297388L;

	public void removerAghProfissionaisEquipePorAGHEquipe(Integer equipe){
		final String hql = "delete from "+AghProfissionaisEquipe.class.getName()+" ape "
				+ " where "+AghProfissionaisEquipe.Fields.EQP_SEQ.toString()+" = :equipe";
		
		Query query = createQuery(hql);
		query.setParameter("equipe", equipe.intValue());
		
		query.executeUpdate();
	}
	
	public List<AghProfissionaisEquipe> pesquisarServidoresPorEquipe(Integer eqpSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghProfissionaisEquipe.class);
		criteria.add(Restrictions.eq(AghProfissionaisEquipe.Fields.EQUIPE_SEQ.toString(), eqpSeq));
		criteria.createAlias(AghProfissionaisEquipe.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria);
	}

	public List<AghProfissionaisEquipe> pesquisarEquipesPorProfissionalPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores servidor) {
		DetachedCriteria criteria = obterCriteriaPesquisaEquipesPorProfissionalPaginado(servidor);
		criteria.addOrder(Order.asc(AghProfissionaisEquipe.Fields.EQUIPE_SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long countPesquisarEquipesPorProfissionalPaginado(RapServidores servidor) {
		DetachedCriteria criteria = obterCriteriaPesquisaEquipesPorProfissionalPaginado(servidor);
		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisaEquipesPorProfissionalPaginado(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfissionaisEquipe.class);
		criteria.createAlias(AghProfissionaisEquipe.Fields.EQUIPE.toString(), "equipe");
		if (servidor != null) {
			criteria.add(Restrictions.eq(AghProfissionaisEquipe.Fields.SERVIDOR.toString(), servidor));
		}
		return criteria;
	}
}