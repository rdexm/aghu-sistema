package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.ExportacaoDadoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciExportacaoDado;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MciExportacaoDadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciExportacaoDado> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7767649429410883437L;
	
	public List<ExportacaoDadoVO> pesquisarExpDados(final String pesquisa, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		List<ExportacaoDadoVO> lista = null;
		if (pesquisa != null && CoreUtil.isNumeroShort(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaExpDados(pesquisa, true);
			criteria.addOrder(Order.asc(MciExportacaoDado.Fields.DESCRICAO.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		if (lista == null) {
			DetachedCriteria criteria = montarCriteriaExpDados(pesquisa, false);
			criteria.addOrder(Order.asc(MciExportacaoDado.Fields.DESCRICAO.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		return lista; 
	}
	
	public Long pesquisarExpDadosCount(final String pesquisa) {
		Long retorno = null;
		if (pesquisa != null && CoreUtil.isNumeroShort(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaExpDados(pesquisa, true);
			retorno = executeCriteriaCount(criteria);
		}
		if (retorno == null || retorno.longValue() == 0) {
			DetachedCriteria criteria = montarCriteriaExpDados(pesquisa, false);
			retorno = executeCriteriaCount(criteria);
		}
		return retorno;
	}
	
	private DetachedCriteria montarCriteriaExpDados(final String pesquisa, boolean isCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciExportacaoDado.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MciExportacaoDado.Fields.SEQ.toString()), ExportacaoDadoVO.Fields.SEQ.toString())
				.add(Projections.property(MciExportacaoDado.Fields.DESCRICAO.toString()), ExportacaoDadoVO.Fields.DESCRICAO.toString())
				);
		
		if (pesquisa != null && !pesquisa.isEmpty()) {
			if (isCodigo) {
				criteria.add(Restrictions.eq(MciExportacaoDado.Fields.SEQ.toString(), Short.valueOf(pesquisa)));
			} else {
				criteria.add(Restrictions.ilike(MciExportacaoDado.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MciExportacaoDado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ExportacaoDadoVO.class));
		
		return criteria;
	}
}
