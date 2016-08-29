package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelValorValidoQuestao;

public class AelValorValidoQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelValorValidoQuestao> {
	
	private static final long serialVersionUID = 328647615384304495L;

	public short obterSeqValorValidoQuestao(Integer questaoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorValidoQuestao.class);
		
		criteria.setProjection(Projections
				.max(AelValorValidoQuestao.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eq(AelValorValidoQuestao.Fields.QAO_SEQ.toString(), questaoSeq));

		Short valor = (Short) this.executeCriteriaUniqueResult(criteria);

		if (valor == null) {
			valor = 0;
		}
		return valor;
	}
	
	public List<AelValorValidoQuestao> buscarValoresValidosQuestaoPorQuestao(Integer questaoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorValidoQuestao.class);
		
		criteria.add(Restrictions.eq(AelValorValidoQuestao.Fields.QAO_SEQ.toString(), questaoId));
		
		return this.executeCriteria(criteria);
	}
	
	public Long contarValoresValidosQuestaoPorQuestao(Integer questaoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorValidoQuestao.class);
		
		criteria.add(Restrictions.eq(AelValorValidoQuestao.Fields.QAO_SEQ.toString(), questaoId));
		
		return this.executeCriteriaCount(criteria);
	}

	public List<AelValorValidoQuestao> buscarValoresValidosAtivosQuestaoPorQuestao(final Integer seq) {
		return executeCriteria(createCriteriaValoresValidosAtivosQuestaoPorQuestao(seq));
	}

	private DetachedCriteria createCriteriaValoresValidosAtivosQuestaoPorQuestao(final Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorValidoQuestao.class);
		
		criteria.add(Restrictions.eq(AelValorValidoQuestao.Fields.QAO_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelValorValidoQuestao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
}
