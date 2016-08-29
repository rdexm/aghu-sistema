package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @modulo sicon.cadastrosbasicos
 * @author cvagheti
 * 
 */
public class ScoCatalogoSiconDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCatalogoSicon> {

	private static final long serialVersionUID = 6628196753787369694L;

	public Long pesquisarCatalogoSiconCount(Integer codigoSicon, String descricao, DominioTipoItemContrato tipoItemContrato,
			DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCatalogoSicon.class);

		if (codigoSicon != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.CODIGO_SICON.toString(), codigoSicon));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(ScoCatalogoSicon.Fields.DESCRICAO.toString(), descricao.trim(), MatchMode.ANYWHERE));
		}

		if (tipoItemContrato != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.TIPOITEMCONTRATO.toString(), tipoItemContrato));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.SITUACAO.toString(), situacao));
		}

		return this.executeCriteriaCount(criteria);
	}

	public List<ScoCatalogoSicon> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoSicon, String descricao, DominioTipoItemContrato tipoItemContrato, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCatalogoSicon.class);

		if (codigoSicon != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.CODIGO_SICON.toString(), codigoSicon));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(ScoCatalogoSicon.Fields.DESCRICAO.toString(), descricao.trim(), MatchMode.ANYWHERE));
		}

		if (tipoItemContrato != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.TIPOITEMCONTRATO.toString(), tipoItemContrato));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(ScoCatalogoSicon.Fields.CODIGO_SICON.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria listarCatalogoSiconServicoAtivoCriteria(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCatalogoSicon.class);

		criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.TIPOITEMCONTRATO.toString(), DominioTipoItemContrato.S));

		

		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.CODIGO_SICON.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(ScoCatalogoSicon.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));

		}

		return criteria;
	}

	public List<ScoCatalogoSicon> listarCatalogoSiconServicoAtivo(Object pesquisa) {
		
		DetachedCriteria criteria = listarCatalogoSiconServicoAtivoCriteria(pesquisa);
		
		criteria.addOrder(Order.asc(ScoCatalogoSicon.Fields.DESCRICAO.toString()));
		
		if(pesquisa == null || StringUtils.isBlank((String)pesquisa)){
			return executeCriteria(criteria, 0, 100, null, false);
		}
		
		return this.executeCriteria(criteria);

	}
	
	public Long listarCatalogoSiconServicoAtivoCount(Object pesquisa) {
		
		DetachedCriteria criteria = listarCatalogoSiconServicoAtivoCriteria(pesquisa);
		
		return this.executeCriteriaCount(criteria);

	}

	public List<ScoCatalogoSicon> listarCatalogoSiconMaterialAtivo(Object pesquisa) {
		DetachedCriteria criteria = listarCatalogoSiconMaterialAtivoCriteria(pesquisa);

		criteria.addOrder(Order.asc(ScoCatalogoSicon.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);

	}

	public Long listarCatalogoSiconMaterialAtivoCount(Object pesquisa) {
		DetachedCriteria criteria = listarCatalogoSiconMaterialAtivoCriteria(pesquisa);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria listarCatalogoSiconMaterialAtivoCriteria(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCatalogoSicon.class);

		criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.TIPOITEMCONTRATO.toString(), DominioTipoItemContrato.M));

		String strParametro = (String) pesquisa;
		Integer codigo = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}
		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoCatalogoSicon.Fields.CODIGO_SICON.toString(), codigo));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(ScoCatalogoSicon.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

}
