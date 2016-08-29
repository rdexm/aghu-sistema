package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelGrupoResultadoCodificadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoResultadoCodificado> {
	
	private static final long serialVersionUID = -4890275892590909088L;

	/**
	 * Pesquisa Grupo Resultado Codificação por seq ou descrição
	 * @param objPesquisa
	 * @return
	 */
	public List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificadoPorSeqDescricao(Object objPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCodificado.class);

		if (objPesquisa != null) {
			
			String strPesquisa = (String) objPesquisa;
			
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelGrupoResultadoCodificado.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AelGrupoResultadoCodificado.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
			
		}

		criteria.addOrder(Order.asc(AelGrupoResultadoCodificado.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}

	public List<AelGrupoResultadoCodificado> pesquisaGrupoResultadosCodificadosPorParametros(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ResultadosCodificadosVO filtroResultado) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCodificado.class, "grp");

		if(filtroResultado.getSeq() != null){
			criteria.add(Restrictions.eq(AelGrupoResultadoCodificado.Fields.SEQ.toString(), filtroResultado.getSeq()));
		}

		if(!StringUtils.isBlank(filtroResultado.getDescricao())){
			criteria.add(Restrictions.ilike(AelGrupoResultadoCodificado.Fields.DESCRICAO.toString(), filtroResultado.getDescricao(), MatchMode.ANYWHERE));
		}

		if(filtroResultado.getSituacao() != null){
			criteria.add(Restrictions.eq(AelGrupoResultadoCodificado.Fields.SITUACAO.toString(), filtroResultado.getSituacao()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisaGrupoResultadosCodificadosPorParametrosCount(ResultadosCodificadosVO filtroResultado) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCodificado.class, "grp");

		if(filtroResultado.getSeq() != null){
			criteria.add(Restrictions.eq(AelGrupoResultadoCodificado.Fields.SEQ.toString(), filtroResultado.getSeq()));
		}

		if(!StringUtils.isBlank(filtroResultado.getDescricao())){
			criteria.add(Restrictions.ilike(AelGrupoResultadoCodificado.Fields.DESCRICAO.toString(), filtroResultado.getDescricao(), MatchMode.ANYWHERE));
		}

		if(filtroResultado.getSituacao() != null){
			criteria.add(Restrictions.eq(AelGrupoResultadoCodificado.Fields.SITUACAO.toString(), filtroResultado.getSituacao()));
		}

		return executeCriteriaCount(criteria);
	}
}