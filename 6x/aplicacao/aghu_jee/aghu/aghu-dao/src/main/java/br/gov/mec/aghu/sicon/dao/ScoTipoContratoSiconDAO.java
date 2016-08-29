package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @modulo sicon.cadastrosbasicos
 * @author cvagheti
 *
 */
public class ScoTipoContratoSiconDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoTipoContratoSicon> {

	private static final long serialVersionUID = -3640608693081067286L;

	/**
	 * Retorna a listagem de todos os tipos de contrato, obedecendo a filtragem.
	 * A listagem está sendo ordenada pela SEQ
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigoSicon
	 * @param descricao
	 * @param situacao
	 * @return Listagem recuperada
	 */
	public List<ScoTipoContratoSicon> pesquisarTiposContrato(
			Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc, Integer _codigoSicon, String _descricao,
			DominioSituacao _situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoTipoContratoSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if ((_descricao != null) && (StringUtils.isNotBlank(_descricao))) {
			criteria.add(Restrictions.ilike(
					ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
					_descricao.trim(), MatchMode.ANYWHERE));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.SITUACAO.toString(), _situacao));
		}

		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString()));
		
		return this.executeCriteria(criteria, _firstResult, _maxResult,
				_orderProperty, _asc);
	}

	/**
	 * Retorna contagem da lista recuperada.
	 * 
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _situacao
	 * @return Total de registros encontrados.
	 */
	public Long listarTiposContratoCount(Integer _codigoSicon,
			String _descricao, DominioSituacao _situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoTipoContratoSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if ((_descricao != null) && (StringUtils.isNotBlank(_descricao))) {
			criteria.add(Restrictions.ilike(
					ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
					_descricao, MatchMode.ANYWHERE));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.SITUACAO.toString(), _situacao));
		}

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Retorna a listagem de todos os tipos de contrato.
	 * 
	 * @param _codigoSicon
	 * @param _descricao
	 * @param _situacao
	 * @return Listagem filtrada.
	 */
	public List<ScoTipoContratoSicon> listarTiposContrato(Integer _codigoSicon,
			String _descricao, DominioSituacao _situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoTipoContratoSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}
		if (_descricao != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
					_descricao));
		}
		if (_situacao != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.SITUACAO.toString(), _situacao));
		}

		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString()));
		
		return this.executeCriteria(criteria);
	}
	/**
	 * Obter um tipo de contrato pelo seu código sicon.
	 * 
	 * @param _codigoSicon
	 * @return
	 */
	public ScoTipoContratoSicon obterPorCodigoSicon(Integer _codigoSicon) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoTipoContratoSicon.class);

		if (_codigoSicon != null) {
			criteria.add(Restrictions.eq(
					ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(),
					_codigoSicon));
		}

		return (ScoTipoContratoSicon) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoTipoContratoSicon> listarTiposContrato(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoContratoSicon.class);
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SITUACAO.toString(),
									 DominioSituacao.A));
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.IND_ADITIVO.toString(), false));
		
		String strParametro = (String) pesquisa;
		Integer seq = null;

		if(CoreUtil.isNumeroInteger(strParametro)){
			seq = Integer.valueOf(strParametro);
		}
		
		if (seq != null) {
			criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SEQ.toString(), seq));

		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}
	
	
	public List<ScoTipoContratoSicon> listarTiposContratoAtivosComAditivo(Object pesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoContratoSicon.class);
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.IND_ADITIVO.toString(), true));
		
		String strParametro = (String) pesquisa;
		Integer seq = null;
		
		if(CoreUtil.isNumeroInteger(strParametro)){
			seq = Integer.valueOf(strParametro);
		}
		
		if (seq != null) {
			criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SEQ.toString(), seq));
			
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<ScoTipoContratoSicon> listarTiposContratoSemAditivoInsItems(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoContratoSicon.class);
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SITUACAO.toString(),
									 DominioSituacao.A));
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.IND_ADITIVO.toString(), false));
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.IND_INS_ITENS.toString(), true));
		
		String strParametro = (String) pesquisa;
		Integer seq = null;

		if(CoreUtil.isNumeroInteger(strParametro)){
			seq = Integer.valueOf(strParametro);
		}

		if (seq != null) {
			criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SEQ.toString(), seq));

		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<ScoTipoContratoSicon> listarTodosTiposContratoAtivos(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTipoContratoSicon.class);
		
		criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SITUACAO.toString(),
									 DominioSituacao.A));
		
		String strParametro = (String) pesquisa;
		Integer seq = null;

		if(CoreUtil.isNumeroInteger(strParametro)){
			seq = Integer.valueOf(strParametro);
		}

		if (seq != null) {
			criteria.add(Restrictions.eq(ScoTipoContratoSicon.Fields.SEQ.toString(), seq));

		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoTipoContratoSicon.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoTipoContratoSicon.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}
	
}
