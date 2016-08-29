package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import org.hibernate.criterion.MatchMode;

public class MciEtiologiaInfeccaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciEtiologiaInfeccao> {

	private static final long serialVersionUID = -8187423728472811615L;

	
	public List<OrigemInfeccoesVO> listarOrigemInfeccoes(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigoOrigem, String descricao, DominioSituacao situacao) {
		return executeCriteria(obterCriteriaListarOrigemInfeccoes(Boolean.FALSE, codigoOrigem, descricao, situacao), firstResult, maxResults, orderProperty, asc);
	}

	private DetachedCriteria obterCriteriaListarOrigemInfeccoes(Boolean isCount,String codigoOrigem, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MciEtiologiaInfeccao.class);
		
		if(!isCount){
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(MciEtiologiaInfeccao.Fields.CODIGO.toString()), OrigemInfeccoesVO.Fields.CODIGO_ORIGEM.toString())
					.add(Projections.property(MciEtiologiaInfeccao.Fields.DESCRICAO.toString()), OrigemInfeccoesVO.Fields.DESCRICAO.toString())
					.add(Projections.property(MciEtiologiaInfeccao.Fields.TEXTO_NOTIFICACAO.toString()), OrigemInfeccoesVO.Fields.TEXTO_NOTIFICACAO.toString())
					.add(Projections.property(MciEtiologiaInfeccao.Fields.SITUACAO.toString()), OrigemInfeccoesVO.Fields.SITUACAO.toString())
					.add(Projections.property(MciEtiologiaInfeccao.Fields.UNF_SEQ.toString()), OrigemInfeccoesVO.Fields.UNF_SEQ.toString()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(OrigemInfeccoesVO.class));
			
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(MciEtiologiaInfeccao.Fields.SITUACAO.toString(), situacao));
		}
		
		if (codigoOrigem != null && !codigoOrigem.isEmpty()) {
			criteria.add(Restrictions.eq(MciEtiologiaInfeccao.Fields.CODIGO.toString(), codigoOrigem));
		}
		
		if (descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.or(
					Restrictions.ilike(MciEtiologiaInfeccao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE), 
					Restrictions.ilike(MciEtiologiaInfeccao.Fields.TEXTO_NOTIFICACAO.toString(), descricao, MatchMode.ANYWHERE)));
		} 
		
		return criteria;
	}
	
	public Long listarOrigemInfeccoesCount(String codigoOrigem, String descricao, DominioSituacao situacao) {
		return executeCriteriaCount(obterCriteriaListarOrigemInfeccoes(Boolean.TRUE, codigoOrigem, descricao, situacao));
	}
	
	public List<OrigemInfeccoesVO> suggestionBoxTopografiaOrigemInfeccoes(String strPesquisa) {
		
		if (executeCriteriaCount(obterCriteriaListarOrigemInfeccoes(Boolean.TRUE, strPesquisa, null, DominioSituacao.A)) > 0) {
			return executeCriteria(
				obterCriteriaListarOrigemInfeccoes(Boolean.FALSE, strPesquisa, null, DominioSituacao.A), 0, 100, MciEtiologiaInfeccao.Fields.DESCRICAO.toString(), Boolean.TRUE);
		}else {
			return executeCriteria(
				obterCriteriaListarOrigemInfeccoes(Boolean.FALSE, null, strPesquisa, DominioSituacao.A), 0, 100, MciEtiologiaInfeccao.Fields.DESCRICAO.toString(), Boolean.TRUE);
		}
	}
	
	public Long suggestionBoxTopografiaOrigemInfeccoesCount(String strPesquisa) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MciEtiologiaInfeccao.class);
		
		criteria.add(Restrictions.eq(MciEtiologiaInfeccao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (strPesquisa != null && !strPesquisa.isEmpty()) {
			criteria.add(Restrictions.or(
					Restrictions.ilike(MciEtiologiaInfeccao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE), 
					Restrictions.ilike(MciEtiologiaInfeccao.Fields.TEXTO_NOTIFICACAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}
		
		return executeCriteriaCount(criteria);
	}
}
