package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoProcedimentoObstetricosDAO extends
		BaseDao<McoProcedimentoObstetricos> {

	private static final long serialVersionUID = -797615665491568334L;

	public List<McoProcedimentoObstetricos> pesquisarMcoProcedimentoObstetricos(
			Integer firstResult, Integer maxResults,String orderProperty,boolean asc, Short seq,
			String descricao, Integer codigoPHI, DominioSituacao dominioSituacao) {
		final DetachedCriteria criteria = montarCriteriaPesquisa(seq, descricao,
				codigoPHI, dominioSituacao);
 
		return super.executeCriteria(criteria,firstResult,maxResults,orderProperty, asc);
	}

	private DetachedCriteria montarCriteriaPesquisa(Short seq, String descricao,
			Integer codigoPHI, DominioSituacao dominioSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoProcedimentoObstetricos.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.like(
					McoProcedimentoObstetricos.Fields.DESCRICAO.toString(),
					descricao.toLowerCase(), MatchMode.ANYWHERE).ignoreCase());
		}

		if (codigoPHI != null) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.CODIGOPHI.toString(),
					codigoPHI));
		}

		if (dominioSituacao != null) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.SITUACAO.toString(),
					dominioSituacao));
		}
		return criteria;
	}

	public boolean existeMcoProcedimentoObstetricosPorDescricao(String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoProcedimentoObstetricos.class);

		if (!StringUtils.isEmpty(descricao)) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.DESCRICAO.toString(),
					descricao.toLowerCase()).ignoreCase());
		}
		criteria.setProjection(Projections
				.count(McoProcedimentoObstetricos.Fields.DESCRICAO.toString()));
		Long result = (Long) super.executeCriteriaUniqueResult(criteria);
		return (result > 0);
	}

	public boolean descricaoFoiAlterada(Short seq, String descricao) {
	 
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoProcedimentoObstetricos.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.SEQ.toString(), seq));
		}

		if (!StringUtils.isEmpty(descricao)) {
			criteria.add(Restrictions.eq(
					McoProcedimentoObstetricos.Fields.DESCRICAO.toString(),
					descricao.toLowerCase()).ignoreCase());
		}
		criteria.setProjection(Projections
				.count(McoProcedimentoObstetricos.Fields.SEQ.toString()));
		Long result = (Long) super.executeCriteriaUniqueResult(criteria);
		return (result.equals(0l));
	}

	public Long pesquisarMcoProcedimentoObstetricosCount(Short seq,
			String descricao, Integer codigoPHI, DominioSituacao dominioSituacao) {
		DetachedCriteria criteria = montarCriteriaPesquisa(seq, descricao, codigoPHI, dominioSituacao);
		return super.executeCriteriaCount(criteria);
	}
}
