package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AinObservacoesPacAltaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinObservacoesPacAlta> {
	
	private static final long serialVersionUID = -3984809494220311524L;

	public List<AinObservacoesPacAlta> pesquisaObservacoesPacAlta(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = createPesquisaObservacoesPacAltaCriteria(
				codigo, descricao, indSituacao);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisaObservacoesPacAltaCount(Integer codigo, String descricao, DominioSituacao indSituacao) {
		return executeCriteriaCount(createPesquisaObservacoesPacAltaCriteria(
				codigo, descricao, indSituacao));
	}

	private DetachedCriteria createPesquisaObservacoesPacAltaCriteria(
			Integer codigo, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinObservacoesPacAlta.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AinObservacoesPacAlta.Fields.CODIGO
					.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AinObservacoesPacAlta.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(
					AinObservacoesPacAlta.Fields.IND_SITUACAO.toString(),
					indSituacao));
		}

		return criteria;
	}
	
	public List<AinObservacoesPacAlta> pesquisarObservacoesPacAlta(Object objPesquisa) {
		String str = (String) objPesquisa;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinObservacoesPacAlta.class);

		if (CoreUtil.isNumeroInteger(str)) {
			criteria.add(Restrictions.eq(AinObservacoesPacAlta.Fields.CODIGO.toString(), Integer.parseInt(str)));
		}
		List<AinObservacoesPacAlta> list = executeCriteria(criteria);
		if(list != null && list.size() > 0){
			return list;
		}
		
		DetachedCriteria crit = DetachedCriteria.forClass(AinObservacoesPacAlta.class);
		if (StringUtils.isNotBlank(str)) {
			crit.add(Restrictions.ilike(AinObservacoesPacAlta.Fields.DESCRICAO.toString(), str, MatchMode.ANYWHERE));
		}
		return executeCriteria(crit);
		
	}	

	
	public AinObservacoesPacAlta obterObservacoesPacAlta(Integer ainObservacoesPacAltaCodigo) {
		AinObservacoesPacAlta retorno = this.obterPorChavePrimaria(ainObservacoesPacAltaCodigo);
		return retorno;
	}


}
