package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatTipoCaractItensDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoCaractItens> {

	private static final long serialVersionUID = 3587687175254466941L;

	private DetachedCriteria obterDetachedFatTipoCaractItens(){
		return  DetachedCriteria.forClass(FatTipoCaractItens.class);
	}
	
	public List<FatTipoCaractItens> listarTipoCaractItensPorCaracteristica(String caracteristica){
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();
		
		criteria.add(Restrictions.ilike(FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica, MatchMode.EXACT));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return executeCriteria(criteria, true);
		
	}
	
	/**
	 * Metodo para montar uma criteria para pesquisar por tipos de características,
	 * filtrando pela característica ou pelo seq do tipo de característica.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarTiposCaracteristicasParaItens(Object objPesquisa){
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatTipoCaractItens.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatTipoCaractItens.Fields.CARACTERISTICA.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por tipos de características,
	 * filtrando pela característica ou pelo seq do tipo de característica.
	 * @param objPesquisa
	 * @return
	 */
	public List<FatTipoCaractItens> listarTiposCaracteristicasParaItens(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarTiposCaracteristicasParaItens(objPesquisa);
		return executeCriteria(criteria);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por tipos de características,
	 * filtrando pela característica ou pelo seq do tipo de característica.
	 * @param objPesquisa
	 * @return
	 */
	public Long listarTiposCaracteristicasParaItensCount(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarTiposCaracteristicasParaItens(objPesquisa);

		return executeCriteriaCount(criteria);
	}

	public List<Integer> listarSeqsPorCaracteristica(DominioFatTipoCaractItem caracteristica) {
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();

		if (caracteristica != null) {
			criteria.add(Restrictions.eq(FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica.getDescricao()));
		}
		
		criteria.setProjection(Projections.property(FatTipoCaractItens.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	public List<FatTipoCaractItens> pesquisarTiposCaractItensPorSeqCaracteristica(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seq, String caracteristica) { 
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();
		criarFiltrosTipoCaractItens(seq, caracteristica, criteria);
		criteria.addOrder(Order.asc(FatTipoCaractItens.Fields.SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	private void criarFiltrosTipoCaractItens(Integer seq,
			String caracteristica, DetachedCriteria criteria) {
		if (seq != null) {
			criteria.add(Restrictions.eq(FatTipoCaractItens.Fields.SEQ.toString(), seq));
		}
		if(StringUtils.isNotBlank(caracteristica)) {
			criteria.add(Restrictions.ilike(FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica, MatchMode.ANYWHERE));
		}
	}

	public Long pesquisarTiposCaractItensPorSeqCaracteristicaCount(Integer seq, String caracteristica) {
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();
		criarFiltrosTipoCaractItens(seq, caracteristica, criteria);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * 42011
	 * @param caracteristica
	 * @return
	 */
	public Integer buscarSeqPorCaracteristica(String caracteristica){
		DetachedCriteria criteria = obterDetachedFatTipoCaractItens();
		
		criteria.add(Restrictions.eq(FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica));
		
		FatTipoCaractItens fatTipoCaractItens = (FatTipoCaractItens) executeCriteriaUniqueResult(criteria);
		if (fatTipoCaractItens == null) {
			return -1;
		}
		return fatTipoCaractItens.getSeq();
	}
}
