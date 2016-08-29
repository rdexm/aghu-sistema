/**
 * 
 */
package br.gov.mec.aghu.estoque.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPesquisaNotaRecebimento;
import br.gov.mec.aghu.estoque.vo.ItemNotaRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoVO;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VSceItemNotaRecebimentoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class SceNotaRecebimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceNotaRecebimento> {

	private static final long serialVersionUID = 8801282254592693735L;

	/**
	 * Pesquisa notas de recebimento para consulta
	 */
	public List<SceNotaRecebimento> pesquisarNotasRecebimentoConsulta(
			Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, 
			Date dataSituacao, Date dataFinal, Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn, 
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		DetachedCriteria criteria = obterCriteriaNotaRecebimento(seqNotaRecebimento, estorno, 
				debitoNotaRecebimento, situacaoPesquisaNR,  dataSituacao,dataFinal,
				numeroProcessoCompAutorizacaoForn, numeroComplementoAutorizacaoForn, 
				situacaoAutorizacaoForn, numeroFornecedor, documentoFiscalEntrada);

		return this.executeCriteria(criteria, firstResult, maxResult, SceNotaRecebimento.Fields.NUMERO_NR.toString(), false);
	}

	/**
	 * Efetua o count da pesquisa notas de recebimento para consulta
	 */
	public Long pesquisarNotasRecebimentoConsultaCount(
			Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, 
			Date dataSituacao, Date dataFim, Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn, 
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		DetachedCriteria criteria = obterCriteriaNotaRecebimento(seqNotaRecebimento, estorno, 
				debitoNotaRecebimento, situacaoPesquisaNR,  dataSituacao,dataFim,
				numeroProcessoCompAutorizacaoForn, numeroComplementoAutorizacaoForn, 
				situacaoAutorizacaoForn, numeroFornecedor, documentoFiscalEntrada);
		return this.executeCriteriaCount(criteria);
	}	

	/**
	 * Monta o critéria para consulta de notas de recebimento
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaNotaRecebimento(
			Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, 
			Date dataSituacao, Date dataFinal, Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn, 
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		String campoDataSituacao = null,
		campoServidorSituacao = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NR");

		ProjectionList p = Projections.projectionList();
		//Projection NR
		p.add(Projections.property("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DATA_GERACAO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.SERVIDOR_GERACAO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DATA_ESTORNO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.SERVIDOR_ESTORNO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DT_DEBITO_NR.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.SERVIDOR_DEBITADO.toString()));
		//Projection AFN
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + "." + 
				ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.SITUACAO.toString()));
		//Projection FRN
		p.add(Projections.property("NR." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + "." + 
				ScoPropostaFornecedor.Fields.FORNECEDOR.toString() + "." +
				ScoFornecedor.Fields.NUMERO.toString()));
		p.add(Projections.property("NR." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + "." + 
				ScoPropostaFornecedor.Fields.FORNECEDOR.toString() + "." +
				ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		p.add(Projections.property("NR." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString() + "." + 
				ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + "." + 
				ScoPropostaFornecedor.Fields.FORNECEDOR.toString() + "." +
				ScoFornecedor.Fields.NOME_FANTASIA.toString()));
		//Projection DFE
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString() + "." + 
				SceDocumentoFiscalEntrada.Fields.NUMERO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString() + "." + 
				SceDocumentoFiscalEntrada.Fields.TIPO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString() + "." + 
				SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()));
		p.add(Projections.property("NR." +SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString() + "." + 
				SceDocumentoFiscalEntrada.Fields.DT_ENTRADA.toString()));
	
		// ALIASES
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FOR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PPF", JoinType.INNER_JOIN);
		criteria.createAlias("PPF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);

		// ALIAS DA VERSÃO 6
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.SERVIDOR_DEBITADO.toString(), "SER_DEB", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_DEB." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_DEB", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.SERVIDOR_ESTORNO.toString(), "SER_EST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_EST." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_EST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.SERVIDOR_GERACAO.toString(), "SER_GER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_GER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_GER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NR." + SceNotaRecebimento.Fields.SERVIDOR_LIB.toString(), "SER_LIB", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_LIB." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_LIB", JoinType.LEFT_OUTER_JOIN);

		if(seqNotaRecebimento != null){
			criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), seqNotaRecebimento));			
		}

		if (DominioSituacaoPesquisaNotaRecebimento.G.equals(situacaoPesquisaNR)) {
			campoDataSituacao = SceNotaRecebimento.Fields.DATA_GERACAO.toString(); 
			campoServidorSituacao = SceNotaRecebimento.Fields.SERVIDOR_GERACAO.toString();
		}

		if (DominioSituacaoPesquisaNotaRecebimento.E.equals(situacaoPesquisaNR)) {
			campoDataSituacao = SceNotaRecebimento.Fields.DATA_ESTORNO.toString(); 
			campoServidorSituacao = SceNotaRecebimento.Fields.SERVIDOR_ESTORNO.toString();
		}
		if(campoDataSituacao != null && campoServidorSituacao != null){
			if(dataSituacao != null)
			{
				//Se somente a data inicial estiver preenchida
				if(dataFinal == null){
					//usa between pq a data na tela nao tem hora e no banco tem
					criteria.add(Restrictions.between("NR."+ campoDataSituacao,
							DateUtil.truncaData(dataSituacao), DateUtil.truncaDataFim(dataSituacao)));
				}
				else{
					//se as duas estiverem
					criteria.add(Restrictions.between("NR."+ campoDataSituacao,
							DateUtil.truncaData(dataSituacao), DateUtil.truncaDataFim(dataFinal)));
				}
			}
		}	
		if(estorno != null){
			criteria.add(Restrictions.eq("NR."+ SceNotaRecebimento.Fields.IND_ESTORNO.toString(), estorno));
		} 
		if(debitoNotaRecebimento != null){
			criteria.add(Restrictions.eq("NR."+SceNotaRecebimento.Fields.IND_DEBITO.toString(), debitoNotaRecebimento));
		}
		if(numeroProcessoCompAutorizacaoForn!= null){
			criteria.add(Restrictions.eq("PPF." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(),numeroProcessoCompAutorizacaoForn));
		}
		if(numeroComplementoAutorizacaoForn != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(),
					numeroComplementoAutorizacaoForn));
		}				
		if(situacaoAutorizacaoForn != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(),
					situacaoAutorizacaoForn));
		}		
		if(numeroFornecedor != null){
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		}		
		if(documentoFiscalEntrada != null) {

			if (documentoFiscalEntrada.getNumero() != null) {

				criteria.add(Restrictions.eq("DFE."+ SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), documentoFiscalEntrada.getNumero()));

			}

			if (documentoFiscalEntrada.getFornecedor() != null) {

				criteria.add(Restrictions.eq("DFE."+ SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), documentoFiscalEntrada.getFornecedor()));

			}

			if (documentoFiscalEntrada.getTipo() != null) {

				criteria.add(Restrictions.eq("DFE."+ SceDocumentoFiscalEntrada.Fields.TIPO.toString(), documentoFiscalEntrada.getTipo()));

			}


		}

		return criteria;
	}

	/**
	 * Obtém os itens de uma nota de recebimento dado seu código
	 * @param seqNotaRecebimento
	 * @return List<ItemNotaRecebimentoVO>
	 */
	public List<ItemNotaRecebimentoVO> pesquisarItensNotaRecebimento(Integer seqNotaRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VSceItemNotaRecebimentoAutorizacaoFornecimento.class, "VINR_IAF");

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("INR." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()),
				ItemNotaRecebimentoVO.Fields.QUANTIDADE.toString());
		p.add(Projections.property("UM." + ScoUnidadeMedida.Fields.CODIGO.toString()),
				ItemNotaRecebimentoVO.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		p.add(Projections.property("UM." + ScoUnidadeMedida.Fields.DESCRICAO.toString()),
				ItemNotaRecebimentoVO.Fields.DESCRICAO_UNIDADE_MEDIDA.toString());
		p.add(Projections.property("INR." + SceItemNotaRecebimento.Fields.VALOR.toString()),
				ItemNotaRecebimentoVO.Fields.VALOR.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.NUMERO_ITEM_LICITACAO.toString()),
				ItemNotaRecebimentoVO.Fields.CODIGO_ITEM_AUTORIZACAO_FORN.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.TIPO.toString()),
				ItemNotaRecebimentoVO.Fields.TIPO.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.NOME_MATERIAL_SERVICO.toString()),
				ItemNotaRecebimentoVO.Fields.NOME_MATERIAL_SERVICO.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.NOME_MARCA_COMERCIAL.toString()),
				ItemNotaRecebimentoVO.Fields.NOME_MARCA_COMERCIAL.toString());		
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.PERCENTUAL_ACRESCIMO.toString()),
				ItemNotaRecebimentoVO.Fields.PERCENTUAL_ACRESCIMO.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.PERCENTUAL_ACRESCIMO_ITEM.toString()),
				ItemNotaRecebimentoVO.Fields.PERCENTUAL_ACRESCIMO_ITEM.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.PERCENTUAL_DESCONTO.toString()),
				ItemNotaRecebimentoVO.Fields.PERCENTUAL_DESCONTO.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.PERCENTUAL_DESCONTO_ITEM.toString()),
				ItemNotaRecebimentoVO.Fields.PERCENTUAL_DESCONTO_ITEM.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.PERCENTUAL_IPI.toString()),
				ItemNotaRecebimentoVO.Fields.PERCENTUAL_IPI.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.VALOR_UNITARIO_ITEM_AUTORIZACAO_FORN.toString()),
				ItemNotaRecebimentoVO.Fields.VALOR_UNITARIO_ITEM_AUTORIZACAO_FORN.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.QUANTIDADE_RECEBIDA.toString()),
				ItemNotaRecebimentoVO.Fields.QUANTIDADE_RECEBIDA.toString());
		p.add(Projections.property("VINR_IAF." + VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.CODIGO_MARCA.toString()),
				ItemNotaRecebimentoVO.Fields.CODIGO_MARCA.toString());

		criteria.setProjection(p);

		criteria.createAlias("VINR_IAF."+ VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORNECIMENTO.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias("IAF."+ ScoItemAutorizacaoForn.Fields.ITEM_NOTA_RECEBIMENTO.toString(), "INR", JoinType.INNER_JOIN);
		criteria.createAlias("INR."+ SceItemNotaRecebimento.Fields.UNIDADE_MEDIDA.toString(), "UM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INR."+ SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NR", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), seqNotaRecebimento));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemNotaRecebimentoVO.class));

		return executeCriteria(criteria);
	}	

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public NotaRecebimentoVO pesquisaDadosNotaRecebimento(Integer numNotaRec, Short tmvNR_seq, boolean isConsiderarNotaEmpenho)throws BaseException{
		NotaRecebimentoVO notaRec = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "nrs");
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.MOVIMENTO_MATERIAL.toString(), "mmt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "afn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("afn."+ScoAutorizacaoForn.Fields.AF_EMPENHO.toString(), "afp", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("afn."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "pfn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("afn."+ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "ntd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ntd."+FsoNaturezaDespesa.Fields.RELACIONA_NATUREZAS.toString(), "rnt", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("pfn."+ScoPropostaFornecedor.Fields.LICITACAO.toString(), "lct", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pfn."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "frn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("frn."+ScoFornecedor.Fields.CONTA_CORRENTE_FORNECEDOR.toString(), "cnf", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("mmt."+SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.TITULO.toString(), "ttl", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.NOTA_RECEB_PROVISORIO.toString(), "nrp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrp."+SceNotaRecebProvisorio.Fields.SERVIDOR.toString(), "snrp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("snrp."+RapServidores.Fields.PESSOA_FISICA.toString(), "pef", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.SERVIDOR_GERACAO.toString(), "snrs", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("snrs."+RapServidores.Fields.PESSOA_FISICA.toString(), "pef2", JoinType.LEFT_OUTER_JOIN);

		/*Projections*/
		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.distinct(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString())), "dfe_type");
		projection.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), "numero_nf");
		projection.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()), "dt_emissao");

		projection.add(Projections.property("frn." + ScoFornecedor.Fields.NUMERO.toString()), "cod_fornec");
		projection.add(Projections.property("frn." + ScoFornecedor.Fields.CGC.toString()), "cgc");
		projection.add(Projections.property("frn." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "razao_social");

		projection.add(Projections.sqlProjection("cnf10_.agb_bco_codigo as banco_nro", new String[]{"banco_nro"}, new Type[] { IntegerType.INSTANCE }),"banco_nro");
		projection.add(Projections.sqlProjection("cnf10_.conta_corrente", new String[]{"conta_corrente"}, new Type[] { StringType.INSTANCE }),"conta_corrente");
		projection.add(Projections.sqlProjection("cnf10_.agb_codigo as agenc_nro", new String[]{"agenc_nro"}, new Type[] { StringType.INSTANCE }),"agenc_nro");

		projection.add(Projections.property("nrs." + SceNotaRecebimento.Fields.NUMERO_NR.toString()), "nr");
		projection.add(Projections.property("nrs." + SceNotaRecebimento.Fields.DATA_GERACAO.toString()), "dt_geracao");

		projection.add(Projections.sqlProjection("case 	when {alias}.numero_empenho_siafi is null then" +
				"			case 	when afp4_.ano_empenho is null then" +
				"				afp4_.numero" +
				"			else" +
				"				afp4_.ano_empenho||'/'||afp4_.numero" +
				"			end" +
				"		else" +
				"			substr({alias}.numero_empenho_siafi, 1,4)||'/'||substr({alias}.numero_empenho_siafi, 7,6)" +
				" end as num_empenho", new String[]{"num_empenho"}, new Type[] { StringType.INSTANCE }),"num_empenho");

		projection.add(Projections.sqlProjection("case 	when {alias}.numero_empenho_siafi is null then" +
				"			case 	when afp4_.nro_lista_itens_af_siafi is not null then" +
				"				afp4_.ano_lista_itens_af_siafi||'/'||afp4_.nro_lista_itens_af_siafi " +
				"			end " +
				"		else" +
				"			substr({alias}.numero_empenho_siafi, 1,4)||'/'||substr({alias}.numero_empenho_siafi, 7,6)" +
				" end as ano_lista_itens", new String[]{"ano_lista_itens"}, new Type[] { StringType.INSTANCE }),"ano_lista_itens");

		projection.add(Projections.sqlProjection(" coalesce({alias}.frf_codigo, afp4_.frf_codigo) as frf_codigo ", new String[]{"frf_codigo"}, new Type[] { StringType.INSTANCE }),"frf_codigo");

		projection.add(Projections.sqlProjection("afn3_.pfr_lct_numero||'/'||afn3_.nro_complemento as afn_numero", new String[]{"afn_numero"}, new Type[] { StringType.INSTANCE }),"afn_numero");

		projection.add(Projections.property("afn." + ScoAutorizacaoForn.Fields.CVF_CODIGO.toString()), "convenio");
		projection.add(Projections.property("afn." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()), "modl_empenho");

		projection.add(Projections.sqlProjection("	(select  sum(case when afp2.especie = 3 then valor * -1 else afp2.valor end) as vlr_empenho" +
				"	from    agh.sco_af_empenhos afp2 " +
				"	where   afp2.afn_numero           = afn3_.numero" +
				"	and     afp2.ind_enviado                = 'S'" +
				"	and     afp2.ind_confirmado_siafi = 'S') as vlr_empenho", new String[]{"vlr_empenho"}, new Type[] { StringType.INSTANCE }),"vlr_empenho");

		projection.add(Projections.sqlProjection("rnt7_.ntp_gnp_codigo||' 0'||rnt7_.ntp_codigo as nat_despesa ", new String[]{"nat_despesa"}, new Type[] { StringType.INSTANCE }),"nat_despesa");

		projection.add(Projections.property("afn." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString()), "dt_geracao_af");
		projection.add(Projections.property("lct." + ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString()), "mlc_codigo");
		projection.add(Projections.property("lct." + ScoLicitacao.Fields.ARTIGO_LICITACAO.toString()), "artigo_licitacao");
		projection.add(Projections.property("lct." + ScoLicitacao.Fields.INCISO_ARTIGO_LICITACAO.toString()), "inciso");

		projection.add(Projections.property("mmt." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()), "dt_comp");
		projection.add(Projections.property("ttl." + FcpTitulo.Fields.NUMERO_TITULO.toString()), "nro_titulo");
		projection.add(Projections.property("ttl." + FcpTitulo.Fields.DT_VENCIMENTO.toString()), "data_vencimento");

		projection.add(Projections.sqlProjection("case 	when nrp13_.ser_matricula is not null then" +
				" 			'CP. '||nrp13_.ser_matricula||' - '||substr(pef15_.nome,1,25) " +
				"		else" +
				"			null " +
				"end as recebimento", new String[]{"recebimento"}, new Type[] { StringType.INSTANCE }),"recebimento");
		projection.add(Projections.property("nrp." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()), "dt_recebimento");

		projection.add(Projections.sqlProjection("case 	when {alias}.ser_matricula is not null then" +
				" 			'CP. '||{alias}.ser_matricula||' - '||substr(pef2x17_.nome,1,25) " +
				"		else" +
				"			null " +
				"end as confirmacao", new String[]{"confirmacao"}, new Type[] { StringType.INSTANCE }),"confirmacao");

		criteria.setProjection(projection);
		/*Fim projections*/

		/*restrictions*/

		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numNotaRec));
		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), false));
		criteria.add(Restrictions.or(Restrictions.isNull("tmv." + SceTipoMovimento.Fields.SEQ.toString()),
				Restrictions.eq("tmv." + SceTipoMovimento.Fields.SEQ.toString(), tmvNR_seq)));
		criteria.add(Restrictions.or(Restrictions.isNull("cnf." + ScoContaCorrenteFornecedor.Fields.IND_PREFERENCIAL.toString()),
				Restrictions.eq("cnf." + ScoContaCorrenteFornecedor.Fields.IND_PREFERENCIAL.toString(), DominioSimNao.S)));

		/*
		 *  Verifica se deve considerar os critérios da nota de empenho SIAFI
		 *  TODO: Remover e desconsiderar essa regra quando a estória SIAFI for implementada
		 */
		if(isConsiderarNotaEmpenho){

			Criterion restri1 = Restrictions.isNotNull("nrs."+SceNotaRecebimento.Fields.NUMERO_EMPENHO_SIAFI.toString());

			String subQuery = " {alias}.numero_empenho_siafi is null " +
			"		AND afp4_.seq IN (select 	max(afp1.seq)" +
			"				        from 	agh.sco_af_empenhos afp1," +
			"				               	agh.sco_listas_siafi_fonte_rec lfr" +
			"				        where 	lfr.dt_inicial_empenho <= {alias}.dt_geracao " +
			"						and 	coalesce(lfr.dt_final_empenho, ?) >= {alias}.dt_geracao " +
			"						and 	afp1.afn_numero = afn3_.numero" +
			"						and 	substr(afp1.numero_documento,1,7) = lfr.ano_empenho||'NE'||lfr.seq_lista" +
			"						and afp1.especie = 1)";

			Object[] values = {new Date()};
			Type[] types = {TimestampType.INSTANCE};

			criteria.add(Restrictions.or(restri1, Restrictions.sqlRestriction(subQuery, values, types)));
		}


		/*fim restrictions*/

		List<Object[]> lstObj = executeCriteria(criteria);
		for (Object[] objRetorno : lstObj) {
			notaRec = new NotaRecebimentoVO();

			if(objRetorno[0]!=null){
				notaRec.setTipoDocumento(objRetorno[0].toString());
			}

			if(objRetorno[1]!=null){
				notaRec.setNumeroDocumento(Long.parseLong(objRetorno[1].toString()));
			}

			if(objRetorno[2]!=null){
				notaRec.setDtEmissaoDocumento((Date)objRetorno[2]);
			}

			if(objRetorno[3]!=null){
				notaRec.setNumeroFornecedor(Integer.parseInt(objRetorno[3].toString()));
			}

			if(objRetorno[4]!=null){
				notaRec.setCgcFornecedor(objRetorno[4].toString());
			}

			if(objRetorno[5]!=null){
				notaRec.setRazaoSocialFornecedor(objRetorno[5].toString());
			}

			if(objRetorno[6]!=null){
				notaRec.setNumeroBanco(Integer.parseInt(objRetorno[6].toString()));
			}

			if(objRetorno[7]!=null){
				notaRec.setNumeroConta(objRetorno[7].toString());
			}

			if(objRetorno[8]!=null){				
				notaRec.setNumeroAgencia(Integer.parseInt(objRetorno[8].toString()));
			}

			if(objRetorno[9]!=null){
				notaRec.setNumeroNotaReceb(Integer.parseInt(objRetorno[9].toString()));
			}

			if(objRetorno[10]!=null){
				notaRec.setDtGeracaoNota((Date)objRetorno[10]);
			}

			if(objRetorno[11]!=null){
				notaRec.setNumeroEmpenho(objRetorno[11].toString());
			}

			if(objRetorno[12]!=null){
				notaRec.setAnoListaItens(objRetorno[12].toString());
			}

			if(objRetorno[13]!=null){
				notaRec.setFrfCodigo(Long.valueOf(objRetorno[13].toString()));
			}

			if(objRetorno[14]!=null){
				notaRec.setAfnNumero(objRetorno[14].toString());
			}

			if(objRetorno[15]!=null){
				notaRec.setCodigoConvenio(((FsoConveniosFinanceiro)objRetorno[15]).getCodigo());
			}

			if(objRetorno[16]!=null){
				notaRec.setModalidadeEmpenho(((DominioModalidadeEmpenho)objRetorno[16]).getCodigo());
			}

			if(objRetorno[17]!=null){
				notaRec.setValorEmpenho(Double.parseDouble(objRetorno[17].toString()));
			}

			if(objRetorno[18]!=null){
				notaRec.setNaturezaDespesa(objRetorno[18].toString());
			}

			if(objRetorno[19]!=null){
				notaRec.setDtGeracaoAf((Date)objRetorno[19]);
			}

			if(objRetorno[20]!=null){
				notaRec.setCodigoModalidadeLicitacao(objRetorno[20].toString());
			}

			if(objRetorno[21]!=null){
				notaRec.setArtigoLicitacao(Integer.parseInt(objRetorno[21].toString()));
			}

			if(objRetorno[22]!=null){
				notaRec.setIncisoArtigoLicitacao(objRetorno[22].toString());
			}

			if(objRetorno[23]!=null){
				notaRec.setDtCompetencia((Date)objRetorno[23]);
			}

			if(objRetorno[24]!=null){
				notaRec.setNumeroTitulo((Integer)objRetorno[24]);
			}

			if(objRetorno[25]!=null){
				notaRec.setDtVencimento((Date)objRetorno[25]);
			}

			if(objRetorno[26]!=null){
				notaRec.setRecebimento(objRetorno[26].toString());
			}

			if(objRetorno[27]!=null){
				notaRec.setDtRecebimento((Date)objRetorno[27]);
			}

			if(objRetorno[28]!=null){
				notaRec.setConfirmacao(objRetorno[28].toString());
			}
		}
		return notaRec;
	}

	/**
	 * Nota de Recebimento com Geração de AF Automática
	 * @param numNotaRec
	 * @param tmvNR_seq
	 * @return
	 * @throws BaseException
	 */
	public NotaRecebimentoVO pesquisaDadosNotaRecebimentoSemAf(Integer numNotaRec, Short tmvNR_seq) throws BaseException {
		NotaRecebimentoVO notaRec = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "nrs");
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.MOVIMENTO_MATERIAL.toString(), "mmt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("nrs."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "afn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("afn."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "pfn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pfn."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "frn", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("mmt."+SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.LEFT_OUTER_JOIN);

		/*Projections*/
		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.distinct(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString())), "dfe_type");
		projection.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), "numero_nf");
		projection.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()), "dt_emissao");

		projection.add(Projections.property("frn." + ScoFornecedor.Fields.NUMERO.toString()), "cod_fornec");
		projection.add(Projections.property("frn." + ScoFornecedor.Fields.CGC.toString()), "cgc");
		projection.add(Projections.property("frn." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "razao_social");

		projection.add(Projections.property("nrs." + SceNotaRecebimento.Fields.NUMERO_NR.toString()), "nr");
		projection.add(Projections.property("nrs." + SceNotaRecebimento.Fields.DATA_GERACAO.toString()), "dt_geracao");

		projection.add(Projections.property("mmt." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()), "dt_comp");

		criteria.setProjection(projection);
		/*Fim projections*/

		/*restrictions*/

		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numNotaRec));
		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), false));
		criteria.add(Restrictions.eq("tmv."+SceTipoMovimento.Fields.SEQ.toString(), tmvNR_seq));	

		/*fim restrictions*/

		List<Object[]> lstObj = executeCriteria(criteria);
		for (Object[] objRetorno : lstObj) {
			notaRec = new NotaRecebimentoVO();

			if(objRetorno[0]!=null){
				notaRec.setTipoDocumento(objRetorno[0].toString());
			}

			if(objRetorno[1]!=null){
				notaRec.setNumeroDocumento(Long.parseLong(objRetorno[1].toString()));
			}

			if(objRetorno[2]!=null){
				notaRec.setDtEmissaoDocumento((Date)objRetorno[2]);
			}

			if(objRetorno[3]!=null){
				notaRec.setNumeroFornecedor(Integer.parseInt(objRetorno[3].toString()));
			}

			if(objRetorno[4]!=null){
				notaRec.setCgcFornecedor(objRetorno[4].toString());
			}

			if(objRetorno[5]!=null){
				notaRec.setRazaoSocialFornecedor(objRetorno[5].toString());
			}

			if(objRetorno[6]!=null){
				notaRec.setNumeroNotaReceb(Integer.parseInt(objRetorno[6].toString()));
			}

			if(objRetorno[7]!=null){
				notaRec.setDtGeracaoNota((Date)objRetorno[7]);
			}


			if(objRetorno[8]!=null){
				notaRec.setDtCompetencia((Date)objRetorno[8]);
			}

		}

		return notaRec;

	}

	/**
	 * Obtem a criteria necessária para a consulta da estória
	 * @param material
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public DetachedCriteria getCriteriaEstonarNotaRecebimento(SceNotaRecebimento notaRecebimento, Boolean ordena){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NRE");
		criteria.createAlias("NRE." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AUF");
		criteria.createAlias("NRE." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("AUF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PRF");
		criteria.createAlias("PRF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FOR");

		if (notaRecebimento.getSeq() != null) {
			criteria.add(Restrictions.eq("NRE." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), notaRecebimento.getSeq()));
		}

		if (notaRecebimento.getAutorizacaoFornecimento() != null && notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor() != null && 
				notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero() != null) {
			final Integer lctNumero = notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero();
			criteria.add(Restrictions.eq("PRF." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), lctNumero));

		}

		if (notaRecebimento.getDocumentoFiscalEntrada() != null && notaRecebimento.getDocumentoFiscalEntrada().getNumero() != null) {
			criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), notaRecebimento.getDocumentoFiscalEntrada().getNumero()));
		}

		if(notaRecebimento.getDtGeracao() != null){
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
			final String sqlRestrictionToChar = "TO_CHAR(this_.DT_GERACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar,formatador.format(notaRecebimento.getDtGeracao()),StringType.INSTANCE));
		}

		if (notaRecebimento.getDebitoNotaRecebimento() != null) {
			criteria.add(Restrictions.eq("NRE." + SceNotaRecebimento.Fields.IND_DEBITO.toString(), notaRecebimento.getDebitoNotaRecebimento()));
		}

		if (notaRecebimento.getAutorizacaoFornecimento() != null && notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor() != null &&
				notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor() != null) {
			criteria.add(Restrictions.eq("PRF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), notaRecebimento.getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor()));
		}

		if (notaRecebimento.getAutorizacaoFornecimento() != null && notaRecebimento.getAutorizacaoFornecimento().getSituacao() != null) {
			criteria.add(Restrictions.eq("AUF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), notaRecebimento.getAutorizacaoFornecimento().getSituacao()));
		}

		if (ordena) {

			criteria.addOrder(Order.desc(SceNotaRecebimento.Fields.DATA_GERACAO.toString()));

		}

		return criteria;

	}

	public Long pesquisarEstornarNotaRecebimentoCount(SceNotaRecebimento notaRecebimento) {
		DetachedCriteria criteria = this.getCriteriaEstonarNotaRecebimento(notaRecebimento, Boolean.FALSE);
		return this.executeCriteriaCount(criteria);
	}

	public List<SceNotaRecebimento> pesquisarEstornarNotaRecebimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SceNotaRecebimento notaRecebimento) {
		DetachedCriteria criteria = this.getCriteriaEstonarNotaRecebimento(notaRecebimento, Boolean.TRUE);
		return this.executeCriteria(criteria, firstResult, maxResult, null, asc);
	}


	public List<SceNotaRecebimento> pesquisarNotaRecebimentoPorDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(SceNotaRecebimento.class);
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), documentoFiscalEntrada));
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}

	public List<SceNotaRecebimento> pesquisarNotaRecebimentoPorNotaRecebimentoProvisorio(final Integer nrpSeq, final Boolean indEstornado) {
		return executeCriteria(obterCriteriaNotaRecebimentoPorNotaRecebimentoProvisorio(nrpSeq, indEstornado));
	}

	private DetachedCriteria obterCriteriaNotaRecebimentoPorNotaRecebimentoProvisorio(final Integer nrpSeq, final Boolean indEstornado) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		if (nrpSeq != null) {
			criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.NRP_SEQ.toString(), nrpSeq));
		}
		if (indEstornado != null) {
			criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.IND_ESTORNO.toString(), indEstornado));
		}
		return criteria;
	}

	public SceNotaRecebimento obterNotaRecebimentoPorNotaRecebimentoProvisorio(Integer nrpSeq) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(SceNotaRecebimento.class);
		criteria.createAlias(SceNotaRecebimento.Fields.NOTA_RECEB_PROVISORIO.toString(), SceNotaRecebimento.Fields.NOTA_RECEB_PROVISORIO.toString());
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.NOTA_RECEB_PROVISORIO.toString()+"."+SceNotaRecebProvisorio.Fields.NRP_SEQ, nrpSeq));
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		return (SceNotaRecebimento) executeCriteriaUniqueResult(criteria);
	}

	public SceNotaRecebimento obterSceNotaRecebimentoFULL(Integer seq) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(SceNotaRecebimento.class);
		
		criteria.createAlias(SceNotaRecebimento.Fields.NOTA_RECEB_PROVISORIO.toString(),"NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.SERVIDOR_GERACAO.toString(),"SEG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.SERVIDOR_DEBITADO.toString(),"SED", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.SERVIDOR_ESTORNO.toString(),"SEE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.SERVIDOR_LIB.toString(),"SEL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.TIPO_MOVIMENTO.toString(),"TM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(),"DFE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(),"AF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "AF_PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF_PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "AF_PF_FNC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.NUMERO_NR.toString(), seq));
		
		return (SceNotaRecebimento) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa a data de geração das notas de recebimento através do seq do documento fiscal de entrada
	 * @param documentoFiscalEntrada
	 * @return
	 */
	public List<Date> pesquisarDataGeracaoNotaRecebimentoPorDocumentoFiscalEntrada(Integer seqDocumentoFiscalEntradada) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class);
		criteria.createAlias(SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);

		criteria.setProjection(Projections.property(SceNotaRecebimento.Fields.DATA_GERACAO.toString()));

		criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString(), seqDocumentoFiscalEntradada));
		criteria.add(Restrictions.eq(SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.TRUE));

		return  executeCriteria(criteria);
	}
	
	public Boolean verificarAutorizacaoFornecimentoSaldoNotaRecebimento(Integer afnNumero, Short sequenciaAlteracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NR");
		criteria.createAlias(SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		criteria.add(Restrictions.or(Restrictions.and(
										Restrictions.isNull("AF."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), 
										Restrictions.gtProperty("NR."+SceNotaRecebimento.Fields.DATA_GERACAO.toString(), 
																"AF."+ScoAutorizacaoForn.Fields.DT_GERACAO.toString())),
									Restrictions.and(
												Restrictions.isNotNull("AF."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), 
												Restrictions.gtProperty("NR."+SceNotaRecebimento.Fields.DATA_GERACAO.toString(), 
																		"AF."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()))
				));
		
		
		return executeCriteriaCount(criteria) > 0;
	}

	/**
	 * Conta a quantidade de itens de nota de recebimento por codigo de material
	 * @param numeroNr
	 * @param matCodigo
	 * @return Integer
	 */
	public Long contarQtdeEntradaPorNr(Integer numeroNr, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NR");
		criteria.createAlias("NR."+SceNotaRecebimento.Fields.ITEM_NOTA_RECEBIMENTO.toString(), "INR", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("NR."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), matCodigo));
		
		return executeCriteriaCount(criteria);
	}	

	public Date buscaDataEncerramento(Integer pac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "SNR");
		criteria.createAlias("SNR."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.setProjection(Projections.max("SNR."+SceNotaRecebimento.Fields.DATA_GERACAO.toString()));
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), pac));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
}