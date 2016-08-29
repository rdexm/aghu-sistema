package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapTipoInformacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapTipoInformacoes> {

	private static final long serialVersionUID = 5616977744017191015L;

	protected RapTipoInformacoesDAO() {
	}
	
	public List<RapTipoInformacoes> pesquisarTipoInformacoes(
			Integer firstResult, Integer maxResults, Integer codigo,
			String descricao) {
		DetachedCriteria criteria = criarCriteriaTipoInformacoess(codigo,
				descricao);

		criteria.addOrder(Order.asc(RapTipoInformacoes.Fields.SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				RapTipoInformacoes.Fields.SEQ.toString(), true);
	}
	
	public Long obterTipoInformacoesCount(Integer codigo, String descricao) {
		DetachedCriteria criteria = criarCriteriaTipoInformacoess(codigo,
				descricao);

		return executeCriteriaCountDistinct(criteria,
				RapTipoInformacoes.Fields.SEQ.toString(), true);
	}
	
	public Long pesquisarTipoInformacoesPorDescricaoCount(String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoInformacoes.class);
		criteria.add(Restrictions.eq(RapTipoInformacoes.Fields.DESCRICAO.toString(), descricao));

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaPesquisarTipoInformacoes(Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoInformacoes.class);

		if (objPesquisa != null && StringUtils.isNotBlank(objPesquisa.toString())){
			if(CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq(RapTipoInformacoes.Fields.SEQ.toString(),Integer.valueOf(objPesquisa.toString())));
				
			} else {
				criteria.add(Restrictions.ilike(RapTipoInformacoes.Fields.DESCRICAO.toString(),objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	public Long pesquisarTipoInformacoesSuggestionCount(Object objPesquisa) {
		return executeCriteriaCount(getCriteriaPesquisarTipoInformacoes(objPesquisa));
	}	
	
	public List<RapTipoInformacoes> pesquisarTipoInformacoesSuggestion(Object objPesquisa) {
		final DetachedCriteria criteria = getCriteriaPesquisarTipoInformacoes(objPesquisa);
		return executeCriteria(criteria, 0, 100, null, false);
	}	
	
	public  List<Integer> pesquisarTipoInformacoesPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoInformacoes.class);
		criteria.setProjection(
				Projections.projectionList().add(Projections.property(RapTipoInformacoes.Fields.SEQ.toString())));
		criteria.add(Restrictions.ilike(RapTipoInformacoes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		
		return executeCriteria(criteria);
	}
		
	private DetachedCriteria criarCriteriaTipoInformacoess(Integer codigo,
			String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapTipoInformacoes.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapTipoInformacoes.Fields.SEQ.toString(), codigo));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					RapTipoInformacoes.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}

		return criteria;
	}	
}
