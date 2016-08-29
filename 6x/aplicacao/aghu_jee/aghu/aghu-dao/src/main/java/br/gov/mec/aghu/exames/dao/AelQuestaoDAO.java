package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelQuestao> {

	private static final long serialVersionUID = -912647077246431330L;

	public List<AelQuestao> buscarAelQuestao(final AelQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		return executeCriteria(criarCriteriaBuscarAelQuestao(questao), arg0, arg1, arg2, arg3);
	}

	private DetachedCriteria criarCriteriaBuscarAelQuestao(final AelQuestao questao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestao.class);

		if (questao.getSeq() != null) {
			criteria.add(Restrictions.eq(AelQuestao.Fields.SEQ.toString(), questao.getSeq()));
		}
		if (StringUtils.isNotEmpty(questao.getDescricao())) {
			criteria.add(Restrictions.ilike(AelQuestao.Fields.DESCRICAO.toString(), questao.getDescricao(), MatchMode.ANYWHERE));
		}
		if (questao.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(AelQuestao.Fields.IND_SITUACAO.toString(), questao.getIndSituacao()));
		}
		if (StringUtils.isNotEmpty(questao.getMascara())) {
			criteria.add(Restrictions.ilike(AelQuestao.Fields.MASCARA.toString(), questao.getMascara(), MatchMode.ANYWHERE));
		}
		if (questao.getTipoDados() != null) {
			criteria.add(Restrictions.eq(AelQuestao.Fields.TIPO_DADOS.toString(), questao.getTipoDados()));
		}

		return criteria;
	}

	public Long contarAelQuestao(final AelQuestao questao) {
		return executeCriteriaCount(criarCriteriaBuscarAelQuestao(questao));
	}

	public Long contarReferenciaQuestionario(final AelQuestao aelQuestao) {
		return executeCriteriaCount(criarReferenciaQuestionario(aelQuestao));
	}
	
	private DetachedCriteria criarReferenciaQuestionario(final AelQuestao questao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestoesQuestionario.class);
		criteria.add(Restrictions.eq(AelQuestoesQuestionario.Fields.SEQ_QUESTAO.toString(), questao.getSeq()));
		return criteria;
	}

	private DetachedCriteria criarBuscarAelQuestao(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelQuestao.class);
		if (objPesquisa != null) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq(AelQuestao.Fields.SEQ.toString(), Integer.parseInt(objPesquisa.toString())));
			} else {
				criteria.add(Restrictions.ilike(AelQuestao.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public List<AelQuestao> buscarAelQuestaoSuggestion(final Object objPesquisa, final Integer first, final Integer max, final String order, final boolean asc) {
		final DetachedCriteria criteria = this.criarBuscarAelQuestao(objPesquisa);
		criteria.addOrder(Order.asc(AelQuestao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, first, max, order, asc);
	}

	public Long contarAelQuestaoSuggestion(final Object objPesquisa) {
		return executeCriteriaCount(this.criarBuscarAelQuestao(objPesquisa));
	}

}
