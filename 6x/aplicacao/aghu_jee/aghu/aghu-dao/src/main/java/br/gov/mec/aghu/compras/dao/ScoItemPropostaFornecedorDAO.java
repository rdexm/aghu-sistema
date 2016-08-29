package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.compras.pac.vo.ItemPropostaAFVO;
import br.gov.mec.aghu.compras.pac.vo.VisualizarExtratoJulgamentoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.compras.vo.PropostasVencedorasFornecedorVO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoPareceresMateriais;
import br.gov.mec.aghu.model.ScoPareceresTecnicos;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoItemPropostaFornecedorDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemPropostaFornecedor> {
	
    @Inject
    private ScoFaseSolicitacaoDAO aScoFaseSolicitacaoDAO;

	private static final long serialVersionUID = 1493991465946852333L;

	public List<ScoItemPropostaFornecedor> listarItemPropostaFornecedor(Object pesquisa,int consulta2) {
		DetachedCriteria criteria =  createListarItemPropostaFornecedor(pesquisa,consulta2);
		return this.executeCriteria(criteria);
	}
	
	public Long listarItemPropostaFornecedorCount(Object pesquisa,int consulta2) {
		DetachedCriteria criteria =  createListarItemPropostaFornecedor(pesquisa,consulta2);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria  createListarItemPropostaFornecedor(Object pesquisa, int consulta2) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		String strParametro = (String) pesquisa;
		
		if(StringUtils.isNotBlank(strParametro)){
			criteria.add(Restrictions.or(
					Restrictions.ilike(ScoItemPropostaFornecedor.Fields.NUMERO.toString(), strParametro, MatchMode.ANYWHERE),     
					Restrictions.ilike(ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(), strParametro, MatchMode.ANYWHERE)
					     ));
		}
		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), consulta2));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), consulta2));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), "N"));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), "S"));
		return criteria;
	}
	
	public List<ScoItemPropostaFornecedor> listarRazaoPropostaFornecedor(Object pesquisa,int consulta2,int consulta3) {
		DetachedCriteria criteria =  createListarRazaoPropostaFornecedor(pesquisa,consulta2,consulta3);
		return this.executeCriteria(criteria);
	}

	public Long listarRazaoPropostaFornecedorCount(Object pesquisa,int consulta2,int consulta3) {
		DetachedCriteria criteria =  createListarRazaoPropostaFornecedor(pesquisa,consulta2,consulta3);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria  createListarRazaoPropostaFornecedor(Object pesquisa,int consulta2,int consulta3) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		String strParametro = (String) pesquisa;
		
		if(StringUtils.isNotBlank(strParametro)){
			criteria.add(Restrictions.or(					
					Restrictions.ilike(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), strParametro, MatchMode.ANYWHERE),
					Restrictions.ilike(ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strParametro, MatchMode.ANYWHERE)
					     ));
		}
				
		criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString(), ScoFornecedor.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString(), consulta2));		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.NUMERO.toString(),consulta3));
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(),consulta3));
		criteria.add(Restrictions.eq(ScoFornecedor.Fields.SITUACAO.toString(), "A"));
		return criteria;
	}
	

	public Long obterQuantidadePropostasEscolhidasPeloNumLicitacao(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), numLicitacao));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), true));

		return executeCriteriaCount(criteria);
	}

	public Long obterQuantidadePropostasEscolhidasPeloNumLicitacaoENumeroItem(Integer numLicitacao, Short numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		criteria.createAlias(ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL");
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), numLicitacao));
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), true));

		return executeCriteriaCount(criteria);
	}

	/**
	 * Verifica se um item da licitação possui proposta escolhida
	 * @param numLicitacao
	 * @param numero
	 * @return
	 */
	public Boolean verificarItemPacPossuiPropostaEscolhida(Integer numLicitacao, Short numero) {
		return this.obterQuantidadePropostasEscolhidasPeloNumLicitacaoENumeroItem(numLicitacao, numero) > 0;
	}
	
	/**
	 * Busca custo unitário médio de até 10 entradas (compras).
	 * 
	 * @param matCodigo
	 * @return
	 */
	public List<Double> buscarValorUnitarioPropostas2Anos(ScoMaterial matCodigo) {
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT ipf."
				).append( ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()
				).append( ", ipf."
				).append( ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString()
				).append(' ');
		hql.append("FROM " ).append( ScoItemPropostaFornecedor.class.getSimpleName()
				).append( " ipf,");
		hql.append(ScoPropostaFornecedor.class.getSimpleName() ).append( " prf,");
		hql.append(ScoFaseSolicitacao.class.getSimpleName() ).append( " fsc,");
		hql.append(ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " slc ");
		hql.append("WHERE slc."
				).append( ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()
				).append( " = :slcMaterial ");
		hql.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()
				).append( " = slc." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString()
				).append(' ');
		hql.append(" AND ipf."
				).append( ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString()
				).append( " = fsc."
				).append( ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString() ).append(' ');
		hql.append(" AND ipf."
				).append( ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()
				).append( " > :valorunitario");
		hql.append(" AND ipf."
				).append( ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString()
				).append( " > :fatorconversao");
		hql.append(" AND prf."
				).append( ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()
				).append( " = ipf."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID
						.toString() ).append(' ');
		hql.append(" AND prf."
				).append( ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()
				).append( " = ipf."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID
						.toString() ).append(' ');
		hql.append(" AND prf."
				).append( ScoPropostaFornecedor.Fields.DT_DIGITACAO.toString()
				).append( " > :dtdigitacao");

		Query query = createQuery(hql.toString());
		query.setParameter("slcMaterial", matCodigo);
		query.setParameter("valorunitario", BigDecimal.ZERO);
		query.setParameter("fatorconversao", 0);
		query.setParameter("dtdigitacao",
				DateUtil.adicionaDias(new Date(), -730));

		@SuppressWarnings("unchecked")
		List<Object[]> objs = query.getResultList();
		List<Double> valores = new ArrayList<Double>();

		for (Object[] obj : objs) {
			Double valorUnitario = ((BigDecimal)obj[0]).doubleValue();
			Integer fatorConversao = (Integer) obj[1];
			valores.add(valorUnitario / fatorConversao);
		}

		return valores;
	}
	
	/**
	 * Obtem a lista de itens de propostas de fornecedor para um item de licitação
	 * @param numLicitacao
	 * @param numeroItem
	 * @return
	 */
	public List<ScoItemPropostaFornecedor> obterItemPropostaFornecedorPeloNumLicitacaoENumeroItem(Integer numLicitacao, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		criteria.createAlias(ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL");
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO.toString(), numeroItem));
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numLicitacao));

		return executeCriteria(criteria);
	}
	
	public Long obterQuantidadePropostasEscolhidasPeloItlLctNumero(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO_ID_LCT_NUMERO.toString(), numLicitacao));

		return executeCriteriaCount(criteria);
	}
	
	/** 
	 * #5521
	 * @author marcusflorencio
	 * @param Integer itlLctNumero, Short itlNumero
	 * @return List<Object[]>
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<Object[]> createAgrupamentoIpfHql(Integer nroLicitacao, Short itlNumero,  ScoSolicitacaoServico scoBuscaSolLicitServico, 
			 ScoSolicitacaoDeCompra scoBuscaSolLicitCompra  ) {
		
		StringBuffer hql = new StringBuffer(640);
		
		hql.append("SELECT IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString() + " ");//0
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString() + " ");//1
		hql.append(",PFR."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString() + " ");//2
		hql.append(",FRN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString() + " ");//3
		hql.append(",FRN."+ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString() + " ");//4
		hql.append(",FRN."+ScoFornecedor.Fields.DT_VALIDADE_INSS.toString() + " ");//5
		hql.append(",FRN."+ScoFornecedor.Fields.DT_VALIDADE_DAU.toString() + " ");//6
		hql.append(",FRN."+ScoFornecedor.Fields.DT_VALIDADE_TCF.toString() + " ");//7
		hql.append(",PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString() + " ");//8
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.MCM_CODIGO.toString() + " ");//9
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.NC_MCM_CODIGO.toString() + " ");//10
		hql.append(",MCM."+ScoMarcaComercial.Fields.DESCRICAO.toString() + " ");//11
		hql.append(",NC."+ScoNomeComercial.Fields.NOME.toString() + " ");//12
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString() + " ");//13
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.NUMERO.toString() + " ");//14
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString() + " ");//15
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString() + " ");//16
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString() + " ");//17
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString() + " ");//18
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.PERC_IPI.toString() + " ");//19
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.PERC_ACRESCIMO.toString() + " ");//20
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.PERC_DESCONTO.toString() + " ");//21
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.IND_NACIONAL.toString() + " ");//22
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.UMD_CODIGO.toString() + " ");//23
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString() + " ");//24
		hql.append(",IPF."+ScoItemPropostaFornecedor.Fields.JUSTIF_AUTORIZ_USR.toString() + " ");//25
//		hql.append(",SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString() + " ");//26
//		hql.append(",SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString() + " ");//27
		
		/*(SLC_NUMERO IS NULL AND SLS_NUMERO IS NOT NULL) OR (SLC_NUMERO IS NOT NULL AND SLS_NUMERO IS NULL)*/
		
