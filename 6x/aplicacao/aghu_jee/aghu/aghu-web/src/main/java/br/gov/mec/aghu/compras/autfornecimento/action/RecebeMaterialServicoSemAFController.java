package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroRecebeMaterialServicoSemAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.SceEntrSaidSemLicitacaoVO;
import br.gov.mec.aghu.estoque.vo.ValidaConfirmacaoRecebimentoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RecebeMaterialServicoSemAFController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RecebeMaterialServicoSemAFController.class);

	private static final long serialVersionUID = 662093894299211128L;

	@EJB
	IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	private FiltroRecebeMaterialServicoSemAFVO filtro = new FiltroRecebeMaterialServicoSemAFVO();
	
	private List<ItensRecebimentoAdiantamentoVO> listaItensRecebimentoAdiantamentoVO = new ArrayList<ItensRecebimentoAdiantamentoVO>();
	private List<ItensRecebimentoVO> listaItensRecebimentoVO = new ArrayList<ItensRecebimentoVO>();
	private ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO;
	
	private Integer numeroAF;
	private Integer numeroAFPK;
	private Short complementoAF;
	private Integer numeroFornecedor;
	private Integer seqNota;
	private SceNotaRecebProvisorio notaRecebProvisorio;
	
	private ScoMaterial materialAdd;
	private ScoMarcaComercial marcaComercialAdd;
	private Integer idUnico;
	
	private ItensRecebimentoVO materialRemove;
	
	private String voltarPara;
	
	private Boolean readOnlyFornecedor;
	
	private static final String PAGE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";
	
	public void inicio() {
		
		this.readOnlyFornecedor = Boolean.FALSE;
		this.filtro.setValorComprometidoNFAntesRecebimento(BigDecimal.ZERO);
		
		if (this.idUnico == null) {
			this.idUnico = 0;
		}
		
		try {
			// RN07
			if (autFornecimentoFacade.permiteNotaFiscalEntrada()) {
				this.filtro.setInibeNotaFiscalEntrada(Boolean.FALSE);
				
				if(this.seqNota != null) {
					this.filtro.setDocumentoFiscalEntrada(estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seqNota));
				}
				
				SceDocumentoFiscalEntrada documentoFiscalEntrada = this.filtro.getDocumentoFiscalEntrada();

				if (documentoFiscalEntrada != null) {
					this.filtro.setSerieNotaFiscal(documentoFiscalEntrada.getSerie());
					if (documentoFiscalEntrada.getTipo() != null) {
						this.filtro.setTipoNotaFiscal(documentoFiscalEntrada.getTipo().getDescricao());
					}
					this.filtro.setDataEmissao(documentoFiscalEntrada.getDtEmissao());
					this.filtro.setDataEntrada(documentoFiscalEntrada.getDtEntrada());
					if (documentoFiscalEntrada.getValorTotalNf() != null) {
						this.filtro.setValorNota(new BigDecimal(documentoFiscalEntrada.getValorTotalNf()));						
					} else {
						this.filtro.setValorNota(BigDecimal.ZERO);
					}

					Double valorComprometido = estoqueFacade.obterValorTotalItemNotaFiscal(documentoFiscalEntrada.getSeq());
					if (valorComprometido == null) {
						this.filtro.setValorComprometidoNFAntesRecebimento(BigDecimal.ZERO);
					} else {
						this.filtro.setValorComprometidoNFAntesRecebimento(new BigDecimal(valorComprometido));
					}											
					
					this.filtro.setValorComprometidoNF(this.filtro.getValorComprometidoNFAntesRecebimento());
					if (this.listaItensRecebimentoAdiantamentoVO != null && !this.listaItensRecebimentoAdiantamentoVO.isEmpty()) {
						calcularValorComprometido();
						
					} else {
						calcularValorComprometidoMaterial();
					}
					this.setFornecedor();
				}
			} else {
				this.filtro.setInibeNotaFiscalEntrada(Boolean.TRUE);
				this.filtro.setValorComprometidoNF(null);
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_P_ENTRADA_NF_MAT_SERV");
		}	
 	}
	
	public void setFornecedor() {
		SceDocumentoFiscalEntrada documentoFiscalEntrada = this.filtro.getDocumentoFiscalEntrada();
		ScoAutorizacaoForn autorizacaoForn = this.filtro.getAutorizacaoForn();
		ScoFornecedor fornecedor = this.filtro.getFornecedor();
		Long qtdCnpjRaiz = this.comprasFacade.contarVFornecedorPorCnpjRaiz(getCnpjRaiz(documentoFiscalEntrada.getFornecedor()));
		if (autorizacaoForn == null) {
			if (fornecedor == null) {
				if (qtdCnpjRaiz < 2) {
					this.filtro.setFornecedor(documentoFiscalEntrada.getFornecedor());
				}
			} else {
				if (!getVerificaCNPJDiferentes(documentoFiscalEntrada.getFornecedor().getCgc(), fornecedor.getCgc())) {
					if (qtdCnpjRaiz > 1) {
						limpar();
					} else {
						this.filtro.setFornecedor(documentoFiscalEntrada.getFornecedor());
					}
				}
			}
		} else {
			ScoFornecedor forn = this.comprasFacade.obterFornecedorPorNumero(autorizacaoForn.getPropostaFornecedor().getFornecedor().getNumero());
			String cnpjRaizFornAF = "";
			String cnpjRaizFornNF = getCnpjRaiz(documentoFiscalEntrada.getFornecedor());
			if (forn != null) {
				cnpjRaizFornAF = getCnpjRaiz(forn);
			}
			
			if (qtdCnpjRaiz < 2) {
				if (cnpjRaizFornAF.equals(cnpjRaizFornNF)) {
					this.filtro.setFornecedor(documentoFiscalEntrada.getFornecedor());
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "ERRO_NF_OUTRO_FORNECEDOR");
					this.limparNotaFiscal();
				}
			} else {
				this.filtro.setFornecedor(null);
			}
		}
	}
	
	private Boolean getVerificaCNPJDiferentes(Long cgc, Long cgc2) {
		
		if(cgc == null || cgc2 == null){
			return Boolean.FALSE;
		}
		
		String cnpjFornecedor1 = cgc.toString().length() >= 8 ? cgc.toString().substring(0, 7) : cgc.toString();
		String cnpjFornecedor2 = cgc2.toString().length() >= 8 ? cgc2.toString().substring(0, 7) : cgc2.toString();
		
		return cnpjFornecedor1.equals(cnpjFornecedor2);
	}

	public String getCnpjRaiz(ScoFornecedor forn) {
		String cnpjRaiz = "";
		
		if (forn != null) {
			if (forn.getCnpjCpf().toString().length() >= 8) {
				cnpjRaiz = forn.getCnpjCpf().toString().substring(0, 7);	
			} else {
				cnpjRaiz = forn.getCnpjCpf().toString();
			}
		}
		
		return cnpjRaiz;
	}
	
	public List<SceTipoMovimento> getObterSiglaTipoMovimento(String filter) {
		return this.estoqueFacade.obterSiglaTipoMovimento(filter);
	}
	
	public List<SceEntrSaidSemLicitacaoVO> obterNumeroESLPorSiglaTipoMovimento(String siglaTipoMovimento){
		return returnSGWithCount(this.estoqueFacade.obterNumeroESLPorSiglaTipoMovimento(siglaTipoMovimento, this.filtro.getTipoMovimento().getSigla()), 
				this.estoqueFacade.obterNumeroESLPorSiglaTipoMovimentoCount(siglaTipoMovimento, this.filtro.getTipoMovimento().getSigla()));
	}
	
	public void selecionarFornecedorAposESL() {
		ScoFornecedor scoFornecedor = this.estoqueFacade.obterFornecedorPorId(this.filtro.getNumeroESL().getNumeroFornecedor());
		this.filtro.setFornecedor(scoFornecedor);
		this.readOnlyFornecedor = Boolean.TRUE;
	}
	
	public void selecionarComplementoFornecedorAposAF() {
		ScoAutorizacaoForn numeroComplemento = this.estoqueFacade.obterPorChavePrimaria(this.filtro.getAutorizacaoForn().getNroComplemento().intValue());
		this.filtro.setNroComplementoAF(numeroComplemento);
		this.filtro.setFornecedor(this.filtro.getAutorizacaoForn().getScoFornecedor());
	}
	
	public void limparFiltroFornecedor() {
		this.filtro.setFornecedor(null);		
	}
	
	public void getAtualizaValorSuggestions() {
		this.filtro.setAutorizacaoForn(null);
		this.numeroAF = null;
		this.filtro.setNroComplementoAF(null);
		this.complementoAF = null;
	}
	
	public List<ScoAutorizacaoForn> pesquisarAF(String numeroAf) {
		List<ScoAutorizacaoForn> listaAF = null;
		
		Short complementoAf = this.filtro.getNroComplementoAF() != null ? this.filtro.getNroComplementoAF().getNroComplemento() : null;
		Integer numFornecedor = this.filtro.getFornecedor() != null ? this.filtro.getFornecedor().getNumero() : null;
		
		listaAF = estoqueFacade.pesquisarAF(numeroAf, complementoAf, numFornecedor);
		
		if(numFornecedor == null){
			this.limparNotaFiscal();
		}
		
		return listaAF;
	}

