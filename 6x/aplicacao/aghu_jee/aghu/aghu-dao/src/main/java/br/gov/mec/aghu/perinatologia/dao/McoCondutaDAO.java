package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class McoCondutaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoConduta> {

	private static final long serialVersionUID = -3240249000141915791L;
	
	@Inject
	McoCondutaPorSituacaoECodOuDescricaoQueryBuilder mcoCondutaPorSituacaoECodOuDescricaoQueryBuilder;

	public List<McoConduta> listarCondutas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Long codigo,
			String descricao, Integer faturamento, DominioSituacao situacao) {

		DetachedCriteria criteria = this.montarCriterialistarCondutas(codigo, descricao, faturamento, situacao);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long listarCondutasCount(Long codigo, String descricao, Integer faturamento, DominioSituacao situacao) {

		DetachedCriteria criteria = this.montarCriterialistarCondutas(codigo, descricao, faturamento, situacao);

		return executeCriteriaCount(criteria);
	}

	public Boolean pesquisaCondutaExistente(String descricao) {

		DetachedCriteria criteria = this.montarCriteriaPesquisaCondutaPorDescricao(descricao);

		return  executeCriteriaExists(criteria);
	}

	private DetachedCriteria montarCriteriaPesquisaCondutaPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoConduta.class);

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.eq(McoConduta.Fields.DESCRICAO.toString(), descricao));
		}

		return criteria;
	}

	private DetachedCriteria montarCriterialistarCondutas(Long codigo, String descricao, Integer faturamento, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(McoConduta.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(McoConduta.Fields.SEQ.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(McoConduta.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (faturamento != null) {
			criteria.add(Restrictions.eq(McoConduta.Fields.COD.toString(), faturamento));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(McoConduta.Fields.IND_SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	public List<McoConduta> pesquisarMcoCondutaSuggestion(String strPesquisa, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = montarCriteriaCondutaPorSeqDescricaoSituacao(strPesquisa, indSituacao);
		return executeCriteria(criteria, 0, 100, McoConduta.Fields.DESCRICAO.toString(), true);
	}

	private DetachedCriteria montarCriteriaCondutaPorSeqDescricaoSituacao(String strPesquisa, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoConduta.class);
		
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(McoConduta.Fields.SEQ.toString(), Long.valueOf(strPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(McoConduta.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(McoConduta.Fields.IND_SITUACAO.toString(), indSituacao));
		return criteria;
	}

	public Long pesquisarMcoCondutaSuggestionCount(String strPesquisa, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = montarCriteriaCondutaPorSeqDescricaoSituacao(strPesquisa, indSituacao);
		return executeCriteriaCount(criteria);
	}

}

