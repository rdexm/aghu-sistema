package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamRespQuestEvolucoes;


public class MamRespQuestEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRespQuestEvolucoes>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6734022193643927987L;

	public List<MamRespQuestEvolucoes> pesquisarPorEvolucao(Long evoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespQuestEvolucoes.class);
		criteria.add(Restrictions.eq(MamRespQuestEvolucoes.Fields.REV_EVO_SEQ.toString(), evoSeq));
		return executeCriteria(criteria);
	}

	/**
	 * Verifica existência de registro de MamRespQuestEvolucoes para os filtros informados.
	 * 
	 * @param evoSeq
	 * @param qusQutSeq
	 * @param qusSeqp
	 * 
	 * @return Flag indicando existência de registro para os filtros informados
	 */
	public boolean verificarExistenciaRespQuestEvolucaoComRespostaOuValorValido(Long evoSeq, Integer qusQutSeq, Short qusSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespQuestEvolucoes.class);

		criteria.setProjection(Projections.property(MamRespQuestEvolucoes.Fields.REV_EVO_SEQ.toString()));

		criteria.add(Restrictions.eq(MamRespQuestEvolucoes.Fields.REV_EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamRespQuestEvolucoes.Fields.REV_QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq(MamRespQuestEvolucoes.Fields.REV_QUS_SEQP.toString(), qusSeqp));

		return executeCriteriaExists(criteria);
	}

}
