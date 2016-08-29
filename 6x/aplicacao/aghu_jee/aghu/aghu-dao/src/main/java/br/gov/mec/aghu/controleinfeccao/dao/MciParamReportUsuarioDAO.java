package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.ParamReportUsuarioVO;
import br.gov.mec.aghu.model.MciParamReportUsuario;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MciParamReportUsuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciParamReportUsuario> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7767649429410883437L;
	
	public List<ParamReportUsuarioVO> pesquisarParamsReportUsuario(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		List<ParamReportUsuarioVO> lista = null;
		if (pesquisa != null && CoreUtil.isNumeroInteger(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaParamsReportUsuario(pesquisa, true);
			criteria.addOrder(Order.asc(MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		if (lista == null) {
			DetachedCriteria criteria = montarCriteriaParamsReportUsuario(pesquisa, false);
			criteria.addOrder(Order.asc(MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		return lista; 
	}
	
	public Long pesquisarParamsReportUsuarioCount(final String pesquisa) {
		Long retorno = null;
		if (pesquisa != null && CoreUtil.isNumeroShort(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaParamsReportUsuario(pesquisa, true);
			retorno = executeCriteriaCount(criteria);
		}
		if (retorno == null || retorno.longValue() == 0) {
			DetachedCriteria criteria = montarCriteriaParamsReportUsuario(pesquisa, false);
			retorno = executeCriteriaCount(criteria);
		}
		return retorno;
	}
	
	private DetachedCriteria montarCriteriaParamsReportUsuario(final String pesquisa, boolean isCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciParamReportUsuario.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MciParamReportUsuario.Fields.SEQ.toString()), ParamReportUsuarioVO.Fields.SEQ.toString())
				.add(Projections.property(MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()), ParamReportUsuarioVO.Fields.NOME_PARAM_PERMANENTE.toString())
				.add(Projections.property(MciParamReportUsuario.Fields.NRO_COPIAS.toString()), ParamReportUsuarioVO.Fields.NRO_COPIAS.toString())
				);
		
		if (pesquisa != null && !pesquisa.isEmpty()) {
			if (isCodigo) {
				criteria.add(Restrictions.eq(MciParamReportUsuario.Fields.SEQ.toString(), Integer.valueOf(pesquisa)));
			} else {
				criteria.add(Restrictions.ilike(MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString(), pesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.isNotNull(MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ParamReportUsuarioVO.class));
		
		return criteria;
	}
}
