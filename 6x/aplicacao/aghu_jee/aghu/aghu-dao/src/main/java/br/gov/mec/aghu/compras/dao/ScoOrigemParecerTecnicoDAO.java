package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoOrigemParecerTecnicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoOrigemParecerTecnico> {

	private static final long serialVersionUID = 2962598660870128710L;
	
	/**
	 * Método de pesquisa para a Suggestion Box. 
	 * @param param
	 * @return lista com registros encontrados
	 */
	public List<ScoOrigemParecerTecnico> obterOrigemParecerTecnicoPorSeqDescricao(Object param) {
		
		DetachedCriteria criteria = obterCriteriaOrigemParecerTecnicoPorSeqDescricao(param);

		criteria.addOrder(Order.desc(ScoOrigemParecerTecnico.Fields.AGRUPAMENTO_MATERIAIS.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Método de count para a Suggestion Box. 
	 * @param param
	 * @return qtde de registros encontrados
	 */
	public Long obterOrigemParecerTecnicoPorSeqDescricaoCount(Object param) {
		
		DetachedCriteria criteria = obterCriteriaOrigemParecerTecnicoPorSeqDescricao(param);

		return this.executeCriteriaCount(criteria);
		
	}
	
	private DetachedCriteria obterCriteriaOrigemParecerTecnicoPorSeqDescricao(Object param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoOrigemParecerTecnico.class);		
		criteria.createAlias(ScoOrigemParecerTecnico.Fields.CENTRO_CUSTO.toString(), "CCT",Criteria.LEFT_JOIN);
		criteria.createAlias(ScoOrigemParecerTecnico.Fields.AGRUPAMENTO_MATERIAIS.toString(), "AGM",Criteria.LEFT_JOIN);
		
		String strPesquisa = (String) param;
			
		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroInteger(strPesquisa)) {
			
				criteria.add(Restrictions.eq(ScoOrigemParecerTecnico.Fields.CODIGO.toString(), Integer.parseInt(strPesquisa)));
				
			} else{
				Criterion c1 = Restrictions.ilike("CCT."+FccCentroCustos.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE);
				Criterion c2 = Restrictions.ilike("AGM."+ScoAgrupamentoMaterial.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(c1, c2));
			}
		}

		return criteria;
	}
	
	/**
	 * Método de pesquisa para a página de lista. 
	 * @param codigo, agrupamento, centroCusto, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 12/04/2013
	 */
	public List<ScoOrigemParecerTecnico> listarOrigemParecer(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoOrigemParecerTecnico scoOrigemParecer) {

		DetachedCriteria criteria = montarClausulaWhere(scoOrigemParecer);

		criteria.addOrder(Order.asc(ScoOrigemParecerTecnico.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método count de pesquisa para a página de lista.
	 * @param codigo, agrupamento, centroCusto
	 * @author dilceia.alves
	 * @since 12/04/2013
	 */
	public Long listarOrigemParecerCount(ScoOrigemParecerTecnico scoOrigemParecer) {

		DetachedCriteria criteria = montarClausulaWhere(scoOrigemParecer);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarClausulaWhere(ScoOrigemParecerTecnico scoOrigemParecer) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoOrigemParecerTecnico.class);
		criteria.createAlias(ScoOrigemParecerTecnico.Fields.AGRUPAMENTO_MATERIAIS.toString(), "AGM", Criteria.LEFT_JOIN);
		criteria.createAlias(ScoOrigemParecerTecnico.Fields.CENTRO_CUSTO.toString(), "CCT", Criteria.LEFT_JOIN);
		
		if (scoOrigemParecer != null) {
			if (scoOrigemParecer.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoOrigemParecerTecnico.Fields.CODIGO.toString(),
						scoOrigemParecer.getCodigo()));
			}
		}

		if (scoOrigemParecer.getScoAgrupamentoMateriais() != null) {
			criteria.add(Restrictions.eq(
					ScoOrigemParecerTecnico.Fields.AGRUPAMENTO_MATERIAIS.toString(),
					scoOrigemParecer.getScoAgrupamentoMateriais()));
		}
		
		if (scoOrigemParecer.getFccCentroCusto() != null) {
			criteria.add(Restrictions.eq(
					ScoOrigemParecerTecnico.Fields.CENTRO_CUSTO.toString(),
					scoOrigemParecer.getFccCentroCusto()));
		}

		return criteria;
	}
	
	/**
	 * Método para obter registro com Agrupamento ou Centro de Custo específico.
	 * @param scoOrigemParecer
	 * @author dilceia.alves
	 * @since 15/04/2013
	 */
	public ScoOrigemParecerTecnico obterOrigemParecerPorAgrupamentoOuCCusto(ScoOrigemParecerTecnico scoOrigemParecer) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoOrigemParecerTecnico.class);
		
		if (scoOrigemParecer.getScoAgrupamentoMateriais() != null) {
			criteria.add(Restrictions.eq(
					ScoOrigemParecerTecnico.Fields.AGRUPAMENTO_MATERIAIS.toString(),
					scoOrigemParecer.getScoAgrupamentoMateriais()));
		} 
		else {
			criteria.add(Restrictions.eq(
					ScoOrigemParecerTecnico.Fields.CENTRO_CUSTO.toString(),
					scoOrigemParecer.getFccCentroCusto()));
		}

		return (ScoOrigemParecerTecnico) this.executeCriteriaUniqueResult(criteria);
	}
}
