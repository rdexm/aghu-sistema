package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamItemEvolucoes;

public class MamItemEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamItemEvolucoes> {

	private static final long serialVersionUID = -2851691163867570484L;

	public DetachedCriteria criteriaPesquisarItemEvolucoesPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemEvolucoes.class);
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.EVO_SEQ.toString(),
				evoSeq));
		return criteria;
	}
	
	public List<MamItemEvolucoes> pesquisarItemEvolucoesPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemEvolucoesPorEvolucao(evoSeq);
		return executeCriteria(criteria);
	}
	
	public Long pesquisarItemEvolucoesPorEvolucaoCount(Long evoSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemEvolucoesPorEvolucao(evoSeq);
		criteria.add(Restrictions.isNotNull(MamItemEvolucoes.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}
	
	public MamItemEvolucoes primeiroItemEvolucoesPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemEvolucoesPorEvolucao(evoSeq);
		List<MamItemEvolucoes> lista = executeCriteria(criteria, 0, 1, null, true);
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
	}	
	// #49956 C3
	public List<MamItemEvolucoes> pesquisarItemEvolucaoPorEvolucaoTipoItem(Long evoSeq, Integer tieSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemEvolucoes.class);
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.TIE_SEQ.toString(), tieSeq));
		return executeCriteria(criteria);
	}

	public Boolean pesquisarExisteItemEvolucaoPorEvolucaoTipoItem(Long evoSeq, Integer tieSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemEvolucoes.class);
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.TIE_SEQ.toString(), tieSeq));
		return executeCriteriaCount(criteria) > 0;
	}

	public List<MamItemEvolucoes> pesquisarItemEvolucoesPorEvolucaoEDescricaoNula(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemEvolucoes.class);
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.EVO_SEQ.toString(),evoSeq));
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.DESCRICAO.toString(),null));

		return executeCriteria(criteria);
	}

	/**
	 * #49992 - Consulta para obter cursor CUR_IEV P2
	 * @param evoSeq
	 * @param tieSeq
	 * @return
	 */
	public boolean obterCurIev(Long evoSeq, Integer tieSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemEvolucoes.class);
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamItemEvolucoes.Fields.TIE_SEQ.toString(), tieSeq));
		
		return executeCriteriaExists(criteria);
	}
}