//	private boolean fornecedorDiferenteDocumentoFiscal(Integer numFornecedor) {
//		return this.filtro.getDocumentoFiscalEntrada() != null && this.filtro.getDocumentoFiscalEntrada().getFornecedor() != null && !this.filtro.getDocumentoFiscalEntrada().getFornecedor().getNumero().equals(numFornecedor);
//	}
	
	public void selecionarAF() {
		if (this.filtro.getAutorizacaoForn() != null) {
			numeroAF = this.filtro.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero();
			numeroAFPK = this.filtro.getAutorizacaoForn().getNumero();
		}
		
		if (this.filtro.getNroComplementoAF() == null) {
			List<ScoAutorizacaoForn> listaNroComplemento = estoqueFacade.pesquisarComplemento(numeroAF, complementoAF, getNumeroFornecedor());
			if (listaNroComplemento.size() == 1) {
				this.filtro.setNroComplementoAF(listaNroComplemento.get(0));
				complementoAF = this.filtro.getNroComplementoAF().getNroComplemento();
				
				pesquisarItensRecebimento();				
			} 
		}
		
		if (this.filtro.getFornecedor() == null) {
			List<ScoFornecedor> listaFornecedor = estoqueFacade
				.pesquisarFornecedor(numeroAF, complementoAF, getNumeroFornecedor(), this.filtro.getIndAdiantamentoAF());
			if (listaFornecedor.size() == 1) {
				this.filtro.setFornecedor(listaFornecedor.get(0));
				numeroFornecedor = this.filtro.getFornecedor().getNumero();
			}
		}		
	}
	
	//#11033 - assinatura alterada de object para String conforme nova arquitetura
	public List<ScoAutorizacaoForn> pesquisarComplemento(String nroComplementoAf) {
		List<ScoAutorizacaoForn> listaNroComplemento = null;

		Integer numeroAf = this.filtro.getAutorizacaoForn() != null
		? this.filtro.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero() : null;
		Integer numFornecedor = this.filtro.getFornecedor() != null ? this.filtro.getFornecedor().getNumero() : null;

		listaNroComplemento = estoqueFacade.pesquisarComplemento(numeroAf, nroComplementoAf, numFornecedor);

		return listaNroComplemento;
	}
	
	public void selecionarComplemento() {
		if (this.filtro.getNroComplementoAF() != null) {
			complementoAF = this.filtro.getNroComplementoAF().getNroComplemento();
		}
	
		if (this.filtro.getAutorizacaoForn() == null) {
			List<ScoAutorizacaoForn> listaAF = estoqueFacade.pesquisarAF(numeroAF, complementoAF, getNumeroFornecedor());
			if (listaAF.size() == 1) {
				this.filtro.setAutorizacaoForn(listaAF.get(0));
				numeroAF = this.filtro.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero();
				numeroAFPK = this.filtro.getAutorizacaoForn().getNumero();
		   }
		}
		
		if (this.filtro.getFornecedor() == null) {
			List<ScoFornecedor> listaFornecedor = estoqueFacade
				.pesquisarFornecedor(numeroAF, complementoAF, numeroFornecedor, this.filtro.getIndAdiantamentoAF());
			if (listaFornecedor.size() == 1) {
				this.filtro.setFornecedor(listaFornecedor.get(0));
				numeroFornecedor = this.filtro.getFornecedor().getNumero();
			}
		}
		
		pesquisarItensRecebimento();
	}
	
	public void limparComplemento() {
		this.complementoAF = null;
	}
	
	public void getLimparFornecedor() {
		this.numeroFornecedor = null;
	}
	
	//#11033 - assinatura alterada de object para String conforme nova arquitetura
	public List<ScoFornecedor> pesquisarFornecedor(String numFornecedor) {
		List<ScoFornecedor> listaFornecedor = null;
		
		Integer numeroAf = this.filtro.getAutorizacaoForn() != null
		? this.filtro.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero() : null;
		Short nroComplementoAf = this.filtro.getNroComplementoAF() != null ? this.filtro.getNroComplementoAF().getNroComplemento() : null;
		
		listaFornecedor = estoqueFacade.pesquisarFornecedor(numeroAf, nroComplementoAf, numFornecedor, this.filtro.getIndAdiantamentoAF());
		
		return listaFornecedor;
	}

	public void selecionarFornecedor() {
		if (this.filtro.getAutorizacaoForn() == null) {
			List<ScoAutorizacaoForn> listaAF = estoqueFacade.pesquisarAF(numeroAF, complementoAF, getNumeroFornecedor());
			if (listaAF.size() == 1) {
				this.filtro.setAutorizacaoForn(listaAF.get(0));
				numeroAF = this.filtro.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero();
				numeroAFPK = this.filtro.getAutorizacaoForn().getNumero();
		   }
		}

		if (this.filtro.getNroComplementoAF() == null) {
			List<ScoAutorizacaoForn> listaNroComplemento = estoqueFacade.pesquisarComplemento(numeroAF, complementoAF, getNumeroFornecedor());
			if (listaNroComplemento.size() == 1) {
				this.filtro.setNroComplementoAF(listaNroComplemento.get(0));
				complementoAF = this.filtro.getNroComplementoAF().getNroComplemento();
			}
		}
		
		if (this.filtro.getDocumentoFiscalEntrada() != null) {
			limparNotaFiscal();
		}
		pesquisarItensRecebimento();
	}
	
	public void pesquisarItensRecebimento() {
		if (numeroAF != null) {
			if (this.filtro.getIndAdiantamentoAF()) {
				listaItensRecebimentoAdiantamentoVO = estoqueFacade.pesquisarItensRecebimentoAdiantamento(numeroAF);
			}
		}
	}
	
	public String consultarAf() {
		return "consultarAf";
	}
	
	public String voltar() {
		this.limpar();
		this.limparGradeNroRecebimento();
		this.limparNotaFiscal();
		return this.voltarPara;
	}
	
	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntrada(String strPesquisa) {
		ScoFornecedor fornecedor = this.filtro.getFornecedor();
		List<SceDocumentoFiscalEntrada> listaNotaFiscalEntrada = null;

		listaNotaFiscalEntrada = estoqueFacade
				.pesquisarNFEntradaGeracaoNumeroOuFornecedor(strPesquisa, fornecedor);

		return listaNotaFiscalEntrada;
	}
	
	public String buscarNotaFiscal() {
		return PAGE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA;
	}
	
	public void limparNotaFiscal() {
		this.seqNota = null;
		this.filtro.setDocumentoFiscalEntrada(null);
		this.filtro.setSerieNotaFiscal(null);
		this.filtro.setTipoNotaFiscal(null);
		this.filtro.setDataEmissao(null);
		this.filtro.setDataEntrada(null);
		this.filtro.setValorNota(null);
		this.filtro.setValorComprometidoNFAntesRecebimento(null);
		this.filtro.setValorComprometidoNF(null);
	}
	
	public void limpar() {
		this.numeroAF = null;
		this.complementoAF = null;
		this.numeroFornecedor = null;
		this.filtro.setTipoMovimento(null);
		this.filtro.setIndAdiantamentoAF(false);
		this.filtro.setAutorizacaoForn(null);
		this.filtro.setNroComplementoAF(null);
		this.filtro.setFornecedor(null);
		this.filtro.setNumeroESL(null);
		this.idUnico = 0;
		
		limparGradeNroRecebimento();
	}
	
	public void limparGradeNroRecebimento() {
		this.listaItensRecebimentoAdiantamentoVO = null;
		this.listaItensRecebimentoVO = new ArrayList<ItensRecebimentoVO>();
		this.idUnico = 0;
		this.filtro.setNumeroRecebimento(null);
		this.filtro.setNroComplementoAF(null);
		this.filtro.setFornecedor(null);
	}
		
	// Cálculo do valor unitário x quantidade entregue de um material
	public void calcularValorTotal(ItensRecebimentoAdiantamentoVO item) {		

		if (item.getSaldoQtd() == null || item.getSaldoQtd() <= 0) {
			return;
		}
		// Não aceita digitação de valores <= 0
		if (item.getQtdEntregue() != null && item.getQtdEntregue() <= 0) {
			item.setQtdEntregue(null);
			item.setQtdEntregueAnterior(null);
			item.setValorTotal(null);
			return;
		}
		
		if (item.getQtdEntregue() == null && item.getQtdEntregueAnterior() == null) {
			item.setQtdEntregue(item.getSaldoQtd());
			
		} else if (item.getQtdEntregue() == null && item.getQtdEntregueAnterior() != null) {
			item.setValorTotal(null);
			item.setQtdEntregueAnterior(null);
			
		} else {
			item.setQtdEntregueAnterior(null);
		}

		if (item.getQtdEntregue() != null && item.getValorUnitario() != null && !item.getQtdEntregue().equals(item.getQtdEntregueAnterior())) {
			item.setValorTotal((new BigDecimal(item.getQtdEntregue()).multiply(item.getValorUnitarioConvertido())).setScale(2, RoundingMode.HALF_UP));
			item.setQtdEntregueAnterior(item.getQtdEntregue());
		}			
		calcularValorComprometido();
	}
	
	public void calcularValorComprometido() {
		// Não aceita digitação de valores <= 0
		if (this.listaItensRecebimentoAdiantamentoVO != null) {
			for (ItensRecebimentoAdiantamentoVO itemRecebimento : this.listaItensRecebimentoAdiantamentoVO) {
				if (itemRecebimento.getValorTotal() != null && itemRecebimento.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
					itemRecebimento.setValorTotal(null);
					return;
				}
				
			}
		}
		
		if (this.filtro.getDocumentoFiscalEntrada() != null && this.filtro.getNumeroRecebimento() == null) {
			
			this.filtro.setValorComprometidoNF(this.filtro.getValorComprometidoNFAntesRecebimento());
			
			if (this.listaItensRecebimentoAdiantamentoVO != null) {
				for (ItensRecebimentoAdiantamentoVO itemRecebimento : this.listaItensRecebimentoAdiantamentoVO) {
					if (itemRecebimento.getValorTotal() != null) {
						this.filtro.setValorComprometidoNF(this.filtro.getValorComprometidoNF().add(itemRecebimento.getValorTotal()));
					}
					
				}
			}
		}
	}
	
	public void calcularValorTotalMaterial(ItensRecebimentoVO item) {

		if (item.getQtdEntregue() != null && item.getQtdEntregue() <= 0) {
			item.setQtdEntregue(null);
			item.setValorTotal(null);
			return;
		}
		
		if (item.getValorUnitario() != null && new BigDecimal(item.getValorUnitario()).compareTo(BigDecimal.ZERO) <= 0) {
			item.setValorUnitario(null);
			return;
		}
		
		if (item.getQtdEntregue() != null && item.getValorUnitario() != null) {
			item.setValorTotal((new BigDecimal(item.getQtdEntregue())
				.multiply(new BigDecimal(item.getValorUnitario()))).setScale(2, RoundingMode.HALF_UP));
		}

		calcularValorComprometidoMaterial();
	}
	
	public void calcularValorComprometidoMaterial() {
		if (this.listaItensRecebimentoVO != null) {
			for (ItensRecebimentoVO itemRecebimento : this.listaItensRecebimentoVO) {
				if (itemRecebimento.getValorTotal() != null && itemRecebimento.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
					itemRecebimento.setValorTotal(null);
					return;
				}
			}
		}
		
		if (this.filtro.getDocumentoFiscalEntrada() != null && this.filtro.getNumeroRecebimento() == null) {
			
			this.filtro.setValorComprometidoNF(this.filtro.getValorComprometidoNFAntesRecebimento());
			
			if (this.listaItensRecebimentoVO != null) {
				for (ItensRecebimentoVO itemRecebimento : this.listaItensRecebimentoVO) {
					if (itemRecebimento.getValorTotal() != null) {
						this.filtro.setValorComprometidoNF(this.filtro.getValorComprometidoNF().add(itemRecebimento.getValorTotal()));
					}
				}
			}
		}
	}
	
	public void receberItensAF() {
		this.filtro.setNumeroRecebimento(null);
		
		try {
			// Realiza o recebimento provisório, gerando Nota de Recebimento Provisório
			if (this.listaItensRecebimentoAdiantamentoVO != null && !this.listaItensRecebimentoAdiantamentoVO.isEmpty()) {
				notaRecebProvisorio = estoqueFacade
					.preReceberItensAdiantamentoAF(this.filtro, this.listaItensRecebimentoAdiantamentoVO);
				
				//estoqueFacade.flush();
				
			} else if (this.listaItensRecebimentoVO != null && !this.listaItensRecebimentoVO.isEmpty()) {
				notaRecebProvisorio = estoqueFacade
					.preReceberItensMateriais(this.filtro, this.listaItensRecebimentoVO);
				
				//estoqueFacade.flush();
			}

			if (notaRecebProvisorio != null) {
				// Atualiza atributo que exibe na tela o número da nota
				this.filtro.setNumeroRecebimento(notaRecebProvisorio.getSeq());
				
				// Caso esteja parametrizado, realiza a Confirmação do Recebimento
				if (autFornecimentoFacade.verificarConfirmacaoImediataRecebimento()) {
					this.confirmarRecebimentoAutomaticamente();
				} else {					
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_RECEBIMENTO");						
				}
			}			
		} catch (BaseException e) {
			//getLog().error(e);
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void confirmarRecebimentoAutomaticamente() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e1) {
			LOG.error(e1.getMessage(), e1);
		}
		try{
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal =  this.estoqueFacade.clonarNotaRecebimentoProvisorio(this.notaRecebProvisorio);
			this.validaConfirmacaoRecebimentoVO = this.estoqueFacade.confirmarRecebimento(notaRecebProvisorio, notaRecebimentoProvisorioOriginal,
					nomeMicrocomputador, true, false);

     		//this.estoqueFacade.flush();	
				
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_RECEBIMENTO_CONFIRMACAO");		
			
		} catch(BaseException e){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_RECEBIMENTO");
			this.apresentarExcecaoNegocio(e);
			//info(e);
		} 
	}
	
	public String confirmarRecebimento(){
		return "confirmarRecebimento";
	}
	
	public List<ScoMaterial> getListarMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivos(filter),getListarMateriaisCount(filter));
	}

	public Long getListarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMatriaisAtivosCount(filter);
	}
	
	public List<ScoMarcaComercial> getMarcaComercial(String param) throws BaseException{
		return this.returnSGWithCount(this.comprasFacade.getListaMarcasByNomeOrCodigo(param),getMarcaComercialCount(param));
	}
	
	public Long getMarcaComercialCount(String objPesquisa){
		return this.comprasFacade.getListaMarcasByNomeOrCodigoCount(objPesquisa);
	}
	
	public void adicionarMaterial() {
		boolean camposVazios = false;
		if (this.materialAdd == null) {
			apresentarMsgNegocio("sbMaterial", Severity.ERROR, "CAMPO_OBRIGATORIO", "Material");
			camposVazios = true;
		}
		if (camposVazios) {
			return;
		}

		ItensRecebimentoVO itemRecebimentoVO = new ItensRecebimentoVO();
		itemRecebimentoVO.setCodigoMaterial(materialAdd.getCodigo());
		itemRecebimentoVO.setNomeMaterial(materialAdd.getNome());
		itemRecebimentoVO.setDescricaoMaterial(materialAdd.getDescricao());
		itemRecebimentoVO.setUnidade(materialAdd.getUmdCodigo());
		if (this.marcaComercialAdd != null) {
			itemRecebimentoVO.setNomeMarcaComercial(marcaComercialAdd.getDescricao());
			itemRecebimentoVO.setCodigoMarcaComercial(marcaComercialAdd.getCodigo());
		}
		// Seta um identificador único para poder calcular corretamente os valores de cada linha da grade.
		itemRecebimentoVO.setIdUnico(idUnico);
		
		Double valorUnitario = estoqueFacade.obterValorMaterialSemAF(materialAdd.getCodigo());
		if (valorUnitario != null) {
			itemRecebimentoVO.setValorUnitario(valorUnitario);
		}
		
		this.idUnico ++;

		listaItensRecebimentoVO.add(itemRecebimentoVO);
		materialAdd = null;
		marcaComercialAdd = null;
	}
	
	public void removerMaterial() {
		if (this.listaItensRecebimentoVO.contains(this.materialRemove)) {
			this.listaItensRecebimentoVO.remove(this.materialRemove);
		}
		this.materialRemove = null;
	}
	
	// Getters and Setters
	public FiltroRecebeMaterialServicoSemAFVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRecebeMaterialServicoSemAFVO filtro) {
		this.filtro = filtro;
	}
	
	public List<ItensRecebimentoAdiantamentoVO> getListaItensRecebimentoAdiantamentoVO() {
		return listaItensRecebimentoAdiantamentoVO;
	}

	public void setListaItensRecebimentoAdiantamentoVO(
			List<ItensRecebimentoAdiantamentoVO> listaItensRecebimentoAdiantamentoVO) {
		this.listaItensRecebimentoAdiantamentoVO = listaItensRecebimentoAdiantamentoVO;
	}

	public List<ItensRecebimentoVO> getListaItensRecebimentoVO() {
		return listaItensRecebimentoVO;
	}

	public void setListaItensRecebimentoVO(
			List<ItensRecebimentoVO> listaItensRecebimentoVO) {
		this.listaItensRecebimentoVO = listaItensRecebimentoVO;
	}

	public ValidaConfirmacaoRecebimentoVO getValidaConfirmacaoRecebimentoVO() {
		return validaConfirmacaoRecebimentoVO;
	}

	public void setValidaConfirmacaoRecebimentoVO(
			ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO) {
		this.validaConfirmacaoRecebimentoVO = validaConfirmacaoRecebimentoVO;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}
	
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	
	public Integer getNumeroAFPK() {
		return numeroAFPK;
	}

	public void setNumeroAFPK(Integer numeroAFPK) {
		this.numeroAFPK = numeroAFPK;
	}

	public Short getComplementoAF() {
		return complementoAF;
	}
	
	public void setComplementoAF(Short complementoAF) {
		this.complementoAF = complementoAF;
	}
	
	public Integer getNumeroFornecedor() {
		if (this.filtro.getFornecedor() == null) {
			numeroFornecedor = null;
		} else {
			numeroFornecedor = this.filtro.getFornecedor().getNumero();
		}
		return numeroFornecedor;
	}
	
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	
	public Integer getSeqNota() {
		return seqNota;
	}

	public void setSeqNota(Integer seqNota) {
		this.seqNota = seqNota;
	}

	public SceNotaRecebProvisorio getNotaRecebProvisorio() {
		return notaRecebProvisorio;
	}

	public void setNotaRecebProvisorio(SceNotaRecebProvisorio notaRecebProvisorio) {
		this.notaRecebProvisorio = notaRecebProvisorio;
	}

	public ScoMaterial getMaterialAdd() {
		return materialAdd;
	}

	public void setMaterialAdd(ScoMaterial materialAdd) {
		this.materialAdd = materialAdd;
	}

	public ScoMarcaComercial getMarcaComercialAdd() {
		return marcaComercialAdd;
	}

	public void setMarcaComercialAdd(ScoMarcaComercial marcaComercialAdd) {
		this.marcaComercialAdd = marcaComercialAdd;
	}

	public Integer getIdUnico() {
		return idUnico;
	}

	public void setIdUnico(Integer idUnico) {
		this.idUnico = idUnico;
	}

	public ItensRecebimentoVO getMaterialRemove() {
		return materialRemove;
	}

	public void setMaterialRemove(ItensRecebimentoVO materialRemove) {
		this.materialRemove = materialRemove;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getReadOnlyFornecedor() {
		return readOnlyFornecedor;
	}

	public void setReadOnlyFornecedor(Boolean readOnlyFornecedor) {
		this.readOnlyFornecedor = readOnlyFornecedor;
	}
	
}