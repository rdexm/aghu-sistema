package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoItemNotaRecebimento;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirNotaRecebimentoController;
import br.gov.mec.aghu.estoque.vo.GerarItemNotaRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.GerarNotaRecebimentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VSceItemNotaRecebimentoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class GerarNotaRecebimentoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO_SEM_AF = "estoque-gerarNotaRecebimentoSemAf";
	private static final String PAGE_ESTOQUE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";
	private static final String PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO = "estoque-gerarNotaRecebimento";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538959294285952902L;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@Inject
	private ImprimirNotaRecebimentoController imprimirNotaRecebimentoController;

	@EJB
	private IComprasFacade comprasFacade;

	// Parâmetros da conversação
	private Integer seq;
	private boolean modoSomenteLeitura; // Modo somente leitura ocorre após a
										// geração de NR com sucesso, ambos
										// campos da tela não permitem a edição.
	private String voltarPara;
	private Integer numeroFornecedorDocumentoFiscalEntrada;

	// Variáveis que representam os campos do XHTML
	private Date dataGeracao;
	private GerarNotaRecebimentoVO gerarNotaRecebimentoVO;
	private ScoAutorizacaoForn autorizacaoFornecimento;
	private Integer seqDocumentoFiscalEntrada;
	private Long numeroDocumentoFiscalEntrada;
	private String serieDocumentoFiscalEntrada;
	private SceDocumentoFiscalEntrada documentoFiscalEntrada; // Nota fiscal de
																// entrada!

	// Variáveis para resultados da pesquisa de item de nota
	private List<GerarItemNotaRecebimentoVO> listaItemNotaRecebimentoOrigem;
	private List<GerarItemNotaRecebimentoVO> listaItemNotaRecebimentoDestino;

	// Variáveis para interações na tabela de itens de nota
	private Integer indiceItemNotaRecebimentoOrigemSelecionado; // Determina o
																// indice do
																// item foi
																// selecionado
	private GerarItemNotaRecebimentoVO itemSelecionado; // Determina qual item
														// foi selecionado na
														// lista de item de nota
														// de recebimento
	private Double valorTotalItens; // Valor total dos itens

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public String iniciar() {
	 

	 

		String retorno = ""; // ATENÇÃO: Mesma página
		// Verifica a necessidade de redirecionar para geração de Nota de Recebimendo sem AF (Com Solicitação de Compra Automática)
		if (this.redirecionarGeracaoNotaRecebimentoSemAutorizacaoFornecimento() || mostrarBotaoVoltar()) { 
			retorno = PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO_SEM_AF;
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SISTEMA_COMPRAS_ATIVADO");
			retorno = PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO;
		}

		if (!this.modoSomenteLeitura) {
			// Popula documento fiscal de entrada através do parâmetro de conversão
			this.obterDocumentoFiscalEntradaPorSeq();
		}
		return retorno;
	
	}

	/**
	 * Quando o Módulo de Compras do AGHU estiver desativado o usuário será redirecionado para geração de Nota de Recebimendo sem AF (Com Solicitação de Compra Automática)
	 */
	private Boolean redirecionarGeracaoNotaRecebimentoSemAutorizacaoFornecimento() {
		Boolean retorno = false;
		try {

			final AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);

			// O Módulo de Compras do AGHU está INATIVO quando valor numérico do parâmetro for igual a ZERO
			if (parametro != null && BigDecimal.ZERO.equals(parametro.getVlrNumerico())) {
				retorno = true;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;

	}

	/**
	 * Obtem lista para suggestionBox de Autorização de Fornecimento
	 * 
	 * @param param
	 * @return
	 */
	public List<GerarNotaRecebimentoVO> obterAutorizacoesFornecimento(Object param) {
		return this.estoqueFacade.pesquisarAutorizacoesFornecimentoPorSeqDescricao(param);
	}

	/**
	 * Quando um documento fiscal de entrada é selecionado através da tela "gerar nota de recebimento", a alteração do fornecedor deve advertir o usuário quando o mesmo for
	 * diferente do fornecedor original da autorização de fornecimento.
	 */
	public void validarFornecedorGerarNotaRecebimento() {
		if (this.gerarNotaRecebimentoVO != null && this.documentoFiscalEntrada != null) {
			ScoFornecedor fornecedor = documentoFiscalEntrada.getFornecedor();
			if (fornecedor != null && !fornecedor.getNumero().equals(this.numeroFornecedorDocumentoFiscalEntrada)) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_FORNECEDOR_ORIGINAL_GERAR_NR_MODIFICADO");
			}
		}
	}

	public Boolean mostrarBotaoVoltar() {
		return (this.voltarPara != null &&  this.voltarPara.equalsIgnoreCase("/compras/autfornecimento/receberMaterialServico.seam"));
	}
	
	/**
	 * Resta a tela para geração uma Nova Nota de Recebimento
	 */
	public void gerarNovaNotaRecebimento() {
		this.limpar();
		this.seq = null;
		this.modoSomenteLeitura = false;
		this.dataGeracao = null;
		this.iniciar();
	}

	/**
	 * Obtem o documento fiscal de entrada quando o parâmetro seqDocumentoFiscalEntrada foi preenchido
	 */
	public void obterDocumentoFiscalEntradaPorSeq() {

		ScoFornecedor fornecedor = null;
		// Caso o fornecedor venha de uma conversação
		if (this.numeroFornecedorDocumentoFiscalEntrada != null) {
			fornecedor = this.comprasFacade.obterFornecedorPorNumero(this.numeroFornecedorDocumentoFiscalEntrada);
		} else {

			// Esta validação é importante
			if (this.gerarNotaRecebimentoVO == null) {
				return;
			}

			// Caso o fornecedor venha da SGBOX de autorização de fornecimento
			fornecedor = this.gerarNotaRecebimentoVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor();
		}

		// / Popula documento fiscal de entrada
		if (this.seqDocumentoFiscalEntrada != null && fornecedor != null) {
			// Obtem documento fiscal de entrada atraves do seq/id
			this.documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorSeqFornecedor(this.seqDocumentoFiscalEntrada, fornecedor.getNumero());
			if (this.documentoFiscalEntrada != null) {
				this.numeroDocumentoFiscalEntrada = this.documentoFiscalEntrada.getNumero();
				this.serieDocumentoFiscalEntrada = this.documentoFiscalEntrada.getSerie();
				// this.documentoFiscalEntrada.setFornecedor(fornecedor);
			}

			// Esta validação só ocorre quando o fornecedor é selecionado
			// através da SGBOX de autorização de fornecimento
			if (this.numeroFornecedorDocumentoFiscalEntrada == null) {
				this.validarDocumentoFiscalEntrada();
			}

		}

	}

	/**
	 * Obtem o documento fiscal de entrada quando o número e série do documento fiscal estiverem preenchidos
	 */
	public void obterDocumentoFiscalEntradaPorNumeroSerie() {

		if (this.numeroDocumentoFiscalEntrada != null && StringUtils.isNotEmpty(this.serieDocumentoFiscalEntrada)) {

			final ScoFornecedor fornecedor = this.gerarNotaRecebimentoVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor();

			if (this.numeroDocumentoFiscalEntrada != null && this.serieDocumentoFiscalEntrada != null && fornecedor != null) {
				this.documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(this.numeroDocumentoFiscalEntrada, this.serieDocumentoFiscalEntrada,
						fornecedor.getNumero());
				this.validarDocumentoFiscalEntrada();

				if (this.documentoFiscalEntrada == null) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_ERRO_NENHUM_DOCUMENTO_FISCAL_ENTRADA_ENCONTRADO");
				}
			}
		}

	}

	/**
	 * Valida se o documento fiscal de entrada selecionado não está em uso
	 */
	public void validarDocumentoFiscalEntrada() {

		if (this.documentoFiscalEntrada != null) {

			// Verifica se o documento fiscal de entrada não está sendo
			// utilizado por outra nota de recebimento
			List<SceNotaRecebimento> listaNotaRecebimentoUtilizadas = this.estoqueFacade.pesquisarNotaRecebimentoPorDocumentoFiscalEntrada(this.documentoFiscalEntrada);
			if (listaNotaRecebimentoUtilizadas != null && !listaNotaRecebimentoUtilizadas.isEmpty()) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_GERAR_NR_DFE_UTILIZADO_NR");
			}

			// Verifica se o documento fiscal de entrada possuí o mesmo
			// fornecedor da autorização de fornecimento
			final ScoFornecedor fornecedor = this.gerarNotaRecebimentoVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor();
			if (!this.documentoFiscalEntrada.getFornecedor().getNumero().equals(fornecedor.getNumero())) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_GERAR_NR_DFE_FORNECEDOR_INVALIDO");
				this.limparNotaFiscalEntrada();
			}
		}

	}

	/**
	 * Confirma a nota fiscal de entrada
	 * 
	 * @return
	 */
	public String selecionarNotaFiscalEntrada() {
		return PAGE_ESTOQUE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA;
	}

	/**
	 * Limpa a nota fiscal de entrada
	 */
	public void limparNotaFiscalEntrada() {
		this.documentoFiscalEntrada = null;
		this.seqDocumentoFiscalEntrada = null;
		this.numeroFornecedorDocumentoFiscalEntrada = null;
		this.numeroDocumentoFiscalEntrada = null;
		this.serieDocumentoFiscalEntrada = null;
	}

	/**
	 * Impressão Direta da Nota de Recebimento
	 * 
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void imprimirNotaRecebimento(Integer numeroNotaRecebimento) throws BaseException, JRException, SystemException, IOException {
		this.imprimirNotaRecebimentoController.setNumNotaRec(numeroNotaRecebimento);
		this.imprimirNotaRecebimentoController.setDuasVias(DominioSimNao.N);
		this.imprimirNotaRecebimentoController.setConsiderarNotaEmpenho(true);
		this.imprimirNotaRecebimentoController.directPrint();
	}

	/**
	 * Popula a lista de item nota recebimento de origem quando a autorização de fornecimento estiver populada
	 */
	public void pesquisarListaItemNotaRecebimentoOrigem() {

		if (this.gerarNotaRecebimentoVO != null) {

			this.autorizacaoFornecimento = gerarNotaRecebimentoVO.getAutorizacaoForn();

			final Integer afnNumero = this.autorizacaoFornecimento.getNumero();

			List<VSceItemNotaRecebimentoAutorizacaoFornecimento> listaResultadoView = this.estoqueFacade.pesquisarMaterialPorAutorizacaoFornecimentoNaoEfetivadaOuParcialmenteAtendida(afnNumero);

			if (listaResultadoView != null && !listaResultadoView.isEmpty()) {

				// Instancia a lista de origem
				this.listaItemNotaRecebimentoOrigem = new LinkedList<GerarItemNotaRecebimentoVO>();

				for (VSceItemNotaRecebimentoAutorizacaoFornecimento item : listaResultadoView) {

					// Instancia de vo
					final GerarItemNotaRecebimentoVO vo = new GerarItemNotaRecebimentoVO();

					vo.setAfnNumero(item.getId().getNumeroAutorizacaoFornecimento());
					vo.setCodigo(item.getId().getCodigoMaterialServico());
					vo.setFatorConversao(item.getId().getFatorConversao());
					vo.setMarcaNome(item.getId().getMarcaNome());
					vo.setNome(item.getId().getNome());
					vo.setNumero(item.getId().getNumero());
					vo.setQtdeRecebida(item.getId().getQtdeRecebida());
					vo.setQtdeSolicitada(item.getId().getQtdeSolicitada());
					vo.setTipo(item.getId().getTipo());
					vo.setUnidSc(item.getId().getUnidadeMedidaSolicitacaoCompras());
					vo.setValorUnitIaf(item.getId().getValorUnitarioItemAutorizacaoFornecimento());
					vo.setUnidadeMedidaMaterialServico(item.getId().getUnidadeMedidaMaterialServico());
					vo.setDescrMatSrv(item.getId().getDescricaoMaterialServico());

					this.listaItemNotaRecebimentoOrigem.add(vo);

				}

				// Caso a lista de origem contenha dados a lista de destino será
				// instanciada
				if (!listaItemNotaRecebimentoOrigem.isEmpty()) {
					this.listaItemNotaRecebimentoDestino = new LinkedList<GerarItemNotaRecebimentoVO>();
				}

			}

		} else {
			// Quando a autorização de fornecimento estiver nula: Ambas as
			// listas de item nota de recebimento serão limpas
			this.listaItemNotaRecebimentoOrigem = null;
			this.listaItemNotaRecebimentoDestino = null;
		}

		this.validarDocumentoFiscalEntrada();

	}

	/**
	 * Move todos itens de nota de recebimento da lista de origem para lista de destino
	 */
	public void moverTodosItensNotaRecebimentoListaDestino() {

		if (this.listaItemNotaRecebimentoOrigem != null && !this.listaItemNotaRecebimentoOrigem.isEmpty()) {

			// Adiciona todos itens da lista de origem na lista de destino
			this.listaItemNotaRecebimentoDestino.addAll(this.listaItemNotaRecebimentoOrigem);

			// Limpa item da lista de destino
			this.listaItemNotaRecebimentoOrigem.clear();

			// Ordena lista de destino
			this.ordenarListaItemNotaRecebimento(this.listaItemNotaRecebimentoDestino);

			// Calcula todos os itens
			if (this.listaItemNotaRecebimentoDestino != null && !this.listaItemNotaRecebimentoDestino.isEmpty()) {
				for (GerarItemNotaRecebimentoVO item : this.listaItemNotaRecebimentoDestino) {
					item.setQuantidadeReceber(this.calcularQuantidadeReceber(item));
					// Cálculos que dizem respeito ao item de nota de
					// recebimento para os atributos: quantidade convertida
					// (quantidade), unidade e valor
					item.setQuantidadeConvertida(this.calcularQuantidadeConvertida(item));
					item.setValor(BigDecimal.valueOf(this.calcularValor(item)));
				}
			}

			// Seleciona automaticamente o primeiro item da lista de destino
			this.selecionarLinhaTabelaItemNota(this.listaItemNotaRecebimentoDestino.get(0));

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_MOVER_TODOS_ITENS_NOTA_LISTA_DESTINO");
		}

	}

	/**
	 * Move todos itens de nota de recebimento da lista de origem para lista de destino
	 */
	public void removerTodosListaDestino() {
		this.listaItemNotaRecebimentoDestino.clear();
		this.pesquisarListaItemNotaRecebimentoOrigem();
		this.itemSelecionado = new GerarItemNotaRecebimentoVO();
		this.valorTotalItens = null;
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_REMOVER_TODOS_ITENS_NOTA_LISTA_DESTINO");
	}

	/**
	 * Move um item de nota da lista de origem para lista de destino, REMOVENDO o mesmo da lista de origem
	 * 
	 * @param item
	 */
	public void moverItemNotaRecebimentoListaDestino() {

		if (this.listaItemNotaRecebimentoOrigem != null && !this.listaItemNotaRecebimentoOrigem.isEmpty()) {

			if (this.indiceItemNotaRecebimentoOrigemSelecionado != null) {

				// Obtem item selecionado na lista
				GerarItemNotaRecebimentoVO item = this.listaItemNotaRecebimentoOrigem.get(indiceItemNotaRecebimentoOrigemSelecionado);

				// Testa se a lista de destino contem o item lista de origem
				if (item != null && !this.listaItemNotaRecebimentoDestino.contains(item)) {

					// Adiciona item na lista de destino
					this.listaItemNotaRecebimentoDestino.add(item);

					// Remove item da lista de destino
					this.listaItemNotaRecebimentoOrigem.remove(item);

					// Ordena lista de destino
					this.ordenarListaItemNotaRecebimento(this.listaItemNotaRecebimentoDestino);

					// Seleciona automaticamente o item inserido na lista de
					// destino
					this.selecionarLinhaTabelaItemNota(item);

					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_MOVER_ITEM_NOTA_LISTA_DESTINO");
				}
			}

		}
	}

	/**
	 * Remove um item de nota da lista de destino, MOVENDO o mesmo da lista de origem
	 * 
	 * @param item
	 */
	public void removerItemNotaRecebimentoListaDestino(GerarItemNotaRecebimentoVO item) {

		item.setQuantidadeReceber(null);
		this.listaItemNotaRecebimentoOrigem.add(item);
		this.listaItemNotaRecebimentoDestino.remove(item);
		this.ordenarListaItemNotaRecebimento(this.listaItemNotaRecebimentoOrigem);

		// Para o último item removido o item selecionado, quantidade a receber
		// e valor total dos itens serão limpos
		if (this.listaItemNotaRecebimentoDestino != null && this.listaItemNotaRecebimentoDestino.isEmpty()) {
			this.itemSelecionado = new GerarItemNotaRecebimentoVO();
			this.valorTotalItens = null;
		}

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_REMOVER_ITEM_NOTA_LISTA_DESTINO");
	}

	/**
	 * Ordena uma lista de item de nota de recebimento
	 * 
	 * @param lista
	 */
	private void ordenarListaItemNotaRecebimento(List<GerarItemNotaRecebimentoVO> lista) {

		Collections.sort(lista, new Comparator<GerarItemNotaRecebimentoVO>() {

			@Override
			public int compare(GerarItemNotaRecebimentoVO o1, GerarItemNotaRecebimentoVO o2) {
				return o1.getNumero().compareTo(o2.getNumero());
			}

		});

	}

	public void selecionarLinhaTabelaItemNota(GerarItemNotaRecebimentoVO item) {

		// Determina qual foi o item selecionado na tabela
		this.itemSelecionado = item;

		// Calcula a quantidade a receber (Variável de tela), valor que altera a
		// quantidade convertida e valor do item
		if (item.getQuantidadeReceber() == null) {

			item.setQuantidadeReceber(this.calcularQuantidadeReceber(this.itemSelecionado));

			// Cálculos que dizem respeito ao item de nota de recebimento para
			// os atributos: quantidade convertida (quantidade), unidade e valor
			this.itemSelecionado.setQuantidadeConvertida(this.calcularQuantidadeConvertida(this.itemSelecionado));
			this.itemSelecionado.setValor(BigDecimal.valueOf(this.calcularValor(this.itemSelecionado)));
		}

	}

	public void alterarQuantidadeReceber() {

		if (this.itemSelecionado != null) {

			// Caso a quantidade a receber do item selecionado a receber é nulo
			// o valor padrão para a mesmo será ZERO
			if (this.itemSelecionado.getQuantidadeReceber() == null) {
				this.itemSelecionado.setQuantidadeReceber(0);
			}

			// Cálculos que dizem respeito ao item de nota de recebimento para
			// os atributos: quantidade convertida (quantidade), unidade e valor
			this.itemSelecionado.setQuantidadeConvertida(this.calcularQuantidadeConvertida(this.itemSelecionado));
			this.itemSelecionado.setValor(BigDecimal.valueOf(this.calcularValor(this.itemSelecionado)));
		}
	}

	/**
	 * Calcula a quantidade a receber do item de nota
	 * 
	 * @param item
	 * @return
	 */
	public Integer calcularQuantidadeReceber(final GerarItemNotaRecebimentoVO item) {
		return item.getQtdeSolicitada() - item.getQtdeRecebida();
	}

	/**
	 * Calcula a quantidade convertida do item da nota de recebimento
	 * 
	 * @param item
	 * @return
	 */
	public String obterUnidadeItemSelecionado() {

		if (this.itemSelecionado != null && DominioTipoItemNotaRecebimento.M.equals(this.itemSelecionado.getTipo())) {
			return this.itemSelecionado.getUnidadeMedidaMaterialServico() != null ? this.itemSelecionado.getUnidadeMedidaMaterialServico().getCodigo() : "";
		}

		return "";

	}

	/**
	 * Calcula a quantidade convertida do item da nota de recebimento
	 * 
	 * @param item
	 * @return
	 */
	public Integer calcularQuantidadeConvertida(final GerarItemNotaRecebimentoVO item) {
		return item.getQuantidadeReceber() * item.getFatorConversao();
	}

	/**
	 * Calcula o valor do item da nota de recebimento
	 * 
	 * @param item
	 * @return
	 */
	public Double calcularValor(final GerarItemNotaRecebimentoVO item) {
		return item.getQuantidadeReceber() * item.getValorUnitIaf();
	}

	/**
	 * Calcula o valor total unitário líquido dos itens da tabela
	 * 
	 * @return
	 */
	public Double calcularValorTotalItens() {

		Double valor = null;

		if (this.listaItemNotaRecebimentoDestino != null && !this.listaItemNotaRecebimentoDestino.isEmpty()) {
			valor = new Double(0);
			for (GerarItemNotaRecebimentoVO item : this.listaItemNotaRecebimentoDestino) {

				// Caso o valor do item selecionado a receber é nulo o valor
				// padrão para a mesmo será ZERO
				item.setValor(item.getValor() != null ? item.getValor() : BigDecimal.ZERO);

				// Acumula total
				valor += item.getValor().doubleValue();

			}
		}

		return valor;

	}

	/**
	 * Confirma a operacao de gravar/alterar um material
	 * 
	 * @return
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 */
	public String confirmar() throws JRException, SystemException, IOException {

		String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();

		try {

			if (this.documentoFiscalEntrada == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_ERRO_DOCUMENTO_FISCAL_NAO_INFORMADO");
				return null;
			}

			if (this.itemSelecionado != null && (this.itemSelecionado.getQuantidadeReceber() == null)) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_ERRO_VALOR_RECEBER_ITEM_NAO_INFORMADO");
				return null;
			}

			if (this.itemSelecionado != null && (this.itemSelecionado.getValor() == null)) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_ERRO_VALOR_ITEM_NAO_INFORMADO");
				return null;
			}

			// Instancia SceNotaRecebimento de acordo com o tipo de operação:
			// Inclusão/Edição
			SceNotaRecebimento notaRecebimento = new SceNotaRecebimento();

			// Popula instância de SceNotaRecebimento
			notaRecebimento.setAutorizacaoFornecimento(this.gerarNotaRecebimentoVO.getAutorizacaoForn());
			notaRecebimento.setDocumentoFiscalEntrada(this.documentoFiscalEntrada);

			// Valores default de alguns indicadores
			notaRecebimento.setEstorno(false);
			notaRecebimento.setIndGerado(false);
			notaRecebimento.setDebitoNotaRecebimento(false);
			notaRecebimento.setIndTribLiberada(false);
			notaRecebimento.setIndImpresso(false);
			notaRecebimento.setIndSaldoEmpenho(false);
			// Gera e Persiste Nota de Recebimento
			this.estoqueBeanFacade.gerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);

			// Persiste itens de nota de recebimento através da lista de destino
			for (GerarItemNotaRecebimentoVO vo : this.listaItemNotaRecebimentoDestino) {

				final SceItemNotaRecebimento itemNotaRecebimento = new SceItemNotaRecebimento();

				// Nota recebimento
				itemNotaRecebimento.setNotaRecebimento(notaRecebimento);

				// Autorização de item fornecimento
				final ScoItemAutorizacaoForn itemAutorizacaoForn = this.comprasFacade.obterItemAutorizacaoFornPorId(vo.getAfnNumero(), vo.getNumero());
				itemNotaRecebimento.setItemAutorizacaoForn(itemAutorizacaoForn);

				// Material
				final ScoMaterial material = this.comprasFacade.obterMaterialPorId(vo.getCodigo());
				if (material != null) {
					itemNotaRecebimento.setMaterial(material);
				} else {
					// Serviço
					final ScoServico servico = this.comprasFacade.obterServicoPorId(vo.getCodigo());
					itemNotaRecebimento.setServico(servico);
				}

				// Unidade de medida
				itemNotaRecebimento.setUnidadeMedida(vo.getUnidadeMedidaMaterialServico());

				itemNotaRecebimento.setQuantidade(vo.getQuantidadeConvertida());
				itemNotaRecebimento.setValor(vo.getValor().doubleValue());
				itemNotaRecebimento.setIndDebitoNrIaf(false);

				// Gera e Persiste o Item de Nota de Recebimento
				this.estoqueBeanFacade.gerarItemNotaRecebimento(itemNotaRecebimento, nomeMicrocomputador);

			}

			// Entra AUTOMATICAMENTE em modo somente leitura após um inclusão!
			if (!this.modoSomenteLeitura) {
				this.seq = notaRecebimento.getSeq();
				this.dataGeracao = notaRecebimento.getDtGeracao();
				this.modoSomenteLeitura = true;
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GERAR_NR");

			// Imprime automaticamente a Nota de Recebimento
			this.imprimirNotaRecebimento(notaRecebimento.getSeq());

			this.iniciar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return "confirmado";
	}

	/**
	 * Limpa os filtros do cadastro
	 */
	public void limpar() {

		this.gerarNotaRecebimentoVO = null;
		this.autorizacaoFornecimento = null;
		this.documentoFiscalEntrada = null;

		this.listaItemNotaRecebimentoOrigem = null;
		this.listaItemNotaRecebimentoDestino = null;

		this.indiceItemNotaRecebimentoOrigemSelecionado = null;
		this.itemSelecionado = new GerarItemNotaRecebimentoVO();

		this.valorTotalItens = null;

		this.limparNotaFiscalEntrada();
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		final String retorno = this.voltarPara;
		this.limparParametros();
		return retorno;
	}

	private void limparParametros() {
		this.seq = null;
		this.seqDocumentoFiscalEntrada = null;
		this.numeroFornecedorDocumentoFiscalEntrada = null;
		this.voltarPara = null;
		this.modoSomenteLeitura = false;
		this.gerarNotaRecebimentoVO = null;
	}

	/*
	 * Getters e setters
	 */

	public Double getValorTotalItens() {
		this.valorTotalItens = this.calcularValorTotalItens();
		return valorTotalItens;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public boolean isModoSomenteLeitura() {
		return modoSomenteLeitura;
	}

	public void setModoSomenteLeitura(boolean modoSomenteLeitura) {
		this.modoSomenteLeitura = modoSomenteLeitura;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public ScoAutorizacaoForn getAutorizacaoFornecimento() {
		return autorizacaoFornecimento;
	}

	public void setAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) {
		this.autorizacaoFornecimento = autorizacaoFornecimento;
	}

	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}

	public List<GerarItemNotaRecebimentoVO> getListaItemNotaRecebimentoOrigem() {
		return listaItemNotaRecebimentoOrigem;
	}

	public void setListaItemNotaRecebimentoOrigem(List<GerarItemNotaRecebimentoVO> listaItemNotaRecebimentoOrigem) {
		this.listaItemNotaRecebimentoOrigem = listaItemNotaRecebimentoOrigem;
	}

	public List<GerarItemNotaRecebimentoVO> getListaItemNotaRecebimentoDestino() {
		return listaItemNotaRecebimentoDestino;
	}

	public void setListaItemNotaRecebimentoDestino(List<GerarItemNotaRecebimentoVO> listaItemNotaRecebimentoDestino) {
		this.listaItemNotaRecebimentoDestino = listaItemNotaRecebimentoDestino;
	}

	public Integer getIndiceItemNotaRecebimentoOrigemSelecionado() {
		return indiceItemNotaRecebimentoOrigemSelecionado;
	}

	public void setIndiceItemNotaRecebimentoOrigemSelecionado(Integer indiceItemNotaRecebimentoOrigemSelecionado) {
		this.indiceItemNotaRecebimentoOrigemSelecionado = indiceItemNotaRecebimentoOrigemSelecionado;
	}

	public GerarItemNotaRecebimentoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(GerarItemNotaRecebimentoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getSeqDocumentoFiscalEntrada() {
		return seqDocumentoFiscalEntrada;
	}

	public void setSeqDocumentoFiscalEntrada(Integer seqDocumentoFiscalEntrada) {
		this.seqDocumentoFiscalEntrada = seqDocumentoFiscalEntrada;
	}

	public void setValorTotalItens(Double valorTotalItens) {
		this.valorTotalItens = valorTotalItens;
	}

	public GerarNotaRecebimentoVO getGerarNotaRecebimentoVO() {
		return gerarNotaRecebimentoVO;
	}

	public void setGerarNotaRecebimentoVO(GerarNotaRecebimentoVO gerarNotaRecebimentoVO) {
		this.gerarNotaRecebimentoVO = gerarNotaRecebimentoVO;
	}

	public Long getNumeroDocumentoFiscalEntrada() {
		return numeroDocumentoFiscalEntrada;
	}

	public void setNumeroDocumentoFiscalEntrada(Long numeroDocumentoFiscalEntrada) {
		this.numeroDocumentoFiscalEntrada = numeroDocumentoFiscalEntrada;
	}

	public String getSerieDocumentoFiscalEntrada() {
		return serieDocumentoFiscalEntrada;
	}

	public void setSerieDocumentoFiscalEntrada(String serieDocumentoFiscalEntrada) {
		this.serieDocumentoFiscalEntrada = serieDocumentoFiscalEntrada;
	}

	public Integer getNumeroFornecedorDocumentoFiscalEntrada() {
		return numeroFornecedorDocumentoFiscalEntrada;
	}

	public void setNumeroFornecedorDocumentoFiscalEntrada(Integer numeroFornecedorDocumentoFiscalEntrada) {
		this.numeroFornecedorDocumentoFiscalEntrada = numeroFornecedorDocumentoFiscalEntrada;
	}
}