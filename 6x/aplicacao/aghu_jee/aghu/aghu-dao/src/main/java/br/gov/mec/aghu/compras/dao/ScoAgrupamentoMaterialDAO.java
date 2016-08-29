package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoAgrupamentoMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAgrupamentoMaterial> {

	private static final long serialVersionUID = -8565097568704465226L;

	/**
	 * Método de pesquisa para a página de lista. 
	 * @param codigo, descricao, indSituacao, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 09/04/2013
	 */
	public List<ScoAgrupamentoMaterial> listarAgrupamentoMaterial(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoAgrupamentoMaterial scoAgrupMaterial) {

		DetachedCriteria criteria = montarClausulaWhere(scoAgrupMaterial);

		criteria.addOrder(Order.asc(ScoAgrupamentoMaterial.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método count de pesquisa para a página de lista.
	 * @param codigo, descricao, indSituacao
	 * @author dilceia.alves
	 * @since 09/04/2013
	 */
	public Long listarAgrupamentoMaterialCount(
			ScoAgrupamentoMaterial scoAgrupMaterial) {

		DetachedCriteria criteria = montarClausulaWhere(scoAgrupMaterial);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarClausulaWhere(ScoAgrupamentoMaterial scoAgrupMaterial) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAgrupamentoMaterial.class);
		
		if (scoAgrupMaterial != null) {
			if (scoAgrupMaterial.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoAgrupamentoMaterial.Fields.CODIGO.toString(),
						scoAgrupMaterial.getCodigo()));
			}
		}

		if (StringUtils.isNotBlank(scoAgrupMaterial.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoAgrupamentoMaterial.Fields.DESCRICAO.toString(),
					scoAgrupMaterial.getDescricao(), MatchMode.ANYWHERE));
		}

		if (scoAgrupMaterial.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoAgrupamentoMaterial.Fields.INDICADOR_SITUACAO.toString(),
					scoAgrupMaterial.getIndSituacao()));
		}

		return criteria;
	}

	/**
	 * Método para obter registro com uma descrição específica.
	 * @param descricao
	 * @author dilceia.alves
	 * @since 09/04/2013
	 */
	public ScoAgrupamentoMaterial pesquisarAgrupamentoMaterialPorDescricao(String descricao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAgrupamentoMaterial.class);

		criteria.add(Restrictions.eq(ScoAgrupamentoMaterial.Fields.DESCRICAO.toString(), descricao));

		return (ScoAgrupamentoMaterial)this.executeCriteriaUniqueResult(criteria);
	}		
	
	/**
	 * Pesquisa utilizada por suggestion box (retorna todos agrupamentos ou somente os ativos)
	 * @param parametro, indAtivo
	 * @return lista
	 * @author dilceia.alves
	 * @since 09/04/2013
	 */
	public List<ScoAgrupamentoMaterial> pesquisarAgrupamentoMaterialPorCodigoOuDescricao(Object parametro, Boolean indAtivo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAgrupamentoMaterial.class);
		
		String strParametro = (String) parametro;
		
		if (StringUtils.isNotBlank(strParametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(
						ScoAgrupamentoMaterial.Fields.CODIGO.toString(), Short.valueOf(strParametro)));
			} else {
				criteria.add(Restrictions.ilike(
						ScoAgrupamentoMaterial.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			}			
		}
		
		if(indAtivo != null) {
			if (indAtivo) {
				criteria.add(Restrictions.eq(ScoAgrupamentoMaterial.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.A));
			}
		}
		
		criteria.addOrder(Order.asc(ScoAgrupamentoMaterial.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria, 0, 100, ScoAgrupamentoMaterial.Fields.DESCRICAO.toString(), true);
	}
}