package br.gov.mec.aghu.sig.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuEntrDadoAgua;
import br.gov.mec.aghu.model.FcuEntrDadoLuz;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;


/**
 * Classe para acesso a dados do model {@code SigAtividades}.
 * 
 */
public class SigAtividadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividades> {

	private static final long serialVersionUID = 7825375910005479969L;

	/**
	 * Obtem lista de atividades
	 * 
	 */
	public List<SigAtividades> pesquisarAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigAtividades atividade,
			FccCentroCustos fccCentroCustos) {
		return this.executeCriteria(this.createPesquisaAtividadesCriteria(atividade, fccCentroCustos), firstResult, maxResult, orderProperty, true);
	}

	public Long pesquisarAtividadesCount(SigAtividades atividade, FccCentroCustos fccCentroCustos) {
		return this.executeCriteriaCount(this.createPesquisaAtividadesCriteria(atividade, fccCentroCustos));
	}

	private DetachedCriteria createPesquisaAtividadesCriteria(SigAtividades atividade, FccCentroCustos fccCentroCustos) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atv");

		criteria.createAlias("atv." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.LEFT_OUTER_JOIN);
		

		if (fccCentroCustos != null) {
			criteria.add(Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));
		}

		if (StringUtils.isNotBlank(atividade.getNome())) {
			criteria.add(Restrictions.ilike(SigAtividades.Fields.NOME.toString(), atividade.getNome(), MatchMode.ANYWHERE));
		}

		if (atividade.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(SigAtividades.Fields.IND_SITUACAO.toString(), atividade.getIndSituacao()));
		}

		if (atividade.getIndOrigemDados() != null) {
			criteria.add(Restrictions.eq(SigAtividades.Fields.IND_ORIGEM_DADOS.toString(), atividade.getIndOrigemDados()));
		}
		return criteria;
	}

	/**
	 * Busca todas as atividades que estão vinculadas a um determinado centro de
	 * custo
	 * 
	 * @param fccCentroCustos
	 *            - Centro de custo ao qual as atividades estão vinculadas
	 * @return List<SigAtividades> que pertencem a um determinado centro de
	 *         custo
	 */
	public List<SigAtividades> pesquisarAtividades(FccCentroCustos fccCentroCustos) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atividade");

		criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));

		return this.executeCriteria(criteria);
	}

	
	public List<SigAtividades> buscarAtividadesRelatorio(FccCentroCustos centroCusto, SigAtividades atividade, DominioSituacao situacao){
	
		DetachedCriteria criteria = this.montarCriteriaPesquisarAtividades(centroCusto, null, situacao);
		
		//Caso um centro de custo seja selecionado e nenhuma atividade seja selecionada, irá imprimir todas as atividades do centro de custo selecionado.
		if(atividade != null){
			//Retorna a própria atividade selecionada
			criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.SEQ.toString(), atividade.getSeq()));
		}
		
		//Quando selecionador um centro de custo
		if(centroCusto != null){
			//Restringe todas as atividades do centro de custo
			criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}
		//Se não for selecionado o centro de custo, então restringe todas as tarefas sem centro de custo
		else{
			//Se um centro de custo não é selecionado, então exibe apenas as atividades que não estão associadas a um centro de custo.
			criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.isNull("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString()));	
		}
		
		return this.executeCriteria(criteria);
	}
	
	public SigAtividades obterAtividadeByNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atividade");
		criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.NOME.toString(), nome));
		return (SigAtividades) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<SigAtividades> listarAtividadesRestringindoCentroCusto(FccCentroCustos fccCentroCustos, Object objPesquisa) {
		
		DetachedCriteria criteria = this.montarCriteriaPesquisarAtividades(fccCentroCustos, objPesquisa, null);

		criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.LEFT_OUTER_JOIN);
		
		if (fccCentroCustos != null && fccCentroCustos.getCodigo() != null) {
			criteria.add(Restrictions.or(
				Restrictions.and(	
					Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), fccCentroCustos),
					Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.IND_SITUACAO.toString(), DominioSituacao.A)
				),
				//Visualiza as atividades sem centro de custo também (genéricas)
				Restrictions.isNull("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString()) 
			));
		}
		else {
			//Visualiza somente as ativades genéricas
			criteria.add(Restrictions.isNull("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString()));
		}
	

		return this.executeCriteria(criteria);
	}
	
	public List<SigAtividades> pesquisarAtividadesAtivas(List<FccCentroCustos> listCentroCusto, Object objPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atividade");

		criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("atividade." + SigAtividades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.or(
			Restrictions.and(	
				Restrictions.in("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), listCentroCusto),
				Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.IND_SITUACAO.toString(), DominioSituacao.A)
			),
			//Visualiza as atividades sem centro de custo também (genéricas)
			Restrictions.isNull("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString()) 
		));


		criteria.addOrder(Order.asc("atividade." + SigAtividades.Fields.NOME.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<SigAtividades> pesquisarAtividadesAtivas(FccCentroCustos fccCentroCustos, Object objPesquisa) {

		DetachedCriteria criteria = this.montarCriteriaPesquisarAtividades(fccCentroCustos, objPesquisa, DominioSituacao.A);

		criteria.createCriteria("atividade." + SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "atvCC", JoinType.INNER_JOIN);
		
		if (fccCentroCustos != null && fccCentroCustos.getCodigo() != null) {
			criteria.add(Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));
		}

		criteria.add(Restrictions.eq("atvCC." + SigAtividadeCentroCustos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));		

		return this.executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisarAtividades(FccCentroCustos fccCentroCustos, Object objPesquisa, DominioSituacao situacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atividade");

		if(situacao != null){
			criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.IND_SITUACAO.toString(), situacao));
		}

		if(objPesquisa != null){
			String strPesquisa = (String) objPesquisa;
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq("atividade." + SigAtividades.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			} else if (StringUtils.isNotBlank(strPesquisa)) {
				criteria.add(Restrictions.ilike("atividade." + SigAtividades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.addOrder(Order.asc("atividade." + SigAtividades.Fields.NOME.toString()));
		
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> buscarAutorizacaoFornecimento(Object paramPesquisa){
		
		StringBuilder hql = new StringBuilder(600);
		
		hql.append(" SELECT AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " as numeroInternoAf ")
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.LICITACAO_NUMERO.toString() ).append( " as numeroAf ")
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()  ).append( " as complementoAf ")
		.append(" , SRV." ).append( ScoServico.Fields.NOME.toString() ).append( " as nomeServ ")
		.append(" , SRV." ).append( ScoServico.Fields.CODIGO.toString() ).append( " as codigoServ ")
		.append(" , SUM(coalesce(IPF." ).append( ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString() ).append( ",1) * " ).append( "coalesce(IPF."
				).append( ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString() ).append( ",0) * " ).append( "coalesce(LCT." ).append( ScoLicitacao.Fields.FREQUENCIA_ENTREGA.toString()
				).append( ",1) " ).append( ") as totalItem ")
		
		.append(" FROM " ).append( ScoServico.class.getSimpleName() ).append( " SRV, ")
		.append(ScoSolicitacaoServico.class.getSimpleName() ).append( " SLS, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()    ).append( " FSC, ")
		//Essas duas tabelas estão presentes em relacionamentos de outras tabelas, por isso deve ser utilizado o join
		//.append(ScoPropostaFornecedor.class.getSimpleName() ).append( " PFR, ")
		//.append(ScoItemPropostaFornecedor.class.getSimpleName() ).append( " IPF, ")
		.append(ScoAutorizacaoForn.class.getSimpleName()     ).append( " AFN ")
		.append("join AFN.").append(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR ).append( " PFR, ")
		.append(ScoItemAutorizacaoForn.class.getSimpleName() ).append( " IAFN ")
		.append("join IAFN.").append(ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR ).append( " IPF, ")
		.append(ScoLicitacao.class.getSimpleName() ).append( " LCT ")
		
		.append(" WHERE PFR." ).append( ScoPropostaFornecedor.Fields.LICITACAO_ID.toString() ).append( " = IPF."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
		.append(" AND PFR." ).append( ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString() ).append( " = IPF."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
		  
	    .append(" AND IPF.").append(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString()).append(" = 'S'")
	    
	    //O join já faz esta comparação
	    /*
	    .append(" AND PFR.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()
		  	      ).append(" = AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO)
	    .append(" AND PFR.").append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()
		  	      ).append(" = AFN.").append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO)
	    */
	    .append(" AND AFN.").append(ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString()).append(" = 'N'")
	    
		.append(" AND AFN." ).append( ScoAutorizacaoForn.Fields.SITUACAO.toString() ).append( " in('" ).append( DominioSituacaoAutorizacaoFornecimento.AE ).append( '\'' ).append( ",'"
				).append( DominioSituacaoAutorizacaoFornecimento.PA ).append( "') ")
	    
	    .append(" AND AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " = IAFN." ).append( ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
	 
	    //O join já faz esta comparação
	    /*
	    .append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString()).append('.' ).append( ScoItemPropostaFornecedor.Fields.NUMERO.toString()
		  	  ).append(" = IPF.").append(ScoItemPropostaFornecedor.Fields.NUMERO.toString())
	    .append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString()).append('.' ).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString()  
		  	  ).append(" = IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
	    .append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString()).append('.' ).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()
		        ).append(" = IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
        */
	    
	    .append(" AND AFN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString())
	    
		.append(" AND FSC." ).append( ScoFaseSolicitacao.Fields.LCT_NUMERO.toString() ).append( " = IAFN."
				).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString() ).append( '.'
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
		.append(" AND FSC." ).append( ScoFaseSolicitacao.Fields.ITL_NUMERO.toString() ).append( " = IAFN."
				).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString() ).append( '.' ).append( ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString())
	    
	    .append(" AND FSC." ).append( ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = 'N'")
	    .append(" AND FSC." ).append( ScoFaseSolicitacao.Fields.TIPO.toString() ).append( " = '" ).append( DominioTipoFaseSolicitacao.S ).append('\'')
	    
	    .append(" AND SLS." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = FSC." ).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
	    
	    .append(" AND SRV." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = SLS." ).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString());
		
	    if(paramPesquisa != null){
			String srtPesquisa = (String) paramPesquisa;
			if (StringUtils.isNotBlank(srtPesquisa)) {
				if (CoreUtil.isNumeroInteger(paramPesquisa)) {
					hql.append(" AND AFN." + ScoAutorizacaoForn.Fields.LICITACAO_NUMERO.toString() + " = " + paramPesquisa);
				}else{
					hql.append(" AND lower(SRV." + ScoServico.Fields.NOME.toString() + ") like '%").append(srtPesquisa.toLowerCase()).append("%' ");
				}
			}
		}
	    
		hql.append(" GROUP BY AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString()) 
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.LICITACAO_NUMERO.toString())
                .append(" , AFN." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
                .append(" , SRV." ).append( ScoServico.Fields.NOME.toString())
                .append(" , SRV." ).append( ScoServico.Fields.CODIGO.toString())
                
                .append(" ORDER BY AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString());
		       
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		
		return createQuery.list();
	}
	
	public SigAtividades obterAtividadeDetalhada(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividades.class, "atividade");
		criteria.createAlias(SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "ACC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ACC."+SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SigAtividades.Fields.SEQ.toString(), seq));
		return (SigAtividades) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<FcuEntrDadoAgua> listarEntradasAguaByDt(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date ultimoDia = c.getTime();

		DetachedCriteria criteria = DetachedCriteria.forClass(FcuEntrDadoAgua.class, "feea");

		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.isNull(FcuEntrDadoAgua.Fields.DT_FIM_VALIDADE.toString()),
						Restrictions.le(FcuEntrDadoAgua.Fields.DT_INICIO_VALIDADE.toString(), ultimoDia)),

				Restrictions.and(
						Restrictions.isNotNull(FcuEntrDadoAgua.Fields.DT_FIM_VALIDADE.toString()),
						Restrictions.and(Restrictions.le(FcuEntrDadoAgua.Fields.DT_INICIO_VALIDADE.toString(), ultimoDia),
								Restrictions.ge(FcuEntrDadoAgua.Fields.DT_FIM_VALIDADE.toString(), date)))));

		return this.executeCriteria(criteria);

	}
	
	public List<FcuEntrDadoLuz> listarEntradasLuzByDt(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date ultimoDia = c.getTime();

		DetachedCriteria criteria = DetachedCriteria.forClass(FcuEntrDadoLuz.class, "feea");

		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.isNull(FcuEntrDadoLuz.Fields.DT_FIM_VALIDADE.toString()),
						Restrictions.le(FcuEntrDadoLuz.Fields.DT_INICIO_VALIDADE.toString(), ultimoDia)),

				Restrictions.and(
						Restrictions.isNotNull(FcuEntrDadoLuz.Fields.DT_FIM_VALIDADE.toString()),
						Restrictions.and(Restrictions.le(FcuEntrDadoLuz.Fields.DT_INICIO_VALIDADE.toString(), ultimoDia),
								Restrictions.ge(FcuEntrDadoLuz.Fields.DT_FIM_VALIDADE.toString(), date)))));

		return this.executeCriteria(criteria);
	}
}