//		hql.append(",SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString() + " "); //28
//		hql.append(",SLS."+ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString() + " "); //29
		
		hql.append(",MOE."+FcpMoeda.Fields.REPRESENTACAO.toString() + " "); //30
		hql.append(",FRN."+ScoFornecedor.Fields.CGC.toString() + " "); //31
		hql.append(",FRN."+ScoFornecedor.Fields.CPF.toString() + " "); //32
		
        hql.append("FROM " +ScoItemPropostaFornecedor.class.getSimpleName()+" IPF ");
//        hql.append("left join IPF."+ScoItemPropostaFornecedor.Fields.SLS.toString()+" SLS ");
//        hql.append("left join IPF."+ScoItemPropostaFornecedor.Fields.SLC.toString()+" SLC ");
        hql.append("inner join IPF."+ScoItemPropostaFornecedor.Fields.FRN.toString()+" FRN ");
        hql.append("inner join IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString()+" PFR ");
        hql.append("left join IPF."+ScoItemPropostaFornecedor.Fields.NOME_COMERCIAL.toString()+" NC ");
        hql.append("left join IPF."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString()+" MCM ");
        hql.append("inner join IPF."+ScoItemPropostaFornecedor.Fields.MOEDA.toString()+" MOE ");
        
        hql.append("WHERE IPF."+ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString() +" = 'N' ");
        hql.append("AND IPF."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString() +" = :nroLicitacao ");
        hql.append("AND IPF."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString() +" = :itlNumero ");	
       // hql.append("AND IPF."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString() +" = :itlNumero ");
        hql.append("AND MCM."+ScoMarcaComercial.Fields.CODIGO.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.MCM_CODIGO.toString()+" ");
        hql.append("AND NC."+ScoNomeComercial.Fields.MARCA_COMERCIAL_ID.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.NC_MCM_CODIGO.toString()+" ");
        hql.append("AND NC."+ScoNomeComercial.Fields.NUMERO.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.NC_NUMERO.toString()+" ");
        hql.append("AND PFR."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()+" ");
        hql.append("AND PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString()+" ");
        hql.append("AND FRN."+ScoFornecedor.Fields.NUMERO.toString() +" = PFR."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()+" ");
//        if(scoBuscaSolLicitCompra != null){
//        	hql.append("AND SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString() +" = :scoBuscaSolLicitCompra ");	
//        }
//        if(scoBuscaSolLicitServico != null)
//        {
//        	hql.append("AND SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString() +" = :scoBuscaSolLicitServico ");
//        }
        hql.append(" AND MOE."+FcpMoeda.Fields.CODIGO.toString() +" = IPF."+ScoItemPropostaFornecedor.Fields.MDA_CODIGO.toString()+" ");
        hql.append(" AND EXISTS( SELECT 1 ");
        hql.append(" FROM " +ScoPareceresMateriais.class.getSimpleName() +" PM ");
        hql.append(" inner join PM."+ScoPareceresMateriais.Fields.PARECER_TECNICO.toString()+" PT ");
        hql.append(" WHERE PT."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString() +" IN ("+"IPF."+ScoItemPropostaFornecedor.Fields.MCM_CODIGO.toString()+" ");
        hql.append(" ,IPF."+ScoItemPropostaFornecedor.Fields.NC_MCM_CODIGO.toString() + ") ");
        
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.MAT_CODIGO.toString()+" = :matCodigo");
        
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.PTC_MCM_CODIGO.toString() +" = PT."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString()+" ");
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.PTC_NRO_SUB_PASTA.toString() +" = PT."+ScoPareceresTecnicos.Fields.NRO_SUB_PASTA.toString()+" ");
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.PTC_OPT_CODIGO.toString() +" = PT."+ScoPareceresTecnicos.Fields.OPT_CODIGO.toString()+" ");
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.PARECER.toString() +" = 'F' ");
        hql.append(" AND PM."+ScoPareceresMateriais.Fields.IND_EXCLUIDO.toString() +" = 'N' )");
        //hql.append("AND ROWNUM = 1)");
        //hql.append(" ORDER BY IPF."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()+", (IPF."+ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()+" " );
       // hql.append(" / IPF."+ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString()+") " );
        
        org.hibernate.Query query = createHibernateQuery(hql.toString());
        
        query.setParameter("nroLicitacao", nroLicitacao);
        query.setParameter("itlNumero", itlNumero);
        if(scoBuscaSolLicitServico != null){
        	query.setParameter("matCodigo", scoBuscaSolLicitServico.getServico().getCodigo());
        }else if(scoBuscaSolLicitCompra != null){
        	query.setParameter("matCodigo", scoBuscaSolLicitCompra.getMaterial().getCodigo());
        }
		
		return query.list();
		
	}
	
	/** 
	 * #5521
	 * @author felipecruz
	 * @param Integer pfrnNumero, 
	 * @paramInteger pfrnLctNumero, 
	 * @paramShort numero
	 * @return ScoItemPropostaFornecedor
	 */
	public ScoItemPropostaFornecedor obterItemPorLicitacaoFornecedorNumero(Integer pfrnNumero, Integer pfrnLctNumero, Short numero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), pfrnNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), pfrnLctNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.NUMERO.toString(), numero));
				
		return (ScoItemPropostaFornecedor) executeCriteriaUniqueResult(criteria);		
	}
	
	/** 
	 * #5521
	 * @author mpessoa
	 * @param Integer pfrnNumero, 
	 * @paramInteger pfrnLctNumero, 
	 * @paramShort itlNumero
	 * @return ScoItemPropostaFornecedor
	 */	
	public ScoItemPropostaFornecedor obterItemPorLicitacaoFornecedorNumeroItem(Integer pfrnNumero, Integer pfrnLctNumero, Short itlNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), pfrnNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), pfrnLctNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), itlNumero));
				
		return (ScoItemPropostaFornecedor) executeCriteriaUniqueResult(criteria);		
	}
	

	/**
	 * Retorna o maior seq de item de proposta do fornecedor existe no banco para determinada proposta
	 * @param pfrnNumero
	 * @param pfrnLctNumero
	 * @return Short
	 */
	public Short obterMaxNumeroItemPropostaFornecedor(Integer pfrnNumero, Integer pfrnLctNumero, Boolean incrementar) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), pfrnNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), pfrnLctNumero));
		
		criteria.setProjection(Projections.max(ScoItemPropostaFornecedor.Fields.NUMERO.toString()));
		Short max = (Short) executeCriteriaUniqueResult(criteria);
		
		if (max != null) {
			if (incrementar) {
				max++;
			}
		} else {
			if (incrementar) {
				max = 1;
			} else {
				max = 0;
			}
		}
		return max;
	}
	
	/**
	 * Contabiliza itens de uma proposta escolhidos ou autorizados pelo fornecedor.
	 * 
	 * @param proposta Proposta
	 * @return Itens
	 */
	public long contarItensEmAf(ScoPropostaFornecedor proposta) {
		final String IPF = "IPF", IPF2 = "IPF2", IAF = "IAF";

		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoItemPropostaFornecedor.class, IPF);
		
		DetachedCriteria afQuery = DetachedCriteria.forClass(
				ScoItemAutorizacaoForn.class, IAF);
		
		afQuery.setProjection(Projections.property(IAF + "." +
				ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		
		afQuery.createAlias(IAF + "." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), IPF2);
		
		afQuery.add(Restrictions.eqProperty( IPF2 + "." + ScoItemPropostaFornecedor.Fields.ID.toString(),
				IPF + "." + ScoItemPropostaFornecedor.Fields.ID.toString()));
		
		criteria.add(Restrictions.or(
				Restrictions.eq(IPF + "." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), true), 
				Subqueries.exists(afQuery)));
		
		criteria.add(Restrictions
				.eq(IPF + "." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), 
						proposta));

		return executeCriteriaCount(criteria);
	}
	
	public List<ScoItemPropostaFornecedor> pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(Integer numeroPac, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoItemPropostaFornecedor.class, "IT");
		
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "F", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "item_lic", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString(), "MARCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.MODELO_COMERCIAL.toString(), "MODELO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.CRITERIO_ESCOLHA_PROPOSTA.toString(), "CRI_ESCOLHA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "COND_PAG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("item_lic." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "fases", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("fases." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "sol_compras", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sol_compras." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "material", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), numeroPac));
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), numeroItem));		
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		return executeCriteria(criteria);
	}
	
	public List<ScoItemPropostaFornecedor> pesquisarItemPropostaEscolhidaPorNumeroLicitacaoENumeroItem(Integer numeroPac, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoItemPropostaFornecedor.class, "IT");
		criteria.createAlias(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "F", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), numeroPac));
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), numeroItem));
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));
				
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa os itens de proposta existentes no banco conforme um número de licitação (PAC) e o fornecedor
	 * @param numeroPac
	 * @param fornecedor
	 * @param verificarItemPacJulgado
	 * @return List
	 */
	public List<ScoItemPropostaVO> pesquisarItemPropostaPorNumeroLicitacao(Integer numeroPac, ScoFornecedor fornecedor, Boolean verificarOutraPropostaEscolhida, DominioSimNaoTodos julgados) {
		List<ScoItemPropostaFornecedor> listaItemProposta;

		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoItemPropostaFornecedor.class, "IT");
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.INNER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.UNIDADE_MEDIDA.toString(), "UNDD_MDD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString(), "MRC_CMRCL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.MODELO_COMERCIAL.toString(), "MDL_CMRCL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IT."+ScoItemPropostaFornecedor.Fields.MOEDA.toString(), "MOEDA_CMRCL", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("IT."+ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), numeroPac));

		if (fornecedor != null) {
			criteria.add(Restrictions.eq("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), fornecedor));
		}
		
		if (verificarOutraPropostaEscolhida) {
			criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			
			DetachedCriteria subQueryPropostaEscolhida = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "ITPROP2");		
	    	subQueryPropostaEscolhida.setProjection(Projections.property("ITPROP2." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO_ID_LCT_NUMERO.toString()));
	    	subQueryPropostaEscolhida.add(Restrictions.eq("ITPROP2."+ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));
	    	subQueryPropostaEscolhida.add(Restrictions.eqProperty("ITPROP2."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), 
	    			"IT."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString()));
	    	subQueryPropostaEscolhida.add(Restrictions.eqProperty("ITPROP2."+ScoItemPropostaFornecedor.Fields.NUMERO.toString(), 
	    			"IT."+ScoItemPropostaFornecedor.Fields.NUMERO.toString()));
	    	subQueryPropostaEscolhida.add(Restrictions.neProperty("ITPROP2."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), 
	    			"IT."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()));
	    	
	    	criteria.add(Subqueries.notExists(subQueryPropostaEscolhida));
		}
		
		if (julgados != null) {
			if (julgados.equals(DominioSimNaoTodos.SIM)) {				
				criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString(), Boolean.TRUE));
			} else if (julgados.equals(DominioSimNaoTodos.NAO)) {
				criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString(), Boolean.FALSE));
			}
		}
		
		listaItemProposta = executeCriteria(criteria);
		
		return preencherVO(numeroPac, listaItemProposta, fornecedor);
	}

	/**
	 * Baseado na lista de itens de proposta do fornecedor vindo do banco cria uma lista de ScoItemPropostaVO
	 * @param numeroPac
	 * @param listaItemProposta
	 * @param fornecedor
	 * @return List
	 */
	private List<ScoItemPropostaVO> preencherVO(Integer numeroPac, List<ScoItemPropostaFornecedor> listaItemProposta, ScoFornecedor fornecedor) {
		List<ScoItemPropostaVO> listaRetorno = new ArrayList<ScoItemPropostaVO>();
		
		for (ScoItemPropostaFornecedor item : listaItemProposta) {
			listaRetorno.add(montarObjetoVO(numeroPac,item, fornecedor));
		}
		
		return listaRetorno;
	}		
		
	/**
	 * Baseado no item de proposta do fornecedor vindo do banco cria um ScoItemPropostaVO 
	 * @param numeroPac
	 * @param item
	 * @param fornecedor
	 * @return ScoItemPropostaVO
	 */
	private ScoItemPropostaVO montarObjetoVO(Integer numeroPac, ScoItemPropostaFornecedor item, ScoFornecedor fornecedor) {
		ScoItemPropostaVO obj = new ScoItemPropostaVO();
		obj.setNumeroPac(numeroPac);
		obj.setNumeroItemPac(item.getItemLicitacao().getId().getNumero());
		obj.setNumeroItemProposta(item.getId().getNumero());
		obj.setFornecedorProposta(fornecedor);
		obj.setCodigoMaterialFornecedor(item.getCodigoItemFornecedor());
		obj.setDescricaoItem(this.getScoFaseSolicitacaoDAO().obterNomeMaterialServico(item.getItemLicitacao(), true));
								
		if (item.getNomeComercial() != null) {
			obj.setNomeComercial(item.getNomeComercial());
		}
		obj.setQtdItemProposta(item.getQuantidade());
		obj.setUnidadeProposta(item.getUnidadeMedida());
		obj.setFatorConversao(item.getFatorConversao());
		obj.setIndNacional(item.getIndNacional());
		obj.setMoedaItemProposta(item.getMoeda());
		obj.setIndAutorizUsr(item.getIndAutorizUsr());
		
		if (item.getApresentacao() != null) {
			obj.setApresentacao(item.getApresentacao());
		}
		if (item.getValorUnitario() != null) {
			obj.setValorUnitarioItemProposta(item.getValorUnitario());
		}
				
		obj.setIndEscolhido(item.getIndEscolhido());
		if (item.getCriterioEscolhaProposta() != null) {
			obj.setCriterioEscolha(item.getCriterioEscolhaProposta().getDescricao()); 
		}
		obj.setIndDesclassificado(item.getIndDesclassificado());
		
		obj.setIndAnalisadoParecerTecnico(item.getIndAnalisadoPt());
		obj.setParecerTecnicoMarca(null);
		obj.setDesabilitaCheckboxJulgamentoLote(Boolean.FALSE);
		obj.setTipoOperacao(DominioOperacaoBanco.UPD);
		
		this.preencherCamposCondicionaisVO(obj, item);
		
		return obj;
	}	
			
	private void preencherCamposCondicionaisVO(ScoItemPropostaVO obj, ScoItemPropostaFornecedor item) {
		if (item.getMotDesclassif() != null) {
			obj.setMotivoDesclassificacao(item.getMotDesclassif());
		}		
		if (item.getNroOrcamento() != null) {
			obj.setNumeroOrcamento(item.getNroOrcamento());
		}
		if (item.getObservacao() != null) {
			obj.setObservacao(item.getObservacao());
		}
		if (item.getMarcaComercial() != null) {
			obj.setMarcaComercial(item.getMarcaComercial());			
		}
		if (item.getModeloComercial() != null) {
			obj.setModeloComercial(item.getModeloComercial());
		}
		if (item.getJustifAutorizUsr() != null) {
			obj.setJustifAutorizUsr(item.getJustifAutorizUsr());
		}
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return aScoFaseSolicitacaoDAO;
	}

	/**
	 * Verifica se existe item de proposta associado a uma condição de pagamento.
	 * 
	 * @param id ID da condição.
	 * @return Flag
	 */
	public boolean existeItemAssociadoACondicao(Integer id) {
		final String I = "I", C = "C";
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemPropostaFornecedor.class, I);
		
		criteria.createAlias(I + "." + ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), C);
		
		criteria.add(Restrictions.eq(C + "." + ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), id));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<PropFornecAvalParecerVO> pesquisarItensPropostaFornecedorPAC(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
		
		ObterCriteriaItensPropostaFornecedorPACQueryBuilder builderItensPropostaFornecedorPAC = new ObterCriteriaItensPropostaFornecedorPACQueryBuilder();
		
		DetachedCriteria criteria = builderItensPropostaFornecedorPAC.build(numeroPAC, listaSituacaoParecer);	
        
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("ITL_LICITACAO." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()), PropFornecAvalParecerVO.Fields.NUMERO_LICITACAO.toString())
				.add(Projections.property("ITL_LICITACAO." + ScoItemLicitacao.Fields.NUMERO.toString()), PropFornecAvalParecerVO.Fields.NUMERO_ITEM_LICITACAO.toString())
		        .add(Projections.property("FORNEC." + ScoFornecedor.Fields.NUMERO.toString()), PropFornecAvalParecerVO.Fields.NUMERO_FORNECEDOR.toString())
		        .add(Projections.property("FORNEC." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PropFornecAvalParecerVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString())
		        .add(Projections.property("FORNEC." + ScoFornecedor.Fields.CGC.toString()), PropFornecAvalParecerVO.Fields.CGC_FORNECEDOR.toString())
		        .add(Projections.property("FORNEC." + ScoFornecedor.Fields.CPF.toString()), PropFornecAvalParecerVO.Fields.CPF_FORNECEDOR.toString())	
		        .add(Projections.property("ITL_LICITACAO." + ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString()), PropFornecAvalParecerVO.Fields.PROPOSTA_ESCOLHIDA_ITEM_LICITACAO.toString())
		        .add(Projections.property("MATERIAL." + ScoMaterial.Fields.CODIGO.toString()), PropFornecAvalParecerVO.Fields.CODIGO_MATERIAL.toString())
		        .add(Projections.property("MATERIAL." + ScoMaterial.Fields.NOME.toString()), PropFornecAvalParecerVO.Fields.NOME_MATERIAL.toString())
		        .add(Projections.property("MATERIAL." + ScoMaterial.Fields.DESCRICAO.toString()), PropFornecAvalParecerVO.Fields.DESCRICAO_MATERIAL.toString())
		        .add(Projections.property("UN_MED_MATERIAL." + ScoUnidadeMedida.Fields.CODIGO.toString()), PropFornecAvalParecerVO.Fields.CODIGO_UNIDADE_MEDIDA_MATERIAL.toString())
		        .add(Projections.property("SOLICITACAO_COMPRAS." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), PropFornecAvalParecerVO.Fields.QTDE_APROVADA_SC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()), PropFornecAvalParecerVO.Fields.VALOR_UNITARIO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString()), PropFornecAvalParecerVO.Fields.FATOR_CONVERSAO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("MARCA_ITL." + ScoMarcaComercial.Fields.CODIGO.toString()), PropFornecAvalParecerVO.Fields.COD_MARCA_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("MARCA_ITL." + ScoMarcaComercial.Fields.DESCRICAO.toString()), PropFornecAvalParecerVO.Fields.DESCR_MARCA_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("MODELO_ITL." + ScoMarcaModelo.Fields.SEQP.toString()), PropFornecAvalParecerVO.Fields.COD_MODELO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("MODELO_ITL." + ScoMarcaModelo.Fields.DESCRICAO.toString()), PropFornecAvalParecerVO.Fields.DESCR_MODELO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.DT_ENTREGA_AMOSTRA.toString()), PropFornecAvalParecerVO.Fields.DT_ENTREGA_AMOSTRA.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString()), PropFornecAvalParecerVO.Fields.NUMERO_LICITACAO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()), PropFornecAvalParecerVO.Fields.FORNEC_NUMERO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.NUMERO.toString()), PropFornecAvalParecerVO.Fields.NUMERO_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.IND_AUTORIZ_USR.toString()), PropFornecAvalParecerVO.Fields.IND_AUT_USR_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.JUSTIF_AUTORIZ_USR.toString()), PropFornecAvalParecerVO.Fields.JUSR_AUT_USR_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.IND_DESCLASSIFICADO.toString()), PropFornecAvalParecerVO.Fields.IND_DESCLASSIF_ITEM_PROPOS_FORNEC.toString())
		        .add(Projections.property("ITL." + ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()), PropFornecAvalParecerVO.Fields.MOT_DESCLASSIF_ITEM_PROPOS_FORNEC.toString());
				
		
		
		
		criteria.addOrder(Order.asc("ITL_LICITACAO." + ScoItemLicitacao.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("ITL." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()));
		
		criteria.setProjection(projection);		
				
		criteria.setResultTransformer(Transformers.aliasToBean(PropFornecAvalParecerVO.class));
				
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}
	
	public Long contarItensPropostaFornecedorPAC(Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
        ObterCriteriaItensPropostaFornecedorPACQueryBuilder builderItensPropostaFornecedorPAC = new ObterCriteriaItensPropostaFornecedorPACQueryBuilder();		
		DetachedCriteria criteria = builderItensPropostaFornecedorPAC.build(numeroPAC, listaSituacaoParecer);
		return this.executeCriteriaCount(criteria);	
	}	

	/**
	 * Verifica se item da proposta possui outra proposta que seja escolhida.
	 * 
	 * @param item Item da proposta.
	 */
	public boolean verificarItemPossuiPropostaEscolhida(
			ScoItemPropostaFornecedor item) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemPropostaFornecedor.class);

		criteria.add(Restrictions.eq(
				ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(),
				item.getItemLicitacao()));
		
		criteria.add(Restrictions.eq(
				ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), true));
		
		criteria.add(Restrictions.isNull(
				ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString()));
		
		criteria.add(Restrictions.ne(
				ScoItemPropostaFornecedor.Fields.ID.toString(), item.getId()));

		return executeCriteriaCount(criteria) > 0;
	}

	public Integer obterScParaAnaliseTecnica(
			ScoItemPropostaFornecedorId id) {
		final String IPF = "IPF", ITL = "ITL", LCT = "LCT", MLC = "MLC", 
				FSL = "FSL", SLC = "SLC";
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemPropostaFornecedor.class, IPF);
		
		criteria.setProjection(Projections.property(
				SLC + "." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		
		criteria.createAlias(IPF + "." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), ITL);
		criteria.createAlias(ITL + "." + ScoItemLicitacao.Fields.LICITACAO.toString(), LCT);
		criteria.createAlias(LCT + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), MLC);
		criteria.createAlias(ITL + "." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), FSL);
		criteria.createAlias(FSL + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), SLC);
		
		criteria.add(Restrictions.eq(
				IPF + "." + ScoItemPropostaFornecedor.Fields.ID.toString(), id));
		
		criteria.add(Restrictions.eq(
				FSL + "." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		criteria.add(Restrictions.eq(
				FSL + "." + ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ItemPropostaAFVO> pesquisarItensPac(Integer numeroPac){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");

		criteria.createAlias("IPF" + "." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ITL" + "." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSL", JoinType.INNER_JOIN);
		criteria.createAlias("FSL" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSL" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPF" + "." + ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CDP" + "." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPF" + "." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PFO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PFO" + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FOR", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItemPropostaAFVO.Fields.NUMERO_ITEM.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()), ItemPropostaAFVO.Fields.NUMERO_PAC.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), ItemPropostaAFVO.Fields.VALOR_UNITARIO_PREVISTO.toString())
				.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString()), ItemPropostaAFVO.Fields.IND_ESCOLHIDO.toString())
				.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()), ItemPropostaAFVO.Fields.VALOR_UNITARIO_PROP_FORNECEDOR.toString())
				.add(Projections.property("FPG." + ScoFormaPagamento.Fields.CODIGO.toString()), ItemPropostaAFVO.Fields.COD_FORMA_PAGAMENTO.toString())
				.add(Projections.property("FPG." + ScoFormaPagamento.Fields.DESCRICAO.toString()), ItemPropostaAFVO.Fields.DESCR_FORMA_PAGAMENTO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.TIPO.toString()), ItemPropostaAFVO.Fields.TIPO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()), ItemPropostaAFVO.Fields.SLC_NUMERO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()), ItemPropostaAFVO.Fields.SLS_NUMERO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()), ItemPropostaAFVO.Fields.SLC_IND_EXCLUSAO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString()), ItemPropostaAFVO.Fields.SLC_VBG_SEQ.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString()), ItemPropostaAFVO.Fields.SLC_NTD_GND_CODIGO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString()), ItemPropostaAFVO.Fields.SLC_NTD_CODIGO.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString()), ItemPropostaAFVO.Fields.SLS_IND_EXCLUSAO.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.VBG_SEQ.toString()), ItemPropostaAFVO.Fields.SLS_VBG_SEQ.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString()), ItemPropostaAFVO.Fields.SLS_NTD_GND_CODIGO.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NTD_CODIGO.toString()), ItemPropostaAFVO.Fields.SLS_NTD_CODIGO.toString())
				.add(Projections.property("FOR." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ItemPropostaAFVO.Fields.RAZAO_SOCIAL_FORNECEDOR.toString());
		
				criteria.setProjection(projection);	
		
		if(numeroPac != null) {
			criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroPac));
		}
		
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemPropostaAFVO.class));
		return executeCriteria(criteria);
	}
	
	public Long listarItemPropostaFornecedorPorLicitacaoCount(Integer numPac){
		StringBuilder sql = new StringBuilder(1200);

		sql.append("SELECT COUNT(*)")
		.append(" FROM  AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPF ")
		.append(" RIGHT JOIN AGH.SCO_ITENS_LICITACOES ITL ")
		.append("ON IPF.ITL_LCT_NUMERO=ITL.LCT_NUMERO AND IPF.ITL_NUMERO=ITL.NUMERO and IPF.IND_EXCLUSAO='N' ") 
		.append("JOIN AGH.SCO_FASES_SOLICITACOES FSE ")
		.append("ON FSE.ITL_LCT_NUMERO = ITL.LCT_NUMERO AND ITL.NUMERO=FSE.ITL_NUMERO and FSE.IND_EXCLUSAO='N' ")
		.append(" LEFT JOIN AGH.SCO_SOLICITACOES_DE_COMPRAS SLC ")
		.append(" ON FSE.SLC_NUMERO=SLC.NUMERO ")
		.append(" LEFT JOIN AGH.SCO_SOLICITACOES_SERVICO SLS ")
		.append(" ON FSE.SLS_NUMERO=SLS.NUMERO ")
		.append(" WHERE")
		.append(" ITL.LCT_NUMERO = ").append(numPac )
		.append(" AND ( (FSE.TIPO =  'C'")
		.append(" AND (SLC.IND_EXCLUSAO = 'S' ")
		.append(" OR SLC.VBG_SEQ IS NULL ")
		.append(" OR SLC.NTD_GND_CODIGO IS NULL OR SLC.NTD_CODIGO IS NULL)) ")
		.append(" OR (FSE.TIPO = 'S' ")
		.append(" AND (SLS.IND_EXCLUSAO = 'S' ")
		.append(" OR SLS.VBG_SEQ IS NULL ")
		.append(" OR SLS.NTD_GND_CODIGO IS NULL OR SLS.NTD_CODIGO IS NULL)) ")
		.append(" OR ITL.NUMERO NOT IN (")
		.append(" SELECT IPF.ITL_NUMERO FROM ")
		.append(" AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPFO") 
		.append(" WHERE IPFO.ITL_LCT_NUMERO = ITL.LCT_NUMERO ")
		.append(" AND IPFO.ITL_NUMERO = ITL.NUMERO ")
		.append(" AND IPFO.IND_EXCLUSAO = 'N' ")
		.append(" AND IPFO.IND_ESCOLHIDO = 'S' ")
		.append(" AND IPFO.VALOR_UNITARIO IS NOT NULL ")
		.append(" AND IPFO.CDP_NUMERO IS NOT NULL))");

		Query query = this.createNativeQuery(sql.toString());
		return isOracle() ? ((BigDecimal) query.getSingleResult()).longValue() : ((BigInteger) query.getSingleResult()).longValue();
	}

	public List<VisualizarExtratoJulgamentoLicitacaoVO> buscarPropostasFornecedor(Integer numeroPac) {

		StringBuilder sql = new StringBuilder(950);

		sql.append("SELECT ") 
		.append("IPF.PFR_FRN_NUMERO as pfrFrnNumero, ") 
		.append("FRN.RAZAO_SOCIAL as razaoSocial, ")
		.append("FRN.CGC as cgc, ")
		.append("IPF.ITL_NUMERO as itemSolicitacaoNumero, ")
		.append("IPF.UMD_CODIGO as umdCodigo, ")
		.append("IPF.FATOR_CONVERSAO as fatorConversao, ")
		.append("CEP.DESCRICAO as criterio, ")
		.append("FPG.DESCRICAO as condPagamento, ")
		.append("IPF.VALOR_UNITARIO as valorUnitario ")
		.append("FROM ")
		.append("AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPF, ")
		.append("AGH.SCO_PROPOSTAS_FORNECEDORES PFR, ")
		.append("AGH.SCO_FORNECEDORES FRN, ")
		.append("AGH.SCO_CRITERIO_ESCOLHA_PROPOSTAS CEP, ")
		.append("AGH.SCO_FORMA_PAGAMENTOS FPG, ")
		.append("AGH.SCO_CONDICOES_PAGAMENTO_PROPOS CDP ")
		.append("WHERE ")
		.append("PFR.FRN_NUMERO = IPF.PFR_FRN_NUMERO ")
		.append("AND PFR.LCT_NUMERO = IPF.PFR_LCT_NUMERO ")
		.append("AND FRN.NUMERO = PFR.FRN_NUMERO ")
		.append("AND CEP.CODIGO = IPF.CEP_CODIGO ")
		.append("AND CDP.PFR_FRN_NUMERO = IPF.PFR_FRN_NUMERO ")
		.append("AND CDP.PFR_LCT_NUMERO = IPF.PFR_LCT_NUMERO ")
		.append("AND FPG.CODIGO         = CDP.FPG_CODIGO ")
		.append("AND IPF.PFR_LCT_NUMERO = ").append( numeroPac ).append(' ')
		.append("AND IPF.IND_ESCOLHIDO = 'S' ")
		.append("AND IPF.IND_EXCLUSAO  = 'N' ")
		.append("ORDER BY IPF.ITL_NUMERO ");

		SQLQuery q = createSQLQuery(sql.toString());

		return  q.addScalar("pfrFrnNumero", IntegerType.INSTANCE).
		addScalar("razaoSocial",StringType.INSTANCE).
		addScalar("cgc",LongType.INSTANCE).
		addScalar("itemSolicitacaoNumero",ShortType.INSTANCE).
		addScalar("umdCodigo",StringType.INSTANCE).
		addScalar("fatorConversao",IntegerType.INSTANCE).
		addScalar("criterio",StringType.INSTANCE).
		addScalar("condPagamento",StringType.INSTANCE).
		addScalar("valorUnitario",BigDecimalType.INSTANCE).
		setResultTransformer(Transformers.aliasToBean(VisualizarExtratoJulgamentoLicitacaoVO.class)).list();
	}
	
	public List<ScoItemPropostaFornecedor> consultarPropostasVencedoras(Integer lctNumero, Short numero) {
				DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
				criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString(), lctNumero));
				criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(), numero));
				criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
				criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));
				return executeCriteria(criteria);
	}

	public List<PropostasVencedorasFornecedorVO> buscarItemFornecedor(ScoItemPropostaFornecedor itemPropostaFornecedor, Integer numeroLicitacao) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT IPF.CDP_NUMERO AS cdpNumero, ");
        sql.append("FRN.RAZAO_SOCIAL AS razaoSocial ");	  
        sql.append("FROM AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPF, ");
        sql.append("AGH.SCO_FORNECEDORES FRN ");
        sql.append("WHERE IPF.PFR_FRN_NUMERO = FRN.NUMERO ");
        sql.append("AND IPF.PFR_LCT_NUMERO   = "+numeroLicitacao+" ");
        sql.append("AND IPF.NUMERO           = "+itemPropostaFornecedor.getId().getNumero()+" ");
        sql.append("AND FRN.NUMERO           = "+itemPropostaFornecedor.getId().getPfrFrnNumero()+" ");
        sql.append("AND FRN.IND_SITUACAO     = 'A' ");
     
		SQLQuery query = createSQLQuery(sql.toString());
		
        return  query.addScalar("cdpNumero",IntegerType.INSTANCE).
				addScalar("razaoSocial",StringType.INSTANCE).
				setResultTransformer(Transformers.aliasToBean(PropostasVencedorasFornecedorVO.class)).list();
	}
	
	
	public boolean consultarFornecedorAdjudicado(Integer lctNumero, Integer frnNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), lctNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), frnNumero));
		criteria.add(Restrictions.eq(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), DominioSimNao.S.isSim()));
		
		return (boolean) executeCriteriaExists(criteria);
	}
	
}
