package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.dao.FcpMoedaDAO;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class GerarSolicitacaoCompraAutomaticaRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(GerarSolicitacaoCompraAutomaticaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FcpMoedaDAO fcpMoedaDAO;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IOrcamentoFacade orcamentoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7135183963249225868L;
	
	public enum GerarSolicitacaoCompraAutomaticaRNExceptionCode implements BusinessExceptionCode {
		USUARIO_NAO_TEM_CENTRO_DE_CUSTO_DE_LOTACAO,MENSAGEM_PARAMETRO_PPS_LICITACAO_NAO_ENCONTRADO;
	}
	
	private static final String DESCRICAO_LICITACAO = "LICITAÇÃO GERADA AUTOMATICAMENTE PELA NR";
	private static final String DESCRICAO_PONTO_PARADA_SOLICITACAO = "PONTO PARADA SOLICITAÇÃO GERADO AUTOMATICAMENTE PELA NR";
	
	
	/*
	 * Métodos relacionados a Nota de Recebimento com Solicitação de Compra Automática
	 */
	
	/**
	 * Gera uma Solicitação de Compra Automática através de uma Nota de Recebimento
	 * Obs. Método relacionado com a tarefa #16137 Gerar Nota de Recebimento sem AF
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void gerarSolicitacaoCompraAutomaticaNotaRecebimento(final SceNotaRecebimento notaRecebimento) throws BaseException{
		this.gerarAutorizacaoFornecimento(notaRecebimento);
	}
	
	/**
	 * Gera uma Autorização de Fornecimento através de uma Nota de Recebimento
	 * @param servidorSolicitacaoDeCompraAutomatica
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void gerarAutorizacaoFornecimento(final SceNotaRecebimento notaRecebimento) throws BaseException{

		
		final ScoModalidadeLicitacao modalidadeLicitacao = getCadastrosBasicosFacade().obterScoModalidadeLicitacaoPorChavePrimaria("TP"); // Código TP (Tomada de Preço)
		
	
		final ScoLicitacao licitacao = new ScoLicitacao();
		licitacao.setModalidadeLicitacao(modalidadeLicitacao);
		licitacao.setServidorDigitado(this.getServidorSolicitacaoDeCompraAutomatica());
		licitacao.setDescricao(DESCRICAO_LICITACAO);
		licitacao.setDtDigitacao(new Date());
		licitacao.setExclusao(false);
		licitacao.setDtLimiteAceiteProposta(new Date());
		licitacao.setDthrAberturaProposta(new Date());
		licitacao.setSituacao(DominioSituacaoLicitacao.EF);
		licitacao.setHrAberturaProposta(0);
		licitacao.setDtPublicacao(new Date());
		
		this.getComprasFacade().inserirScoLicitacao(licitacao);
		
		/*
		 * SCO_PROPOSTAS_FORNECEDORES: Proposta do Fornecedor
		 */
		final ScoPropostaFornecedor propostaFornecedor = new ScoPropostaFornecedor();
		propostaFornecedor.setLicitacao(licitacao);
		propostaFornecedor.setFornecedor(notaRecebimento.getDocumentoFiscalEntrada().getFornecedor());
		propostaFornecedor.setServidor(this.getServidorSolicitacaoDeCompraAutomatica());
		propostaFornecedor.setDtDigitacao(new Date());
		propostaFornecedor.setIndExclusao(false);
		
		this.getComprasFacade().inserirScoPropostaFornecedor(propostaFornecedor);
		
		/*
		 * SCO_FORMA_PAGAMENTOS: Forma de Pagamento
		 */
		final ScoFormaPagamento formaPagamento = this.getComprasFacade().obterScoFormaPagamentoPorChavePrimaria((short)1); // Código 1 (À vista)
		
		
		/*
		 * SCO_CONDICOES_PAGAMENTO_PROPOS: Condições de Pagamento da Proposta do Fornecedor
		 */
		final ScoCondicaoPagamentoPropos condicaoPagamentoPropos = new ScoCondicaoPagamentoPropos();
		condicaoPagamentoPropos.setFormaPagamento(formaPagamento);
		condicaoPagamentoPropos.setPropostaFornecedor(propostaFornecedor);
		condicaoPagamentoPropos.setIndCondEscolhida(true);

		this.getComprasFacade().inserir(condicaoPagamentoPropos, new ArrayList<ScoParcelasPagamento>());

		/*
		 * SCO_AUTORIZACOES_FORN: Autorização de Fornecimento
		 */
		
		final ScoAutorizacaoForn autorizacaoFornecimento = new ScoAutorizacaoForn();
		autorizacaoFornecimento.setMoeda(this.getMoedaNacional());
		autorizacaoFornecimento.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
		autorizacaoFornecimento.setPropostaFornecedor(propostaFornecedor);
		
		// FSO_CONVENIOS_FINANCEIROS: Convênio Financeiro
		final FsoConveniosFinanceiro convenioFinanceiro = this.getOrcamentoFacade().obterFsoConveniosFinanceiroPorChavePrimaria(2); 
		autorizacaoFornecimento.setConvenioFinanceiro(convenioFinanceiro);
		
		// FSO_NATUREZAS_DESPESA: Natureza da Despesa
		final FsoNaturezaDespesaId idNaturezaDespesa = new FsoNaturezaDespesaId(0,(byte)0); // Código 0 (Importação)
		final FsoNaturezaDespesa naturezaDespesa = this.getOrcamentoFacade().obterFsoNaturezaDespesaPorChavePrimaria(idNaturezaDespesa); 
		autorizacaoFornecimento.setNaturezaDespesa(naturezaDespesa);
		
		autorizacaoFornecimento.setNroComplemento((short)0);
		//autorizacaoFornecimento.setModalidadeEmpenho(DominioModalidadeEmpenho.IMPORTACAO); // Código 0 (Importação)
		autorizacaoFornecimento.setServidor(this.getServidorSolicitacaoDeCompraAutomatica());
		autorizacaoFornecimento.setGeracao(true);
		autorizacaoFornecimento.setExclusao(false);
		autorizacaoFornecimento.setAprovada(DominioAprovadaAutorizacaoForn.A);
		autorizacaoFornecimento.setSituacao(DominioSituacaoAutorizacaoFornecimento.EF);
		autorizacaoFornecimento.setDtGeracao(new Date());
		autorizacaoFornecimento.setSequenciaAlteracao((short)0);
		
		//this.inserirComFlush(scoAutorizacaoFornDAO;
		//this.getAutFornecimentoFacade().atualizarAutorizacaoForn(autorizacaoFornecimento, true);
		this.getAutFornecimentoFacade().persistirAutorizacaoForn(autorizacaoFornecimento, true);
		
		
		/*
		 * SCO_AF_JN: Journal da Autorização de Fornecimento
		 */
		
		final ScoAutorizacaoFornJn journalAutorizacaoFornecimento = new ScoAutorizacaoFornJn();
		
		journalAutorizacaoFornecimento.setJnUser(this.getServidorSolicitacaoDeCompraAutomatica().getUsuario());
		journalAutorizacaoFornecimento.setJnOperation(DominioOperacoesJournal.INS.toString());
		journalAutorizacaoFornecimento.setJnDateTime(new Date());
		journalAutorizacaoFornecimento.setServidor(this.getServidorSolicitacaoDeCompraAutomatica());
		journalAutorizacaoFornecimento.setCvfCodigo(convenioFinanceiro.getCodigo());
		journalAutorizacaoFornecimento.setNaturezaDespesa(naturezaDespesa);
		journalAutorizacaoFornecimento.setGrupoNaturezaDespesa(naturezaDespesa.getGrupoNaturezaDespesa());
		journalAutorizacaoFornecimento.setPropostaFornecedor(propostaFornecedor);
		journalAutorizacaoFornecimento.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
		journalAutorizacaoFornecimento.setDtGeracao(autorizacaoFornecimento.getDtGeracao());
		journalAutorizacaoFornecimento.setNroComplemento(autorizacaoFornecimento.getNroComplemento());
		journalAutorizacaoFornecimento.setSituacao(autorizacaoFornecimento.getSituacao());
		journalAutorizacaoFornecimento.setIndGeracao(autorizacaoFornecimento.getGeracao());
		journalAutorizacaoFornecimento.setServidorAutorizado(this.getServidorSolicitacaoDeCompraAutomatica());
		journalAutorizacaoFornecimento.setValorFrete(autorizacaoFornecimento.getValorFrete());
		journalAutorizacaoFornecimento.setIndExclusao(autorizacaoFornecimento.getExclusao());
		journalAutorizacaoFornecimento.setServidorAssinaCoord(this.getServidorSolicitacaoDeCompraAutomatica());
		journalAutorizacaoFornecimento.setIndAprovada(autorizacaoFornecimento.getAprovada());
		journalAutorizacaoFornecimento.setModalidadeEmpenho(autorizacaoFornecimento.getModalidadeEmpenho());
		journalAutorizacaoFornecimento.setNumero(autorizacaoFornecimento.getNumero());
		journalAutorizacaoFornecimento.setSequenciaAlteracao(autorizacaoFornecimento.getSequenciaAlteracao());
		
		this.getComprasFacade().inserirScoAutorizacaoFornJnComFlush(
				journalAutorizacaoFornecimento);

		/**
		 * SCE_NOTA_RECEBIMENTOS 
		 */
		notaRecebimento.setAutorizacaoFornecimento(autorizacaoFornecimento);

	}
	
	/*
	 * Métodos relacionados aos Ítens da Nota de Recebimento com Solicitação de Compra Automática
	 */
	

	/**
	 * Gera uma Solicitação de Compra Automática através de um Item de Nota de Recebimento
	 * Obs. Método relacionado com a tarefa #16137 Gerar Nota de Recebimento sem AF
	 * @param itemNotaRecebimento
	 * @param marcaComercial
	 * @param fatorConversao
	 * @throws BaseException
	 */
	public void gerarSolicitacaoCompraAutomaticaItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao) throws BaseException{
		final ScoItemLicitacao itemLicitacao = this.gerarItemAutorizacaoFornecimento(itemNotaRecebimento, numeroItem, marcaComercial, fatorConversao);
		this.gerarFaseSolicitacao(itemNotaRecebimento, itemLicitacao);
	}
	
	/**
	 * Gera uma Fase de Solicitação de Compra através de um Item de Nota de Recebimento
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	private void gerarFaseSolicitacao(final SceItemNotaRecebimento itemNotaRecebimento, final ScoItemLicitacao itemLicitacao) throws BaseException{
		
		ScoPontoParadaSolicitacao pontoParadaSolicitacao = new ScoPontoParadaSolicitacao();
		
		List<ScoPontoParadaSolicitacao>  listaPontoParada = this.getCadastrosBasicosFacade().pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(DESCRICAO_PONTO_PARADA_SOLICITACAO);
		if (listaPontoParada==null || listaPontoParada.isEmpty()){
			/*
			 * SCO_PONTOS_PARADA_SOLICITACOES: Ponto Parada da Solicitação de Compra
			 */
			pontoParadaSolicitacao.setSituacao(DominioSituacao.A);
			pontoParadaSolicitacao.setExigeResponsavel(false);
			pontoParadaSolicitacao.setDescricao(DESCRICAO_PONTO_PARADA_SOLICITACAO);
			this.getCadastrosBasicosFacade().inserirPontoParadaSolicitacao(pontoParadaSolicitacao);  //alterado em 29/10/2012
		}
		else {
			pontoParadaSolicitacao = listaPontoParada.get(0);
		}		/*
		 * Servidor sem centro de custo de lotacao issue #28524
		 */
		this.validaCentroCustoLotacao();
		ScoPontoParadaSolicitacao ppsSolicitante = this.getCadastrosBasicosFacade().obterPontoParadaPorTipo(DominioTipoPontoParada.SL);
		ScoPontoParadaSolicitacao ppsLicitacao = this.getCadastrosBasicosFacade().obterPontoParadaPorTipo(DominioTipoPontoParada.LI);

		if (ppsLicitacao == null || ppsSolicitante == null) {
			throw new ApplicationBusinessException(
					GerarSolicitacaoCompraAutomaticaRNExceptionCode.MENSAGEM_PARAMETRO_PPS_LICITACAO_NAO_ENCONTRADO);
		}
		/*
		 * SCO_SOLICITACOES_DE_COMPRAS: Solicitação de Compra
		 */
		final ScoSolicitacaoDeCompra solicitacaoDeCompra = new ScoSolicitacaoDeCompra();
		
		solicitacaoDeCompra.setPontoParada(ppsSolicitante);
		solicitacaoDeCompra.setPontoParadaProxima(ppsLicitacao);
		solicitacaoDeCompra.setUnidadeMedida(itemNotaRecebimento.getUnidadeMedida());
		solicitacaoDeCompra.setServidor(this.getServidorSolicitacaoDeCompraAutomatica());
		solicitacaoDeCompra.setMaterial(itemNotaRecebimento.getMaterial());
		solicitacaoDeCompra.setCentroCusto(this.getServidorSolicitacaoDeCompraAutomatica().getCentroCustoLotacao());
		solicitacaoDeCompra.setCentroCustoAplicada(this.getServidorSolicitacaoDeCompraAutomatica().getCentroCustoLotacao());
		solicitacaoDeCompra.setDtDigitacao(new Date());
		solicitacaoDeCompra.setQtdeSolicitada(itemNotaRecebimento.getQuantidade().longValue());
		solicitacaoDeCompra.setQtdeEntregue(itemNotaRecebimento.getQuantidade().longValue());
		solicitacaoDeCompra.setQtdeAprovada(itemNotaRecebimento.getQuantidade().longValue());
		solicitacaoDeCompra.setUrgente(false);
		solicitacaoDeCompra.setRecebimento(false);
		solicitacaoDeCompra.setFundoFixo(false);
		solicitacaoDeCompra.setEfetivada(true);
		solicitacaoDeCompra.setGeracaoAutomatica(true);
		solicitacaoDeCompra.setDiasDuracao(Short.valueOf("99"));		
		solicitacaoDeCompra.setDevolucao(false);
		solicitacaoDeCompra.setExclusao(false);
		solicitacaoDeCompra.setDtSolicitacao(new Date());
		solicitacaoDeCompra.setPrioridade(false);
		
		this.getSolicitacaoComprasFacade().inserirSolicitacaoDeCompra(solicitacaoDeCompra);
		
		/*
		 * SCO_FASES_SOLICITACOES: Fase 1 da Solicitação de Compra
		 */
		final ScoFaseSolicitacao faseSolicitacao2 = new ScoFaseSolicitacao();
		faseSolicitacao2.setTipo(DominioTipoFaseSolicitacao.C);
		faseSolicitacao2.setNumero(solicitacaoDeCompra.getNumero());
		faseSolicitacao2.setExclusao(false);
		faseSolicitacao2.setSolicitacaoDeCompra(solicitacaoDeCompra);
		
		// Fase por Item de Licitação
		faseSolicitacao2.setItemLicitacao(itemLicitacao);
		
		this.getComprasFacade().inserirScoFaseSolicitacaoSemRn(faseSolicitacao2, true);
		
		/*
		 * SCO_FASES_SOLICITACOES: Fase 2 da Solicitação de Compra
		 */
		final ScoFaseSolicitacao faseSolicitacao1 = new ScoFaseSolicitacao();
		faseSolicitacao1.setTipo(DominioTipoFaseSolicitacao.C);
		faseSolicitacao1.setNumero(solicitacaoDeCompra.getNumero());
		faseSolicitacao1.setExclusao(false);
		faseSolicitacao1.setSolicitacaoDeCompra(solicitacaoDeCompra);
		
		// Fase por Item de Autorização de Fornecimento
		faseSolicitacao1.setItemAutorizacaoForn(itemNotaRecebimento.getItemAutorizacaoForn());
		
		this.getComprasFacade().inserirScoFaseSolicitacaoSemRn(faseSolicitacao1, true);
	}
	
	/**
	 * Gera um Item de Autorização de Fornecimento através de uma Nota de Recebimento
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private ScoItemLicitacao gerarItemAutorizacaoFornecimento(final SceItemNotaRecebimento itemNotaRecebimento, final Short numeroItem, ScoMarcaComercial marcaComercial, final Integer fatorConversao) throws BaseException{

		/*
		 * SCO_LOTES_LICITACAO: Lote da Licitação
		 */
		final ScoLoteLicitacao loteLicitacao = new ScoLoteLicitacao();

		final ScoLicitacao licitacao = itemNotaRecebimento.getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getLicitacao();
		
		final ScoLoteLicitacaoId idLoteLicitacao = new ScoLoteLicitacaoId();	
		idLoteLicitacao.setLctNumero(licitacao.getNumero());
		idLoteLicitacao.setNumero(numeroItem); // Número do Item
		loteLicitacao.setId(idLoteLicitacao);
		loteLicitacao.setScoLicitacao(licitacao);
		
		this.getComprasFacade().inserirScoLoteLicitacao(loteLicitacao);
		
		/*
		 * SCO_ITENS_LICITACOES: Item da Licitação
		 */
		final ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
		
		final ScoItemLicitacaoId idItemLicitacao = new ScoItemLicitacaoId();
		idItemLicitacao.setLctNumero(licitacao.getNumero());
		idItemLicitacao.setNumero(numeroItem); // Número do Item
		itemLicitacao.setId(idItemLicitacao);
		
		itemLicitacao.setLicitacao(licitacao);
		itemLicitacao.setLoteLicitacao(loteLicitacao);
		itemLicitacao.setExclusao(false);
		itemLicitacao.setEmAf(false);
		itemLicitacao.setPropostaEscolhida(true);		

		this.getComprasFacade().inserirScoItemLicitacao(itemLicitacao);
		
		/*
		 * SCO_ITEM_PROPOSTAS_FORNECEDOR: Item da Proposta do Fornecedor
		 */
		final ScoItemPropostaFornecedor itemPropostaFornecedor = new ScoItemPropostaFornecedor();
		
		final ScoItemPropostaFornecedorId idItemPropostaFornecedor = new ScoItemPropostaFornecedorId();
		idItemPropostaFornecedor.setPfrFrnNumero(itemNotaRecebimento.getNotaRecebimento().getDocumentoFiscalEntrada().getFornecedor().getNumero());
		idItemPropostaFornecedor.setPfrLctNumero(licitacao.getNumero());
		idItemPropostaFornecedor.setNumero(numeroItem); // Número item
		itemPropostaFornecedor.setId(idItemPropostaFornecedor);
		
		itemPropostaFornecedor.setPropostaFornecedor(itemNotaRecebimento.getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor());
		itemPropostaFornecedor.setMarcaComercial(marcaComercial); // Marca Comercial
		itemPropostaFornecedor.setItemLicitacao(itemLicitacao);
		itemPropostaFornecedor.setUnidadeMedida(itemNotaRecebimento.getUnidadeMedida());
		itemPropostaFornecedor.setMoeda(this.getMoedaNacional());
		itemPropostaFornecedor.setQuantidade(itemNotaRecebimento.getQuantidade().longValue());
		itemPropostaFornecedor.setIndEscolhido(true);
		itemPropostaFornecedor.setIndAutorizUsr(false);
		itemPropostaFornecedor.setIndAnalisadoPt(false);
		itemPropostaFornecedor.setIndExclusao(false);
		itemPropostaFornecedor.setIndComDesconto(false);
		itemPropostaFornecedor.setIndNacional(true);
		itemPropostaFornecedor.setIndDesclassificado(false);
		itemPropostaFornecedor.setFatorConversao(fatorConversao); // Fator Conversão
	
		this.getComprasFacade().inserirScoItemPropostaFornecedorDAO(itemPropostaFornecedor);
		
		/*
		 * SCO_ITENS_AUTORIZACAO_FORN: Item da Autorização de Fornecimento
		 */
		final ScoItemAutorizacaoForn itemAutorizacaoFornecimento = new ScoItemAutorizacaoForn();
		itemAutorizacaoFornecimento.setAutorizacoesForn(itemNotaRecebimento.getNotaRecebimento().getAutorizacaoFornecimento());
		
		final ScoItemAutorizacaoFornId idItemAutorizacaoFornecimento = new ScoItemAutorizacaoFornId();
		idItemAutorizacaoFornecimento.setAfnNumero(itemNotaRecebimento.getNotaRecebimento().getAutorizacaoFornecimento().getNumero());
		idItemAutorizacaoFornecimento.setNumero((int)numeroItem); // Número do Item
		itemAutorizacaoFornecimento.setId(idItemAutorizacaoFornecimento);
		
		itemAutorizacaoFornecimento.setUnidadeMedida(itemNotaRecebimento.getUnidadeMedida());
		itemAutorizacaoFornecimento.setMarcaComercial(marcaComercial); // Marca Comercial
		itemAutorizacaoFornecimento.setServidor(this.getServidorSolicitacaoDeCompraAutomatica());
		itemAutorizacaoFornecimento.setItemPropostaFornecedor(itemPropostaFornecedor);
		itemAutorizacaoFornecimento.setQtdeSolicitada(itemNotaRecebimento.getQuantidade());
		itemAutorizacaoFornecimento.setQtdeRecebida(itemNotaRecebimento.getQuantidade());
		itemAutorizacaoFornecimento.setFatorConversao(fatorConversao); // Fator Conversão
		itemAutorizacaoFornecimento.setFatorConversaoForn(fatorConversao); // Fator Conversão
		itemAutorizacaoFornecimento.setValorUnitario((itemNotaRecebimento.getValor() / itemNotaRecebimento.getQuantidade()));
		itemAutorizacaoFornecimento.setIndRecebimento(false);
		itemAutorizacaoFornecimento.setIndExclusao(false);
		itemAutorizacaoFornecimento.setIndEstorno(false);
		itemAutorizacaoFornecimento.setIndConsignado(false);
		itemAutorizacaoFornecimento.setValorEfetivado(itemNotaRecebimento.getValor());
		itemAutorizacaoFornecimento.setPercDesconto((double)0);
		itemAutorizacaoFornecimento.setPercDescontoItem((double)0);
		itemAutorizacaoFornecimento.setPercAcrescimo((double)0);
		itemAutorizacaoFornecimento.setPercAcrescimoItem((double)0);
		itemAutorizacaoFornecimento.setSequenciaAlteracao(0);
		itemAutorizacaoFornecimento.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EF);
		
		this.getComprasFacade().inserirScoItemAutorizacaoForn(itemAutorizacaoFornecimento);
		
		/*
		 * SCO_IAF_JN: Journal de Item da Autorização de Fornecimento
		 */
		
		final ScoItemAutorizacaoFornJn journalAutorizacaoFornecimentoJn = new ScoItemAutorizacaoFornJn();
		journalAutorizacaoFornecimentoJn.setJnUser(this.getServidorSolicitacaoDeCompraAutomatica().getUsuario());
		journalAutorizacaoFornecimentoJn.setJnOperation(DominioOperacoesJournal.INS.toString());
		journalAutorizacaoFornecimentoJn.setJnDateTime(new Date()); 
		journalAutorizacaoFornecimentoJn.setAfnNumero(itemAutorizacaoFornecimento.getId().getAfnNumero());
		journalAutorizacaoFornecimentoJn.setNumero(itemAutorizacaoFornecimento.getId().getNumero());
		journalAutorizacaoFornecimentoJn.setItemPropostaFornecedor(itemPropostaFornecedor);
	//	journalAutorizacaoFornecimentoJn.setIpfPfrFrnNumero(itemPropostaFornecedor.getId().getPfrFrnNumero());
	//	journalAutorizacaoFornecimentoJn.setIpfNumero(itemPropostaFornecedor.getId().getNumero());
		journalAutorizacaoFornecimentoJn.setIndEstorno(itemAutorizacaoFornecimento.getIndEstorno());
		journalAutorizacaoFornecimentoJn.setUnidadeMedida(itemAutorizacaoFornecimento.getUnidadeMedida());
		journalAutorizacaoFornecimentoJn.setQtdeSolicitada(itemAutorizacaoFornecimento.getQtdeSolicitada());
		journalAutorizacaoFornecimentoJn.setQtdeRecebida(itemAutorizacaoFornecimento.getQtdeSolicitada());
		journalAutorizacaoFornecimentoJn.setFatorConversao(itemAutorizacaoFornecimento.getFatorConversao());		
		journalAutorizacaoFornecimentoJn.setValorUnitario(itemAutorizacaoFornecimento.getValorUnitario());
		journalAutorizacaoFornecimentoJn.setIndSituacao(itemAutorizacaoFornecimento.getIndSituacao());
		journalAutorizacaoFornecimentoJn.setIndExclusao(itemAutorizacaoFornecimento.getIndExclusao());
		journalAutorizacaoFornecimentoJn.setSequenciaAlteracao(0);
		journalAutorizacaoFornecimentoJn.setMarcaComercial(itemAutorizacaoFornecimento.getMarcaComercial());
		journalAutorizacaoFornecimentoJn.setPercVarPreco((double)0);
		journalAutorizacaoFornecimentoJn.setQtdeRecebida(itemAutorizacaoFornecimento.getQtdeRecebida());
		journalAutorizacaoFornecimentoJn.setIndRecebimento(itemAutorizacaoFornecimento.getIndRecebimento());
		journalAutorizacaoFornecimentoJn.setValorEfetivado(itemAutorizacaoFornecimento.getValorEfetivado());
		journalAutorizacaoFornecimentoJn.setSequenciaAlteracao(itemAutorizacaoFornecimento.getSequenciaAlteracao());
		
		this.getComprasFacade().inserirScoItemAutorizacaoFornJn(journalAutorizacaoFornecimentoJn);
		
		/*
		 *  SCE_ITEM_NRS: Item da Nota de Recebimento
		 */
		itemNotaRecebimento.setItemAutorizacaoForn(itemAutorizacaoFornecimento);
		
		// Retorna Item de Licitação necessário para geração da Fase de Solicitação
		return itemLicitacao; 
		
	}
	
	
	/**
	 * Valida se o usuario possui um centro de custo de lotacao
	 * @return
	 */
	protected void validaCentroCustoLotacao() throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado.getCentroCustoLotacao() == null ){
			throw new ApplicationBusinessException(GerarSolicitacaoCompraAutomaticaRNExceptionCode.USUARIO_NAO_TEM_CENTRO_DE_CUSTO_DE_LOTACAO);
		}
	}
	
	/*
	 * Métodos utilitários
	 */

	/**
	 * Obtém o Servidor Padrão para geração de Solicitação de Compra Automática
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	protected RapServidores getServidorSolicitacaoDeCompraAutomatica() throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// TODO:  DEVEMOS GERAR UM SERVIDOR, POR ENQUANTO UTILIZAMOS O LOGADO
		return servidorLogado;
	}
	
	/**
	 * Obtém a Moeda Nacional
	 * @return
	 */
	protected FcpMoeda getMoedaNacional(){
		return this.getFcpMoedaDAO().obterPorChavePrimaria((short)1); // Código 1 (Real)
	}
	
	/*
	 * Getters, DAOs, etc.
	 */

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getCadastrosBasicosFacade() {
		return this.comprasCadastrosBasicosFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade(){
		return this.autFornecimentoFacade;
	}

	protected IOrcamentoFacade getOrcamentoFacade() {
		return this.orcamentoFacade;
	}
	
	protected FcpMoedaDAO getFcpMoedaDAO(){
		return fcpMoedaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
