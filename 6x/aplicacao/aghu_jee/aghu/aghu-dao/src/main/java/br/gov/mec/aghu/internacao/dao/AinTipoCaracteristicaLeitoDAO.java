package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;

/**
 * 
 * @author rcorvalao
 *
 */
public class AinTipoCaracteristicaLeitoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinTipoCaracteristicaLeito> {

	private static final long serialVersionUID = 3804020625388231806L;

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<AinTipoCaracteristicaLeito> pesquisaTiposCaracteristicaLeito(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao) {
		DetachedCriteria criteria = createPesquisaTiposCaracteristicaLeitoCriteria(
				codigo, descricao);
		criteria.addOrder(Order.asc(AinTipoCaracteristicaLeito.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public Long pesquisaTiposCaracteristicaLeitoCount(Integer codigo,
			String descricao) {
		return executeCriteriaCount(createPesquisaTiposCaracteristicaLeitoCriteria(
				codigo, descricao));
	}

	private DetachedCriteria createPesquisaTiposCaracteristicaLeitoCriteria(
			Integer codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTipoCaracteristicaLeito.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AinTipoCaracteristicaLeito.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTipoCaracteristicaLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));

		}

		return criteria;
	}

	/**
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param ainTiposCaracteristicaLeitosCodigo
	 * @return
	 */
	public AinTipoCaracteristicaLeito obterTiposCaracteristicaLeitos(
			Integer ainTiposCaracteristicaLeitosCodigo) {
		AinTipoCaracteristicaLeito retorno = this.obterPorChavePrimaria(ainTiposCaracteristicaLeitosCodigo);
		return retorno;
	}

	public List<AinTipoCaracteristicaLeito> getTiposCaracteristicaLeitoComMesmaDescricao(
			AinTipoCaracteristicaLeito tipoCaracteristicaLeito) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTipoCaracteristicaLeito.class);

		criteria.add(Restrictions.ilike(
				AinTipoCaracteristicaLeito.Fields.DESCRICAO.toString(),
				tipoCaracteristicaLeito.getDescricao(), MatchMode.EXACT));

		// editada
		if (tipoCaracteristicaLeito.getCodigo() != null) {
			criteria.add(Restrictions.ne(
					AinTipoCaracteristicaLeito.Fields.CODIGO.toString(),
					tipoCaracteristicaLeito.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}

	/**
	 * Metódo de consulta de Acomodação por Codigo ou Descrição
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(
			Object parametro) {

		String descricao = null;
		Integer codigo = null;

		if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		if (parametro instanceof Integer) {
			codigo = (Integer) parametro;
		}
		DetachedCriteria criteria = obterCriteriaTiposCaracteristicas();

		if (descricao != null) {
			criteria.add(Restrictions.ilike(
					AinTipoCaracteristicaLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AinTipoCaracteristicaLeito.Fields.CODIGO.toString(), codigo));
		}

		return criteria;

	}

	/**
	 * @return
	 */
	private DetachedCriteria obterCriteriaTiposCaracteristicas() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTipoCaracteristicaLeito.class);
		return criteria;
	}

	/**
	 * Retorna uma lista de tipos de caracteríticas de leitos
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinTipoCaracteristicaLeito> pesquisarTipoCaracteristicaPorCodigoOuDescricao(
			Object parametro) {

		List<AinTipoCaracteristicaLeito> listaResultado;

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro);

		listaResultado = this.executeCriteria(criteria);

		return listaResultado;
	}

}
