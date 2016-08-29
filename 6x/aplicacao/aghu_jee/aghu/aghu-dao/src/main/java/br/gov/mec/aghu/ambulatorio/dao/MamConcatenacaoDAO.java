package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamConcatenacao;

public class MamConcatenacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamConcatenacao> {
	
	private static final long serialVersionUID = -8809521087158398136L;

	public List<MamConcatenacao> pesquisarConcatenacaoAtivaPorIdQuestao(Integer qusQutSeq, Short qusSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConcatenacao.class,"CNN");
		
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.QUS_QUT_SEQ_CONCATENADO.toString(), qusQutSeq));
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.QUS_SEQP_CONCATENADO.toString(), qusSeq));
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * #50937
	 * cursor: cur_concat
	 * @param qusQutSeq
	 * @param qusSeq
	 * @return
	 */
	public MamConcatenacao obterConcatenacaoAtivaPorIdQuestao(Integer qusQutSeq, Short qusSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamConcatenacao.class,"CNN");
		
		criteria.setProjection(Projections.property(MamConcatenacao.Fields.CARACTER.toString()).as(MamConcatenacao.Fields.CARACTER.toString()));
		
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.QUS_QUT_SEQ_CONCATENADO.toString(), qusQutSeq));
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.QUS_SEQP_CONCATENADO.toString(), qusSeq));
		criteria.add(Restrictions.eq("CNN." + MamConcatenacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MamConcatenacao.class));
		
		return (MamConcatenacao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_c1
	 * @param qusQutSeq
	 * @param qusSeq
	 * @return
	 */
	public Boolean existeConcatenacaoPorIdQuestao(Integer qusQutSeq, Short qusSeq){
		
		MamConcatenacao mamConcatenacao = this.obterConcatenacaoAtivaPorIdQuestao(qusQutSeq, qusSeq);

		return (mamConcatenacao != null);
	}

}
