package br.gov.mec.aghu.compras.contaspagar.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.mapping.Collection;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroPesquisaPosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.MovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioMovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloNrVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpDfTitulo;
import br.gov.mec.aghu.model.FcpPagamento;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.model.FcpValorTributos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimentoHist;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe de acesso a dados pertencente ao modelo de Título.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public class FcpTituloDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FcpTitulo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5997275452375665254L;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	/**
	 * Consulta Títulos NR
	 * @return
	 */
	public DetachedCriteria pesquisarTituloNrCriteria(TituloNrVO tituloNrVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");

		criteria.createAlias("TTL." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.add(Restrictions.isNull(FcpTitulo.Fields.ECG_SEQ.toString()));

		ProjectionList projectionList =Projections.projectionList();
		projectionList.add(Projections.property(FcpTitulo.Fields.NUMERO_TITULO.toString()), TituloNrVO.Fields.TITULO_SEQ.toString());		
		projectionList.add(Projections.property(FcpTitulo.Fields.NRO_PARCELA.toString()), TituloNrVO.Fields.NUMERO_PARCELA.toString());
		projectionList.add(Projections.property(FcpTitulo.Fields.DT_COMPETENCIA.toString()), TituloNrVO.Fields.DATA_COMPETENCIA.toString());
		projectionList.add(Projections.property(FcpTitulo.Fields.DT_GERACAO.toString()), TituloNrVO.Fields.DATA_GERACAO.toString());		
		projectionList.add(Projections.property(FcpTitulo.Fields.NRS_SEQ.toString()), TituloNrVO.Fields.NOTA_RECEBIMENTO_NUMERO.toString());
		projectionList.add(Projections.property(FcpTitulo.Fields.DT_VENCIMENTO.toString()), TituloNrVO.Fields.TITULO_DATA_VENCIMENTO.toString());		
		projectionList.add(Projections.property(FcpTitulo.Fields.VALOR.toString()), TituloNrVO.Fields.TITULO_VALOR.toString());		
		projectionList.add(Projections.property(FcpTitulo.Fields.IND_SITUACAO.toString()), TituloNrVO.Fields.TITULO_IND_SITUACAO.toString());
		projectionList.add(Projections.property(FcpTitulo.Fields.IND_ESTORNO.toString()), TituloNrVO.Fields.PAGAMENTO_IND_ESTORNO.toString());		
		projectionList.add(Projections.property(FcpTitulo.Fields.MOTIVO_ESTORNO.toString()), TituloNrVO.Fields.MOTIVO_ESTORNO.toString());	
		projectionList.add(Projections.property(FcpTitulo.Fields.DT_ESTORNO.toString()), TituloNrVO.Fields.DATA_ESTORNO.toString());
		projectionList.add(Projections.property("NRS." + SceNotaRecebimento.Fields.DATA_GERACAO.toString()), TituloNrVO.Fields.NOTA_RECEBIMENTO_DATA_GERACAO.toString());		
		projectionList.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), TituloNrVO.Fields.LCT_NUMERO.toString());
		projectionList.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), TituloNrVO.Fields.AUTORIZ_FORN_NUM_COMPL.toString());
		projectionList.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), TituloNrVO.Fields.DOCUMENTO_FISCAL_ENTRADA_NUMERO.toString());
		projectionList.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.SERIE.toString()), TituloNrVO.Fields.DOCUMENTO_FISCAL_SERIE.toString());

		if(tituloNrVO.getTituloSeq() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.NUMERO_TITULO.toString(), tituloNrVO.getTituloSeq()));
		}
		if(tituloNrVO.getNumeroParcela() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.NRO_PARCELA.toString(), tituloNrVO.getNumeroParcela()));
		}
		if(tituloNrVO.getDataCompetencia() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.DT_COMPETENCIA.toString(), tituloNrVO.getDataCompetencia()));
		}
		if(tituloNrVO.getDataGeracao() != null) {
			criteria.add(Restrictions.between(FcpTitulo.Fields.DT_GERACAO.toString(), DateUtil.truncaData(tituloNrVO.getDataGeracao()), DateUtil.truncaDataFim(tituloNrVO.getDataGeracao())));
		}
		if(tituloNrVO.getNotaRecebimentoNumero() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.NRS_SEQ.toString(), tituloNrVO.getNotaRecebimentoNumero()));
		}
		if(tituloNrVO.getPagamentoIndEstorno() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.IND_ESTORNO.toString(), tituloNrVO.getPagamentoIndEstorno()));
		}
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(TituloNrVO.class));
		return criteria;
	}

	/**
	 * Consulta Títulos NR
	 * @return
	 */
	public List<TituloNrVO> pesquisarTituloNr(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, TituloNrVO tituloNrVO) {
		DetachedCriteria criteria = pesquisarTituloNrCriteria(tituloNrVO);
		List<TituloNrVO> results = executeCriteria(criteria, firstResult, maxResult, orderProperty,asc);
		return results;
	}
	
	/**
	 * Consulta Títulos NR count
	 * @return
	 */
	public Long pesquisarTituloNrCount(TituloNrVO tituloNrVO) {
		DetachedCriteria criteria = pesquisarTituloNrCriteria(tituloNrVO);
		return executeCriteriaCount(criteria);
	}

	public List<FcpTitulo> pesquisarTitulosPagosPorNotaRecebimento(
			SceNotaRecebimento notaRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class,
				"tit");
		criteria.add(Restrictions.eq(
				"tit." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(),
				notaRecebimento));
		criteria.add(Restrictions.eq(
				"tit." + FcpTitulo.Fields.IND_SITUACAO.toString(),
				DominioSituacaoTitulo.PG));
		criteria.add(Restrictions.eq(
				"tit." + FcpTitulo.Fields.IND_ESTORNO.toString(), Boolean.FALSE));

		return executeCriteria(criteria);
	}


	

	public Long pesquisarTitulosCount(FiltroConsultaTitulosVO filtros){
		
		PesquisaTitulosQueryBuilder builder =  new PesquisaTitulosQueryBuilder(filtros, isOracle(), Boolean.TRUE, null, null);
		Query query = this.createNativeQuery(builder.builder());

		builder.querySetParameter(query);
		
		List<Object> results = query.getResultList();
		
		if (results != null) {
			return ((Number) results.get(0)).longValue();
		}
		
		return 0L;
	}
	/**
	 * Método de consulta de títulos
	 * 
	 * @param parametros
	 * @return {@link Collection} com os registros da pesquisa.
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength","PMD.NcssMethodCount","PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
	public List<TituloVO> pesquisarTitulos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroConsultaTitulosVO filtros) {

		List<TituloVO> titulos = new ArrayList<TituloVO>();


		PesquisaTitulosQueryBuilder builder =  new PesquisaTitulosQueryBuilder(filtros, isOracle(), Boolean.FALSE, orderProperty, asc);
		
		Query query = this.createNativeQuery(builder.builder());


		builder.querySetParameter(query);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		// Retorno BD
		List<Object[]> results = query.getResultList();

		for (Object[] objectResult : results) {
			TituloVO itemResult = new TituloVO();

			if (objectResult[0] != null) {
				itemResult.setNotaRecebimentoNumero(((Number) objectResult[0]).intValue());
			}

			if (objectResult[1] != null) {
				itemResult.setNotaRecebimentoDataGeracao((Date) objectResult[1]);
			}

			if (objectResult[2] != null) {
				itemResult.setFonteRecursoFinancCodigo(((Number) objectResult[2]).longValue());
			}

			if (objectResult[3] != null) {
				itemResult.setFonteRecursoFinanDescricao((String) objectResult[3]);
			}				

			if (objectResult[4] != null) {
				itemResult.setLiquidacaoSiafiVinculacaoPagamento(((Number) objectResult[4]).intValue());
			}
			
			if (objectResult[5] != null) {
				itemResult.setTituloSeq(((Number) objectResult[5]).intValue());
			}

			if (objectResult[6] != null) {
				itemResult.setDocumentoFiscalEntradaNumero(((Number) objectResult[6]).longValue());
			}

			if (objectResult[7] != null) {
				itemResult.setDocumentoFiscalSerie((String) objectResult[7]);
			}
			
			if (objectResult[8] != null) {
				itemResult.setVerbaGestaoSeq(((Number) objectResult[8]).intValue());
			}

			if (objectResult[9] != null) {
				itemResult.setVerbaGestaoDescricao((String) objectResult[9]);
			}

			if (objectResult[10] != null) {
				itemResult.setTituloDataVencimento((Date) objectResult[10]);
			}

			if (objectResult[11] != null) {
				itemResult.setLiquidacaoSiafiNumeroEmpenho((String) objectResult[11]);
			}

			if (objectResult[12] != null) {
				itemResult.setTituloDataProgramacaoPagamento((Date) objectResult[12]);
			}

			if (objectResult[13] != null) {
				itemResult.setTituloIndSituacao(DominioSituacaoTitulo.getInstance((String) objectResult[13]));
			}

			if (objectResult[14] != null) {
				itemResult.setFornecedorNumero(((Number) objectResult[14]).intValue());
			}

			if (objectResult[15] != null) {
				itemResult.setFornecedorCgc(((Number) objectResult[15]).longValue());
			}

			if (objectResult[16] != null) {
				itemResult.setFornecedorCpf(((Number) objectResult[16]).longValue());
			}

			if (objectResult[17] != null) {
				itemResult.setFornecedorRazaoSocial((String) objectResult[17]);
				itemResult.setFornecedor(this.scoFornecedorDAO.obterPorChavePrimaria(((Number) objectResult[14]).intValue()));
			}

			if (objectResult[18] != null) {
				itemResult.setFornecedorDataValidadeFgts((Date) objectResult[18]);
			}

			if (objectResult[19] != null) {
				itemResult.setFornecedorDataValidadeInss((Date) objectResult[19]);
			}

			if (objectResult[20] != null) {
				itemResult.setFornecedorDataValidadeRecFed((Date) objectResult[20]);
			}

			if (objectResult[21] != null) {
				itemResult.setFornecedorTelefone((String) objectResult[21]);
			}

			if (objectResult[22] != null) {
				itemResult.setAutorizacaoFornNaturezaDespesaGndCodigo(((Number) objectResult[22]).intValue());
			}

			if (objectResult[23] != null) {
				itemResult.setAutorizacaoFornNaturezaDespesaCodigo(((Number) objectResult[23]).byteValue());
			}

			if (objectResult[24] != null) {
				itemResult.setNaturezaDespesaDescricao((String) objectResult[24]);
			}

			if (objectResult[25] != null) {
				itemResult.setAutorizacaoFornNaturezaPropostaFornecedorLctNumero(((Number) objectResult[25]).intValue());
			}

			if (objectResult[26] != null) {
				itemResult.setAutorizacaoFornNumeroComplemento(((Number) objectResult[26]).shortValue());
			}

			if (objectResult[27] != null) {
				itemResult.setTipoMaterial((String) objectResult[27]);
			}

			if (objectResult[28] != null) {
				itemResult.setPagamentoNumeroDocumento(((Number) objectResult[28]).intValue());
			}

			if (objectResult[29] != null) {
				itemResult.setTipoDocPagamentoDescricao((String) objectResult[29]);
			}

			if (objectResult[30] != null) {
				itemResult
						.setPagamentoIndEstorno((String) objectResult[30]);
			}

			if (objectResult[31] != null) {
				itemResult.setPagamentoDataPagamento((Date) objectResult[31]);
			}

			if (objectResult[32] != null) {
				itemResult.setPagamentoObservacao((String) objectResult[32]);
			}

			if (objectResult[33] != null) {
				itemResult.setRetencaoAliquotaValorTributos(((Number) objectResult[33]).doubleValue());
			}

			if (objectResult[34] != null) {
				itemResult.setTituloValor(((Number) objectResult[34]).doubleValue());
			}

			if (objectResult[35] != null) {
				itemResult.setRetencaoAliquotaValorDesconto(((Number) objectResult[35]).doubleValue());
			}

			if (objectResult[36] != null) {
				itemResult.setRetencaoAliquotaValorMulta(((Number) objectResult[36]).doubleValue());
			}

			if (objectResult[37] != null) {
				itemResult.setPagamentoValorDesconto(((Number) objectResult[37]).doubleValue());
			}

			if (objectResult[38] != null) {
				itemResult.setPagamentoValorAcrescimo(((Number) objectResult[38]).doubleValue());
			}

			if (objectResult[39] != null) {
				itemResult.setContaCorrenteFornecedorAgbBcoCodigo(((Number) objectResult[39]).intValue());
			}

			if (objectResult[40] != null) {
				itemResult.setBancoNomeBanco((String) objectResult[40]);
			}

			if (objectResult[41] != null) {
				itemResult.setContaCorrenteFornecedorAgbCodigo(((Number) objectResult[41]).intValue());
			}

			if (objectResult[42] != null) {
				itemResult.setContaCorrenteFornecedorContaCorrente((String) objectResult[42]);
			}

			if (objectResult[43] != null) {
				itemResult.setFormaPagamentoDescricao((String) objectResult[43]);
			}

			if (objectResult[44] != null) {
				itemResult.setTemINSS((String) objectResult[44]);
			}

			if (objectResult[45] != null) {
				itemResult.setTemMulta((String) objectResult[45]);
			}
			titulos.add(itemResult);
		}
		
		return titulos;

	}


	public Long pesquisarPagamentosProgramadosCount(Date dataPagamento){
		PagamentoProgramadoQueryBuilder builder =  new PagamentoProgramadoQueryBuilder(dataPagamento, Boolean.TRUE, null, null);	
		Query query = createNativeQuery(builder.builder());


		List<Object> results = query.getResultList();
		
		if (results != null) {
			return ((Number) results.get(0)).longValue();
		}
		
		return 0L;
	}

	
	
	/**
	 * Método que busca por {@link FcpTitulo} que possuem a data de pagamento
	 * programada igual a data enviada como parâmetro.
	 * 
	 * @param dataPagamento
	 *            data a ser usada para filtrar os {@link FcpTitulo}
	 * @return Uma {@link List} de {@link TituloProgramadoVO} com as
	 *         informações.
	 */
	public List<TituloProgramadoVO> pesquisarPagamentosProgramados(
			Date dataPagamento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<TituloProgramadoVO> titulosProgramados = new ArrayList<TituloProgramadoVO>();
		
		PagamentoProgramadoQueryBuilder builder =  new PagamentoProgramadoQueryBuilder(dataPagamento, Boolean.FALSE, orderProperty, asc);	
		Query query = createNativeQuery(builder.builder());
		
		query.setFirstResult(verificaFirstResult(firstResult));
	
		query.setMaxResults(verificaMaxResult(maxResult));

		List<Object[]> results = query.getResultList();

		for (Object[] objectResult : results) {
			TituloProgramadoVO titulo = new TituloProgramadoVO();
			
			if (objectResult[0] != null) {
				titulo.setFornecedorRazaoSocial((String) objectResult[0].toString());
				titulo.setFornecedor(this.scoFornecedorDAO.obterPorChavePrimaria(((Number) objectResult[1]).intValue()));
			}
			if (objectResult[2] != null) {
				titulo.setNotaRecebimentoNumero(((Number) objectResult[2]).intValue());
			}
			if (objectResult[3] != null) {
				titulo.setFonteRecursoFinancCodigo(((Number) objectResult[3]).longValue());
			}
			if (objectResult[4] != null) {
				titulo.setFonteRecursoFinanDescricao((String) objectResult[4]);
			}
			if (objectResult[5] != null) {
				titulo.setLiquidacaoSiafiVinculacaoPagamento(((Number) objectResult[5]).intValue());
			}
			if (objectResult[6] != null) {
				titulo.setTituloSeq(((Number) objectResult[6]).intValue());
			}
			if (objectResult[7] != null) {
				titulo.setTituloDataVencimento((Date) objectResult[7]);
			}
			if (objectResult[8] != null) {
				titulo.setTituloIndSituacao(DominioSituacaoTitulo
						.getInstance((String) objectResult[8]));
				titulo.setDescricaoSituacao(titulo.getTituloIndSituacao().getDescricao());
			}
			if (objectResult[9] != null) {
				titulo.setTituloValor(((Number) objectResult[9]).doubleValue());
			}
			titulosProgramados.add(titulo);
		}
		return titulosProgramados;
	}
	
	/**
	 * Método criado devido ao NPath do PMD
	 * @author ndeitch
	 */
	private Integer verificaFirstResult(Integer firstResult) {
		
		if(firstResult != null){
			return firstResult;
		} else {
			return 0;
		}
	}
	
	/**
	 * Método criado devido ao NPath do PMD
	 * @author ndeitch
	 */
	private Integer verificaMaxResult(Integer maxResult) {
		
		if(maxResult != null){
			return maxResult;
		} else {
			return 100;
		}
	}

	/**
	 * Método de pesquisa de {@link FcpTitulo} pendente passando as datas do
	 * {@link FcpTitulo} e do {@link SceDocumentoFiscalEntrada} além do
	 * {@link ScoFornecedor}
	 * 
	 * @param dtInicialVencimentoTitulo
	 *            Data inicial do período de vencimento do título.
	 * @param dtFinalVencimentoTitulo
	 *            Data final do período de vencimento do título.
	 * @param dtInicialEmissaoDocumentoFiscal
	 *            Data inicial do período de emissão do documento fiscal.
	 * @param dtFinalEmissaoDocumentoFiscal
	 *            Data final do período de emissão do documento fiscal.
	 * @param fornecedor
	 *            Fornecedor que possui o título a ser pago.
	 * @return Coleção contendo o resultado da pesquisa.
	 */
	public List<TituloPendenteVO> pesquisaDividaSemNF(Date tituloDataInicial,
			Date tituloDataFinal, Date emissaoDataInicial,
			Date emissaoDataFinal, ScoFornecedor fornecedor) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class,	"TTL");

		StringBuilder projectionNumeroSerie = new StringBuilder(50);
		projectionNumeroSerie
				.append("dfe2_.NUMERO||'/'||dfe2_.SERIE numeroSerie");

		StringBuilder projectionLicitacao = new StringBuilder(60);
		projectionLicitacao
				.append("afs4_.PFR_LCT_NUMERO||'/'||afs4_.NRO_COMPLEMENTO licitacao");

		// ordenacao por documento Fiscal
		StringBuilder orderByDocFiscar = new StringBuilder(60);
		orderByDocFiscar.append("dfe2_.NUMERO||'/'||dfe2_.SERIE");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("FRN."
						+ ScoFornecedor.Fields.CGC.toString()), "cgcFornecedor")
				.add(Projections.property("FRN."
						+ ScoFornecedor.Fields.CPF.toString()), "cpfFornecedor")
				.add(Projections.property("DFE."
						+ SceDocumentoFiscalEntrada.Fields.FORNECEDOR_NUMERO
								.toString()), "numeroFornecedor")
				.add(Projections.property("FRN."
						+ ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
						"razaoSocialFornecedor")
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.NUMERO_TITULO.toString()), "titulo")
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.NRO_PARCELA.toString()),
						"nroParcela")
				.add(Projections.sqlProjection(
						projectionNumeroSerie.toString(),
						new String[] { "numeroSerie" },
						new Type[] { StringType.INSTANCE }))
				.add(Projections.property("DFE."
						+ SceDocumentoFiscalEntrada.Fields.DT_EMISSAO
								.toString()), "dtEmissao")
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.DT_VENCIMENTO.toString()),
						"dtVencimento")
				.add(Projections.sqlProjection(projectionLicitacao.toString(),
						new String[] { "licitacao" },
						new Type[] { StringType.INSTANCE }))
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.VALOR.toString()), "valor")
				.add(Projections.property("NRS."
						+ SceNotaRecebimento.Fields.AFN_NUMERO.toString()),
						"numeroNF")
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.IND_DOCUMENTACAO.toString()),
						"indDocumentacao")
				.add(Projections.property("TTL."
						+ FcpTitulo.Fields.IND_SITUACAO.toString()),
						"indSituacao"));

		criteria.createAlias(
				"TTL." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias(
				"NRS."
						+ SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA
								.toString(), "DFE");
		criteria.createAlias("DFE."
				+ SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("NRS."
				+ SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFS");

		if (tituloDataInicial != null) {
			criteria.add(Restrictions.ge("TTL."
					+ FcpTitulo.Fields.DT_VENCIMENTO.toString(),
					tituloDataInicial));
		}
		if (tituloDataFinal != null) {
			criteria.add(Restrictions.le("TTL."
					+ FcpTitulo.Fields.DT_VENCIMENTO.toString(),
					tituloDataFinal));
		}
		if (emissaoDataInicial != null) {
			criteria.add(Restrictions.ge("DFE."
					+ SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(),
					emissaoDataInicial));
		}
		if (emissaoDataFinal != null) {
			criteria.add(Restrictions.le("DFE."
					+ SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(),
					emissaoDataFinal));
		}
		if (fornecedor != null && fornecedor.getNumeroFornecedor() != null) {
			criteria.add(Restrictions.eq(
					"FRN." + ScoFornecedor.Fields.NUMERO.toString(),
					fornecedor.getNumeroFornecedor()));
		}

		criteria.add(Restrictions.in(
				"TTL." + FcpTitulo.Fields.IND_SITUACAO.toString(),
				new DominioSituacaoTitulo[] { DominioSituacaoTitulo.APG,
						DominioSituacaoTitulo.BLQ }));

		criteria.add(Restrictions.eq("NRS."
				+ SceNotaRecebimento.Fields.IND_ESTORNO,
				DominioSimNao.N.isSim()));

		criteria.addOrder(
				Order.asc("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString()))
				.addOrder(
						Order.asc("FRN." + ScoFornecedor.Fields.CGC.toString()))
				.addOrder(
						Order.asc("FRN." + ScoFornecedor.Fields.CPF.toString()))
				.addOrder(
						Order.asc("DFE."
								+ SceDocumentoFiscalEntrada.Fields.FORNECEDOR_NUMERO
										.toString()))
				.addOrder(
						Order.asc("FRN."
								+ ScoFornecedor.Fields.RAZAO_SOCIAL.toString()))
				.addOrder(OrderBySql.sql(orderByDocFiscar.toString()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(TituloPendenteVO.class));
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria obterCriteriaMovimentacaoFornecedor(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf){
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "ttl");
		criteria.createAlias("ttl." + FcpTitulo.Fields.PAGAMENTO.toString(), "pag", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ttl." + FcpTitulo.Fields.NOTA_RECEBIMENTO, "nrs");
		criteria.createAlias("nrs." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe");
		criteria.createAlias("nrs." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "afs");
		criteria.createAlias("dfe." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "frn");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ttl." + FcpTitulo.Fields.NUMERO_TITULO.toString()), MovimentacaoFornecedorVO.Fields.NUMERO_TITULO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.NRO_PARCELA.toString()), MovimentacaoFornecedorVO.Fields.NRO_PARCELA.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.IND_SITUACAO.toString()), MovimentacaoFornecedorVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.NRS_SEQ.toString()), MovimentacaoFornecedorVO.Fields.NRS_SEQ.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.NUMERO.toString()), MovimentacaoFornecedorVO.Fields.NRO_CONTRATO.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), MovimentacaoFornecedorVO.Fields.NRO_COMPLEMENTO.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString()), MovimentacaoFornecedorVO.Fields.NRO_CONTRATO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.DT_GERACAO.toString()), MovimentacaoFornecedorVO.Fields.DATA_GERACAO.toString())
				//.add(Projections.property("ttl." + FcpTitulo.Fields.SER_MATRICULA.toString()), MovimentacaoFornecedorVO.Fields.MATRICULA.toString())
				//.add(Projections.property("ttl." + FcpTitulo.Fields.SER_VIN_CODIGO.toString()), MovimentacaoFornecedorVO.Fields.VIN_CODIGO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.DT_VENCIMENTO.toString()), MovimentacaoFornecedorVO.Fields.DATA_VENCIMENTO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.VALOR.toString()), MovimentacaoFornecedorVO.Fields.VALOR.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.NRO_EMPENHO.toString()), MovimentacaoFornecedorVO.Fields.NRO_EMPENHO.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.VALOR_EMPENHO.toString()), MovimentacaoFornecedorVO.Fields.VALOR_EMPENHO.toString())
				.add(Projections.property("frn." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), MovimentacaoFornecedorVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property("frn." + ScoFornecedor.Fields.CGC.toString()), MovimentacaoFornecedorVO.Fields.CGC.toString())
				.add(Projections.property("frn." + ScoFornecedor.Fields.CPF.toString()), MovimentacaoFornecedorVO.Fields.CPF.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.IND_ESTORNO.toString()), MovimentacaoFornecedorVO.Fields.IND_ESTORNO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.MOTIVO_ESTORNO.toString()), MovimentacaoFornecedorVO.Fields.MOTIVO_ESTORNO.toString())
				.add(Projections.property("ttl." + FcpTitulo.Fields.DT_ESTORNO.toString()), MovimentacaoFornecedorVO.Fields.DATA_ESTORNO.toString())
				//.add(Projections.property("afs." + FcpTitulo.Fields.SER_MATRICULA_ESTORNADO.toString()), MovimentacaoFornecedorVO.Fields.MATRICULA_ESTORNADO.toString())
				//.add(Projections.property("afs." + FcpTitulo.Fields.SER_VIN_CODIGO_ESTORNADO.toString()), MovimentacaoFornecedorVO.Fields.SER_VIN_CODIGO_ESTORNADO.toString())
				.add(Projections.property("pag." + FcpPagamento.Fields.NRO_DOCUMENTO.toString()), MovimentacaoFornecedorVO.Fields.NRO_DOCUMENTO.toString())
				.add(Projections.property("pag." + FcpPagamento.Fields.VALOR.toString()), MovimentacaoFornecedorVO.Fields.VALOR_PAGAMENTO.toString())
				.add(Projections.property("pag." + FcpPagamento.Fields.DT_PAGAMENTO.toString()), MovimentacaoFornecedorVO.Fields.DATA_PAGAMENTO.toString())
				.add(Projections.property("pag." + FcpPagamento.Fields.VLR_DESCONTO.toString()), MovimentacaoFornecedorVO.Fields.VALOR_DESCONTO.toString())
				.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), MovimentacaoFornecedorVO.Fields.NUMERO_DOCUMENTO_FISCAL.toString())
				.add(Projections.property("dfe." + SceDocumentoFiscalEntrada.Fields.SERIE.toString()), MovimentacaoFornecedorVO.Fields.SERIE.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), MovimentacaoFornecedorVO.Fields.PFRLCTNUMERO.toString())
				.add(Projections.property("afs." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), MovimentacaoFornecedorVO.Fields.NRO_AF.toString()));
		
		if (frnNumero != null){
			criteria.add(Restrictions.eq("dfe." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR_NUMERO.toString(), frnNumero));
		}
		
		if (situacao != null){
			criteria.add(Restrictions.eq("ttl." + FcpTitulo.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if (notaRecebimento != null) {
			criteria.add(Restrictions.eq("ttl." + FcpTitulo.Fields.NRS_SEQ.toString(), notaRecebimento));
		}
		
		if (serie != null && !serie.isEmpty()) {
			criteria.add(Restrictions.eq("dfe." + SceDocumentoFiscalEntrada.Fields.SERIE, serie));
		}
		
		if (nroAf != null) {
			criteria.add(Restrictions.eq("afs." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), nroAf));
		}
		
		if(nroComplemento != null) {
			criteria.add(Restrictions.eq("afs." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));
		}
		
		if (nf != null) {
			criteria.add(Restrictions.eq("dfe." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), nf));
		}
		
		criteria.add(Restrictions.eq("ttl." + FcpTitulo.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MovimentacaoFornecedorVO.class));
		return criteria;
	}

    public Long pesquisarMovimentacaoFornecedorCount(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf){
    	DetachedCriteria criteria = obterCriteriaMovimentacaoFornecedor(frnNumero, situacao, notaRecebimento, serie, 
    			nroAf, nroComplemento, nf);
    	return executeCriteriaCount(criteria);
    }
	
	public List<MovimentacaoFornecedorVO> pesquisarMovimentacaoFornecedor(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaMovimentacaoFornecedor(frnNumero, situacao, notaRecebimento, serie, 
    			nroAf, nroComplemento, nf);

		if (orderProperty == null || orderProperty.isEmpty()) {
			criteria.addOrder(Order.asc("ttl." + FcpTitulo.Fields.IND_SITUACAO.toString()));
			criteria.addOrder(Order.desc("ttl." + FcpTitulo.Fields.DT_VENCIMENTO.toString()));
		}
		
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	
	private static StringBuilder obterSubSelectValorTributos(){
		StringBuilder subSelect = new StringBuilder(300);
		String schema = FcpValorTributos.class.getAnnotation(Table.class).schema();
		String valorTributosTB = schema + "." + FcpValorTributos.class.getAnnotation(Table.class).name();
		String retencaoAliquotaTB = schema + "." + FcpRetencaoAliquota.class.getAnnotation(Table.class).name();
		
		subSelect.append("( select sum(").append(FcpValorTributos.Fields.VALOR.toString()).append(") ")
		.append(" from ").append(valorTributosTB).append(" VTR, ").append(retencaoAliquotaTB).append(" FRA ")
		.append(" where ")
		.append(" VTR.inr_nrs_seq = {alias}.NRS_SEQ and VTR.ttl_seq = {alias}.SEQ and ")
		.append(" FRA.fri_codigo = VTR.fri_codigo and FRA.imposto = VTR.far_imposto and ")
		.append(" FRA.numero = VTR.far_numero and ")
		.append(" FRA.imposto not in ")
		.append(" ('MULTA','DESCONTO','DEVOLUÇÃO')) ").append(" tributos ");
		
		return subSelect;
	}
	
	private static StringBuilder obterProjectionValorPagamento(String pgtAlias, String valorPagamentoAlias){
		
		StringBuilder valorPagamento = new StringBuilder(200);
		
		valorPagamento.append('(').append("{alias}.VALOR").append(" - ");
		valorPagamento.append("(coalesce(").append(pgtAlias).append(".VLR_DESCONTO").append(", 0) + ");
		valorPagamento.append("coalesce(").append(pgtAlias).append(".VLR_ACRESCIMO").append(", 0))) ").append(valorPagamentoAlias);
		
		//,(TTL.VALOR - (PGT.VLR_DESCONTO + PGT.VLR_ACRESCIMO)) as "Valor Pagamento"
		return valorPagamento;
	}
	
	public List<PagamentosRealizadosPeriodoVO> pesquisarPagamentosRealizadosPeriodo(Date inicioPeriodo, Date finalPeriodo){
		DetachedCriteria criteria = DetachedCriteria.forClass(getClazz(), "TTL");
		
		StringBuilder subSelect = obterSubSelectValorTributos();		
		StringBuilder valorPagamento = obterProjectionValorPagamento("pgt4_", PagamentosRealizadosPeriodoVO.Fields.VALOR_PAGAMENTO.toString());
		
		criteria.setProjection(Projections.projectionList()
		.add(Projections.property("TDP." + FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString())
				, PagamentosRealizadosPeriodoVO.Fields.TIPO_DOC.toString())
		.add(Projections.property("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO.toString())
				, PagamentosRealizadosPeriodoVO.Fields.NRO_DOCUMENTO.toString())
		.add(Projections.property("TTL." + FcpTitulo.Fields.VALOR.toString())
				, PagamentosRealizadosPeriodoVO.Fields.VALOR_TOTAL.toString())
		.add(Projections.sqlProjection(subSelect.toString(), new String[]{PagamentosRealizadosPeriodoVO.Fields.TRIBUTOS.toString()}, new Type[]{DoubleType.INSTANCE}))
		.add(Projections.sqlProjection(valorPagamento.toString(), new String[]{PagamentosRealizadosPeriodoVO.Fields.VALOR_PAGAMENTO.toString()}, new Type[]{DoubleType.INSTANCE}))
		.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString())
				, PagamentosRealizadosPeriodoVO.Fields.CNPJ.toString())
		.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString())
				, PagamentosRealizadosPeriodoVO.Fields.CPF.toString())
		.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
				, PagamentosRealizadosPeriodoVO.Fields.RAZAO_SOCIAL.toString())
		.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()) // nao esta mostrando
				, PagamentosRealizadosPeriodoVO.Fields.NUMERO.toString())
		.add(Projections.property("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString())
				, PagamentosRealizadosPeriodoVO.Fields.TITULO.toString())

		.add(Projections.property("TTL." + FcpTitulo.Fields.NRS_SEQ.toString())
				, PagamentosRealizadosPeriodoVO.Fields.NRS_SEQ.toString()));
		
		criteria.createAlias(FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");

		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("TTL." + FcpTitulo.Fields.PAGAMENTOS.toString(), "PGT");
		criteria.createAlias("PGT." + FcpPagamento.Fields.FCP_TIPO_DOC_PAGAMENTOS.toString(), "TDP");		
		
		criteria.add(Restrictions.eq("PGT." + FcpPagamento.Fields.IND_ESTORNO.toString(), "N"));
		criteria.add(Restrictions.between("PGT." + FcpPagamento.Fields.DT_PAGAMENTO.toString(), inicioPeriodo, finalPeriodo));
		
		criteria.addOrder(Order.asc("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PagamentosRealizadosPeriodoVO.class));
		return executeCriteria(criteria);
	}
	
	public List<PagamentosRealizadosPeriodoPdfVO> pesquisarPagamentosRealizadosPeriodoPDF(Date inicioPeriodo, Date finalPeriodo, Integer codVerbaGestao){
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");
		
		StringBuilder subSelectValorTributo = obterSubSelectValorTributos();
		StringBuilder valorPagamento = obterProjectionValorPagamento("pgt5_", PagamentosRealizadosPeriodoPdfVO.Fields.VLR_PAGAMENTO.toString());
		
		criteria.setProjection(Projections.projectionList()
		.add(Projections.property("PGT." + FcpPagamento.Fields.DT_PAGAMENTO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.DT_PAGAMENTO.toString())	
		.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.CNPJ.toString())
		.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.CPF.toString())
		.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.RAZAO_SOCIAL.toString())				
		.add(Projections.property("TDP." + FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.TDP_DESCRICAO.toString())				
		.add(Projections.property("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NRO_DOCUMENTO.toString())
		.add(Projections.property("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.TITULO.toString())
		.add(Projections.property("TTL." + FcpTitulo.Fields.NRS_SEQ.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NRS_SEQ.toString())		
		.add(Projections.property("TTL." + FcpTitulo.Fields.VALOR.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.TTL_VALOR.toString())
		.add(Projections.sqlProjection(valorPagamento.toString(), new String[]{PagamentosRealizadosPeriodoPdfVO.Fields.VLR_PAGAMENTO.toString()}, new Type[]{DoubleType.INSTANCE}))
		.add(Projections.sqlProjection(subSelectValorTributo.toString(), 
				new String[]{PagamentosRealizadosPeriodoPdfVO.Fields.TRIBUTOS.toString()}, new Type[]{DoubleType.INSTANCE}))
		.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_DESCONTO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.VLR_DESCONTO.toString())
		.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_ACRESCIMO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.VLR_ACRESCIMO.toString())
		.add(Projections.property("DFT." + FcpDfTitulo.Fields.VALOR.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.DFT_VALOR.toString())
		.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.PFT_LCT_NUMERO.toString())
		.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NRO_COMPLEMENTO.toString())
		.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.VBG_SEQ.toString())
						, PagamentosRealizadosPeriodoPdfVO.Fields.VBG_SEQ.toString())
		.add(Projections.property("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.VBG_DESCRICAO.toString())
		.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NTD_GND_CODIGO.toString())
		.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NTD_CODIGO.toString())
		.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.DESCRICAO.toString())
				, PagamentosRealizadosPeriodoPdfVO.Fields.NTD_DESCRICAO.toString()));			
		
		criteria.createAlias(FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("TTL." + FcpTitulo.Fields.PAGAMENTOS.toString(), "PGT");
		criteria.createAlias("PGT." + FcpPagamento.Fields.FCP_TIPO_DOC_PAGAMENTOS.toString(), "TDP");		
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TTL." + FcpTitulo.Fields.DF_TITULO.toString(), "DFT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("TTL." + FcpTitulo.Fields.IND_SITUACAO.toString(), DominioSituacaoTitulo.PG));
		criteria.add(Restrictions.eq("PGT." + FcpPagamento.Fields.IND_ESTORNO.toString(), "N"));
		criteria.add(Restrictions.between("PGT." + FcpPagamento.Fields.DT_GERACAO.toString(), inicioPeriodo, finalPeriodo));
		if(codVerbaGestao != null){
			criteria.add(Restrictions.eq("VBG." + FsoVerbaGestao.Fields.SEQ.toString(), codVerbaGestao));
		}
		criteria.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()))
		.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.CGC.toString()))
		.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.CPF.toString()))
		.addOrder(Order.asc("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PagamentosRealizadosPeriodoPdfVO.class));
		return executeCriteria(criteria);
	}
	
	public List<FcpTitulo> pesquisarDividaResumoGrupo(Date dataFinal, boolean grupoAtrasado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class);

		criteria.setProjection(Projections.projectionList()
			.add(Projections.property(FcpTitulo.Fields.DT_VENCIMENTO.toString()),FcpTitulo.Fields.DT_VENCIMENTO.toString())
			.add(Projections.sum(FcpTitulo.Fields.VALOR.toString()),FcpTitulo.Fields.VALOR.toString())
			.add(Projections.groupProperty(FcpTitulo.Fields.DT_VENCIMENTO.toString())));

		criteria.add(Restrictions.in(FcpTitulo.Fields.IND_SITUACAO.toString(), new DominioSituacaoTitulo[] { DominioSituacaoTitulo.APG, DominioSituacaoTitulo.BLQ }));
		criteria.add(Restrictions.eq(FcpTitulo.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));

		// grupo Atrasados
		if (grupoAtrasado) {
		    criteria.add(Restrictions.lt(FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataFinal));
		}
		// grupo A Vencer
		else {
		    criteria.add(Restrictions.ge(FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataFinal));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(FcpTitulo.class));
		criteria.addOrder(Order.asc(FcpTitulo.Fields.DT_VENCIMENTO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaPesquisarPosicaoTitulo() {

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class);

		// Aliases principais
		criteria.createAlias(FcpTitulo.Fields.PAGAMENTOS.toString(), "PGT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFS", JoinType.INNER_JOIN);
		criteria.createAlias("AFS." + ScoAutorizacaoForn.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("AFS." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PRF", JoinType.LEFT_OUTER_JOIN);

		// Aliases complementares para preencher os demais campos/hints da tela
		criteria.createAlias(FcpTitulo.Fields.SERVIDOR.toString(), "TTL_SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TTL_SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "TTL_SER_PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FcpTitulo.Fields.SERVIDOR_ESTORNADO.toString(), "SER_EST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_EST." + RapServidores.Fields.PESSOA_FISICA.toString(), "SER_EST_PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PGT." + FcpPagamento.Fields.RAP_SERVIDORES_BY_FCP_PGT_SER_FK1.toString(), "SER_PGT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_PGT." + RapServidores.Fields.PESSOA_FISICA.toString(), "SER_PGT_PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PGT." + FcpPagamento.Fields.SCO_CONTA_CORRENTE_FORNECEDOR.toString(), "CCF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CCF." + ScoContaCorrenteFornecedor.Fields.BANCO.toString(), "BAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PGT." + FcpPagamento.Fields.FCP_TIPO_DOC_PAGAMENTOS.toString(), "TDP", JoinType.LEFT_OUTER_JOIN);

		// Projeções
		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property(FcpTitulo.Fields.NUMERO_TITULO.toString()), PosicaoTituloVO.Fields.NUMERO.toString());
		projections.add(Projections.property(FcpTitulo.Fields.NRO_PARCELA.toString()), PosicaoTituloVO.Fields.PARCELA.toString());
		projections.add(Projections.property(FcpTitulo.Fields.IND_SITUACAO.toString()), PosicaoTituloVO.Fields.SITUACAO.toString());
		projections.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.SERIE.toString()), PosicaoTituloVO.Fields.SERIE.toString());
		projections.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), PosicaoTituloVO.Fields.NUMERO_NF.toString());

		projections.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString()), PosicaoTituloVO.Fields.NOTA_RECEBIMENTO.toString());
		projections.add(Projections.property("PRF." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), PosicaoTituloVO.Fields.NUMERO_AF.toString());
		projections.add(Projections.property("AFS." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), PosicaoTituloVO.Fields.COMPLEMENTO.toString());
		projections.add(Projections.property("AFS." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString()), PosicaoTituloVO.Fields.NUMERO_CONTRATO.toString());
		projections.add(Projections.property(FcpTitulo.Fields.DT_GERACAO.toString()), PosicaoTituloVO.Fields.DATA_GERACAO.toString());

		projections.add(Projections.property("TTL_SER_PES." + RapPessoasFisicas.Fields.NOME.toString()), PosicaoTituloVO.Fields.GERADO_POR.toString());
		projections.add(Projections.property(FcpTitulo.Fields.DT_VENCIMENTO.toString()), PosicaoTituloVO.Fields.DATA_VENCIMENTO.toString());
		projections.add(Projections.property(FcpTitulo.Fields.VALOR.toString()), PosicaoTituloVO.Fields.VALOR.toString());

		projections.add(Projections.property("AFS." + ScoAutorizacaoForn.Fields.NRO_EMPENHO.toString()), PosicaoTituloVO.Fields.EMPENHO.toString());
		projections.add(Projections.property("AFS." + ScoAutorizacaoForn.Fields.VALOR_EMPENHO.toString()), PosicaoTituloVO.Fields.VALOR_EMPENHO.toString());

		projections.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PosicaoTituloVO.Fields.FORNECEDOR.toString());
		projections.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), PosicaoTituloVO.Fields.CGC.toString());
		projections.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), PosicaoTituloVO.Fields.CPF.toString());

		projections.add(Projections.property(FcpTitulo.Fields.IND_ESTORNO.toString()), PosicaoTituloVO.Fields.ESTORNO.toString());
		projections.add(Projections.property(FcpTitulo.Fields.MOTIVO_ESTORNO.toString()), PosicaoTituloVO.Fields.MOTIVO_ESTORNO.toString());
		projections.add(Projections.property(FcpTitulo.Fields.DT_ESTORNO.toString()), PosicaoTituloVO.Fields.DATA_ESTORNO.toString());

		projections.add(Projections.property("SER_EST_PES." + RapPessoasFisicas.Fields.NOME.toString()), PosicaoTituloVO.Fields.RESPONSAVEL_ESTORNADO.toString());

		projections.add(Projections.property("TDP." + FcpTipoDocumentoPagamento.Fields.SEQ.toString()), PosicaoTituloVO.Fields.TIPO_DOC_PAGAMENTO.toString());
		projections.add(Projections.property("TDP." + FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString()), PosicaoTituloVO.Fields.TIPO_DOC_PAGAMENTO_DESCRICAO.toString());
		
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO.toString()), PosicaoTituloVO.Fields.DOCUMENTO.toString());

		projections.add(Projections.property("CCF." + ScoContaCorrenteFornecedor.Fields.AGB_BCO_CODIGO.toString()), PosicaoTituloVO.Fields.BANCO.toString());
		projections.add(Projections.property("CCF." + ScoContaCorrenteFornecedor.Fields.AGB_CODIGO.toString()), PosicaoTituloVO.Fields.AGENCIA.toString());
		projections.add(Projections.property("CCF." + ScoContaCorrenteFornecedor.Fields.CONTA_CORRENTE.toString()), PosicaoTituloVO.Fields.CONTA.toString());
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.DT_PAGAMENTO.toString()), PosicaoTituloVO.Fields.PAGO_EM.toString());

		projections.add(Projections.property("SER_PGT_PES." + RapPessoasFisicas.Fields.NOME.toString()), PosicaoTituloVO.Fields.RESPONSAVEL_PAGAMENTO.toString());

		projections.add(Projections.property("PGT." + FcpPagamento.Fields.VALOR.toString()), PosicaoTituloVO.Fields.VALOR_PAGAMENTO.toString());
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_ACRESCIMO.toString()), PosicaoTituloVO.Fields.ACRESCIMO.toString());
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_DESCONTO.toString()), PosicaoTituloVO.Fields.DESCONTO.toString());
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.OBSERVACAO.toString()), PosicaoTituloVO.Fields.OBSERVACAO.toString());

		//projections.add(Projections.property("BO." + SceBoletimOcorrencias.Fields.SEQ.toString()), PosicaoTituloVO.Fields.BO.toString());
		projections.add(Projections.property("BAN." + FcpBanco.Fields.NOME.toString()), PosicaoTituloVO.Fields.BANCO_NOME.toString());
		
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.ID_NUMERO.toString()), PosicaoTituloVO.Fields.PAGAMENTO_NUMERO.toString());
		projections.add(Projections.property("PGT." + FcpPagamento.Fields.ID_TTL_SEQ.toString()), PosicaoTituloVO.Fields.PAGAMENTO_TITULO_SEQ.toString());
		


		criteria.setProjection(Projections.distinct(projections));

		criteria.setResultTransformer(Transformers.aliasToBean(PosicaoTituloVO.class));

		return criteria;
	}
	@SuppressWarnings("PMD.NPathComplexity")
	private void filtrarPesquisarPosicaoTitulo(final DetachedCriteria criteria, final FiltroPesquisaPosicaoTituloVO filtro) {

		// Número (fieldset Título)
		if (filtro.getNumero() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.NUMERO_TITULO.toString(), filtro.getNumero()));
		}

		// Parcela (fieldset Título)
		if (filtro.getParcela() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.NRO_PARCELA.toString(), filtro.getParcela()));
		}
		// Situação
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));
		}

		// Data Geração (data inicio e data fim)
		if (filtro.getDataInicio() != null && filtro.getDataFim() != null) {
			final Date dataInicio = DateUtil.truncaData(filtro.getDataInicio());
			final Date dataFim = DateUtil.truncaDataFim(filtro.getDataFim());			
			criteria.add(Restrictions.between(FcpTitulo.Fields.DT_GERACAO.toString(), dataInicio, dataFim));

		} else if (filtro.getDataInicio() != null && filtro.getDataFim() == null) {
			// Data Geração (data inicio)
			compararDataGeracaoPesquisarPosicaoTitulo(criteria, filtro.getDataInicio());
		} else if (filtro.getDataFim() != null && filtro.getDataInicio() == null) {
			// Data Geração (data fim)
			compararDataGeracaoPesquisarPosicaoTitulo(criteria, filtro.getDataFim());
		}

		// NR
		if (filtro.getNotaRecebimento() != null) {
			criteria.add(Restrictions.eq("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), filtro.getNotaRecebimento()));
		}

		// Gerado Por
		if (filtro.getGeradoPor() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.SERVIDOR.toString(), filtro.getGeradoPor()));
		}

		// Número AF
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("PRF." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), filtro.getNumeroAF()));
		}

		// Complemento
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AFS." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}

		// Fornecedor
		if (filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}

		// BO
		if (filtro.getBo() != null) {
			criteria.createAlias("NRS." + SceNotaRecebimento.Fields.BOLETIM_OCORRENCIAS.toString(), "BO", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("BO." + SceBoletimOcorrencias.Fields.SEQ.toString(), filtro.getBo()));
		}

		// Nro. Contrato
		if (filtro.getNumeroContrato() != null) {
			criteria.add(Restrictions.eq("AFS." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtro.getNumeroContrato()));
		}

		// Estornado Por
		if (filtro.getEstornadoPor() != null) {
			criteria.add(Restrictions.eq(FcpTitulo.Fields.SERVIDOR_ESTORNADO.toString(), filtro.getEstornadoPor()));
		}

		// Número (fieldset Pagamento)
		if (filtro.getDocumento() != null) {
			criteria.add(Restrictions.eq("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO.toString(), filtro.getDocumento()));
		}

		// Pago Por (fieldset Pagamento)
		if (filtro.getPagoPor() != null) {
			criteria.add(Restrictions.eq("PGT." + FcpPagamento.Fields.RAP_SERVIDORES_BY_FCP_PGT_SER_FK1.toString(), filtro.getPagoPor()));
		}

		// Observação (fieldset Pagamento)
		if (StringUtils.isNotBlank(filtro.getObservacao())) {
			criteria.add(Restrictions.ilike("PGT." + FcpPagamento.Fields.OBSERVACAO.toString(), filtro.getObservacao(), MatchMode.ANYWHERE));
		}

	}

	/**
	 * 
	 * Reuso da comparação de data de geração truncada (utilizando SQL
	 * Restriction) na pesquisa de posição de título
	 * 
	 * @param criteria
	 * @param data
	 */
	private void compararDataGeracaoPesquisarPosicaoTitulo(DetachedCriteria criteria, Date data) {
		Date dataInicio = DateUtil.truncaData(data);
		Date dataFim = DateUtil.truncaDataFim(data);		
		criteria.add(Restrictions.between(FcpTitulo.Fields.DT_GERACAO.toString(), dataInicio, dataFim));
	}

	public List<PosicaoTituloVO> pesquisarPosicaoTitulo(Integer firstResult, Integer maxResult, final FiltroPesquisaPosicaoTituloVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarPosicaoTitulo();
		filtrarPesquisarPosicaoTitulo(criteria, filtro); // Filtros ou critérios
		return this.executeCriteria(criteria, firstResult, maxResult, FcpTitulo.Fields.NUMERO_TITULO.toString(), false);
	}

	public Long pesquisarPosicaoTituloCount(final FiltroPesquisaPosicaoTituloVO filtro) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarPosicaoTitulo();
		filtrarPesquisarPosicaoTitulo(criteria, filtro); // Filtros ou critérios
		return executeCriteriaCount(criteria);
	}
	
	public List<RelatorioMovimentacaoFornecedorVO> listarMovimentacaoFornecedor(
			ScoFornecedor fornecedor, Date dataInicio, Date dataFim) {
		
		List<RelatorioMovimentacaoFornecedorVO> result1 = 
				this.listarMovimentacaoFornecedorParte1(fornecedor, dataInicio, dataFim);
				
		List<RelatorioMovimentacaoFornecedorVO> result2 = 
				this.listarMovimentacaoFornecedorHist(fornecedor, dataInicio, dataFim);
		
		result1.addAll(result2);
		
/*		Collections.sort(result1, Collections.reverseOrder(new Comparator<RelatorioMovimentacaoFornecedorVO>() {
			@Override
			public int compare(RelatorioMovimentacaoFornecedorVO arg0, RelatorioMovimentacaoFornecedorVO arg1) {
				return arg0.getDthrLancamento().compareTo(arg1.getDthrLancamento());
			}
		}));*/
		
		return result1;
	}
	
	private List<RelatorioMovimentacaoFornecedorVO> listarMovimentacaoFornecedorParte1(
			ScoFornecedor fornecedor, Date dataInicio, Date dataFim) {

	    DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class,"TTL");
	    criteria.createAlias("TTL."+FcpTitulo.Fields.PAGAMENTOS.toString(), "PGT", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("TTL."+FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);
	    criteria.createAlias("NRS."+SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
	    criteria.createAlias("NRS."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFS", JoinType.INNER_JOIN);
	    criteria.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
	    criteria.createAlias("TTL."+FcpTitulo.Fields.DF_TITULO.toString(), "DFT", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("PGT."+FcpPagamento.Fields.SCO_CONTA_CORRENTE_FORNECEDOR.toString(), "CCF", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("CCF."+ScoContaCorrenteFornecedor.Fields.BANCO.toString(), "BCO", JoinType.LEFT_OUTER_JOIN);

	    // Projeções
 		ProjectionList projections = Projections.projectionList();
 		montarProjectionMovimentacaoFornecedor(projections);
	    
	    criteria.setProjection(Projections.distinct(projections));
	    
	    if(fornecedor != null) {
	    	if(fornecedor.getCgc() != null){
	    		criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.CGC, fornecedor.getCgc()));
	    	}else if(fornecedor.getCpf() != null){
	    		criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.CPF, fornecedor.getCpf()));
	    	}
	    }
	    if(dataInicio != null) {
	    	criteria.add(Restrictions.ge("DFE."+SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(), dataInicio));
	    } 
	    if(dataFim != null) {
	    	criteria.add(Restrictions.le("DFE."+SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(), dataFim));
	    } 
	    
	    // Ordenar por Razão Social, CGC_CPF, DT_EMISSAO
	    criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMovimentacaoFornecedorVO.class));
	    
	    return executeCriteria(criteria);
	}
	
	private List<RelatorioMovimentacaoFornecedorVO> listarMovimentacaoFornecedorHist(
			ScoFornecedor fornecedor, Date dataInicio, Date dataFim) {

	    DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class,"TTL");
	    criteria.createAlias("TTL."+FcpTitulo.Fields.PAGAMENTOS.toString(), "PGT", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("TTL."+FcpTitulo.Fields.NOTA_RECEBIMENTO_HIST.toString(), "NRS", JoinType.INNER_JOIN);
	    criteria.createAlias("NRS."+SceNotaRecebimentoHist.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
	    criteria.createAlias("NRS."+SceNotaRecebimentoHist.Fields.AUTORIZACAO_FORN.toString(), "AFS", JoinType.INNER_JOIN); 
	    criteria.createAlias("AFS."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFN", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("PFN."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);  
	    criteria.createAlias("TTL."+FcpTitulo.Fields.DF_TITULO.toString(), "DFT", JoinType.LEFT_OUTER_JOIN);
	    criteria.createAlias("PGT."+FcpPagamento.Fields.SCO_CONTA_CORRENTE_FORNECEDOR.toString(), "CCF", JoinType.INNER_JOIN);
	    criteria.createAlias("CCF."+ScoContaCorrenteFornecedor.Fields.BANCO.toString(), "BCO", JoinType.LEFT_OUTER_JOIN);

	    // Projeções
 		ProjectionList projections = Projections.projectionList();
 		montarProjectionMovimentacaoFornecedor(projections);
	    
	    criteria.setProjection(Projections.distinct(projections));
	    
	    if(fornecedor != null) {
	    	if(fornecedor.getCgc() != null){
	    		criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.CGC, fornecedor.getCgc()));
	    	}else if(fornecedor.getCpf() != null){
	    		criteria.add(Restrictions.eq("FRN."+ScoFornecedor.Fields.CPF, fornecedor.getCpf()));
	    	}
	    }
	    if(dataInicio != null) {
	    	criteria.add(Restrictions.ge("DFE."+SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(), dataInicio));
	    } 
	    if(dataFim != null) {
	    	criteria.add(Restrictions.le("DFE."+SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString(), dataFim));
	    } 
	    
	    // Ordenar por Razão Social
	    //criteria.addOrder(Order.asc("FRN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
	    criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMovimentacaoFornecedorVO.class));
	    
	    return executeCriteria(criteria);
	}

	public void montarProjectionMovimentacaoFornecedor(ProjectionList projections) {
		projections.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO), RelatorioMovimentacaoFornecedorVO.Fields.NUMERO.toString());
	    projections.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL), RelatorioMovimentacaoFornecedorVO.Fields.RAZAO_SOCIAL.toString());
	    projections.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC), RelatorioMovimentacaoFornecedorVO.Fields.CGC.toString());
	    projections.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF), RelatorioMovimentacaoFornecedorVO.Fields.CPF.toString());
 		projections.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()), RelatorioMovimentacaoFornecedorVO.Fields.DT_EMISSAO.toString());
	    projections.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO), RelatorioMovimentacaoFornecedorVO.Fields.NF_NUMERO.toString());
	    projections.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.SERIE), RelatorioMovimentacaoFornecedorVO.Fields.NF_SERIE.toString());
	    projections.add(Projections.property("TTL." + FcpTitulo.Fields.BOC_SEQ), RelatorioMovimentacaoFornecedorVO.Fields.BO.toString());
	    projections.add(Projections.property("TTL." + FcpTitulo.Fields.NUMERO_TITULO), RelatorioMovimentacaoFornecedorVO.Fields.TITULO.toString()); //changed
	    projections.add(Projections.property("TTL." + FcpTitulo.Fields.NRO_PARCELA), RelatorioMovimentacaoFornecedorVO.Fields.PC.toString());
	    projections.add(Projections.property("TTL." + FcpTitulo.Fields.DT_VENCIMENTO), RelatorioMovimentacaoFornecedorVO.Fields.DT_VENCIMENTO.toString());
	    projections.add(Projections.property("TTL." + FcpTitulo.Fields.VALOR), RelatorioMovimentacaoFornecedorVO.Fields.VALOR_TITULO.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.NRO_DOCUMENTO), RelatorioMovimentacaoFornecedorVO.Fields.DOC.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.DT_PAGAMENTO), RelatorioMovimentacaoFornecedorVO.Fields.DT_PAGAMENTO.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.CCE_AGB_BCO_CODIGO), RelatorioMovimentacaoFornecedorVO.Fields.AGENCIA.toString());
	    projections.add(Projections.property("CCF." + ScoContaCorrenteFornecedor.Fields.CONTA_CORRENTE), RelatorioMovimentacaoFornecedorVO.Fields.CONTA_CORRENTE.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.IND_ESTORNO), RelatorioMovimentacaoFornecedorVO.Fields.ESTORNADO.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_ACRESCIMO), RelatorioMovimentacaoFornecedorVO.Fields.VLR_ACRESC.toString());
	    projections.add(Projections.property("PGT." + FcpPagamento.Fields.VLR_DESCONTO), RelatorioMovimentacaoFornecedorVO.Fields.DESCONTO.toString());
	    projections.add(Projections.property("BCO." + FcpBanco.Fields.NOME), RelatorioMovimentacaoFornecedorVO.Fields.BANCO.toString());
	    projections.add(Projections.property("DFT." + FcpDfTitulo.Fields.VALOR), RelatorioMovimentacaoFornecedorVO.Fields.VALOR_DF.toString());
	    projections.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR), RelatorioMovimentacaoFornecedorVO.Fields.NRS_SEQ.toString());
	}

	public PosicaoTituloVO obterPosicaoTituloVOPorNumero(final Integer numero) {
		if(numero == null){
			throw new IllegalArgumentException();
		}
		DetachedCriteria criteria = this.obterCriteriaPesquisarPosicaoTitulo();
		criteria.add(Restrictions.eq(FcpTitulo.Fields.NUMERO_TITULO.toString(), numero));
		return (PosicaoTituloVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<DatasVencimentosFornecedorVO> pesquisarFornecedoresIrregularidadeFiscal(Date dataInicial, Date dataFinal, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");

		criteria.createAlias("TTL." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.NUMERO.toString())
						, DatasVencimentosFornecedorVO.Fields.NUMERO.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
						, DatasVencimentosFornecedorVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.CGC.toString())
						, DatasVencimentosFornecedorVO.Fields.CGC.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.CPF.toString())
						, DatasVencimentosFornecedorVO.Fields.CPF.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString())
						, DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_FGTS.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString())
						, DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_INSS.toString())
				.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString())
						, DatasVencimentosFornecedorVO.Fields.DATA_VALIDADE_RECEITA_FEDERAL.toString())
				.add(Projections.count("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString())
						, DatasVencimentosFornecedorVO.Fields.TITULOS_PAGAR.toString()));
		
		Date dataAtual = new Date();
		
		criteria.add(Restrictions.eq("TTL." + FcpTitulo.Fields.IND_SITUACAO.toString(), DominioSituacaoTitulo.APG));
		criteria.add(Restrictions.eq("TTL." + FcpTitulo.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.TIPO_FORNECEDOR.toString(), DominioTipoFornecedor.FNA));
		
		Criterion critDtValRecFed1 = Restrictions.lt("FRN." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString(), dataAtual);
		Criterion critDtValRecFed2 = Restrictions.isNull("FRN." + ScoFornecedor.Fields.DT_VALIDADE_RECFED.toString());
		Criterion critCgc = Restrictions.isNotNull("FRN." + ScoFornecedor.Fields.CGC.toString());
		Criterion critDtValFgts1 = Restrictions.lt("FRN." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString(), dataAtual);
		Criterion critDtValFgts2 = Restrictions.isNull("FRN." + ScoFornecedor.Fields.DT_VALIDADE_FGTS.toString());
		Criterion critDtValInss1 = Restrictions.lt("FRN." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString(), dataAtual);
		Criterion critDtValInss2 = Restrictions.isNull("FRN." + ScoFornecedor.Fields.DT_VALIDADE_INSS.toString());
		
		criteria.add(Restrictions.or(
				critDtValRecFed1, Restrictions.or(
						critDtValRecFed2, Restrictions.and(
								critCgc, Restrictions.or(
										critDtValFgts1, Restrictions.or(
												critDtValFgts2, Restrictions.or(
														critDtValInss1, critDtValInss2)))))));
		if (dataInicial != null) {
			criteria.add(Restrictions.ge("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataInicial));
		}
		if (dataFinal != null) {
			criteria.add(Restrictions.le("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataFinal));
		}
		if (numero != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numero));
		}
		
		criteria.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()))
			.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.CGC.toString()))
			.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.CPF.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DatasVencimentosFornecedorVO.class));
		
		return executeCriteria(criteria);
	}
	

	public List<ConsultaGeralTituloVO> consultaGeralTitulos(FiltroConsultaGeralTituloVO filtro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");
		criteria.createAlias("TTL." + FcpTitulo.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("TTL." + FcpTitulo.Fields.TIPO_TITULO.toString(), "TPT");
		criteria.createAlias("TTL." + FcpTitulo.Fields.TITULO_SOLICITACOES.toString(), "TXS");
		criteria.createAlias("TTL." + FcpTitulo.Fields.CLASSIFICACAO_TITULO.toString(), "CLT");
		
		
		if(filtro.getNaturezaDespesa() != null){
			criteria.createAlias("TTL." + FcpTitulo.Fields.NATUREZA_DESPESA.toString(), "NTD");
		}
		else{
			criteria.createAlias("TTL." + FcpTitulo.Fields.NATUREZA_DESPESA.toString(), "NTD",JoinType.LEFT_OUTER_JOIN);
		}
		if(filtro.getGrupoNaturezaDespesa() != null){
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND");
		}
		else{
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND",JoinType.LEFT_OUTER_JOIN);
		}
		
		DetachedCriteria subCriteria1 = DetachedCriteria.forClass(FcpTituloSolicitacoes.class,"FCP1");
		subCriteria1.setProjection(Projections.property("FCP1."+FcpTituloSolicitacoes.Fields.TTL_SEQ.toString()));
		subCriteria1.add(Restrictions.eqProperty("FCP1."+FcpTituloSolicitacoes.Fields.TTL_SEQ.toString(), "TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString()));
		criteria.add(Subqueries.exists(subCriteria1));
		
		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class,"SLC");
		subCriteria2.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		subCriteria2.setProjection(Projections.property("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		subCriteria2.add(Restrictions.eqProperty("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "TXS."+FcpTituloSolicitacoes.Fields.SLC_NUMERO.toString()));
		
		retrictionSubcriteria2(filtro, subCriteria2);
		
		Criterion exists2 = Subqueries.exists(subCriteria2);

		DetachedCriteria subCriteria3 = DetachedCriteria.forClass(ScoSolicitacaoServico.class,"SLS");
		subCriteria3.createAlias("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
		subCriteria3.setProjection(Projections.property("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
		subCriteria3.add(Restrictions.eqProperty("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), "TXS."+FcpTituloSolicitacoes.Fields.SLS_NUMERO.toString()));
		
		retrictionsSubcriteria3(filtro, subCriteria3);
		
		Criterion exists3 = Subqueries.exists(subCriteria3);
		criteria.add(Restrictions.or(exists2, exists3));
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.GND_CODIGO.toString())
						, ConsultaGeralTituloVO.Fields.NTD_GND_CODIGO.toString())
				.add(Projections.property("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString())
						, ConsultaGeralTituloVO.Fields.GND_DESCRICAO.toString())
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.CODIGO.toString())
						, ConsultaGeralTituloVO.Fields.NTD_CODIGO.toString())
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.DESCRICAO.toString())
						, ConsultaGeralTituloVO.Fields.NTD_DESCRICAO.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString())
						, ConsultaGeralTituloVO.Fields.TTL_SEQ.toString())//
				.add(Projections.property("TTL." + FcpTitulo.Fields.MODALIDADE_EMPENHO.toString())
						, ConsultaGeralTituloVO.Fields.MODALIDADE_EMPENHO.toString())
				.add(Projections.property("CLT." + FcpClassificacaoTitulo.Fields.DESCRICAO.toString())
						, ConsultaGeralTituloVO.Fields.CLT_DESCRICAO.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString())
						, ConsultaGeralTituloVO.Fields.FRN_NUMERO.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
						, ConsultaGeralTituloVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString())
						, ConsultaGeralTituloVO.Fields.FRN_CNPJ.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString())
						, ConsultaGeralTituloVO.Fields.FRN_CPF.toString())
				.add(Projections.property("TPT." + FcpTipoTitulo.Fields.DESCRICAO.toString())
						, ConsultaGeralTituloVO.Fields.DESCRICAO_TIPO_TITULO.toString())
				.add(Projections.property("TPT." + FcpTipoTitulo.Fields.CODIGO.toString())
						, ConsultaGeralTituloVO.Fields.TPT_CODIGO.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString())
						, ConsultaGeralTituloVO.Fields.DT_VENCIMENTO.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.IND_SITUACAO.toString())
								, ConsultaGeralTituloVO.Fields.SITUACAO.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.VALOR.toString())
								, ConsultaGeralTituloVO.Fields.VALOR_TITULO.toString()))
				);
		filtroTelaPesquisaGeralTitulosP1(filtro, criteria);	
		filtroTelaPesquisaGeralTitulosP2(filtro, criteria);
		filtroTelaPesquisaGeralTitulos(filtro, criteria);
		filtraDatasInicialFinal(filtro, criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaGeralTituloVO.class));
		criteria.addOrder(Order.desc("TTL."+FcpTitulo.Fields.NUMERO_TITULO.toString()));
		return executeCriteria(criteria);
	}
	
	private void filtroTelaPesquisaGeralTitulos(FiltroConsultaGeralTituloVO filtro, DetachedCriteria criteria) {
		if (filtro.getArrayTtlSeq() != null) {
			criteria.add(Restrictions.in("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString(), filtro.getArrayTtlSeq()));
		}
	}

	private void retrictionsSubcriteria3(FiltroConsultaGeralTituloVO filtro,
			DetachedCriteria subCriteria3) {
		if(filtro.getNumeroSolicitacao() != null){
			subCriteria3.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(),filtro.getNumeroSolicitacao()));
		}
		if(filtro.getServico() != null){
			subCriteria3.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(),filtro.getServico()));
		}
		if(filtro.getGrupoServico() != null){
			subCriteria3.add(Restrictions.eq("SRV."+ScoServico.Fields.GRUPO_SERVICO.toString(),filtro.getGrupoServico()));
		}
		if(filtro.getTipoSolicitacao() != null){
			subCriteria3.add(Restrictions.sqlRestriction(" 'SS' = "+ "'"+filtro.getTipoSolicitacao()+"'"));
		}
	}

	private void retrictionSubcriteria2(FiltroConsultaGeralTituloVO filtro,
			DetachedCriteria subCriteria2) {
		if(filtro.getNumeroSolicitacao() != null){
			subCriteria2.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(),filtro.getNumeroSolicitacao()));
		}

		if(filtro.getMaterial() != null){
			subCriteria2.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(),filtro.getMaterial()));
		}
		if(filtro.getGrupoMaterial() != null){
			subCriteria2.add(Restrictions.eq("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(),filtro.getGrupoMaterial()));
		}
		
		if(filtro.getTipoSolicitacao() != null){
			subCriteria2.add(Restrictions.sqlRestriction(" 'SC' = "+ "'"+filtro.getTipoSolicitacao()+"'"));
		}
	}

	private void filtroTelaPesquisaGeralTitulosP2(
			FiltroConsultaGeralTituloVO filtro, DetachedCriteria criteria) {
				
		if(filtro.getNaturezaDespesa() != null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.NATUREZA_DESPESA.toString(), filtro.getNaturezaDespesa()));
		}
		
		if(filtro.getGrupoNaturezaDespesa() != null){
			criteria.add(Restrictions.eq("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), filtro.getGrupoNaturezaDespesa()));
		}
	}

	private void filtroTelaPesquisaGeralTitulosP1(
			FiltroConsultaGeralTituloVO filtro, DetachedCriteria criteria) {
		if(filtro.getTtlSeq()!=null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.NUMERO_TITULO.toString(), filtro.getTtlSeq()));
		}
		
		if(filtro.getFornecedor()!=null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.FORNECEDOR.toString(), filtro.getFornecedor()));
		}
		
		if(filtro.getClassificacaoTitulo()!=null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.CLASSIFICACAO_TITULO_CODIGO.toString(), filtro.getClassificacaoTitulo().getCodigo()));
		}
		
		if(filtro.getSituacaoTitulo()!=null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.IND_SITUACAO.toString(), filtro.getSituacaoTitulo()));
		}
		
		if(filtro.getTipoTitulo()!=null){
			criteria.add(Restrictions.eq("TTL."+FcpTitulo.Fields.TIPO_TITULO_CODIGO.toString(), filtro.getTipoTitulo().getCodigo()));
		}
	}

	private void filtraDatasInicialFinal(FiltroConsultaGeralTituloVO filtro,
			DetachedCriteria criteria) {
		if(filtro.getDataVencimentoInicial() != null && filtro.getDataVencimentoFinal() != null){
			final Date dataInicio = DateUtil.truncaData(filtro.getDataVencimentoInicial());
			final Date dataFim = DateUtil.truncaDataFim(filtro.getDataVencimentoFinal());			
			criteria.add(Restrictions.between("TTL."+FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataInicio, dataFim));
		}
		else{
			if(filtro.getDataVencimentoInicial() != null){
				final Date dataInicio = DateUtil.truncaData(filtro.getDataVencimentoInicial());
				criteria.add(Restrictions.ge("TTL."+FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataInicio));
			}
			if(filtro.getDataVencimentoFinal() != null){
				final Date dataFim = DateUtil.truncaDataFim(filtro.getDataVencimentoFinal());
				criteria.add(Restrictions.le("TTL."+FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataFim));
			}
		}
	}
		
}

