package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.core.commons.CoreUtil;



public class AipBairrosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipBairros>{
	
	
	private static final long serialVersionUID = 7985085184495021435L;

	public List<AipBairros> pesquisarBairro(Integer firstResult,
			Integer maxResults, Integer codigo, String descricao) {
		DetachedCriteria criteria = criarCriteriaBairros(codigo, descricao);
		
		criteria.addOrder(Order.asc(AipBairros.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults,
				AipBairros.Fields.CODIGO.toString(), true);
	}
	
	
	private DetachedCriteria criarCriteriaBairros(Integer codigo,
			String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairros.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(AipBairros.Fields.CODIGO.toString(),
					codigo));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AipBairros.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	public Long obterBairroCount(Integer codigo, String descricao) {
		DetachedCriteria criteria = criarCriteriaBairros(codigo, descricao);

		return executeCriteriaCountDistinct(criteria, AipBairros.Fields.CODIGO
				.toString(), true);
	}
	
	public AipBairros obterBairroPelaDescricaoExata(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairros.class);

		if (descricao != null && !descricao.equalsIgnoreCase("")) {
			criteria.add(Restrictions.ilike(AipBairros.Fields.DESCRICAO
					.toString(), descricao, MatchMode.EXACT));
		}
		return (AipBairros) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AipBairros> pesquisarBairro(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipBairros.class);

			_criteria.add(Restrictions.eq(
					AipBairros.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			
			List<AipBairros> list = executeCriteria(_criteria, 0, 100, null, false);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipBairros.class);
				
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipBairros.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(AipBairros.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	/**
	 * #44799 C5
	 * @param filtro
	 * @return
	 */
	public List<AipBairros> obterAipBairrosPorDescricao(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipBairrosPorDescricao(filtro);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipBairros.Fields.CODIGO.toString()).as(AipBairros.Fields.CODIGO.toString()))
				.add(Projections.property(AipBairros.Fields.DESCRICAO.toString()).as(AipBairros.Fields.DESCRICAO.toString())
				));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipBairros.class));
		criteria.addOrder(Order.asc(AipBairros.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long obterAipBairrosPorDescricaoCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaAipBairrosPorDescricao(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaAipBairrosPorDescricao(Object filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipBairros.class);
		criteria.add(Restrictions.eq(AipBairros.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		String filtroPesquisa = filtro.toString();
		
		if(StringUtils.isNotBlank(filtroPesquisa)){
			criteria.add(Restrictions.ilike(AipBairros.Fields.DESCRICAO.toString(), filtroPesquisa, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	

}
