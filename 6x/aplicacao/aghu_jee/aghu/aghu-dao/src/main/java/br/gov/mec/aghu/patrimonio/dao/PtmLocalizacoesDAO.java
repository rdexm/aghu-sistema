package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmEdificacao;
import br.gov.mec.aghu.model.PtmLocalizacoes;
import br.gov.mec.aghu.patrimonio.vo.LocalizacaoFiltroVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmLocalizacoesDAO extends BaseDao<PtmLocalizacoes> {
	
	private static final String FCC = "FCC";

	private static final String EDI = "EDI";

	private static final String LOC2 = "LOC";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5572541986611464412L;
	
	private final String LOC = "LOC.";

	public PtmLocalizacoes obterPtmLocalizacoes(Long seqPtmLocalizacoes){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmLocalizacoes.class, LOC2);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.EDIFICACAO.toString(), EDI, JoinType.INNER_JOIN);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FCC, JoinType.INNER_JOIN);
		
		criteria.setFetchMode(PtmLocalizacoes.Fields.EDIFICACAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		
		if(seqPtmLocalizacoes != null){
			criteria.add(Restrictions.eq("LOC."+PtmLocalizacoes.Fields.SEQ.toString(), seqPtmLocalizacoes));
		}
		
		return (PtmLocalizacoes) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #44800 C3 - Botão Pesquisar
	 * @return
	 */
	public List<LocalizacaoFiltroVO> obterListaPaginadaLocalizacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, LocalizacaoFiltroVO localizacaoFiltro){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmLocalizacoes.class, LOC2);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.EDIFICACAO.toString(), EDI, JoinType.INNER_JOIN);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FCC, JoinType.INNER_JOIN);
		
		criteria.setFetchMode(PtmLocalizacoes.Fields.EDIFICACAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		
		if(localizacaoFiltro.getNome() != null){
			criteria.add(Restrictions.ilike(LOC+PtmLocalizacoes.Fields.NOME.toString(), localizacaoFiltro.getNome(), MatchMode.ANYWHERE));
		}
		
		if(localizacaoFiltro.getDescricao() != null){
			criteria.add(Restrictions.ilike(LOC+PtmLocalizacoes.Fields.DESCRICAO.toString(), localizacaoFiltro.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(localizacaoFiltro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.IND_SITUACAO.toString(), localizacaoFiltro.getIndSituacao()));
		}
		
		if(localizacaoFiltro.getCentroCusto() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), localizacaoFiltro.getCentroCusto()));
		}
		
		if(localizacaoFiltro.getSeq() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.SEQ.toString(), localizacaoFiltro.getSeq()));
		}
		
		if(localizacaoFiltro.getEdificacao() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.EDIFICACAO_SEQ.toString(), localizacaoFiltro.getEdificacao().getSeq()));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(LOC+PtmLocalizacoes.Fields.SEQ.toString()), LocalizacaoFiltroVO.Fields.SEQ.toString())
				.add(Projections.property(LOC+PtmLocalizacoes.Fields.NOME.toString()), LocalizacaoFiltroVO.Fields.NOME.toString())
				.add(Projections.property(LOC+PtmLocalizacoes.Fields.DESCRICAO.toString()), LocalizacaoFiltroVO.Fields.DESCRICAO.toString())
				.add(Projections.property("EDI."+PtmEdificacao.Fields.NOME.toString()), LocalizacaoFiltroVO.Fields.EDIFICACAO_NOME.toString())
				.add(Projections.property("FCC."+FccCentroCustos.Fields.DESCRICAO.toString()), LocalizacaoFiltroVO.Fields.CENTRO_CUSTO_DESCRICAO.toString())
				.add(Projections.property("FCC."+FccCentroCustos.Fields.CODIGO.toString()), LocalizacaoFiltroVO.Fields.CENTRO_CUSTO_CODIGO.toString())
				.add(Projections.property("EDI."+PtmEdificacao.Fields.SEQ.toString()), LocalizacaoFiltroVO.Fields.SEQ_EDIFICACAO.toString())
				.add(Projections.property(LOC+PtmLocalizacoes.Fields.IND_SITUACAO.toString()), LocalizacaoFiltroVO.Fields.IND_SITUACAO.toString())
		);
		if(orderProperty == null){
			criteria.addOrder(Order.asc(LOC+PtmLocalizacoes.Fields.NOME.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(LocalizacaoFiltroVO.class));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * #44800 C3 - Count Botão Pesquisar
	 * @return
	 */
	public Long obterListaPaginadaLocalizacaoCount(LocalizacaoFiltroVO localizacaoFiltro){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmLocalizacoes.class, LOC2);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.EDIFICACAO.toString(), EDI, JoinType.INNER_JOIN);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FCC, JoinType.INNER_JOIN);
		
		if(localizacaoFiltro.getNome() != null){
			criteria.add(Restrictions.ilike(LOC+PtmLocalizacoes.Fields.NOME.toString(), localizacaoFiltro.getNome(), MatchMode.ANYWHERE));
		}
		
		if(localizacaoFiltro.getDescricao() != null){
			criteria.add(Restrictions.ilike(LOC+PtmLocalizacoes.Fields.DESCRICAO.toString(), localizacaoFiltro.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(localizacaoFiltro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.IND_SITUACAO.toString(), localizacaoFiltro.getIndSituacao()));
		}
		
		if(localizacaoFiltro.getCentroCusto() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), localizacaoFiltro.getCentroCusto()));
		}
		
		if(localizacaoFiltro.getSeq() != null){
			criteria.add(Restrictions.eq(LOC+PtmLocalizacoes.Fields.SEQ.toString(), localizacaoFiltro.getSeq()));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public PtmLocalizacoes obterPtmLocalizacoesComPtmBemPermanentes(Long seqPtmLocalizacoes){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmLocalizacoes.class, LOC2);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.EDIFICACAO.toString(), EDI, JoinType.INNER_JOIN);
		criteria.createAlias("EDI."+PtmEdificacao.Fields.BPE_SEQ.toString(), "PBP", JoinType.INNER_JOIN);
		criteria.createAlias(LOC+PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FCC, JoinType.INNER_JOIN);
		
		criteria.setFetchMode(PtmLocalizacoes.Fields.EDIFICACAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(PtmEdificacao.Fields.BPE_SEQ.toString(), FetchMode.JOIN);
		criteria.setFetchMode(PtmLocalizacoes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		
		if(seqPtmLocalizacoes != null){
			criteria.add(Restrictions.eq("LOC."+PtmLocalizacoes.Fields.SEQ.toString(), seqPtmLocalizacoes));
		}
		
		return (PtmLocalizacoes) executeCriteriaUniqueResult(criteria);
	}
	
}
