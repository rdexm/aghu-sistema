package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
public class ScoGrupoServicoDAO extends BaseDao<ScoGrupoServico> {

	private static final long serialVersionUID = 6912252427890476435L;

	public List<ScoGrupoServico> listarGrupoServico(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoGrupoServico.class);

		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}
		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoGrupoServico.Fields.CODIGO.toString(), codigo));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoGrupoServico.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}

		criteria.addOrder(Order.asc(ScoGrupoServico.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}

	public List<ScoGrupoServico> pesquisarGrupoServico(Object pesquisa) {

		DetachedCriteria criteria = obterCriteriaPesquisarGrupoServico(pesquisa);

		criteria.addOrder(Order.asc(ScoGrupoServico.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarGrupoServicoCount(Object pesquisa) {
		return executeCriteriaCount(obterCriteriaPesquisarGrupoServico(pesquisa));
	}

	private DetachedCriteria obterCriteriaPesquisarGrupoServico(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoGrupoServico.class);

		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}
		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoGrupoServico.Fields.CODIGO.toString(), codigo));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoGrupoServico.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	/**
	 * Estoria 31614 C1
	 * 
	 * @param engenharia
	 * @param pesquisa
	 * @return
	 */
	/**
	 * Estoria 46294
	 *melhoria
	 */
	
	public List<ScoGrupoServico> pesquisarGrupoServicoPorCodigoDescricao(
			Integer codigo, String descricao, Boolean engenharia, Boolean tituloAvulso,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = obterCriteriaGrupoServicos(codigo,
				descricao, tituloAvulso, engenharia);
		if(orderProperty == null){
			criteria.addOrder(Order.asc(ScoGrupoServico.Fields.CODIGO.toString()));
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria obterCriteriaGrupoServicos(Integer codigo,
			String descricao, Boolean tituloAvulso, Boolean engenharia) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoGrupoServico.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					ScoGrupoServico.Fields.CODIGO.toString(), codigo));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					ScoGrupoServico.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}
		if (engenharia != null) {
			criteria.add(Restrictions.eq(
					ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),
					engenharia));
		}
		if(tituloAvulso != null){
			criteria.add(Restrictions.eq(
					ScoGrupoServico.Fields.IND_GERA_TITULO_AVULSO.toString(), tituloAvulso));
		}

		 
		return criteria;
	}

	/**
	 * Estoria #31614
	 * 
	 * @param pesquisa
	 * @return
	 */
	public Long pesquisarGrupoServicoPorCodigoDescricaoCount(Integer codigo,
			String descricao, Boolean tituloAvulso, Boolean engenharia) {
		return executeCriteriaCount(obterCriteriaGrupoServicos(codigo,
				descricao,  tituloAvulso,engenharia));
	}
	
	/**
	 * #46298 - Obtem Lista de Grupo Serviço por Codigo ou Descrição
	 */
	public List<ScoGrupoServico> obterListaGrupoServicoPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoServico.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoGrupoServico.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoGrupoServico.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria, 0, 100, ScoGrupoServico.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Grupo Serviço por Codigo ou Descrição
	 */
	public Long obterCountGrupoServicoPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoServico.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoGrupoServico.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoGrupoServico.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		return executeCriteriaCount(criteria);
	}
}