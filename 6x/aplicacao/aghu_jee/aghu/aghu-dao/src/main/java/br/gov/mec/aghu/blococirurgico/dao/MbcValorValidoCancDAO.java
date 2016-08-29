package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.MbcValorValidoCancId;

public class MbcValorValidoCancDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcValorValidoCanc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 486833258475903121L;

	
	
	@Override
	protected void obterValorSequencialId(MbcValorValidoCanc elemento) {
		if (elemento == null || elemento.getQuestao() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		MbcValorValidoCancId id = new MbcValorValidoCancId();
		id.setQesMtcSeq(elemento.getQuestao().getId().getMtcSeq());
		id.setQesSeqp(elemento.getQuestao().getId().getSeqp());
		
		
		final Short maxSeqp = this.obterSeqpMax(
				elemento.getQuestao().getId().getMtcSeq(), 
				elemento.getQuestao().getId().getSeqp());
		if (maxSeqp != null) {
			id.setSeqp(Short.valueOf(String.valueOf(maxSeqp + 1)));
		}else{
			id.setSeqp(Short.valueOf("1"));
		}

		elemento.setId(id);
	}


	public Short obterSeqpMax(Short qesMtcSeq, Integer qesSeq) {
		DetachedCriteria criteria = this.obterCriteria();

		criteria.setProjection(Projections.max(MbcValorValidoCanc.Fields.SEQP.toString()));
		
		criteria = this.obterCriterioConsulta(criteria, qesMtcSeq, qesSeq);

		final Object objMax = this.executeCriteriaUniqueResult(criteria);
		return (Short) objMax;
	}

	
	
	protected DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcValorValidoCanc.class);
		return criteria;
	}
	
	
	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Short qesMtcSeq, Integer qesSeq) {
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.QES_MTC_SEQ.toString(), qesMtcSeq));
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.QES_SEQP.toString(), qesSeq));
		return criteria;
	}
	
	
	public List<MbcValorValidoCanc> listarMbcValorValidoCancPorQuestao(Short qesMtcSeq, Integer qesSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria = this.obterCriterioConsulta(criteria, qesMtcSeq, qesSeq);
		
		return this.executeCriteria(criteria);
	}
	
	public Long obterCountValorValidoAtivoPorQesMtcSeq(Short qesMtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcValorValidoCanc.class);
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.QES_MTC_SEQ.toString(), qesMtcSeq));
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcValorValidoCanc> pesquisarValorValidoCancAtivoPorQesMtcSeqEQesSeqp(Short qesMtcSeq, Integer qesSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.obterCriterioConsulta(criteria, qesMtcSeq, qesSeq);
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<MbcValorValidoCanc> pesquisarValorValidoCancAtivoPorQesMtcSeqQesSeqpESeqp(Short qesMtcSeq, Integer qesSeqp, Short seqp) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.obterCriterioConsulta(criteria, qesMtcSeq, qesSeqp);
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MbcValorValidoCanc.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}	
}
