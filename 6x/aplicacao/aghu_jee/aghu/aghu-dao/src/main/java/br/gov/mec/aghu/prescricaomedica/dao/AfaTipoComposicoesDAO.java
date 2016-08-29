package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * @author thiago.cortes
 *
 */
public class AfaTipoComposicoesDAO  extends BaseDao<AfaTipoComposicoes> {
	
	/**#3506 C1
	 * @param afaTipoComposicoes
	 * @return
	 */
	public List<ConsultaTipoComposicoesVO> pesquisarTiposComposicaoNPT(AfaTipoComposicoes afaTipoComposicoes){
		DetachedCriteria criteria = filtroTiposComposicaoNPT(afaTipoComposicoes);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AfaTipoComposicoes.Fields.SEQ.toString()), ConsultaTipoComposicoesVO.Fields.SEQ.toString())
				.add(Projections.property(AfaTipoComposicoes.Fields.DESCRICAO.toString()),ConsultaTipoComposicoesVO.Fields.DESCRICAO.toString())
				.add(Projections.property(AfaTipoComposicoes.Fields.IND_PRODUCAO.toString()),ConsultaTipoComposicoesVO.Fields.IND_PRODUCAO.toString())
				.add(Projections.property(AfaTipoComposicoes.Fields.ORDEM.toString()),ConsultaTipoComposicoesVO.Fields.ORDEM.toString())
				.add(Projections.property(AfaTipoComposicoes.Fields.CRIADO_EM.toString()),ConsultaTipoComposicoesVO.Fields.CRIADO_EM.toString())
				.add(Projections.property(AfaTipoComposicoes.Fields.IND_SITUACAO.toString()),ConsultaTipoComposicoesVO.Fields.IND_SITUACAO.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaTipoComposicoesVO.class));
		
		return executeCriteria(criteria);
	}	
	/**#3506
	 * filtro da consulta principal
	 * @param afaTipoComposicoes
	 * @return
	 */
	private DetachedCriteria filtroTiposComposicaoNPT(AfaTipoComposicoes afaTipoComposicoes){
			DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
			
			if(afaTipoComposicoes.getSeq() != null){
				criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(),afaTipoComposicoes.getSeq()));
			}
			if(afaTipoComposicoes.getOrdem() != null){
				criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.ORDEM.toString(),afaTipoComposicoes.getOrdem()));
			}
			if(afaTipoComposicoes.getIndSituacao() != null){
				criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.IND_SITUACAO.toString(),afaTipoComposicoes.getIndSituacao()));
			}
			return criteria;
	}
	
	/**
	 * #3506 C4
	 * @return
	 */
	public String listaInfoCriacaoTipoComposicao(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class,"ATC");
		criteria.createAlias("ATC."+AfaTipoComposicoes.Fields.RAP_SERVIDOR,"RSE");
		criteria.createAlias("RSE."+RapServidores.Fields.PESSOA_FISICA.toString(), "PEF");
		criteria.setProjection(Projections.distinct(Projections.property("PEF."+RapPessoasFisicas.Fields.NOME.toString())));
		
		if(seq != null){
			criteria.add(Restrictions.eq("ATC."+AfaTipoComposicoes.Fields.SEQ.toString(), seq));
		}
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	
	/** C5
	 * Consultas para o suggestionBox
	 * @param filtro
	 * @return
	 */
	public List<AfaTipoComposicoes> pesquisaAfaTipoComposicoesPorFiltro(Object objPesquisa){
		DetachedCriteria criteria = criaPesquisaAfaTipoComposicoesPorFiltro(objPesquisa);
		return executeCriteria(criteria, 0, 100, "seq", true);
	}
	public long pesquisaAfaTipoComposicoesPorFiltroCount(Object objPesquisa){
		DetachedCriteria criteria = criaPesquisaAfaTipoComposicoesPorFiltro(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * C05 falta Vo
	 * @param filtro
	 * @return
	 */
	public DetachedCriteria  criaPesquisaAfaTipoComposicoesPorFiltro(Object objPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
		String strPesquisa = (String) objPesquisa;
		criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AfaTipoComposicoes.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	
}
