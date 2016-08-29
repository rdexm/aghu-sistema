package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTipoMarcacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTipoMarcacaoExame> {
	
	private static final long serialVersionUID = -7631398933323078578L;

	private DetachedCriteria montarPesquisaTipoMarcacaoExame(Short tipoMarcacaoExameSeq, String tipoMarcacaoExameDescricao, 
			DominioSituacao tipoMarcacaoExameSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTipoMarcacaoExame.class);
		
		if (tipoMarcacaoExameSeq != null) {
			criteria.add(Restrictions.eq(AelTipoMarcacaoExame.Fields.SEQ.toString(), tipoMarcacaoExameSeq));
		}
		
		if (!StringUtils.isEmpty(tipoMarcacaoExameDescricao)) {
			criteria.add(Restrictions.ilike(AelTipoMarcacaoExame.Fields.DESCRICAO.toString(), 
					tipoMarcacaoExameDescricao,	MatchMode.ANYWHERE));
		}
		
		if (tipoMarcacaoExameSituacao != null) {
			criteria.add(Restrictions.eq(AelTipoMarcacaoExame.Fields.IND_SITUACAO.toString(), tipoMarcacaoExameSituacao));
		}
		
		return criteria;
	}
	
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short tipoMarcacaoExameSeq, String tipoMarcacaoExameDescricao, 
			DominioSituacao tipoMarcacaoExameSituacao) {
		
		return executeCriteria(montarPesquisaTipoMarcacaoExame(tipoMarcacaoExameSeq, tipoMarcacaoExameDescricao, 
				tipoMarcacaoExameSituacao),	firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarTipoMarcacaoExameCount(Short tipoMarcacaoExameSeq, String tipoMarcacaoExameDescricao, 
			DominioSituacao tipoMarcacaoExameSituacao) {
		
		return executeCriteriaCount(montarPesquisaTipoMarcacaoExame(tipoMarcacaoExameSeq, tipoMarcacaoExameDescricao,
				tipoMarcacaoExameSituacao));
	}
	
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricaoOrdenado(Object parametro) {
		DetachedCriteria criteria = obterCriteriaTipoMarcacaoExameAtivo(parametro);
		criteria.addOrder(Order.asc(AelTipoMarcacaoExame.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaTipoMarcacaoExameAtivo(Object parametro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTipoMarcacaoExame.class);
		String strPesquisa = "";
		
		if (parametro != null) {
			strPesquisa = (String) parametro;
		}
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(AelTipoMarcacaoExame.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(
						AelTipoMarcacaoExame.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(AelTipoMarcacaoExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
}
