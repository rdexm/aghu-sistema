package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;

public class AelQuestoesQuestionarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelQuestoesQuestionario> {

	private static final long serialVersionUID = -2026892977377313809L;

	public List<AelQuestoesQuestionario> buscarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq, final Integer first, final Integer max,
			final String order, final boolean asc) {
		if (qtnSeq == null) {
			throw new IllegalArgumentException("Deve informar um código de questionário.");
		}
		final DetachedCriteria criteria = criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(qtnSeq);
		criteria.addOrder(Order.asc("GRP." + AelGrupoQuestao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(criteria.getAlias() + "." + AelQuestoesQuestionario.Fields.ORDEM.toString()));
		return executeCriteria(criteria, first, max, order, asc);
	}

	public List<AelQuestoesQuestionario> buscarAelQuestoesQuestionarioPorQuestionario(final Integer[] qtnSeq) {
		if (qtnSeq == null) {
			throw new IllegalArgumentException("Deve informar um código de questionário.");
		}
		final DetachedCriteria criteria = criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(qtnSeq);
		criteria.addOrder(Order.asc("GRP." + AelGrupoQuestao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(criteria.getAlias() + "." + AelQuestoesQuestionario.Fields.ORDEM.toString()));
		criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		return executeCriteria(criteria);
	}
	
	public List<AelQuestoesQuestionario> buscarAelQuestoesQuestionarioOrderByOrdem(final Integer[] qtnSeq) {
		if (qtnSeq == null) {
			throw new IllegalArgumentException("Deve informar um código de questionário.");
		}
		final DetachedCriteria criteria = criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(qtnSeq);
		criteria.addOrder(Order.asc(criteria.getAlias() + "." + AelQuestoesQuestionario.Fields.ORDEM.toString()));
		criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		return executeCriteria(criteria);
	}

	public Long contarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq) {
		return executeCriteriaCount(criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(qtnSeq));
	}

	private DetachedCriteria criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(final Integer[] qtnSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestoesQuestionario.class);
		criteria.createAlias(AelQuestoesQuestionario.Fields.GRUPO_QUESTAO.toString(), "GRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelQuestoesQuestionario.Fields.QUESTAO.toString(), "QUE");
		criteria.createAlias(AelQuestoesQuestionario.Fields.VALOR_VALIDO_QUESTOES.toString(), "VALQUE", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq( AelQuestoesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("QUE." + AelQuestao.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.in(AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString(), qtnSeq));
         		
		return criteria;
	}

	private DetachedCriteria criarCriteriaBuscarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestoesQuestionario.class);
		criteria.createAlias(AelQuestoesQuestionario.Fields.GRUPO_QUESTAO.toString(), "GRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelQuestoesQuestionario.Fields.QUESTAO.toString(), "QT", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString(), qtnSeq));

		return criteria;
	}

}
