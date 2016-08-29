package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrupoQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoQuestao> {

	private static final long serialVersionUID = 2512359914663611971L;

	public List<AelGrupoQuestao> buscarAelGrupoQuestao(final AelGrupoQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		return executeCriteria(criarCriteriaBuscarAelGrupoQuestao(questao), arg0, arg1, arg2, arg3);
	}

	private DetachedCriteria criarCriteriaBuscarAelGrupoQuestao(final AelGrupoQuestao grupoQuestao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoQuestao.class);

		if (grupoQuestao.getSeq() != null) {
			criteria.add(Restrictions.eq(AelGrupoQuestao.Fields.SEQ.toString(), grupoQuestao.getSeq()));
		}
		if (StringUtils.isNotEmpty(grupoQuestao.getDescricao())) {
			criteria.add(Restrictions.ilike(AelGrupoQuestao.Fields.DESCRICAO.toString(), grupoQuestao.getDescricao(), MatchMode.ANYWHERE));
		}

		return criteria;
	}

	public Long contarAelGrupoQuestao(final AelGrupoQuestao grupoQuestao) {
		return executeCriteriaCount(criarCriteriaBuscarAelGrupoQuestao(grupoQuestao));
	}

	public List<AelGrupoQuestao> buscarAelGrupoQuestaoSuggestion(final Object objPesquisa, Integer first, Integer max, String order, boolean asc) {
		final DetachedCriteria criteria = this.criarCriteriaBuscarAelGrupoQuestaoSuggestion(objPesquisa);
		criteria.addOrder(Order.asc(AelGrupoQuestao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, first, max, order, asc) ;
	}

	public Long contarAelGrupoQuestaoSuggestion(final Object objPesquisa) {
		return executeCriteriaCount(this.criarCriteriaBuscarAelGrupoQuestaoSuggestion(objPesquisa));
	}
	
	private DetachedCriteria criarCriteriaBuscarAelGrupoQuestaoSuggestion(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoQuestao.class);
		
		if (objPesquisa != null) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq(AelGrupoQuestao.Fields.SEQ.toString(), Integer.parseInt(objPesquisa.toString())));
			} else {
				criteria.add(Restrictions.ilike(AelGrupoQuestao.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

}
