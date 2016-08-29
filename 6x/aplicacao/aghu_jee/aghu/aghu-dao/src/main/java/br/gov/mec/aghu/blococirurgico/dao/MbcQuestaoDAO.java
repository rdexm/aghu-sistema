package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcQuestaoId;

public class MbcQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcQuestao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3289375382361137960L;
	
	
	@Override
	protected void obterValorSequencialId(MbcQuestao elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		MbcQuestaoId id = new MbcQuestaoId();
		id.setMtcSeq(elemento.getMotivoCancelamento().getSeq());
		
		final Integer maxSeqp = this.obterSeqpMax(elemento.getMotivoCancelamento().getSeq());
		if (maxSeqp != null) {
			id.setSeqp(maxSeqp+1);
		}else{
			id.setSeqp(1);
		}
		
		elemento.setId(id);
	}

	
	
	public Integer obterSeqpMax(Short mtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcQuestao.class);

		criteria.setProjection(Projections.max(MbcQuestao.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eq(MbcQuestao.Fields.MTC_SEQ.toString(), mtcSeq));

		final Object objMax = this.executeCriteriaUniqueResult(criteria);
		return (Integer) objMax;
	}

	
	
	
	protected DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcQuestao.class);
		return criteria;
	}
	
	public List<MbcQuestao> listarQuestoes(Short mtcSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		
		if(mtcSeq != null) {
			criteria.add(Restrictions.eq(MbcQuestao.Fields.MTC_SEQ.toString(), mtcSeq));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public List<MbcQuestao> pesquisarQuestaoAtivaPorMtcSeq(Short mtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcQuestao.class);
		criteria.add(Restrictions.eq(MbcQuestao.Fields.MTC_SEQ.toString(), mtcSeq));
		criteria.add(Restrictions.eq(MbcQuestao.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<MbcQuestao> pesquisarQuestaoAtivaPorMtcSeqSeqp(Short mtcSeq, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcQuestao.class);
		criteria.add(Restrictions.eq(MbcQuestao.Fields.MTC_SEQ.toString(), mtcSeq));
		criteria.add(Restrictions.eq(MbcQuestao.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MbcQuestao.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

}
