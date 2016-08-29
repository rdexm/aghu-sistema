package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoPontoParadaSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPontoParadaSolicitacao> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = -41722233525839967L;

	/**
	 * Pesquisa os pontos de parada de solicitação por código/descrição/situação.
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 * @author dilceia.alves
	 * @param firstResult 
	 * @param maxResult 
	 * @since 26/10/2012
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		
		DetachedCriteria criteria = this.obterCriteriaBasica(scoPontoParadaSolicitacao);
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	/**
	 * Contar os pontos de parada de solicitação por código/descrição/situação.
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 * @author dilceia.alves
	 * @param firstResult 
	 * @param maxResult 
	 * @since 26/10/2012
	 */
	public Long pesquisarPontoParadaSolicitacaoCount(
			final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		
		final DetachedCriteria criteria = this
				.obterCriteriaBasica(scoPontoParadaSolicitacao);
		
		return this.executeCriteriaCount(criteria);
		
	}
	

	/**
	 * Pesquisa ponto de parada de solicitação com descrição igual a informada.
	 * @param descricao
	 * @return
	 * @author dilceia.alves
	 * @since 29/10/2012
	 */
	public boolean pesquisarPontoParadaSolicitacaoPorDescricao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		
		if(scoPontoParadaSolicitacao.getCodigo()!=null){
			criteria.add(Restrictions.ne(
					ScoPontoParadaSolicitacao.Fields.CODIGO.toString(),scoPontoParadaSolicitacao.getCodigo()));
		}
		
		if (scoPontoParadaSolicitacao.getDescricao() != null) {
			criteria.add(Restrictions.ilike(
					ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString(),scoPontoParadaSolicitacao.getDescricao().toUpperCase(),MatchMode.EXACT));
		}
		if (this.executeCriteriaCount(criteria)> 0 ){
			return false;
		}
		return true;
		
	}

	/**
	 * Obtém pontos de parada pelo código ou descrição
	 * @param filtro Código ou Descrição
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		
		restrictionDescricao = Restrictions.like(ScoPontoParadaSolicitacao.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro))
		{
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}

			restrictionCodigo = Restrictions.eq(ScoPontoParadaSolicitacao.Fields
					.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields
				.CODIGO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		
		restrictionDescricao = Restrictions.like(ScoPontoParadaSolicitacao.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro))
		{
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}
			restrictionCodigo = Restrictions.eq(ScoPontoParadaSolicitacao.Fields
					.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtém pontos de parada pelo código ou descrição, somente ATIVOS
	 * @param filtro Código ou Descrição
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		
		restrictionDescricao = Restrictions.like(ScoPontoParadaSolicitacao.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro))
		{
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}
			restrictionCodigo = Restrictions.eq(ScoPontoParadaSolicitacao.Fields
					.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaPorPontoParadaSolicitacaoEnviadoPara(Short ppsEnviadoPara) {
		List<ScoPontoParadaSolicitacao> result = new ArrayList<ScoPontoParadaSolicitacao>();
		if (ppsEnviadoPara != null) {
			
			DetachedCriteria sub = DetachedCriteria.forClass(ScoCaminhoSolicitacao.class, "cs");
			sub.add(Restrictions.eq(ScoCaminhoSolicitacao.Fields.PPS_CODIGO_INICIO.toString(), ppsEnviadoPara));
			sub.setProjection(Projections.projectionList().add(
					Projections.distinct(Projections.property(
							ScoCaminhoSolicitacao.Fields.PPS_CODIGO.toString()))));

			DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
			dc.add(Subqueries.propertyIn(ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), sub));		
			dc.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
			
			result = executeCriteria(dc);
		}
		return result;
	}
	
	/**
	 * lista ScoPontoParadaSolicitacao por codigo ou descrição
	 * @param pesquisa
	 * @return
	 * @author andremachado
	 */
	public List<ScoPontoParadaSolicitacao> listarPontoParadaSolicitacao(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class,"PPR");
		String strParametro = (String) pesquisa;
		Short codigo = null;
		if(CoreUtil.isNumeroShort(strParametro)){
			codigo = Short.valueOf(strParametro);
		}
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), codigo));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString(), strParametro,
						MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));

		return this.executeCriteria(criteria);
	}
	
	
	
	
	/**
	 * pesquisar pontos de parada pelo código ou descrição conforme
	 * direitos do usuário
	 * @param filtro (Código ou Descrição) e Servidor
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao (
			String filtro, RapServidores servidor, String filtraTipos, Boolean fromLiberacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class, "PP");

		SimpleExpression restrictionCodigo = null,
				 restrictionDescricao = null;

		restrictionDescricao = Restrictions.like("PP."+ ScoPontoParadaSolicitacao.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro))
		{
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}

			restrictionCodigo = Restrictions.eq("PP." + ScoPontoParadaSolicitacao.Fields
					.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}

		// pega os pontos de parada que o servidor tem direito
		DetachedCriteria subQueryPontoServidor = DetachedCriteria.forClass(ScoPontoServidor.class);
		ProjectionList projectionListSubQueryPontoServidor = Projections.projectionList()
		.add(Projections.property(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString()));
		subQueryPontoServidor.setProjection(projectionListSubQueryPontoServidor);
		subQueryPontoServidor.add(Restrictions.eq(ScoPontoServidor.Fields.SERVIDOR.toString(), servidor));
		
		
		criteria.add(Subqueries.propertyIn("PP." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), subQueryPontoServidor));
		
		if (fromLiberacao) {
			if (StringUtils.isNotBlank(filtraTipos)) {
				String[] arrayStringTipos = StringUtils.split(filtraTipos, ',');
													
				List<DominioTipoPontoParada> listDominioTipos = new ArrayList<DominioTipoPontoParada>();						
							
				for (String item : arrayStringTipos) {				
					listDominioTipos.add((DominioTipoPontoParada.valueOf(item)));
				}
										
				criteria.add(Restrictions.or(
								Restrictions.and(Restrictions.isNotNull(ScoPontoParadaSolicitacao.Fields.TIPO.toString()), 
										Restrictions.not(
												Restrictions.in(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), listDominioTipos))), 
								Restrictions.isNull(ScoPontoParadaSolicitacao.Fields.TIPO.toString())));	
			}
			
		}
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields
				.CODIGO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	
	/**
	 * Obtém pontos de parada de Compra pelo código ou descrição, somente ATIVOS
	 * @param filtro Código ou Descrição
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(String filtro, Boolean isPerfilGeral) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		
		restrictionDescricao = Restrictions.like(ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro)){
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}
			restrictionCodigo = Restrictions.eq(ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(!isPerfilGeral){
			criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));	
		}
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * Obtém pontos de parada de Compra pelo código ou descrição, somente ATIVOS
	 * @param filtro Código ou Descrição
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public Long pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivosCount(String filtro, Boolean isPerfilGeral) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		
		restrictionDescricao = Restrictions.like(ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro)){
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}
			restrictionCodigo = Restrictions.eq(ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(!isPerfilGeral){
			criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));	
		}
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		return this.executeCriteriaCount(criteria);
	}
	

	/**
	 * pesquisar pontos de parada pelo código ou descrição conforme
	 * caminho da solicitacao
	 * @param filtro (Código ou Descrição) e ponto parada origem
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao (
			String filtro, Short pontoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class, "PP");

		SimpleExpression restrictionCodigo = null,
				 restrictionDescricao = null;

		restrictionDescricao = Restrictions.like("PP."+ ScoPontoParadaSolicitacao.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		
		if(Pattern.matches("\\d+", filtro))
		{
			if (filtro.length() > 3) {
				filtro = filtro.substring(0, 2);
			}

			restrictionCodigo = Restrictions.eq("PP." + ScoPontoParadaSolicitacao.Fields
					.CODIGO.toString(), Short.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}

		// pega os pontos de parada que o servidor tem direito
		DetachedCriteria subQueryCaminho= DetachedCriteria.forClass(ScoCaminhoSolicitacao.class);
		ProjectionList projectionListSubQueryCaminho = Projections.projectionList()
		.add(Projections.property(ScoCaminhoSolicitacao.Fields.PPS_CODIGO.toString()));
		subQueryCaminho.setProjection(projectionListSubQueryCaminho);
		subQueryCaminho.add(Restrictions.eq(ScoCaminhoSolicitacao.Fields.PPS_CODIGO_INICIO.toString(), pontoOrigem));
		
		
		criteria.add(Subqueries.propertyIn("PP." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), subQueryCaminho));
		
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields
				.CODIGO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	/**
	 * Monta a cláusula Where para a pesquisa do ponto de parada solicitação.
	 * @param codigo, descricao, situacao
	 * @return
	 * @author dilceia.alves
	 * @since 26/10/2012
	 */
	private DetachedCriteria obterCriteriaBasica(
			final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoPontoParadaSolicitacao.class);

		if (scoPontoParadaSolicitacao != null) {
			if (scoPontoParadaSolicitacao.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoPontoParadaSolicitacao.Fields.CODIGO.toString(),
						scoPontoParadaSolicitacao.getCodigo()));
			}
		}

		if (StringUtils.isNotBlank(scoPontoParadaSolicitacao.getDescricao())) {
			criteria.add(Restrictions.ilike(
					ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString(),
					scoPontoParadaSolicitacao.getDescricao(), MatchMode.ANYWHERE));
		}

		if (scoPontoParadaSolicitacao.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoPontoParadaSolicitacao.Fields.SITUACAO.toString(),
					scoPontoParadaSolicitacao.getSituacao()));
		}
		
		if (scoPontoParadaSolicitacao.getTipoPontoParada() != null) {
			criteria.add(Restrictions.eq(
					ScoPontoParadaSolicitacao.Fields.TIPO.toString(),
					scoPontoParadaSolicitacao.getTipoPontoParada()));
		}

		return criteria;
	}
	
	public Boolean verificarTipoPontoParada(ScoPontoParadaSolicitacao pontoParada, DominioTipoPontoParada tipoPontoParada) {
		if (pontoParada == null) {
			return false;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), pontoParada.getCodigo()));
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), tipoPontoParada));
		
		return (this.executeCriteriaCount(criteria).intValue() > 0); 		
	}
	
	public ScoPontoParadaSolicitacao obterPontoParadaPorTipo(DominioTipoPontoParada tipoPontoParada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), tipoPontoParada));
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		List<ScoPontoParadaSolicitacao> pontoParada = this.executeCriteria(criteria, true); 
		
		return (pontoParada.size() > 0 ? pontoParada.get(0) : null);
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	/**
	 * Lista os pontos de parada por tipo
	 * @param filtro Tipo
	 * @return List<ScoPontosParadaSolicitacoes>
	 */
	public List<ScoPontoParadaSolicitacao> listarPontoParadaPorTipo(DominioTipoPontoParada tipoPontoParada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPontoParadaSolicitacao.class);
		
		criteria.add(Restrictions.eq(ScoPontoParadaSolicitacao.Fields.TIPO.toString(), tipoPontoParada));
		criteria.addOrder(Order.asc(ScoPontoParadaSolicitacao.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria, true);
	}
}