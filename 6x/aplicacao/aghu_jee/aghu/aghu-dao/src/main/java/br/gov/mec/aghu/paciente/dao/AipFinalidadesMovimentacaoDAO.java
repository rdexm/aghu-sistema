package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipFinalidadesMovimentacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipFinalidadesMovimentacao> {

	private static final long serialVersionUID = -8971875245429257465L;

	private DetachedCriteria createPesquisaCriteria(Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFinalidadesMovimentacao.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.CODIGO.toString(), codigo.shortValue()));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AipFinalidadesMovimentacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.IND_ATIVO.toString(), situacao));
		}
		return criteria;
	}

	private DetachedCriteria createPesquisaFinMovCriteria(Integer codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFinalidadesMovimentacao.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.CODIGO.toString(), codigo.shortValue()));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AipFinalidadesMovimentacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	public List<AipFinalidadesMovimentacao> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public List<AipFinalidadesMovimentacao> pesquisaFinalidadesMovimentacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisaCount(Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	public Long pesquisaFinalidadeMovimentacaoCount(Integer codigo, String descricao) {
		DetachedCriteria criteria = createPesquisaFinMovCriteria(codigo, descricao);
		return executeCriteriaCount(criteria);
	}

	public List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AipFinalidadesMovimentacao.class);
			criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));
			criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.IND_ATIVO.toString(), DominioSituacao.A));
			List<AipFinalidadesMovimentacao> list = executeCriteria(criteria);
			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AipFinalidadesMovimentacao.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AipFinalidadesMovimentacao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(AipFinalidadesMovimentacao.Fields.IND_ATIVO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AipFinalidadesMovimentacao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
}
