package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.cadastrosapoio.action.ManterDocumentoFiscalEntradaController;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirNotaRecebimentoController;
import br.gov.mec.aghu.estoque.vo.GerarItemNotaRecebimentoSemAfVO;
import br.gov.mec.aghu.estoque.vo.GerarNotaRecebimentoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class GerarNotaRecebimentoSemAfController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(GerarNotaRecebimentoSemAfController.class);
	
	private static final long serialVersionUID = -4897725372837353931L;
	private static final String PAGE_ESTOQUE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@Inject
	private ImprimirNotaRecebimentoController imprimirNotaRecebimentoController;
	
	@Inject
	private ManterDocumentoFiscalEntradaController manterDocumentoFiscalEntradaController;

	// Parâmetros da conversação
	private Integer seq;
	// Modo somente leitura ocorre após a geração de NR com sucesso, ambos
	// campos da tela não permitem a edição.
	private boolean modoSomenteLeitura = Boolean.FALSE;
	private String voltarPara;
	private Integer numeroFornecedorDocumentoFiscalEntrada;
	private boolean ativarCampoQuantidadeEmbalagemFc;

	// Variáveis que representam os campos do XHTML
	private Date dataGeracao;
	private GerarNotaRecebimentoVO gerarNotaRecebimentoVO;
	private Integer seqDocumentoFiscalEntrada;
	private Long numeroDocumentoFiscalEntrada;
	private String serieDocumentoFiscalEntrada;
	// Nota fiscal de entrada!
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;

	// Variáveis para resultados da pesquisa de item de nota
	private GerarItemNotaRecebimentoSemAfVO novoItemNotaRecebimento;
	private GerarItemNotaRecebimentoSemAfVO antigoItemNotaRecebimento;
	private List<GerarItemNotaRecebimentoSemAfVO> listaItemNotaRecebimento;

	// Variáveis para interações na tabela de itens de nota
	// Determina qual item foi selecionado na lista de item de nota de
	// recebimento
	private GerarItemNotaRecebimentoSemAfVO itemSelecionado;
	private Double valorTotalItens; // Valor total dos itens
	// Determina se o item de nota da lista está sendo alterado
	private boolean alterandoItemNota;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
		if (!this.modoSomenteLeitura) {

			if (this.gerarNotaRecebimentoVO == null) {
				this.gerarNotaRecebimentoVO = new GerarNotaRecebimentoVO();
			}

			if (this.novoItemNotaRecebimento == null) {
				this.novoItemNotaRecebimento = new GerarItemNotaRecebimentoSemAfVO();
			}

			if (this.listaItemNotaRecebimento == null) {
				this.listaItemNotaRecebimento = new LinkedList<GerarItemNotaRecebimentoSemAfVO>();
			}

			if (!this.modoSomenteLeitura) {
				// Popula documento fiscal de entrada através do parâmetro de
				// conversão
				this.obterDocumentoFiscalEntradaPorSeq();
			}
		}

	
	}

	/**
	 * Obtém lista para suggestionBox de Material (Apenas Ativos)
	 * 
	 * @param param
	 * @return
	 */
	public List<ScoMaterial> obterMaterial(String param) {
		return this.comprasFacade.listarScoMateriaisAtiva(param);
	}

	/**
	 * Obtém lista para suggestionBox de Marca Comercial
	 * 
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	public List<ScoMarcaComercial> obterMarcaComercial(String param) throws BaseException {
		return this.comprasFacade.getListaMarcasByNomeOrCodigo(param);
	}

	/**
	 * Obtém lista para suggestionBox de Unidade de Medida
	 * 
	 * @param param
	 * @return
	 */
	public List<ScoUnidadeMedida> obterUnidadeMedida(String param) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorSigla(param, true);
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

	/**
	 * Resta a tela para geração uma Nova Nota de Recebimento
	 */
	public void gerarNovaNotaRecebimento() {
		this.limpar();
		this.seq = null;
		this.alterandoItemNota = false;
		this.modoSomenteLeitura = false;
		this.dataGeracao = null;
		this.limparNotaFiscalEntrada();
		this.iniciar();
	}
	
	public String editarDocumentoFiscalEntrada() {
		manterDocumentoFiscalEntradaController.setSeq(documentoFiscalEntrada.getSeq());
		manterDocumentoFiscalEntradaController.setEmEdicao(true);
		manterDocumentoFiscalEntradaController.setVoltarPara("estoque-gerarNotaRecebimentoSemAf");
		return "estoque-manterDocumentoFiscalEntradaCRUD";
	}

	/**
	 * Obtem o documento fiscal de entrada quando o parâmetro seqDocumentoFiscalEntrada foi preenchido
	 */
	public void obterDocumentoFiscalEntradaPorSeq() {

		ScoFornecedor fornecedor = null;
		// Caso o fornecedor venha de uma conversação
		if (this.numeroFornecedorDocumentoFiscalEntrada != null) {
			fornecedor = this.comprasFacade.obterFornecedorPorNumero(this.numeroFornecedorDocumentoFiscalEntrada);
		}

		// / Popula documento fiscal de entrada
		if (this.seqDocumentoFiscalEntrada != null && fornecedor != null) {

			// Obtém documento fiscal de entrada atraves do seq/id
			this.documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorSeqFornecedor(this.seqDocumentoFiscalEntrada, fornecedor.getNumero());

			if (this.documentoFiscalEntrada != null) {
				this.numeroDocumentoFiscalEntrada = this.documentoFiscalEntrada.getNumero();
				this.serieDocumentoFiscalEntrada = this.documentoFiscalEntrada.getSerie();
			}

		}

	}

	/**
	 * Obtem o documento fiscal de entrada quando o número e série do documento fiscal estiverem preenchidos
	 */
	public void obterDocumentoFiscalEntradaPorNumeroSerie() {

		if (this.numeroDocumentoFiscalEntrada != null && StringUtils.isNotEmpty(this.serieDocumentoFiscalEntrada)) {

			// final ScoFornecedor fornecedor =
			// this.gerarNotaRecebimentoVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor();

			if (this.numeroDocumentoFiscalEntrada != null && this.serieDocumentoFiscalEntrada != null) {

				this.documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorNumeroSerie(this.numeroDocumentoFiscalEntrada, this.serieDocumentoFiscalEntrada);
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
		this.imprimirNotaRecebimentoController.setConsiderarNotaEmpenho(false);
		this.imprimirNotaRecebimentoController.directPrint();
	}

	/**
	 * Move um item de nota da lista
	 * 
	 * @param item
	 */
	public void adicionarItemNotaRecebimento() {

		// Valida obrigatoriedade dos campos
		if (!validarCamposObrigatorios()) {
			return;
		} else {
			ajustaValorTotal();
		}
		
		// Valida valor informado no campo Valor Total ser maior que Zero		
		if(this.novoItemNotaRecebimento.getValorTotal().compareTo(new BigDecimal(0.0)) <= 0){
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_VALOR_TOTAL");
			return;
		}

		// Valida Fator de Conversão: Sempre maior que ZERO
		if (this.novoItemNotaRecebimento.getUnidadeMedida() != null && this.novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() != null
				&& this.novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() <= 0) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_FC_MAIOR_ZERO");
			return;
		}

		/*
		 * Valida Fator de Conversão: O Fator de Conversão não pode receber 1 quando a Unidade de Medida do Material é diferente da Unidade de Medida do Item de Nota de Recebimento
		 */
		if (this.novoItemNotaRecebimento.getUnidadeMedida() != null && this.novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() != null
				&& !this.novoItemNotaRecebimento.getMaterial().getUnidadeMedida().equals(this.novoItemNotaRecebimento.getUnidadeMedida())
				&& this.novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() == 1) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_FC_INVALIDO");
			return;
		}

		// Adiciona item na lista
		this.listaItemNotaRecebimento.add(this.novoItemNotaRecebimento);

		// Determina o número do item
		this.novoItemNotaRecebimento.setNumero(this.listaItemNotaRecebimento.size());

		// Gera um novo item de nota de recebimento
		this.novoItemNotaRecebimento = new GerarItemNotaRecebimentoSemAfVO();

		// Desativa o campo quantidade embalagem FC
		this.ativarCampoQuantidadeEmbalagemFc = false;

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_MOVER_ITEM_NOTA_LISTA_DESTINO");

	}

	private boolean validarCamposObrigatorios() {

		// Valida obrigatoriedade do campo Material
		if (this.novoItemNotaRecebimento.getMaterial() == null) {
			this.apresentarMsgNegocio("sbMaterial", Severity.ERROR, CAMPO_OBRIGATORIO, "Material");
			return false;
		}

		// Valida obrigatoriedade do campo Marca Comercial
		if (this.novoItemNotaRecebimento.getMarcaComercial() == null) {
			this.apresentarMsgNegocio("sbMarcaComercial", Severity.ERROR, CAMPO_OBRIGATORIO, "Marca Comercial");
			return false;
		}

		// Valida obrigatoriedade do campo Quantidade a Receber
		if (this.novoItemNotaRecebimento.getQuantidadeReceber() == null) {
			this.apresentarMsgNegocio("quantidadeReceber", Severity.ERROR, CAMPO_OBRIGATORIO, "Quantidade a Receber");
			return false;
		}

		// Valida obrigatoriedade do campo Valor Total
		if (this.novoItemNotaRecebimento.getValorTotal() == null || BigDecimal.ZERO.equals(this.novoItemNotaRecebimento.getValorTotal())) {
			this.apresentarMsgNegocio("valorTotal", Severity.ERROR, CAMPO_OBRIGATORIO, "Valor Total");
			return false;
		}

		return true;
	}
	
	private void ajustaValorTotal() {
		/*
		 * Ajusta valor total da nota conforme o padrão da tela: Inclusão de Saldo de Estoque
		 */
		
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#.#############", symbols);

		if(this.novoItemNotaRecebimento != null && this.novoItemNotaRecebimento.getValorTotal() != null) {
			BigDecimal b = new BigDecimal(format.format(this.novoItemNotaRecebimento.getValorTotal()));
			this.novoItemNotaRecebimento.setValorTotal(NumberUtil.truncateFLOOR(b, 2));
		}

		BigDecimal valorFormatado = new BigDecimal(this.novoItemNotaRecebimento.getValorTotal().doubleValue()).setScale(2,BigDecimal.ROUND_HALF_EVEN);
		this.novoItemNotaRecebimento.setValorTotal(valorFormatado);
	}

	/**
	 * Remove um item de nota da lista
	 * 
	 * @param item
	 */
	public void removerItemNotaRecebimento(GerarItemNotaRecebimentoSemAfVO item) {

		this.listaItemNotaRecebimento.remove(item);

		// Para o último item removido o item selecionado, quantidade a receber
		// e valor total dos itens serão limpos
		if (this.listaItemNotaRecebimento != null && this.listaItemNotaRecebimento.isEmpty()) {

			this.itemSelecionado = new GerarItemNotaRecebimentoSemAfVO();
			this.valorTotalItens = null;

		} else {

			// Recalcula número dos itens de nota de recebimento após a remoção
			int numero = 0;
			for (GerarItemNotaRecebimentoSemAfVO vo : this.listaItemNotaRecebimento) {
				vo.setNumero(++numero);
			}

		}

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_NR_REMOVER_ITEM_NOTA_LISTA_DESTINO");
	}

	/**
	 * Seleciona um item de nota para a edição/alteração
	 * 
	 * @param itemNotaRecebimentoSelecionado
	 */
	public void selecionarItemNotaRecebimentoAlteracao(GerarItemNotaRecebimentoSemAfVO itemNotaRecebimentoSelecionado) {

		this.novoItemNotaRecebimento = itemNotaRecebimentoSelecionado;

		// Verifica se o campo Quantidade Embalagem (FC) deve ser ativado
		this.compararUnidadeMedidaMaterialAtivarCampoQuantidadeEmbalagemFc();

		try {
			this.antigoItemNotaRecebimento = (GerarItemNotaRecebimentoSemAfVO) BeanUtils.cloneBean(itemNotaRecebimentoSelecionado);
		} catch (Exception e) {
			LOG.error("Exceção caputada:", e);
		}

		this.itemSelecionado = itemNotaRecebimentoSelecionado;
		this.alterandoItemNota = true;
	}
	
	public void alterarItemNR() {
		alterarItemNotaRecebimento();
	}

	/**
	 * Altera um item de nota de recebimento
	 */
	private boolean alterarItemNotaRecebimento() {
		
		if(this.antigoItemNotaRecebimento.getValorTotal()!= null && this.novoItemNotaRecebimento.getValorTotal() != null){
			// Valida valor informado no campo Valor Total ser maior que Zero	
			if(this.antigoItemNotaRecebimento.getValorTotal().intValue() <= 0){
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_VALOR_TOTAL");
				return false;
			}
		}
		
		if(this.novoItemNotaRecebimento.getValorTotal() != null){
			if(this.novoItemNotaRecebimento.getValorTotal().intValue() <= 0){
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_VALOR_TOTAL");
				return false;
			}
		}
		
		if (this.antigoItemNotaRecebimento != null && antigoItemNotaRecebimento.getUnidadeMedida() != novoItemNotaRecebimento.getUnidadeMedida()
				&& antigoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() == novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao()
				&& antigoItemNotaRecebimento.getMaterial().getUnidadeMedida().equals(novoItemNotaRecebimento.getMaterial().getUnidadeMedida())
				&& antigoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() == 1) {
			this.apresentarMsgNegocio(Severity.ERROR,"SCE_00280");
			this.listaItemNotaRecebimento.set(this.listaItemNotaRecebimento.indexOf(novoItemNotaRecebimento), this.antigoItemNotaRecebimento);
			return false;
		} else {
			this.gerarNovoItemNotaRecebimento();
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_GERAR_NR_ALTERAR_ITEM_NOTA_LISTA");
			return true;
		}
	}

	/**
	 * Cancela edição do item de nota de recebimento
	 */
	public void cancelarEdicaoItemNotaRecebimento() {

		// Restaura o item nota recebimento original sem as alterações
		// realizadas pelo usuário
		this.listaItemNotaRecebimento.set(this.listaItemNotaRecebimento.indexOf(novoItemNotaRecebimento), this.antigoItemNotaRecebimento);

		this.gerarNovoItemNotaRecebimento();

	}

	/**
	 * Gera/Instancia um novo item de nota de recebimento para inclusão na lista
	 */
	private void gerarNovoItemNotaRecebimento() {

		// Gera um novo item nota para inclusão
		this.novoItemNotaRecebimento = new GerarItemNotaRecebimentoSemAfVO();

		// Remove a seleção do item
		this.itemSelecionado = new GerarItemNotaRecebimentoSemAfVO();

		// Desatova o campo Quantidade Embalagem (FC)
		this.ativarCampoQuantidadeEmbalagemFc = false;

		// Desativa a alteração de item de nota
		this.alterandoItemNota = false;
	}

	/**
	 * Compara a Unidade de Medida do Material com a Unidade de Medida informada pelo usuário. Para unidades diferentes o campo Quantidade Embalagem (FC) deve ser ativado
	 */
	public void compararUnidadeMedidaMaterialAtivarCampoQuantidadeEmbalagemFc() {

		final ScoMaterial material = this.novoItemNotaRecebimento.getMaterial();
		final ScoUnidadeMedida unidadeMedida = this.novoItemNotaRecebimento.getUnidadeMedida();

		if (material != null && unidadeMedida != null) {
			// Unidades diferentes ativam campo Quantidade Embalagem (FC)
			this.ativarCampoQuantidadeEmbalagemFc = !material.getUnidadeMedida().equals(unidadeMedida);

		} else {
			// O padrão para o campo Quantidade Embalagem (FC
			this.ativarCampoQuantidadeEmbalagemFc = false;

			// Recalcula a quantidade convertida
			this.novoItemNotaRecebimento.setQuantidadeEmbalagemFatorConversao(1);
			this.calcularQuantidadeConvertida();

		}
	}

	/**
	 * Calcula a Quantidade Convertida do Item de Nota
	 */
	public void calcularQuantidadeConvertida() {

		// Obtém a quantidade a receber
		final Integer quantidadeReceber = this.novoItemNotaRecebimento.getQuantidadeReceber() != null ? this.novoItemNotaRecebimento.getQuantidadeReceber() : 0;

		// Obtém a quantidade da embalagem FC e seta a mesma no novo item de
		// nota de recebimento
		final Integer quantidadeEmbalagemFatorConversao = this.novoItemNotaRecebimento.getQuantidadeEmbalagemFatorConversao() != null ? this.novoItemNotaRecebimento
				.getQuantidadeEmbalagemFatorConversao() : 1;
		this.novoItemNotaRecebimento.setQuantidadeEmbalagemFatorConversao(quantidadeEmbalagemFatorConversao);

		// Calcula a quantidade convertidas
		final Integer quantidadeConvertida = quantidadeReceber * quantidadeEmbalagemFatorConversao;
		this.novoItemNotaRecebimento.setQuantidadeConvertida(quantidadeConvertida);

	}

	/**
	 * Calcula o valor total itens da tabela
	 * @return
	 */
	public Double calcularValorTotalItens() {
		Double valor = null;

		if (this.listaItemNotaRecebimento != null && !this.listaItemNotaRecebimento.isEmpty()) {
			valor = new Double(0);

			for (GerarItemNotaRecebimentoSemAfVO item : this.listaItemNotaRecebimento) {
				// Caso o valor do item selecionado a receber é nulo o valor
				// padrão para a mesmo será ZERO
				item.setValorTotal(item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO);

				// Acumula total
				valor += item.getValorTotal().doubleValue();
			}
		}

		return valor;
	}

	/**
	 * Confirma a operacao de gravar/alterar
	 * 
	 * @return
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 */
	public void confirmar() throws JRException, SystemException, IOException {
		try {
			if (this.documentoFiscalEntrada == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_GERAR_NR_ERRO_DOCUMENTO_FISCAL_NAO_INFORMADO");
				return;
			}
			
			if (!validarValorNotaFiscalComValorTotalItensRecebidos()) {
				return;
			}
			//Verifica se o campo valor total continua com valor positivo 
//			if(antigoItemNotaRecebimento.getValorTotal().intValue() <= 0){
//				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_TOTAL");
//				return;
//			 }
			// Valida se material está em edição e tentou gravar-lo
			if(this.antigoItemNotaRecebimento != null){
			 	if(alterarItemNotaRecebimento() == false) {	
			 		return;
				}
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

			// Gera e Persiste Nota de Recebimento com Solicitação de Compra
			// Automática
			this.estoqueBeanFacade.gerarNotaRecebimentoSolicitacaoCompraAutomatica(notaRecebimento, recuperaNomeMicroComputador());

			// Contador de itens
			Short numeroItem = 0;

			// Persiste itens de nota de recebimento através da lista de destino
			for (GerarItemNotaRecebimentoSemAfVO vo : this.listaItemNotaRecebimento) {
				// Contabiliza itens
				numeroItem++;

				SceItemNotaRecebimento itemNotaRecebimento = new SceItemNotaRecebimento();

				// Nota recebimento
				itemNotaRecebimento.setNotaRecebimento(notaRecebimento);

				// Autorização de item fornecimento
				final ScoItemAutorizacaoForn itemAutorizacaoForn = this.comprasFacade.obterItemAutorizacaoFornPorId(vo.getAfnNumero(), vo.getNumero());
				itemNotaRecebimento.setItemAutorizacaoForn(itemAutorizacaoForn);

				// Material
				itemNotaRecebimento.setMaterial(vo.getMaterial());

				// Aqui é importante setar a Unidade de Medida do Material, não
				// confundir com a Unidade de Medida informada pelo usuário
				itemNotaRecebimento.setUnidadeMedida(vo.getMaterial().getUnidadeMedida());

				itemNotaRecebimento.setQuantidade(vo.getQuantidadeConvertida());
				itemNotaRecebimento.setValor(vo.getValorTotal().doubleValue());
				itemNotaRecebimento.setIndDebitoNrIaf(false);

				// Gera e Persiste o Item de Nota de Recebimento com Solicitação
				// de Compra Automática
				this.estoqueBeanFacade.gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(itemNotaRecebimento, numeroItem, vo.getMarcaComercial(), vo.getQuantidadeEmbalagemFatorConversao(),
						recuperaNomeMicroComputador());
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
	}

	private boolean validarValorNotaFiscalComValorTotalItensRecebidos() {
		if (documentoFiscalEntrada.getValorTotalNf() == null
				|| listaItemNotaRecebimento == null
				|| listaItemNotaRecebimento.isEmpty() ) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NAO_POSSIVEL_VALIDAR_VALOR_NF_DIFERENTE_ITENS_DA_NOTA");
			return false;
		}
		
		if (!valorTotalNotaFiscalIgualValorTotalItens()) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VALOR_NF_DIFERENTE_ITENS_DA_NOTA");
			return false;
		}
		
		return true;
	}
	
	private Boolean valorTotalNotaFiscalIgualValorTotalItens() {
		BigDecimal valorTotalItens = somaValorTotalItensNotaFiscalRecebimento(BigDecimal.ZERO);
		
		return (documentoFiscalEntrada.getValorTotalNf() == valorTotalItens.doubleValue());
	}
	private BigDecimal somaValorTotalItensNotaFiscalRecebimento(BigDecimal valorTotalItens) {
		for (GerarItemNotaRecebimentoSemAfVO itemNotaRecebimento : listaItemNotaRecebimento) {
			valorTotalItens = valorTotalItens.add(itemNotaRecebimento.getValorTotal());
		}
		return valorTotalItens;
	}

	/**
	 * Limpa os filtros do cadastro
	 */
	public void limpar() {

		if (this.alterandoItemNota) { // A edição será descartada quando ativada
			this.cancelarEdicaoItemNotaRecebimento();
		}

		this.gerarNotaRecebimentoVO = null;
		this.listaItemNotaRecebimento = new LinkedList<GerarItemNotaRecebimentoSemAfVO>();
		this.novoItemNotaRecebimento = new GerarItemNotaRecebimentoSemAfVO();
		this.itemSelecionado = new GerarItemNotaRecebimentoSemAfVO();
		this.ativarCampoQuantidadeEmbalagemFc = false;
		this.valorTotalItens = null;
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
		this.novoItemNotaRecebimento = null;
		this.listaItemNotaRecebimento = null;
	}

	private String recuperaNomeMicroComputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		return nomeMicrocomputador;
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

	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}

	public GerarItemNotaRecebimentoSemAfVO getNovoItemNotaRecebimento() {
		return novoItemNotaRecebimento;
	}

	public void setNovoItemNotaRecebimento(GerarItemNotaRecebimentoSemAfVO novoItemNotaRecebimento) {
		this.novoItemNotaRecebimento = novoItemNotaRecebimento;
	}

	public GerarItemNotaRecebimentoSemAfVO getAntigoItemNotaRecebimento() {
		return antigoItemNotaRecebimento;
	}

	public void setAntigoItemNotaRecebimento(GerarItemNotaRecebimentoSemAfVO antigoItemNotaRecebimento) {
		this.antigoItemNotaRecebimento = antigoItemNotaRecebimento;
	}

	public List<GerarItemNotaRecebimentoSemAfVO> getListaItemNotaRecebimento() {
		return listaItemNotaRecebimento;
	}

	public void setListaItemNotaRecebimento(List<GerarItemNotaRecebimentoSemAfVO> listaItemNotaRecebimento) {
		this.listaItemNotaRecebimento = listaItemNotaRecebimento;
	}

	public GerarItemNotaRecebimentoSemAfVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(GerarItemNotaRecebimentoSemAfVO itemSelecionado) {
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

	public boolean isAtivarCampoQuantidadeEmbalagemFc() {
		return ativarCampoQuantidadeEmbalagemFc;
	}

	public void setAtivarCampoQuantidadeEmbalagemFc(boolean ativarCampoQuantidadeEmbalagemFc) {
		this.ativarCampoQuantidadeEmbalagemFc = ativarCampoQuantidadeEmbalagemFc;
	}

	public boolean isAlterandoItemNota() {
		return alterandoItemNota;
	}

	public void setAlterandoItemNota(boolean alterandoItemNota) {
		this.alterandoItemNota = alterandoItemNota;
	}
}