package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcTipoAnestesiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTipoAnestesias> {

	private static final long serialVersionUID = 8838092785798188560L;
	
	public List<MbcTipoAnestesias> obterMbcTipoAnestesiasPorSituacao(final DominioSituacao situacao){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);
		
		if(situacao != null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SITUACAO.toString(), situacao));
		}
		
		criteria.addOrder(Order.asc(MbcTipoAnestesias.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcTipoAnestesias> listarTiposAnestesiaFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);

		criteria.createAlias(MbcTipoAnestesias.Fields.SERVIDOR.toString(), "RAP");
		if(codigo != null ){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SEQ.toString(), codigo));
		}
		if(descricaoTipo!= null && StringUtils.isNotBlank(descricaoTipo)){
			criteria.add(Restrictions.ilike(MbcTipoAnestesias.Fields.DESCRICAO.toString(), descricaoTipo, MatchMode.ANYWHERE));
		}
		if(necessitaAnestesia != null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.IND_NESS_ANES.toString(), necessitaAnestesia));
		}
		if(tipoCombinado!=null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.IND_COMBINADA.toString(), tipoCombinado));
		}
		if(situacaoProfissional!= null){
			criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), situacaoProfissional));
		}

		return executeCriteria(criteria,firstResult, maxResult, MbcTipoAnestesias.Fields.DESCRICAO.toString(), true);
	}
	
	
	public Long listarTiposAnestesiaFiltroCount(final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);

		if(codigo != null ){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SEQ.toString(), codigo));
		}
		if(descricaoTipo!= null && StringUtils.isNotBlank(descricaoTipo)){
			criteria.add(Restrictions.ilike(MbcTipoAnestesias.Fields.DESCRICAO.toString(), descricaoTipo, MatchMode.ANYWHERE));
		}
		if(necessitaAnestesia != null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.IND_NESS_ANES.toString(), necessitaAnestesia));
		}
		if(tipoCombinado!=null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.IND_COMBINADA.toString(), tipoCombinado));
		}
		
		if(situacaoProfissional!= null){
			criteria.add(Restrictions.eq(MbcProfAtuaUnidCirgs.Fields.SITUACAO.toString(), situacaoProfissional));
		}

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPequisarTiposAnestesias(final String filtro, final Boolean indCombinada){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);
		String stParametro = filtro;
		Short codigo = null;
		if (CoreUtil.isNumeroShort(stParametro)) {
			codigo = Short.valueOf(stParametro);
		}
		if (codigo != null) {
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SEQ.toString(), codigo));
		}
		if (indCombinada != null){
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.IND_COMBINADA.toString(), indCombinada));
		}

		if (StringUtils.isNotBlank(stParametro) && codigo == null) {
			criteria.add(Restrictions.ilike(MbcTipoAnestesias.Fields.DESCRICAO.toString(), stParametro, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<MbcTipoAnestesias> pequisarTiposAnestesiaSB(final String filtro, final Boolean indCombinada) {
		DetachedCriteria criteria = this.obterCriteriaPequisarTiposAnestesias(filtro, indCombinada);
		return executeCriteria(criteria, 0, 100, MbcTipoAnestesias.Fields.DESCRICAO.toString(), true);
	}

	public Long pequisarTiposAnestesiaSBCount(final String filtro, final Boolean indCombinada){
		DetachedCriteria criteria = this.obterCriteriaPequisarTiposAnestesias(filtro, indCombinada);
		return executeCriteriaCount(criteria);
	}
	
	public Long obterTipoAnestesiaPorDescricaoCount (String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);
		criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.DESCRICAO.toString(), descricao));
		
		return executeCriteriaCount(criteria);
	}

	public List<MbcTipoAnestesias> obterMbcTipoAnestesiasAtivas(){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);
		
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(MbcTipoAnestesias.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	public List<MbcTipoAnestesias> obterPorCirurgia(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class, "TAN");
		criteria.createAlias(MbcTipoAnestesias.Fields.MBC_ANESTESIA_CIRURGIA.toString(), "ACI");
		criteria.add(Restrictions.eq("ACI." + MbcAnestesiaCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		return super.executeCriteria(criteria);
	}
	
	/**
	 * Criteria para buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	private DetachedCriteria montarCriteriaPequisarTiposAnestesiasAtivas(final Short seq, final String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoAnestesias.class);
		criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (seq != null) {
			criteria.add(Restrictions.eq(MbcTipoAnestesias.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MbcTipoAnestesias.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	public List<MbcTipoAnestesias> pequisarTiposAnestesiasAtivas(final Short seq, final String descricao, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaPequisarTiposAnestesiasAtivas(seq, descricao);
		criteria.addOrder(Order.asc(MbcTipoAnestesias.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(MbcTipoAnestesias.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	public Long pequisarTiposAnestesiasAtivasCount(final Short seq, final String descricao) {
		DetachedCriteria criteria = this.montarCriteriaPequisarTiposAnestesiasAtivas(seq, descricao);
		return super.executeCriteriaCount(criteria);
	}
}
