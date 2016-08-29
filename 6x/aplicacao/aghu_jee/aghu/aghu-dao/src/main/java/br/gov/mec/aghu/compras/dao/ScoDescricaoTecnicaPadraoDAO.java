package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.compras.vo.ScoDescricaoTecnicaPadraoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class ScoDescricaoTecnicaPadraoDAO extends BaseDao<ScoDescricaoTecnicaPadrao>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8969186837339014678L;
	
	public List<ScoDescricaoTecnicaPadrao> listarDescricaoTecnica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoDescricaoTecnicaPadrao.class);
		
		
		if (vo.getLiberadaPublicacao() != null) {
			criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.IND_LIBERADO.toString(), convertDominioSimNaoParaBoolean(vo.getLiberadaPublicacao())));	
		}
		
		if (vo.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.CODIGO.toString(), vo.getCodigo()));
		}
		
		if (material != null) {
			criteria.createAlias(ScoDescricaoTecnicaPadrao.Fields.LISTA_MATERIAIS.toString(), "matDescTec");
			criteria.createAlias("matDescTec.material", "mat");
			criteria.add(Restrictions.eq("mat.codigo", material.getCodigo()));
		}
		
		if (StringUtils.isNotBlank(vo.getDescricao())) {
			criteria.add(Restrictions.like(ScoDescricaoTecnicaPadrao.Fields.DESCRICAO.toString(), vo.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(vo.getTitulo())) {
			criteria.add(Restrictions.like(ScoDescricaoTecnicaPadrao.Fields.TITULO.toString(), vo.getTitulo(), MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(ScoDescricaoTecnicaPadrao.Fields.TITULO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarDescricaoTecnicaCount(ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoDescricaoTecnicaPadrao.class);
		
		if (vo.getLiberadaPublicacao() != null) {
			criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.IND_LIBERADO.toString(), convertDominioSimNaoParaBoolean(vo.getLiberadaPublicacao())));	
		}
		
		if (vo.getCodigo() != null) {
			criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.CODIGO.toString(), vo.getCodigo()));
		}
		
		if (material != null) {
			criteria.createAlias(ScoDescricaoTecnicaPadrao.Fields.LISTA_MATERIAIS.toString(), "matDescTec");
			criteria.createAlias("matDescTec.material", "mat");
			criteria.add(Restrictions.eq("mat.codigo", material.getCodigo()));
		}
		
		if (StringUtils.isNotBlank(vo.getDescricao())) {
			criteria.add(Restrictions.like(ScoDescricaoTecnicaPadrao.Fields.DESCRICAO.toString(), vo.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(vo.getTitulo())) {
			criteria.add(Restrictions.like(ScoDescricaoTecnicaPadrao.Fields.TITULO.toString(), vo.getTitulo(), MatchMode.ANYWHERE));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	private Boolean convertDominioSimNaoParaBoolean(DominioSimNao dominio) {
		Boolean result = null;
		
		if (DominioSimNao.S.equals(dominio)) {
			result = Boolean.TRUE;
		} else {
			result = Boolean.FALSE;
		}
		
		return result;
	}
	
	


	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoDescricaoTecnicaPadrao
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoDescricaoTecnicaPadrao>
	 */
	public List<ScoDescricaoTecnicaPadrao> listarScoDescricaoTecnicaPadrao(Object objPesquisa) {
		List<ScoDescricaoTecnicaPadrao> lista = null;
		DetachedCriteria criteria = montarCriteriaScoDescricaoTecnicaPadrao(objPesquisa);

		criteria.addOrder(Order.asc(ScoDescricaoTecnicaPadrao.Fields.TITULO.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}
	
	
	/**
	 * Metodo que monta uma criteria para pesquisar ScoDescricaoTecnicaPadrao filtrando pela
	 * nome ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaScoDescricaoTecnicaPadrao(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoDescricaoTecnicaPadrao.class);
		String strPesquisa = (String) objPesquisa;

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.CODIGO.toString(), Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(ScoDescricaoTecnicaPadrao.Fields.TITULO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	
    public Long listarScoDescricaoTecnicaPadraoCount(Object objPesquisa) {
        DetachedCriteria criteria =montarCriteriaScoDescricaoTecnicaPadrao(objPesquisa);
        return executeCriteriaCount(criteria);
    }

	public List<ScoDescricaoTecnicaPadrao> buscarListaDescricaoTecnicaMaterial(ScoMaterial material) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoDescricaoTecnicaPadrao.class);
		
		criteria.createAlias(ScoDescricaoTecnicaPadrao.Fields.LISTA_MATERIAIS.toString(), "materialDescTecnica");
		criteria.createAlias("materialDescTecnica.material", "mat");
		
		criteria.add(Restrictions.eq("mat.codigo", material.getCodigo()));
		
		return executeCriteria(criteria);
	}


	/**
	 * Monta o relatorio de descricao tecnica material 
	 * @param material
	 * @return
	 */
	public List<ScoDescricaoTecnicaPadrao> obterRelatorioDescricaoTecnica(Short codigoDescricaoTecnica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoDescricaoTecnicaPadrao.class);
		criteria.add(Restrictions.eq(ScoDescricaoTecnicaPadrao.Fields.CODIGO.toString(), codigoDescricaoTecnica));
		criteria.addOrder(Order.asc(ScoDescricaoTecnicaPadrao.Fields.CODIGO.toString()));
		return this.executeCriteria(criteria);
		
	}
}