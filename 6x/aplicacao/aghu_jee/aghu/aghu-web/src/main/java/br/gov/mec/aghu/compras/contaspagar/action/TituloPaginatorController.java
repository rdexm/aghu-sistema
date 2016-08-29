package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.action.RelatorioProgramacaoDePagamentosController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.estoque.vo.SceBoletimOcorrenciasVO;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

public class TituloPaginatorController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}

	private static final long serialVersionUID = 4081059531367120707L;

	// Cores das colunas
	private static final String COR_AMARELA = "background-color:#FFFF66;";
	private static final String COR_VERMELHA = "background-color:#F08080;";
	private static final String COR_VERDE = "background-color:#00CC66;";
	private static final String COR_BRANCA = "background-color:white;";

	// Quebra de linha para tool tips
	private static final String BREAK_LINE = "<br/>";

	@EJB
	private IContasPagarFacade contasPagarFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private RelatorioProgramacaoDePagamentosController relatorio;
	// Controla o rich:simpleTogglePanel
	private boolean sliderAberto = true;

	// Lista de títulos
	private List<TituloVO> listaTitulos;
	
	@Inject @Paginator("br.gov.mec.aghu.compras.contaspagar.action.TitulosDataModel")
	private DynamicDataModel<TituloVO> dataModelTitulo;
	
	@Inject @Paginator("br.gov.mec.aghu.compras.contaspagar.action.PagamentosDataModel")
	private DynamicDataModel<TituloProgramadoVO> dataModelPagamentos;
	
	// Lista pagamentos programados
	private List<TituloProgramadoVO> listaPagamentos;
	
	// Filtro da pesquisa com os valores padrão
	private FiltroConsultaTitulosVO filtro = new FiltroConsultaTitulosVO(DominioSituacaoTitulo.APG, DominioSimNao.N, DominioSimNao.N, DominioSimNao.N);

	// Campo data de pagamento com data atual
	private Date dataPagamento = Calendar.getInstance().getTime();

	private boolean carregarDataTable = true;
	
	private boolean exibeColunaDataProgramada = true;

	private boolean exibeColunaSituacao = true;
	
	/**
	 * Chamado após renderizar a tela
	 */
	public void iniciar() {
		// Verifica se a página está não está dando submit nela mesma
		prepararFiltro(); // Prepara e valida filtro
		// Pesquisa ao abrir a tela
		pesquisarTitulos();
		dataModelPagamentos.reiniciarPaginator();
	}

	/**
	 * Prepara e valida filtro
	 * 
	 * @return
	 */
	private boolean prepararFiltro() {
		if (this.filtro.getComplementoAF() == null && this.filtro.getDataFinal() == null && this.filtro.getDataGeracaoNR() == null && this.filtro.getDataInicial() == null && this.filtro.getEstornado() == null && this.filtro.getFcpTipoDocPagamento() == null && this.filtro.getFsoNaturezaDespesa() == null && this.filtro.getFsoVerbaGestao() == null && this.filtro.getInss() == null
				&& this.filtro.getNotaFiscal() == null && this.filtro.getNotaRecebimento() == null && this.filtro.getNumeroAF() == null && this.filtro.getNumeroDocumento() == null && this.filtro.getProgramado() == null && this.filtro.getScoFornecedor() == null && this.filtro.getSituacaoTitulo() == null && this.filtro.getTipoPagamento() == null
				&& this.filtro.getTitulo() == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_FAVOR_INFORMAR_FILTRO");
			carregarDataTable = false;
			return false;
		} else {
			carregarDataTable = true;
			exibeColunaDataProgramada = this.isExibirColunaDataProgramada();
			exibeColunaSituacao = this.isTipoSituacaoDiferentePagar();
			return true;
		}
	}

	/**
	 * Pesquisa títulos
	 */
	public void pesquisarTitulos() {
		if (!prepararFiltro()) {
			return;
		}
		
		dataModelTitulo.reiniciarPaginator();
		dataModelPagamentos.reiniciarPaginator();
		sliderAberto = false;
		listaPagamentos = null;
		listaTitulos = null;
	}
	
	/**	
	 * Limpar campos da pesquisa
	 */
	public void limpar() {
		this.filtro = new FiltroConsultaTitulosVO(DominioSituacaoTitulo.APG, DominioSimNao.N, DominioSimNao.N, DominioSimNao.N);
		this.dataPagamento = Calendar.getInstance().getTime();
		pesquisarTitulos(); // Refaz a pesquisa inicial
		listaPagamentos = null;
		listaTitulos = null;
	}

	/**
	 * Controla a exibição da coluna Data Programada
	 * 
	 * @return
	 */
	public boolean isExibirColunaDataProgramada() {
		return DominioSimNao.N.equals(this.filtro.getProgramado()) ? false : true;
	}

	/**
	 * Controla a exibição das colunas Situação, Número Documento, Data
	 * Pagamento e Valor Pagamento
	 * 
	 * @return
	 */
	public boolean isTipoSituacaoDiferentePagar() {
		return !DominioSituacaoTitulo.APG.equals(this.filtro.getSituacaoTitulo()) ? true : false;
	}

	/**
	 * Trunca descrições grandes nas descrições das listagens
	 * 
	 * @param descricao
	 * @return
	 */
	public String truncarDescricao(String descricao) {
		int limite = 64;
		if (StringUtils.length(descricao) >= limite) {
			return StringUtils.substring(descricao, 0, limite).concat("...");
		}
		return descricao;
	}
	
	public void apresentarExcecaoNegocioController(BaseException e) {		
		super.apresentarExcecaoNegocio(e);
	}

	/*
	 * Métodos para preencher a cor de fundo
	 */

	/**
	 * Preenche cor de fundo do campo situação
	 * 
	 * @param situacao
	 * @param tabelaTitulo
	 *            Tabela título ou tabela de pagamentos
	 * @return
	 */
	public String colorirCampoSituacao(DominioSituacaoTitulo situacao, boolean tabelaTitulo) {
		if (tabelaTitulo && DominioSituacaoTitulo.APG.equals(situacao)) {
			return COR_AMARELA; // A pagar
		} else if (DominioSituacaoTitulo.BLQ.equals(situacao) ||
				   DominioSituacaoTitulo.PND.equals(situacao)) {
			return COR_VERMELHA; // Bloqueado
		} else if (tabelaTitulo && DominioSituacaoTitulo.PG.equals(situacao)) {
			return COR_VERDE; // Pago
		}  
		return COR_BRANCA;
	}

	/**
	 * Preenche cor de fundo do campo valor do pagamento
	 * 
	 * @param inss
	 * @param multa
	 * @return
	 */
	public String colorirCampoValorPagamento(String inss, String multa) {
		if ("SIM".equalsIgnoreCase(inss) || "SIM".equalsIgnoreCase(multa)) {
			return COR_VERMELHA;
		}
		return COR_BRANCA;
	}

	/**
	 * Preenche cor de fundo do campo fornecedor do pagamento
	 * 
	 * @param situacao
	 * @param fornecedor
	 * @return
	 */
	public String colorirCampoFornecedor(DominioSituacaoTitulo situacao, ScoFornecedor fornecedor) {

		// Titulos à pagar ou bloqueados
		if (DominioSituacaoTitulo.APG.equals(situacao) || DominioSituacaoTitulo.BLQ.equals(situacao)) {
			/*
			 * Fornecedor CNPJ se Dt Val FGTS ou Dt Val INSS ou Dt Val Rec Fed
			 * for menor que a data atual ou estiver vazia colorir o fundo
			 * VERMELHA
			 */
			if (fornecedor != null) {
				final Date dataAtual = new Date();
				final Date dataRecFed = fornecedor.getDtValidadeRecFed();

				// É CNPJ
				if (fornecedor.getCgc() != null) {

					final Date dataFgts = fornecedor.getDtValidadeFgts();
					final Date dataInss = fornecedor.getDtValidadeInss();

					final boolean isDataVazia = dataFgts == null || dataInss == null || dataRecFed == null;
					final boolean isDataInferior = DateUtil.validaDataMenor(dataFgts, dataAtual) || DateUtil.validaDataMenor(dataInss, dataAtual) || DateUtil.validaDataMenor(dataRecFed, dataAtual);

					if (isDataVazia || isDataInferior) {
						return COR_VERMELHA;
					}

				} else if (fornecedor.getCpf() != null && (dataRecFed == null || DateUtil.validaDataMenor(dataRecFed, dataAtual))) {
					// É CPF
					return COR_VERMELHA;
				}
			}
		}

		return COR_BRANCA;
	}

	/*
	 * Hints do componente rich:toolTip
	 */

	/**
	 * Hint da coluna NR (Nota de Recebimento)
	 * 
	 * @param item
	 * @return
	 */
	public String getHintNotaRecebimento(TituloVO item) {
		StringBuilder buffer = new StringBuilder(128);
		buffer.append("Dt Geração: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getNotaRecebimentoDataGeracao(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Fonte: ").append(transformarNuloVazio(item.getFonteRecursoFinanDescricao())).append(BREAK_LINE)
		.append("Vinculação: ").append(transformarNuloVazio(item.getLiquidacaoSiafiVinculacaoPagamento())).append(BREAK_LINE)
		.append("Empenho: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getNotaRecebimentoDataGeracao(), DateConstants.DATE_PATTERN_YYYY))).append(" NE - ").append(transformarNuloVazio(item.getLiquidacaoSiafiNumeroEmpenho())).append(BREAK_LINE);
		return buffer.toString();
	}

	/*
	 * Verifica se a situação do título é Bloqueada
	 */
	public boolean verificarTituloBloqueado(TituloVO item) {
		return DominioSituacaoTitulo.BLQ.equals(item.getTituloIndSituacao());
	}

	/**
	 * Hint da coluna Situação quando Bloqueada
	 * 
	 * @param item
	 * @return
	 */
	public String getHintSituacaoBloqueada(TituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		SceBoletimOcorrenciasVO boletim = this.contasPagarFacade.obterDadosBoletimTitulo(item.getNotaRecebimentoNumero());
		buffer.append("BO: ").append(boletim != null ? boletim.getSeq() : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Valor: ").append(boletim != null ? AghuNumberFormat.formatarNumeroMoeda(boletim.getValor()) : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Situação: ").append(boletim != null ? boletim.getSituacao().getDescricao() : StringUtils.EMPTY).append(BREAK_LINE)
		.append("Motivo: ").append(boletim != null ? transformarNuloVazio(boletim.getDescricao()) : StringUtils.EMPTY).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Hint da coluna Título
	 * 
	 * @param item
	 * @return
	 */
	public String getHintTitulo(TituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append("NF: ").append(transformarNuloVazio(item.getDocumentoFiscalEntradaNumero())).append(BREAK_LINE)
		.append("Verba de Gestão: ").append(transformarNuloVazio(item.getVerbaGestaoDescricao())).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Hint da coluna Fornecedor Razão Social
	 * 
	 * @param item
	 * @return
	 */
	public String getHintFornecedor(TituloVO item) {
		StringBuilder buffer = new StringBuilder(128);
		buffer.append("Código: ").append(transformarNuloVazio(item.getFornecedorNumero())).append(BREAK_LINE);
		String cnpfCpf = null;
		if (item.getFornecedorCgc() != null) {
			cnpfCpf = CoreUtil.formatarCNPJ(item.getFornecedorCgc());
		} else if (item.getFornecedorCpf() != null) {
			cnpfCpf = CoreUtil.formataCPF(item.getFornecedorCpf());
		}
		buffer.append("CNPJ/CPF: ").append(transformarNuloVazio(cnpfCpf)).append(BREAK_LINE)
		.append("Razão Social: ").append(transformarNuloVazio(item.getFornecedorRazaoSocial())).append(BREAK_LINE)
		.append("Dt Val FGTS: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getFornecedorDataValidadeFgts(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Dt Val INSS: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getFornecedorDataValidadeInss(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Dt Val Rec Fed: ").append(transformarNuloVazio(DateUtil.obterDataFormatada(item.getFornecedorDataValidadeRecFed(), DateConstants.DATE_PATTERN_DDMMYYYY))).append(BREAK_LINE)
		.append("Telefone: ").append(transformarNuloVazio(item.getFornecedorTelefone())).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Hint da coluna Natureza
	 * 
	 * @param item
	 * @return
	 */
	public String getHintNatureza(TituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append("Nro AF: ").append(transformarNuloVazio(item.getAutorizacaoFornNaturezaPropostaFornecedorLctNumero())).append(BREAK_LINE)
		.append("Tipo: ").append(transformarNuloVazio(item.getTipoMaterial())).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Hint da coluna Número Documento
	 * 
	 * @param item
	 * @return
	 */
	public String getHintNumeroDocumento(TituloVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append("Tipo Doc: ").append(transformarNuloVazio(transformarNuloVazio(item.getTipoDocPagamentoDescricao()))).append(BREAK_LINE)
		.append("Estornado: ").append(transformarNuloVazio(transformarNuloVazio(item.getPagamentoIndEstorno()))).append(BREAK_LINE);
		return buffer.toString();
	}

	/**
	 * Hint da coluna Valor
	 * 
	 * @param item
	 * @return
	 */
	public String getHintValor(TituloVO item) {
		StringBuilder buffer = new StringBuilder(200);
		buffer.append("Valor Título: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(item.getTituloValor()))).append(BREAK_LINE)
		.append("Valor Tributos: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(item.getRetencaoAliquotaValorTributos()))).append(BREAK_LINE)
		.append("Valor Desconto: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(item.getRetencaoAliquotaValorDesconto()))).append(BREAK_LINE)
		.append("Valor Acréscimo: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(item.getPagamentoValorAcrescimo()))).append(BREAK_LINE)
		.append("Valor Multa: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(item.getRetencaoAliquotaValorMulta()))).append(BREAK_LINE)
		.append("Valor DF: ").append(transformarNuloVazio(AghuNumberFormat.formatarNumeroMoeda(1d))).append(BREAK_LINE)
		.append("Banco: ").append(transformarNuloVazio(item.getBancoNomeBanco())).append(BREAK_LINE)
		.append("Agência: ").append(transformarNuloVazio(item.getContaCorrenteFornecedorAgbCodigo())).append(BREAK_LINE)
		.append("Conta: ").append(transformarNuloVazio(item.getContaCorrenteFornecedorContaCorrente())).append(BREAK_LINE)
		.append("Condição: ").append(transformarNuloVazio(item.getFormaPagamentoDescricao())).append(BREAK_LINE)
		.append("Tributos: ").append(BREAK_LINE);
		List<String> listaTributos = this.contasPagarFacade.pesquisarTributosNotaRecebimentoTitulo(item.getNotaRecebimentoNumero());
		for (String tributo : listaTributos) {
			buffer.append(tributo).append(BREAK_LINE);
		}
		return buffer.toString();
	}

	/**
	 * Hint da coluna Fonte na Lista de Pagamentos
	 * 
	 * @param item
	 * @return
	 */
	public String getHintFonte(TituloProgramadoVO item) {
		StringBuilder buffer = new StringBuilder(64);
		buffer.append(transformarNuloVazio(item.getFonteRecursoFinanDescricao()));
		return buffer.toString();
	}

	/**
	 * Transforma nulo em vazio
	 * 
	 * @param o
	 * @return
	 */
	private String transformarNuloVazio(Object o) {
		return o == null ? StringUtils.EMPTY : o.toString().trim();
	}

	/*
	 * Botão Adicionar à Lista
	 */

	/**
	 * Bloqueia botão Adicionar à lista
	 * 
	 * @return
	 */
	public boolean isDesabilitarBotaoAdicionarTituloLista() {
		if (this.listaTitulos != null && !this.listaTitulos.isEmpty()) {
			for (TituloVO tituloVO : this.listaTitulos) {
				if (DominioSituacaoTitulo.PG.equals(tituloVO.getTituloIndSituacao())) {
					return true;
				}
			}
			if (listaTitulos.size() == 0) {
				return true;
			}
		} else {
			return true;
		}

		return false;
	}

	
	public void verificarStatusBotaoAdicionar() {
		isDesabilitarBotaoAdicionarTituloLista();
	}
	
	/**
	 * Ação do botão adicionar à lista
	 */
	public void adicionarLista() {
		try {
			for (TituloVO tituloVO : listaTitulos) {
				  this.contasPagarFacade.alterarTituloPagamento(tituloVO.getTituloSeq(), this.dataPagamento, true);
			}
			dataModelPagamentos.reiniciarPaginator();
			pesquisarTitulos();

			// TODO PESQUISA SIMULADA
			// this.listaPagamentos =
			// newTituloSimulacaoController().pesquisaSimuladaProgramado();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisarPagamentos() {
		dataModelPagamentos.reiniciarPaginator();
		listaPagamentos = null;
	}

	/*
	 * Botões da Lista de Pagamentos Programados
	 */

	/**
	 * Verifica se há algum pagamento programado selecionado
	 * 
	 * @return
	 */
	public boolean isPagamentoSelecionado() {
		if(listaPagamentos != null && !listaPagamentos.isEmpty()) {
				return true;
		}
		return false;
	}

	/**
	 * Remove da Lista de Pagamentos Programados
	 */
	public void removerLista() {
		try {
			for (TituloProgramadoVO tituloProgramadoVO : this.listaPagamentos) {
				this.contasPagarFacade.alterarTituloPagamento(tituloProgramadoVO.getTituloSeq(), null, false);
			}
			pesquisarTitulos();
			dataModelPagamentos.reiniciarPaginator();
			listaPagamentos = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Imprimir Relatório Programação de Pagamentos
	 */
	/**
	 * Imprimir Relatório Programação de Pagamentos
	 */
	public void imprimirLista() {
		
		List<TituloProgramadoVO> titulosPrgs = null;
		
		try {
			titulosPrgs = contasPagarFacade.obterListaPagamentosProgramados(
					getDataPagamento(), null, null, null, false);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.relatorio.downloadArquivo(titulosPrgs, this.dataPagamento);
	}

	/*
	 * Métodos para SuggestionBox
	 */

	public List<ScoFornecedor> pesquisarFornecedores(final String pesquisa) {
		return this.returnSGWithCount(comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, true),pesquisarFornecedoresCount(pesquisa));
	}

	public Long pesquisarFornecedoresCount(final String strPesquisa) {
		return comprasFacade.listarFornecedoresAtivosCount(strPesquisa);
	}

	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesas(final String strPesquisa) {
		return this.returnSGWithCount(contasPagarFacade.listarNaturezaDespesaPorSituacaoSuggestionBox((String) strPesquisa),pesquisarNaturezaDespesasCount(strPesquisa));
	}

	/**
	 * CNPJ/CPF formatado
	 * 
	 * @param item
	 * @return
	 */
	public static String getCpfCnpjFormatado(ScoFornecedor item) {
		if (item.getCpf() == null) {
			if (item.getCgc() == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(item.getCgc());
		}
		return CoreUtil.formataCPF(item.getCpf());
	}

	public Long pesquisarNaturezaDespesasCount(final String strPesquisa) {
		return this.contasPagarFacade.countListarNaturezaDespesaPorSituacao((String) strPesquisa);
	}

	public List<FsoVerbaGestao> pesquisarVerbaGestao(final String strPesquisa) {
		return this.returnSGWithCount(this.contasPagarFacade.listarVerbaGestaoPorSituacaoSuggestionBox((String) strPesquisa),pesquisarVerbaGestaoCount(strPesquisa));
	}

	public Long pesquisarVerbaGestaoCount(final String strPesquisa) {
		return this.contasPagarFacade.countListarVerbaGestaoPorSituacao((String) strPesquisa);
	}

	public List<FcpTipoDocumentoPagamento> pesquisarTipoDocumento(final String strPesquisa) {
		return this.returnSGWithCount(this.contasPagarFacade.listarDocumentosPorSituacaoSuggestionBox((String) strPesquisa),pesquisarTipoDocumentoCount(strPesquisa));
	}

	public Long pesquisarTipoDocumentoCount(final String strPesquisa) {
		return this.contasPagarFacade.countListarDocumentosPorSituacao((String) strPesquisa);
	}

	/*
	 * Getters/Setters
	 */

	public boolean isSliderAberto() {
		return sliderAberto;
	}

	public void setSliderAberto(boolean sliderAberto) {
		this.sliderAberto = sliderAberto;
	}

	public List<TituloVO> getListaTitulos() {
		return listaTitulos;
	}
	
	public List<TituloProgramadoVO> getListaPagamentos() {
		return listaPagamentos;
	}

	public FiltroConsultaTitulosVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroConsultaTitulosVO filtro) {
		this.filtro = filtro;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public DynamicDataModel<TituloVO> getDataModelTitulo() {
		return dataModelTitulo;
	}

	public void setDataModelTitulo(DynamicDataModel<TituloVO> dataModelTitulo) {
		this.dataModelTitulo = dataModelTitulo;
	}

	public void setListaTitulos(List<TituloVO> listaTitulos) {
		this.listaTitulos = listaTitulos;
	}

	public void setListaPagamentos(List<TituloProgramadoVO> listaPagamentos) {
		this.listaPagamentos = listaPagamentos;
	}

	public DynamicDataModel<TituloProgramadoVO> getDataModelPagamentos() {
		return dataModelPagamentos;
	}

	public void setDataModelPagamentos(DynamicDataModel<TituloProgramadoVO> dataModelPagamentos) {
		this.dataModelPagamentos = dataModelPagamentos;
	}

	public boolean isCarregarDataTable() {
		return carregarDataTable;
	}

	public void setCarregarDataTable(boolean carregarDataTable) {
		this.carregarDataTable = carregarDataTable;
	}

	public boolean isExibeColunaDataProgramada() {
		return exibeColunaDataProgramada;
	}

	public void setExibeColunaDataProgramada(boolean exibeColunaDataProgramada) {
		this.exibeColunaDataProgramada = exibeColunaDataProgramada;
	}

	public boolean isExibeColunaSituacao() {
		return exibeColunaSituacao;
	}

	public void setExibeColunaSituacao(boolean exibeColunaSituacao) {
		this.exibeColunaSituacao = exibeColunaSituacao;
	}

}