package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Classe DAO responsável pela persistência de grupos de natureza de despesas.
 * 
 * @author mlcruz
 */
public class FsoGrupoNaturezaDespesaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoGrupoNaturezaDespesa> {
	private static final long serialVersionUID = 4449851967103101971L;

	/**
	 * Pesquisa por grupos de natureza de despesa.
	 * 
	 * @param criteria
	 * @param first
	 * @param max
	 * @return Grupos de natureza de despesa.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria, int first, int max, 
			String orderField, Boolean orderAsc) {
		DetachedCriteria detached = getCriteria(criteria);

		return executeCriteria(detached, first, max, orderField, orderAsc);
	}

	/**
	 * Conta grupos de natureza de despesa.
	 * 
	 * @param criteria Critério.
	 * @return Número de grupos.
	 */
	public Long contarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria) {
		DetachedCriteria detached = getCriteria(criteria);

		return executeCriteriaCount(detached);
	}

	/**
	 * Verifica se existe outro grupo com mesma descrição do grupo em questão.
	 * 
	 * @param model Grupo em questão.
	 * @return Existe ou não descrição duplicada.
	 */
	public Boolean existeDescricaoDuplicada(FsoGrupoNaturezaDespesa model) {
		DetachedCriteria detached = DetachedCriteria
				.forClass(FsoGrupoNaturezaDespesa.class);
		
		if (model.getCodigo() != null) {
			detached.add(Restrictions.ne(
					FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), 
					model.getCodigo()));
		}
		
		detached.add(Restrictions.ilike(
				FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(), 
				model.getDescricao(),
				MatchMode.EXACT));

		return executeCriteriaCount(detached) > 0;
	}
	
	/**
	 * Obtem detached criteria.
	 * 
	 * @param criteria Critérios.
	 * @return Detached criteria.
	 */
	private DetachedCriteria getCriteria(
			FsoGrupoNaturezaDespesaCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria
				.forClass(FsoGrupoNaturezaDespesa.class);
		
		if (criteria.getCodigo() != null) {
			detached.add(Restrictions.eq(
					FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), 
					criteria.getCodigo()));
		}
		
		if (!StringUtils.isBlank(criteria.getDescricao())) {
			detached.add(Restrictions.ilike(
					FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(), 
					criteria.getDescricao(),
					MatchMode.ANYWHERE));
		}
		
		if (criteria.getIndSituacao() != null) {
			detached.add(Restrictions.eq(
					FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), 
					criteria.getIndSituacao()));
		}

		if (criteria.getFiltro() != null) {
			if (CoreUtil.isNumeroInteger(criteria.getFiltro())) {
				detached.add(Restrictions.or(Restrictions.ilike(
						FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(),
						criteria.getFiltro().toString(), MatchMode.ANYWHERE),
						Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.CODIGO
								.toString(), Integer.valueOf(criteria
								.getFiltro().toString()))));
			} else {
				detached.add(Restrictions.ilike(
						FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(),
						criteria.getFiltro().toString(), MatchMode.ANYWHERE));
			}
		}
		
		return detached;
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(Object strPesquisa) {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setFiltro(strPesquisa);
		DetachedCriteria detached = getCriteria(criteria);
		detached.addOrder(Order.asc(FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		detached.addOrder(Order.asc(FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()));

		return executeCriteria(detached);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(Object strPesquisa) {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = new FsoGrupoNaturezaDespesaCriteriaVO();
		criteria.setIndSituacao(DominioSituacao.A);
		criteria.setFiltro(strPesquisa);
		DetachedCriteria detached = getCriteria(criteria);
		detached.addOrder(Order.asc(FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		detached.addOrder(Order.asc(FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()));
		return executeCriteria(detached);
	}

	/**
	 * #46298 - Obtem Lista de Grupo Natureza de Despesa Ativos por Codigo ou Descrição
	 */
	public List<FsoGrupoNaturezaDespesa> obterListaGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoGrupoNaturezaDespesa.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)) {
				criteria.add(Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Grupo Natureza de Despesa Ativos por Codigo ou Descrição
	 */
	public Long obterCountGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoGrupoNaturezaDespesa.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)) {
				criteria.add(Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
}