package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.ItemRecebimentoValorExcedido;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.ValidaConfirmacaoRecebimentoVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class RecebeMaterialServicoController extends ActionController{
	
	private static final String ESTOQUE_GERAR_NOTA_RECEBIMENTO = "estoque-gerarNotaRecebimento";
	private static final String RECEBER_MATERIAL_SERVICO_SEM_AF = "estoque-gerarNotaRecebimentoSemAf";
	private static final Log LOG = LogFactory.getLog(RecebeMaterialServicoController.class);
	private static final long serialVersionUID = 662093894299211128L;	
	
	private static final String PAGES_NAVEGAR_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String PRIORIZAR_ENTREGA_SOLICITACAO_MATERIAL_SERVICO = "compras-priorizarEntregaSolicitacaoMaterialServico";
	private static final String PAGES_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";
	private static final String PAGES_PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST = "compras-pesquisaAutorizacaoFornecimentoList";	
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	//@EJB quando navegar de ppriorizarEntregaSolicitacaoMaterialServico possivelmente deva preencher esta lista
	private List<PriorizaEntregaVO> listaPriorizacao;	
	
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private Integer numeroNotaFiscal;
	private String serieNotaFiscal;
	private String tipoNotaFiscal;
	private Date dataEmissao;
	private Date dataEntrada;
	private BigDecimal valorNota;	
	private BigDecimal valorComprometidoNF;
	private BigDecimal valorComprometidoNFAntesRecebimento;
	private Integer seqNota;
	
	private Integer numeroAF;
	private Short complementoAF;
	private ScoFornecedor fornecedor;
	private Integer numeroFornecedor;
	private ScoMaterial material;
	private ScoServico servico;
	
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoAutorizacaoForn nroComplementoAF;
	private DominioTipoFaseSolicitacao tipoSolicitacao;
	
	private Integer numeroRecebimento;
	private boolean inibeNotaFiscalEntrada;
	private RecebimentoMaterialServicoVO itemPriorizado;
	
	List<RecebimentoMaterialServicoVO> listaRecebimentoMaterialServicoVO;
	private SceNotaRecebProvisorio notaRecebProvisorio;
	
	private List<ItemRecebimentoProvisorioVO> listaItensSemSaldo = new ArrayList<ItemRecebimentoProvisorioVO>();
	private List<ItemRecebimentoProvisorioVO> listaItensValoresDivergentes = new ArrayList<ItemRecebimentoProvisorioVO>();
	private ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO;
	private Boolean exibeModalConfirmaRecebimento;
	private Boolean recebimentoAntigoAtivo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	private static final Comparator<Object> PT_BR_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);
			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};
	
	private Comparator<RecebimentoMaterialServicoVO> currentComparator;
	private String currentSortProperty;

	/** Itens com valores excedidos. */
	private List<String> excedidos = new ArrayList<String>();
	
	public void inicio() {
	 

	 

		valorComprometidoNFAntesRecebimento = BigDecimal.ZERO;
		setRecebimentoAntigoAtivo(this.estoqueFacade.mostrarBotaoRecebimentoAntigo());
		
		try {
			if (autFornecimentoFacade.permiteNotaFiscalEntrada()){
				inibeNotaFiscalEntrada = false;

				if(this.seqNota!=null){
					this.documentoFiscalEntrada = estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seqNota);
				}
				
				if (documentoFiscalEntrada != null){
					numeroNotaFiscal = documentoFiscalEntrada.getNumero().intValue();
					serieNotaFiscal = documentoFiscalEntrada.getSerie();
					if (documentoFiscalEntrada.getTipo() != null){
						tipoNotaFiscal = documentoFiscalEntrada.getTipo().getDescricao();
					}
					dataEmissao = documentoFiscalEntrada.getDtEmissao();
					dataEntrada = documentoFiscalEntrada.getDtEntrada();
					if (documentoFiscalEntrada.getValorTotalNf() != null){
						valorNota = new BigDecimal(documentoFiscalEntrada.getValorTotalNf());						
					} else {
						valorNota = BigDecimal.ZERO;
					}

					Double valorComprometido = estoqueFacade.obterValorTotalItemNotaFiscal(this.seqNota);
					if (valorComprometido == null){
						valorComprometidoNFAntesRecebimento = BigDecimal.ZERO;
					} else {
						valorComprometidoNFAntesRecebimento = new BigDecimal(valorComprometido);						
					}											
					
					valorComprometidoNF = valorComprometidoNFAntesRecebimento;
					calcularValorComprometido();
					
					this.setFornecedor();
				}
			} else {
				inibeNotaFiscalEntrada = true;
				valorComprometidoNF = null;
			}
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_P_ENTRADA_NF_MAT_SERV");
		}	

		if (listaPriorizacao != null) {
			verificarRateioRecebimento(itemPriorizado);
		}		
		exibeModalConfirmaRecebimento = Boolean.FALSE;
 	
	}
	
	
	/** Confirma recebimento de itens com valores excedidos. */
	public void confirmaRecebimentoParcelaItensAf() {
		receberParcelaItensAF(true);
	}
	
	public String getCnpjRaiz(ScoFornecedor forn) {
		String cnpjRaiz = "";
		
		if (forn != null) {
			if (forn.getCnpjCpf().toString().length() >= 8) {
				cnpjRaiz = forn.getCnpjCpf().toString().substring(0, 8);	
			} else {
				cnpjRaiz = forn.getCnpjCpf().toString();
			}
		}
		return cnpjRaiz;
	}
	
	public void setFornecedor() {
		Long qtdCnpjRaiz = this.comprasFacade.contarVFornecedorPorCnpjRaiz(getCnpjRaiz(documentoFiscalEntrada.getFornecedor()));
		
		if (this.autorizacaoForn == null) {
			if (fornecedor == null) {
				if (qtdCnpjRaiz < 2) {
					fornecedor =  documentoFiscalEntrada.getFornecedor();
				}
			} else {
				if (!Objects.equals(documentoFiscalEntrada.getFornecedor(), fornecedor)) {
					if (qtdCnpjRaiz > 1) {
						limpar();
					} else {
						fornecedor =  documentoFiscalEntrada.getFornecedor();																			
					}
				}
			}		
		} else {
			ScoFornecedor forn = this.comprasFacade.obterFornecedorPorNumero(this.autorizacaoForn.getPropostaFornecedor().getFornecedor().getNumero());
			String cnpjRaizFornAF = "";
			String cnpjRaizFornNF = getCnpjRaiz(documentoFiscalEntrada.getFornecedor());
			if (forn != null) {
				cnpjRaizFornAF = getCnpjRaiz(forn);
			}
			
			if (qtdCnpjRaiz < 2) {
				if (cnpjRaizFornAF.equals(cnpjRaizFornNF)) {
					fornecedor =  documentoFiscalEntrada.getFornecedor();
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "ERRO_NF_OUTRO_FORNECEDOR");
					this.limparNotaFiscal();
				}
			} else {
				this.fornecedor = null;
			}
		}
	}
	
	/** Recebe itens solicitando confirmação se valores excederem. */
	public void receberParcelaItensAF() {
		receberParcelaItensAF(false);
	}
		
	/**
	 * Recebe itens.
	 * 
	 * @param force true para receber itens com valores excedidos.
	 */
	public void receberParcelaItensAF(boolean force) {
		if (numeroRecebimento != null) {
			return;
		}
		
		verificarStatusTela();
		
		numeroRecebimento = null;
		
		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		} catch (ApplicationBusinessException e2) {
			LOG.error("Erro",e2);
			apresentarExcecaoNegocio(e2);			
		}
		
		try {
			// Antes de iniciar o processo de recebimento validar se existe item informando qtd/valor maior que seu saldo
			if (getSaldoExcedidoTodosItens(listaRecebimentoMaterialServicoVO)){
				return;
			}
			
			excedidos.clear();
			
			// Realiza o recebimento provisório, gerando Nota de Recebimento Provisório
			notaRecebProvisorio = autFornecimentoFacade.receberParcelaItensAF(
					listaRecebimentoMaterialServicoVO, documentoFiscalEntrada,
					servidorLogado, fornecedor, force);
			
			if (notaRecebProvisorio != null){
				// Atualiza atributo que exibe na tela o número da nota
				numeroRecebimento = notaRecebProvisorio.getSeq();
				
				// Caso o item seja de material envia email do chefe do almoxarifado ao solicitante avisando do recebimento
				autFornecimentoFacade.enviarEmailSolicitanteCompras(listaRecebimentoMaterialServicoVO);

				// Caso esteja parametrizado, realiza a Confirmação do Recebimento
				if (autFornecimentoFacade.verificarConfirmacaoImediataRecebimento()){
					this.confirmarRecebimentoAutomaticamente(true);
				} else {					
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RECEBIMENTO_REALIZADO_SUCESSO");						
				}
			}			
		} catch (BaseException e) {
			LOG.error("Erro",e);
			apresentarExcecaoNegocio(e);			
		} catch (ItemRecebimentoValorExcedido e) {
			excedidos = e.getExcedidos();
		}
	}
	
	public void naoConfirmaRecebimento() {
		setExibeModalConfirmaRecebimento(Boolean.FALSE);
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RECEBIMENTO_REALIZADO_SUCESSO");	
	}
	
	public void confirmarRecebimentoSemValidacao(){
		setExibeModalConfirmaRecebimento(Boolean.FALSE);
		this.confirmarRecebimentoAutomaticamente(false);
	}
	
	public void confirmarRecebimentoAutomaticamente(Boolean valida){
		this.listaItensSemSaldo.clear();
		this.listaItensValoresDivergentes.clear();
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e1) {
			LOG.error(e1.getMessage(), e1);
		}
		try {
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal =  this.estoqueFacade.clonarNotaRecebimentoProvisorio(this.notaRecebProvisorio);
			this.validaConfirmacaoRecebimentoVO = this.estoqueFacade.confirmarRecebimento(notaRecebProvisorio, notaRecebimentoProvisorioOriginal,
					nomeMicrocomputador, valida, false);

			if(validaConfirmacaoRecebimentoVO != null && validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao() != null && validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao()){
				this.exibeModalConfirmaRecebimento = Boolean.TRUE;
				return;
			}
			
			setExibeModalConfirmaRecebimento(Boolean.FALSE);				
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONFIRMACAO_RECEBIMENTO_REALIZADO_SUCESSO");			
		} catch(BaseException e){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RECEBIMENTO_REALIZADO_SUCESSO");
			this.apresentarExcecaoNegocio(e);
			LOG.info(e.getMessage(), e);
		} 
	}
	
	public String confirmarRecebimento() {
		return "confirmarRecebimento";
	}
	
	public void pesquisarProgEntregaItensComSaldoPositivo() {			
		if (numeroAF != null && complementoAF != null){
			
			autorizacaoForn = autFornecimentoFacade.buscarAutFornPorNumPac(numeroAF, complementoAF);
			
			if (autorizacaoForn != null) {
				tipoSolicitacao = autFornecimentoFacade.getItemAutorizacaoFornecedorFaseSolicitacaoTipoPorAutorizacaoFornecimento(autorizacaoForn);
			}
			
			listaRecebimentoMaterialServicoVO = autFornecimentoFacade.pesquisarProgEntregaItensComSaldoPositivo(numeroAF, complementoAF, tipoSolicitacao);
		
			this.currentComparator = null;
			this.setOrdenar("codigoMaterialServico");
		}
	}
	
	public void selecionarAF() {
		if (autorizacaoForn != null){
			numeroAF = autorizacaoForn.getPropostaFornecedor().getLicitacao().getNumero();
		}
		
		if (nroComplementoAF == null){
			List<ScoAutorizacaoForn> listaNroComplemento = autFornecimentoFacade.pesquisarComplementoNumAFNumComplementoFornecedor(numeroAF, nroComplementoAF, getNumeroFornecedor(), material, servico);
			if (listaNroComplemento.size() == 1){
				nroComplementoAF = listaNroComplemento.get(0);
				complementoAF = nroComplementoAF.getNroComplemento();
			}
		}
		
		if (fornecedor == null){
			List<ScoFornecedor> listaFornecedor = autFornecimentoFacade.pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAF, complementoAF, fornecedor, material, servico, null);
			if (listaFornecedor.size() == 1){
				fornecedor = listaFornecedor.get(0);
				numeroFornecedor = fornecedor.getNumero();
			}
		}
		
		pesquisarProgEntregaItensComSaldoPositivo();
	}
	
	public void selecionarComplemento() {
		if (nroComplementoAF != null){
			complementoAF = nroComplementoAF.getNroComplemento();
		}
		
		if (autorizacaoForn == null){
		   List<ScoAutorizacaoForn> listaAF = autFornecimentoFacade.pesquisarAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), material, servico);
		   
		   if (listaAF.size() == 1){
			   autorizacaoForn = listaAF.get(0); 
			   numeroAF = autorizacaoForn.getPropostaFornecedor().getLicitacao().getNumero();
		   }
		}
		
		if (fornecedor == null){
			List<ScoFornecedor> listaFornecedor = autFornecimentoFacade.pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAF, complementoAF, numeroFornecedor, material, servico, null);
			if (listaFornecedor.size() == 1){
				fornecedor = listaFornecedor.get(0);
				numeroFornecedor = fornecedor.getNumero();
			}
		}
		
		pesquisarProgEntregaItensComSaldoPositivo();
	}

	public void selecionarFornecedor() {
		if (autorizacaoForn == null){
		   List<ScoAutorizacaoForn> listaAF = autFornecimentoFacade.pesquisarAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), material, servico);		   
		   if (listaAF.size() == 1){
			   autorizacaoForn = listaAF.get(0); 
			   numeroAF = autorizacaoForn.getPropostaFornecedor().getLicitacao().getNumero();
		   }
		}

		if (nroComplementoAF == null){
			List<ScoAutorizacaoForn> listaNroComplemento = autFornecimentoFacade.pesquisarComplementoNumAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), material, servico);
			if (listaNroComplemento.size() == 1){
				nroComplementoAF = listaNroComplemento.get(0);
				complementoAF = nroComplementoAF.getNroComplemento();
			}
		}
		
		pesquisarProgEntregaItensComSaldoPositivo();
	}
	
	public void selecionarMaterial() {
		if (autorizacaoForn == null){
			   List<ScoAutorizacaoForn> listaAF = autFornecimentoFacade.pesquisarAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), material, null);		   
			   if (listaAF.size() == 1){
				   autorizacaoForn = listaAF.get(0); 
				   numeroAF = autorizacaoForn.getPropostaFornecedor().getLicitacao().getNumero();
			   }
			}

			if (nroComplementoAF == null){
				List<ScoAutorizacaoForn> listaNroComplemento = autFornecimentoFacade.pesquisarComplementoNumAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), material, servico);
				if (listaNroComplemento.size() == 1){
					nroComplementoAF = listaNroComplemento.get(0);
					complementoAF = nroComplementoAF.getNroComplemento();
				}
			}
			
			if (fornecedor == null){
				List<ScoFornecedor> listaFornecedor = autFornecimentoFacade.pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAF, complementoAF, null, material, null, null);
				if (listaFornecedor.size() == 1){
					fornecedor = listaFornecedor.get(0);
					numeroFornecedor = fornecedor.getNumero();
				}
			}
			
			pesquisarProgEntregaItensComSaldoPositivo();		
	}
	
	public void selecionarServico() {
		if (autorizacaoForn == null) {
			   List<ScoAutorizacaoForn> listaAF = autFornecimentoFacade.pesquisarAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), null, servico);		   
			   if (listaAF.size() == 1){
				   autorizacaoForn = listaAF.get(0); 
				   numeroAF = autorizacaoForn.getPropostaFornecedor().getLicitacao().getNumero();
			   }
			}

			if (nroComplementoAF == null) {
				List<ScoAutorizacaoForn> listaNroComplemento = autFornecimentoFacade.pesquisarComplementoNumAFNumComplementoFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), null, servico);
				if (listaNroComplemento.size() == 1){
					nroComplementoAF = listaNroComplemento.get(0);
					complementoAF = nroComplementoAF.getNroComplemento();
				}
			}
			
			if (fornecedor == null) {
				List<ScoFornecedor> listaFornecedor = autFornecimentoFacade.pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAF, complementoAF, null, null, servico, null);
				if (listaFornecedor.size() == 1) {
					fornecedor = listaFornecedor.get(0);
					numeroFornecedor = fornecedor.getNumero();
				}
			}
			
			pesquisarProgEntregaItensComSaldoPositivo();		
	}
	
	public String buscarNotaFiscal() {
		return PAGES_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA;
	}
	
	public void limparNotaFiscal() {
		documentoFiscalEntrada = null;
		numeroNotaFiscal = null;
		serieNotaFiscal = null;
		tipoNotaFiscal = null;
		dataEmissao = null;
		dataEntrada = null;
		valorNota = null;
		seqNota = null;
		valorComprometidoNFAntesRecebimento = null;
		valorComprometidoNF = null;
		exibeModalConfirmaRecebimento = Boolean.FALSE;
	}	

	public String consultarAf() {
		///compras/autfornecimento/pesquisaAutorizacaoFornecimentoList.xhtml
		return PAGES_PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST;
	}
	
	public String getReceberSemAf(){
		return RECEBER_MATERIAL_SERVICO_SEM_AF;
	}

	public void limpar(){
		this.numeroAF = null;
		this.complementoAF = null;
		this.fornecedor = null;
		this.numeroFornecedor = null;
		this.material = null;
		this.servico = null;		
		this.autorizacaoForn = null;
		this.nroComplementoAF = null;		
		this.notaRecebProvisorio = null;		
		this.inibeNotaFiscalEntrada = false;		
		listaPriorizacao = null;
		
		if (numeroRecebimento != null){
			limparNotaFiscal();
		} else {
			if (valorComprometidoNFAntesRecebimento != null){
				valorComprometidoNF = valorComprometidoNFAntesRecebimento;
			}
		}
		
		limparGradeNroRecebimento();				
	}
	
	private void limparGradeNroRecebimento(){
		this.listaRecebimentoMaterialServicoVO = null;
		this.numeroRecebimento = null;				
	}
		
	public Boolean isSc() {
		return this.tipoSolicitacao == DominioTipoFaseSolicitacao.C;
	}
	
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		if (documentoFiscalEntrada != null){
			this.seqNota = documentoFiscalEntrada.getSeq();
		} else {
			this.seqNota = null;
		}
		
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}
	
	private void verificarStatusTela(){
		for (RecebimentoMaterialServicoVO item : listaRecebimentoMaterialServicoVO){
			if (item.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C) && item.getQtdEntregue() != null && item.getValorTotal() == null){
				calcularValorTotal(item);
			}
		}		
	}
	
	
	// Cálculo do valor unitário x quantidade entregue de um material
	public void calcularValorTotal(RecebimentoMaterialServicoVO item){		
		
		if (isSc()){
			if (item.getQtdEntregue() == null){
				item.setQtdEntregue(item.getSaldoQtd());			
			}
			if (item.getQtdEntregue() != null && item.getValorUnitario() != null && item.getQtdEntregue() != item.getQtdEntregueAnterior()){
				item.setValorTotal((new BigDecimal(item.getQtdEntregue()).multiply(item.getValorUnitario())).setScale(2, RoundingMode.HALF_UP));
				item.setQtdEntregueAnterior(item.getQtdEntregue());
			}			
		} else {
			if (item.getValorEntregue() == null){
				item.setValorEntregue(item.getValorSaldo());
			}
		}
				
		verificarSaldoExcedido(item);		
		calcularValorComprometido();
	}
	
	public void calcularValorComprometido(){
		if (this.seqNota != null && numeroRecebimento == null){
			
			valorComprometidoNF = valorComprometidoNFAntesRecebimento;
			
			if (listaRecebimentoMaterialServicoVO != null){
				for (RecebimentoMaterialServicoVO itemRecebimento : listaRecebimentoMaterialServicoVO){
					if (itemRecebimento.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){				
						if (itemRecebimento.getValorTotal() != null){
							valorComprometidoNF = valorComprometidoNF.add(itemRecebimento.getValorTotal());
						}
					} else {
						if (itemRecebimento.getValorEntregue() != null){
							valorComprometidoNF = valorComprometidoNF.add(itemRecebimento.getValorEntregue());
						}
					}					
				}
			}
		}
	}
	
	private Boolean getSaldoExcedidoTodosItens(List<RecebimentoMaterialServicoVO> listaItens){
		for (RecebimentoMaterialServicoVO item : listaItens){
			return verificarSaldoExcedido(item);
		}
		
		return false;
	}
	
	
	// Caso o saldo do item seja excedido verifica possibilidade de rateio do excesso entre outras
	// parcelas o item. Se o item não tiver mais parcelas a serem contempladas é gerado uma mensagem de erro.
	public Boolean verificarSaldoExcedido(RecebimentoMaterialServicoVO item) {
		Integer codigoMaterialServico = item.getCodigoMaterialServico();
		
		if (item.getQtdEntregue() != null || item.getValorEntregue() != null) {
			if (item.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)) {
				Integer maxQtdRecebimento = 0;
				for (RecebimentoMaterialServicoVO recebimentoMaterialServico : listaRecebimentoMaterialServicoVO) {
					if (recebimentoMaterialServico.getCodigoMaterialServico() == codigoMaterialServico) {
						maxQtdRecebimento = maxQtdRecebimento + recebimentoMaterialServico.getSaldoQtd();
					}
				}				
				if (item.getQtdEntregue() > maxQtdRecebimento) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_QTD_SUPERIOR_SALDO");
					return true;
				} else {
					item.setPermiteRecebimento(true);
				}
			} else {
				BigDecimal maxValorRecebimento = BigDecimal.ZERO;
				for (RecebimentoMaterialServicoVO recebimentoMaterialServico : listaRecebimentoMaterialServicoVO) {
					if (recebimentoMaterialServico.getCodigoMaterialServico() == codigoMaterialServico) {
						maxValorRecebimento = maxValorRecebimento.add(recebimentoMaterialServico.getValorSaldo());
					}
				}				
				if (item.getValorEntregue().compareTo(maxValorRecebimento) > 0) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VALOR_SUPERIOR_SALDO");
					return true;
				} else {
					item.setPermiteRecebimento(true);
				}
			}
			
			Boolean isExisteItemMenorData = Boolean.FALSE;
			Integer itemParcelaGradeAnterior = null;
			Date    itemDataPrevGradeAnterior = null;
			for (RecebimentoMaterialServicoVO itemGrade : listaRecebimentoMaterialServicoVO) {
					if (itemGrade.getNumeroAf().equals(item.getNumeroAf()) 
							&& itemGrade.getNumeroItemAf().equals(item.getNumeroItemAf())
							&& itemGrade.getSeqParcela().equals(item.getSeqParcela())
							&& !itemGrade.getParcela().equals(item.getParcela())
							&& itemGrade.getDtPrevEntrega().before(item.getDtPrevEntrega())){
						isExisteItemMenorData = Boolean.TRUE;	
						itemParcelaGradeAnterior = itemGrade.getParcela();
						itemDataPrevGradeAnterior = itemGrade.getDtPrevEntrega();
					}
			}
			if (isExisteItemMenorData) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RECEBIMENTO_ITEM_DATA_PREV_ANTERIOR", itemParcelaGradeAnterior, dateFormat.format(itemDataPrevGradeAnterior), dateFormat.format(item.getDtPrevEntrega()));
			}			
		}
		
		verificarRateioRecebimento(item);
		return false;
	}	
	
	// Caso o valor excedente ao saldo de uma parcela não tenha sido priorizado pela tela Priorização de Entregas 
	// este excesso é distribuído às demais parcelas do item segundo os critérios:
	// 1) Parcela selecionada;
	// 2) IND_ENTREGA_IMEDIATA = ‘S’ (tabela  sco_progr_entrega_itens_af);
	// 3) IND_TRAMITE_INTERNO = ‘S’ (tabela  sco_progr_entrega_itens_af);
	// 4) menor DT_PREV_ENTREGA (sco_progr_entrega_itens_af).
	public void verificarRateioRecebimento(RecebimentoMaterialServicoVO itemEditado){	
		DominioTipoSolicitacao tipoSolicitacao;
		if (itemEditado.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){
			tipoSolicitacao = DominioTipoSolicitacao.SC;				
		} else {
			tipoSolicitacao = DominioTipoSolicitacao.SS;
	    }

		if (listaPriorizacao != null){
			itemPriorizado.setListaPriorizacao(listaPriorizacao);
			listaPriorizacao = null;
		} else {			
			Integer quantidadeEntregue = itemEditado.getQtdEntregue() != null ? itemEditado.getQtdEntregue() : 0;
			Double valorEntregue = new Double(0);
			
			if (itemEditado.getValorEntregue() != null){
				valorEntregue = itemEditado.getValorEntregue().doubleValue();
			}			
			itemEditado.setListaPriorizacao(this.autFornecimentoFacade.pesquisarSolicitacaoProgEntregaItemAf(itemEditado.getNumeroAf(), itemEditado.getNumeroItemAf().shortValue(), itemEditado.getSeqParcela(), itemEditado.getParcela(), null, null, quantidadeEntregue, valorEntregue, tipoSolicitacao));
		}
		
		// Caso valor/quantidade entregue seja maior que o valor do saldo de sua parcela, 
		// bloqueia o recebimento de outras parcelas deste item
		boolean permiteRecebimento;
		permiteRecebimento = !((tipoSolicitacao.equals(DominioTipoSolicitacao.SC) && itemEditado.getQtdEntregue() != null && itemEditado.getQtdEntregue() > 0) ||
				               (tipoSolicitacao.equals(DominioTipoSolicitacao.SS) && itemEditado.getValorEntregue() !=null));
				//|| tipoSolicitacao.equals(DominioTipoSolicitacao.SS));
		/*if ((tipoSolicitacao.equals(DominioTipoSolicitacao.SC) && itemEditado.getQtdEntregue() > itemEditado.getSaldoQtd()) 
		  || (tipoSolicitacao.equals(DominioTipoSolicitacao.SS) && itemEditado.getValorEntregue().compareTo(itemEditado.getValorSaldo()) > 0)){
			permiteRecebimento = false;					
		} else {
			permiteRecebimento = true;
		}*/		
	    this.setValorTotal(itemEditado);		
		
	    for (RecebimentoMaterialServicoVO itemGrade : listaRecebimentoMaterialServicoVO){
			if (itemGrade.getNumeroAf().equals(itemEditado.getNumeroAf()) 
					&& itemGrade.getNumeroItemAf().equals(itemEditado.getNumeroItemAf())
					&& itemGrade.getSeqParcela().equals(itemEditado.getSeqParcela())
					&& !itemGrade.getParcela().equals(itemEditado.getParcela())){
				
				itemGrade.setPermiteRecebimento(permiteRecebimento);
				
				// Caso seja necessario bloquear a edição do valor/quantidade recebida,
				// este campo será zerado e seu valor calculado pelo rateio da entrega
				if (!permiteRecebimento){
				    if (itemEditado.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){
				    	itemGrade.setQtdEntregue(null);
				    	itemGrade.setValorTotal(null);
				    }				    
				    if (itemEditado.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.S)){
				    	itemGrade.setValorEntregue(null);	
				    }
				}					
			}
	    }
	}	
	
	public void setValorTotal(RecebimentoMaterialServicoVO itemEditado){		
		if (isSc() && itemEditado.getQtdEntregue() == null){
			itemEditado.setValorTotal(null);			
		}
		if (isSc() && itemEditado.getQtdEntregue() != null && itemEditado.getValorUnitario() != null && itemEditado.getQtdEntregue() != itemEditado.getQtdEntregueAnterior()){
			itemEditado.setValorTotal((new BigDecimal(itemEditado.getQtdEntregue()).multiply(itemEditado.getValorUnitario())).setScale(2, RoundingMode.HALF_UP));
		}		
	}	

	public void setOrdenar(String propriedade) {
		Comparator<RecebimentoMaterialServicoVO> comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaRecebimentoMaterialServicoVO, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}	
	
	public Integer getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	public String getSerieNotaFiscal() {
		return serieNotaFiscal;
	}

	public void setSerieNotaFiscal(String serieNotaFiscal) {
		this.serieNotaFiscal = serieNotaFiscal;
	}

	public String getTipoNotaFiscal() {
		return tipoNotaFiscal;
	}

	public void setTipoNotaFiscal(String tipoNotaFiscal) {
		this.tipoNotaFiscal = tipoNotaFiscal;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public BigDecimal getValorNota() {
		return valorNota;
	}

	public void setValorNota(BigDecimal valorNota) {
		this.valorNota = valorNota;
	}

	public BigDecimal getValorComprometidoNF() {
		return valorComprometidoNF;
	}

	public void setValorComprometidoNF(BigDecimal valorComprometidoNF) {
		this.valorComprometidoNF = valorComprometidoNF;
	}

	public BigDecimal getValorComprometidoNFAntesRecebimento() {
		return valorComprometidoNFAntesRecebimento;
	}

	public void setValorComprometidoNFAntesRecebimento(
			BigDecimal valorComprometidoNFAntesRecebimento) {
		this.valorComprometidoNFAntesRecebimento = valorComprometidoNFAntesRecebimento;
	}

	public Integer getSeqNota() {
		return seqNota;
	}

	public void setSeqNota(Integer seqNota) {
		this.seqNota = seqNota;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		if (numeroAF == null){
			limparGradeNroRecebimento();
		}		
		this.numeroAF = numeroAF;
	}

	public Short getComplementoAF() {
		return complementoAF;
	}

	public void setComplementoAF(Short complementoAF) {
		if (complementoAF == null){
			limparGradeNroRecebimento();
		}		
		this.complementoAF = complementoAF;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getNumeroFornecedor() {
		if (this.fornecedor == null){
			numeroFornecedor = null;
		} else {
			numeroFornecedor = this.fornecedor.getNumero();
		}		
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		if (autorizacaoForn == null){
			numeroAF = null;
			limparGradeNroRecebimento();
		}		
		this.autorizacaoForn = autorizacaoForn;
	}

	public ScoAutorizacaoForn getNroComplementoAF() {
		return nroComplementoAF;
	}

	public void setNroComplementoAF(ScoAutorizacaoForn nroComplementoAF) {
		if (nroComplementoAF == null){
			complementoAF = null;
			limparGradeNroRecebimento();
		}		
		this.nroComplementoAF = nroComplementoAF;
	}	
	
	public String priorizarEntregaSolicitacaoMaterialServico(RecebimentoMaterialServicoVO item) {
		//<!-- view="/compras/autfornecimento/priorizarEntregaSolicitacaoMaterialServico.xhtml" -->
		setItemPriorizado(item);
		return PRIORIZAR_ENTREGA_SOLICITACAO_MATERIAL_SERVICO;
	}
	
	public String navegarEstatisticaConsumo() {
		//<!-- view="/estoque/estatisticaConsumo.xhtml" -->
		return PAGES_NAVEGAR_ESTATISTICA_CONSUMO;
	}	
	
	public String recebimentoV4(){
		return ESTOQUE_GERAR_NOTA_RECEBIMENTO;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}
	
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public DominioTipoFaseSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoFaseSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Integer getNumeroRecebimento() {
		return numeroRecebimento;
	}

	public void setNumeroRecebimento(Integer numeroRecebimento) {
		this.numeroRecebimento = numeroRecebimento;
	}

	public List<RecebimentoMaterialServicoVO> getListaRecebimentoMaterialServicoVO() {
		return listaRecebimentoMaterialServicoVO;
	}

	public void setListaRecebimentoMaterialServicoVO(
			List<RecebimentoMaterialServicoVO> listaRecebimentoMaterialServicoVO) {
		this.listaRecebimentoMaterialServicoVO = listaRecebimentoMaterialServicoVO;
	}

	public List<PriorizaEntregaVO> getListaPriorizacao() {
		return listaPriorizacao;
	}

	public void setListaPriorizacao(List<PriorizaEntregaVO> listaPriorizacao) {
		this.listaPriorizacao = listaPriorizacao;
	}

	public boolean isInibeNotaFiscalEntrada() {
		return inibeNotaFiscalEntrada;
	}

	public void setInibeNotaFiscalEntrada(boolean inibeNotaFiscalEntrada) {
		this.inibeNotaFiscalEntrada = inibeNotaFiscalEntrada;
	}

	public RecebimentoMaterialServicoVO getItemPriorizado() {
		return itemPriorizado;
	}

	public void setItemPriorizado(RecebimentoMaterialServicoVO itemPriorizado) {
		this.itemPriorizado = itemPriorizado;
	}

	public List<ItemRecebimentoProvisorioVO> getListaItensSemSaldo() {
		return listaItensSemSaldo;
	}

	public List<ItemRecebimentoProvisorioVO> getListaItensValoresDivergentes() {
		return listaItensValoresDivergentes;
	}

	public ValidaConfirmacaoRecebimentoVO getValidaConfirmacaoRecebimentoVO() {
		return validaConfirmacaoRecebimentoVO;
	}

	public void setListaItensSemSaldo(
			List<ItemRecebimentoProvisorioVO> listaItensSemSaldo) {
		this.listaItensSemSaldo = listaItensSemSaldo;
	}

	public void setListaItensValoresDivergentes(
			List<ItemRecebimentoProvisorioVO> listaItensValoresDivergentes) {
		this.listaItensValoresDivergentes = listaItensValoresDivergentes;
	}

	public void setValidaConfirmacaoRecebimentoVO(
			ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO) {
		this.validaConfirmacaoRecebimentoVO = validaConfirmacaoRecebimentoVO;
	}

	public SceNotaRecebProvisorio getNotaRecebProvisorio() {
		return notaRecebProvisorio;
	}

	public void setNotaRecebProvisorio(SceNotaRecebProvisorio notaRecebProvisorio) {
		this.notaRecebProvisorio = notaRecebProvisorio;
	}

	public Boolean getExibeModalConfirmaRecebimento() {
		return exibeModalConfirmaRecebimento;
	}

	public void setExibeModalConfirmaRecebimento(
			Boolean exibeModalConfirmaRecebimento) {
		this.exibeModalConfirmaRecebimento = exibeModalConfirmaRecebimento;
	}

	public List<String> getExcedidos() {
		return excedidos;
	}
	
	public Boolean getRecebimentoAntigoAtivo() {
		return recebimentoAntigoAtivo;
	}

	public void setRecebimentoAntigoAtivo(Boolean recebimentoAntigoAtivo) {
		this.recebimentoAntigoAtivo = recebimentoAntigoAtivo;
	}
}