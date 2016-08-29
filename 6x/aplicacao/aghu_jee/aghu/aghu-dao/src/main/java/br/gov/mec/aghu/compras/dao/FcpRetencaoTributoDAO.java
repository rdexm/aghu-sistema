package br.gov.mec.aghu.compras.dao;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpRetencaoTributoDAO extends BaseDao<FcpRetencaoTributo> {

	private static final long serialVersionUID = 7857418208613109173L;

	/**
	 * Critério de pesquisa
	 * @param codigo
	 * @param tipoTributo
	 * @return criteria
	 */
	private DetachedCriteria createPesquisarCriteria(FcpRetencaoTributo fcpRetencaoTributo){
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoTributo.class);

		Integer codigo = fcpRetencaoTributo.getCodigo();
		
//		String tipoTributo = null;
//		if(fcpRetencaoTributo != null && fcpRetencaoTributo.getTipoTributo() != null && fcpRetencaoTributo.getTipoTributo().getSigla() != null){
//			tipoTributo = fcpRetencaoTributo.getTipoTributo().getSigla();
//		}
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.CODIGO.toString(), codigo));
		}
		if(fcpRetencaoTributo != null && fcpRetencaoTributo.getTipoTributo() != null && fcpRetencaoTributo.getTipoTributo().getSigla() != null && !fcpRetencaoTributo.getTipoTributo().getSigla().equals("")){
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString(), fcpRetencaoTributo.getTipoTributo()));
		}		
		return criteria;
	}
	
	/**
	 * Método responsável por pesquisar count código recolhimento
	 * @param codigo
	 * @param tipoTributo
	 * @return long
	 */
	public Long obterCountCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		return executeCriteriaCount(createPesquisarCriteria(fcpRetencaoTributo));
	}
	
	/**
	 * Método responsável por pesquisar lista código recolhimento
	 * @param codigo
	 * @param tipoTributo
	 * @return list
	 */
	public List<FcpRetencaoTributo> obterListaCodigoRecolhimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoTributo.class);

		Integer codigo = fcpRetencaoTributo.getCodigo();
		
//		String tipoTributo = null;
//		if(fcpRetencaoTributo != null && fcpRetencaoTributo.getTipoTributo() != null && fcpRetencaoTributo.getTipoTributo().getSigla() != null){
//			tipoTributo = fcpRetencaoTributo.getTipoTributo().getSigla();
//		}
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.CODIGO.toString(), codigo));
		}
		if(fcpRetencaoTributo != null && fcpRetencaoTributo.getTipoTributo() != null && fcpRetencaoTributo.getTipoTributo().getSigla() != null && !fcpRetencaoTributo.getTipoTributo().getSigla().equals("")){
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString(), fcpRetencaoTributo.getTipoTributo()));
		}
		
		criteria.addOrder(Order.asc(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString()));
		criteria.addOrder(Order.asc(FcpRetencaoTributo.Fields.CODIGO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}

	
	/**
	 * Retorna uma lista de acomodações
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(Object parametro) {

		List<FcpRetencaoTributo> listaResultado;

		DetachedCriteria criteria = obterTributoPesquisaPorCodigoOuDescricao(parametro);

		listaResultado = this.executeCriteria(criteria,0,100,null,true);

		return listaResultado;
	}
	
	/**
	 * Metódo de consulta de Acomodação por Codigo ou Descrição
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterTributoPesquisaPorCodigoOuDescricao(Object parametro) {
		String descricao = null;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(parametro)) {
			if(parametro instanceof String) {
				codigo = Integer.valueOf((String)parametro);
			}
			else {
				codigo = (Integer)parametro;
			}
		}
		
		else if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoTributo.class);
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.CODIGO.toString(), codigo));
		}

		if(descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString(), DominioTipoTributo.getInstance(descricao)));
		}
		
		criteria.addOrder(Order.asc(FcpRetencaoTributo.Fields.CODIGO.toString()));
		

		return criteria;

	}
	
	/**
	 * Metódo de consulta de Acomodação por Codigo ou Descrição
	 * 
	 * @param parametro
	 * @return
	 */
	protected DetachedCriteria obterTributoPesquisaPorCodigoOuDescricaoSemOrderBy(Object parametro) {
		String descricao = null;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(parametro)) {
			if(parametro instanceof String) {
				codigo = Integer.valueOf((String)parametro);
			}
			else {
				codigo = (Integer)parametro;
			}
		}
		
		else if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoTributo.class);
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.CODIGO.toString(), codigo));
		}

		if(descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString(), DominioTipoTributo.getInstance(descricao)));
		}
		
		criteria.addOrder(Order.asc(FcpRetencaoTributo.Fields.CODIGO.toString()));
		

		return criteria;

	}
	
	public Long obterTributoPesquisaPorCodigoOuDescricaoCount(String parametro) {
		DetachedCriteria criteria = obterTributoPesquisaPorCodigoOuDescricao(parametro);
		
		Long i = (long) executeCriteria(criteria).size(); 
		
		return i;
	}
	
	public Long obterTributoPesquisaPorCodigoOuDescricaoCount(Object parametro) {
		DetachedCriteria criteria = obterTributoPesquisaPorCodigoOuDescricao(parametro);
		
		Long i = (long) executeCriteria(criteria).size(); 
		
		return i;
	}
	
	/**
	 * C1 - #41405
	 * @param tipoTributo
	 * @return
	 */
	public List<FcpRetencaoTributo> listaRetencaoTributoPorTipoTributo(DominioTipoTributo tipoTributo){
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoTributo.class);
		if (tipoTributo != null) {
			criteria.add(Restrictions.eq(FcpRetencaoTributo.Fields.TIPO_TRIBUTO.toString(), tipoTributo));
		}
		return executeCriteria(criteria);
	}
}