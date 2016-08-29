package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentosProvisoriosVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirNotaRecebimentoController;
import br.gov.mec.aghu.estoque.vo.ConfirmacaoRecebimentoFiltroVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.ValidaConfirmacaoRecebimentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ConfirmacaoRecebimentoController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(ConfirmacaoRecebimentoController.class);
	private static final long serialVersionUID = 8919020356375252001L;
	
	private static final String ESTOQUE_MANTERDOCUMENTOFISCALENTRADACRUD = "estoque-manterDocumentoFiscalEntradaCRUD";
	private static final String ESTOQUE_PESQUISARDOCUMENTOFISCALENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";
	private static final String CONFIRMACAO_RECEBIMENTO = "confirmacaoRecebimento";
	
	

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ImprimirNotaRecebimentoController imprimirNotaRecebimentoController;
	
	@EJB
	protected IParametroFacade parametroFacade;

	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private ConfirmacaoRecebimentoFiltroVO filtroVO = new ConfirmacaoRecebimentoFiltroVO();
	private VScoFornecedor fornecedor;
	private Integer seqNota;
	private List<RecebimentosProvisoriosVO> notaRecebProvisorios;
	private List<ItemRecebimentoProvisorioVO> itemRecebimentoProvisorioVOs;
	private Integer seqSelecionado;
	private boolean atualizarListagem;
	
	private Boolean exibeModal = Boolean.FALSE;
	private Boolean exibeModalItens = Boolean.FALSE;
	private SceNotaRecebProvisorio notaRecebimentoProvisorioSelecionada;
	private Integer nroRecebimentoProvisorioSelecionado;
	private Integer nroFornecedorSelecionado;
	private boolean alterandoItemNota; // Determina se o item de nota da lista está sendo alterado 
	private List<ItemRecebimentoProvisorioVO> listaItensSemSaldo = new ArrayList<ItemRecebimentoProvisorioVO>();
	private List<ItemRecebimentoProvisorioVO> listaItensValoresDivergentes = new ArrayList<ItemRecebimentoProvisorioVO>();
	private ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO;
	private boolean indMaterial;
	private String voltarPara;
	private List<SceItemEntrSaidSemLicitacao> listaItemRecebProvisorio = new ArrayList<SceItemEntrSaidSemLicitacao>();
	private boolean exibeModalEsl;
	
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

		if (this.nroRecebimentoProvisorioSelecionado!=null && this.seqNota!=null) {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error(e1.getMessage(), e1);
			}
			try {
				this.nroFornecedorSelecionado = null;
				SceNotaRecebProvisorio notaRecebProvisorioOriginal = this.estoqueFacade.clonarNotaRecebimentoProvisorio(notaRecebimentoProvisorioSelecionada);
				if (notaRecebProvisorioOriginal.getScoAfPedido() != null || notaRecebProvisorioOriginal.getEslSeq() != null) {
					SceDocumentoFiscalEntrada  documentoFiscalEntrada = estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seqNota);
					notaRecebimentoProvisorioSelecionada.setDocumentoFiscalEntrada(documentoFiscalEntrada);
					this.estoqueFacade.atualizarNotaRecebimentoProvisorio(notaRecebimentoProvisorioSelecionada, notaRecebProvisorioOriginal, nomeMicrocomputador);
					if (notaRecebProvisorioOriginal.getEslSeq() != null) {
						SceEntrSaidSemLicitacao esl =	this.estoqueFacade.obterEntrSaidSemLicitacao(notaRecebProvisorioOriginal.getEslSeq());
						if (esl != null) {
							esl.setSceDocumentoFiscalEntrada(documentoFiscalEntrada);
							this.estoqueFacade.atualizarEntrSaidSemLicitacao(esl);
						}

					}
				}
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_NOTA_RECEB_PROVISORIO",
						notaRecebimentoProvisorioSelecionada.getSeq());

				final SceNotaRecebProvisorio notaRecebimentoProvisorioSelecionada = this.estoqueFacade
						.obterNotaRecebProvisorio(this.notaRecebimentoProvisorioSelecionada.getSeq());
				//this.estoqueFacade.refresh(notaRecebimentoProvisorioSelecionada);
				if (notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada() != null) {
					for (RecebimentosProvisoriosVO recebimento : this.notaRecebProvisorios) {
						if (recebimento.getSeq().intValue() == this.notaRecebimentoProvisorioSelecionada.getSeq().intValue()) {
							Integer indice	= this.notaRecebProvisorios.indexOf(recebimento);
							if (indice != null) {
								recebimento.setDocFiscalEntradaNumero(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada().getNumero());
								recebimento.setDocFiscalEntradaSeq(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada().getSeq());
								recebimento.setDtEmissao(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada().getDtEmissao());
								recebimento.setDtAutorizacao(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada().getDtAutorizada());
								recebimento.setValorRecebido(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada().getValorTotalNf());
								this.notaRecebProvisorios.set(indice, recebimento);
								}
							}
					}	
				//	this.estoqueFacade.refresh(notaRecebimentoProvisorioSelecionada.getDocumentoFiscalEntrada());
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			this.nroFornecedorSelecionado = null;
			this.notaRecebimentoProvisorioSelecionada = null;
			this.nroRecebimentoProvisorioSelecionado = null;
			this.seqNota = null;
			this.fornecedor = null;
		} else if (this.seqNota != null && !this.atualizarListagem) {
			this.documentoFiscalEntrada = estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seqNota);
		}else if (this.seqNota != null && this.atualizarListagem) {
			SceDocumentoFiscalEntrada documentoFiscalEntrada = estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seqNota);
			for (RecebimentosProvisoriosVO recebimento : this.notaRecebProvisorios) {
				if (recebimento.getDocFiscalEntradaSeq() != null && recebimento.getDocFiscalEntradaSeq().intValue() == documentoFiscalEntrada.getSeq().intValue()) {
					Integer indice	= this.notaRecebProvisorios.indexOf(recebimento);
					if (indice != null) {							
						recebimento.setDocFiscalEntradaNumero(documentoFiscalEntrada.getNumero());
						recebimento.setDocFiscalEntradaSeq(documentoFiscalEntrada.getSeq());
						recebimento.setDtEmissao(documentoFiscalEntrada.getDtEmissao());
						recebimento.setDtAutorizacao(documentoFiscalEntrada.getDtAutorizada());
						recebimento.setValorRecebido(documentoFiscalEntrada.getValorTotalNf());
						this.notaRecebProvisorios.set(indice, recebimento);
						}
					}
			}	
		}
		
		if (filtroVO.getNrpSeq() != null){
			pesquisar();
		}
	
	}

	/**
	 * Limpa a nota fiscal de entrada
	 */
	public void limparNotaFiscalEntrada() {
		this.documentoFiscalEntrada = null;
		this.seqNota = null;
	}
	
	
	public String notaFiscalReferencia() {
		// view="/estoque/cadastrosapoio/manterDocumentoFiscalEntradaCRUD.xhtml"
		return ESTOQUE_MANTERDOCUMENTOFISCALENTRADACRUD;
	}

	/**
	 * Limpa os filtros da pesquisa
	 */
	public void limpar() {
		this.setFornecedor(null);
		this.setFiltroVO(new ConfirmacaoRecebimentoFiltroVO());
		this.setNotaRecebProvisorios(null);
		this.limparNotaFiscalEntrada();
	}
	
	public void pesquisar() {
		if (this.getFornecedor() != null) {
			this.getFiltroVO().setNumeroFornecedor(getFornecedor().getNumeroFornecedor());
		} else {
			this.getFiltroVO().setNumeroFornecedor(null);
		}
		if (this.documentoFiscalEntrada != null) {
			this.getFiltroVO().setSeqNota(this.documentoFiscalEntrada.getSeq());
		} else {
			this.getFiltroVO().setSeqNota(null);
		}
		this.notaRecebProvisorios = this.estoqueFacade.pesquisarRecebimentoProvisorio(this.getFiltroVO(), Boolean.FALSE, Boolean.FALSE);
		if (this.notaRecebProvisorios != null && !this.notaRecebProvisorios.isEmpty()) {
			this.selecionarNotaRecebProvisorio(this.notaRecebProvisorios.get(0).getSeq());
		}
		else {
			this.itemRecebimentoProvisorioVOs = null;
		}
	}
	
	public void fecharModal() {
		this.listaItensSemSaldo.clear();
		this.listaItensValoresDivergentes.clear();
	}

	public void selecionarNotaRecebProvisorio(final Integer seq) {
		if (seq != null && seq > 0 && !seq.equals(this.seqSelecionado)) {
			try {			
				this.seqSelecionado = seq;
				SceNotaRecebProvisorio notaRecebimentoProvisorio =   this.estoqueFacade.obterNotaRecebProvisorio(seq);
				if (notaRecebimentoProvisorio.getScoAfPedido() != null) {
					this.itemRecebimentoProvisorioVOs = this.estoqueFacade.pesquisarItensNotaRecebimentoProvisorio(notaRecebimentoProvisorio.getSeq(), Boolean.FALSE);
					this.listaItemRecebProvisorio = null;
					this.indMaterial = this.itemRecebimentoProvisorioVOs == null || this.itemRecebimentoProvisorioVOs.isEmpty()|| this.itemRecebimentoProvisorioVOs.get(0).getCodigoMaterial() == null;
				}
				if (notaRecebimentoProvisorio.getEslSeq() != null) {
					this.listaItemRecebProvisorio = new ArrayList<SceItemEntrSaidSemLicitacao>();
					this.listaItemRecebProvisorio = this.estoqueFacade.listaItensRecebimentoProvisorio(notaRecebimentoProvisorio.getSeq());
					this.itemRecebimentoProvisorioVOs = null;
				}
				
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public String buscarNotaFiscalEntrada() {
		if(this.fornecedor != null){
			this.nroFornecedorSelecionado = this.fornecedor.getNumeroFornecedor();
		} else {
			this.nroFornecedorSelecionado = null;
		}
		this.nroRecebimentoProvisorioSelecionado = null;
		return ESTOQUE_PESQUISARDOCUMENTOFISCALENTRADA;
	}

	public void confirmarRecebimentoSemValidacao() {
		if (isExibeModalEsl()) {
			this.confirmarRecebimento(seqSelecionado, true, false);
		} else {
			this.confirmarRecebimento(seqSelecionado, false, false);
		}
	}
	
	public String confirmarRecebimento(Integer seq, Boolean valida, Boolean validarEsl){
		SceNotaRecebProvisorio notaRecebimentoProvisorio =  this.estoqueFacade.obterNotaRecebProvisorio(seq);
		this.seqSelecionado = seq;
		this.notaRecebimentoProvisorioSelecionada = notaRecebimentoProvisorio;
		this.listaItensSemSaldo.clear();
		this.listaItensValoresDivergentes.clear();
		this.exibeModal = Boolean.FALSE;
		this.exibeModalEsl	= Boolean.FALSE;
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e1) {
			LOG.error(e1.getMessage(), e1);
		}
		try{
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal =  this.estoqueFacade.clonarNotaRecebimentoProvisorio(notaRecebimentoProvisorioSelecionada);
			this.validaConfirmacaoRecebimentoVO = this.estoqueFacade.confirmarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal,nomeMicrocomputador, valida, false, validarEsl);
			
			if(validaConfirmacaoRecebimentoVO != null && validaConfirmacaoRecebimentoVO.isValidarEsl()){
				this.exibeModalEsl = Boolean.TRUE;
			}
		
			if(validaConfirmacaoRecebimentoVO!=null&&validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao()!=null&&validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao()){
				this.exibeModal = Boolean.TRUE;
				return CONFIRMACAO_RECEBIMENTO;
			} else if (isExibeModalEsl()){
				return CONFIRMACAO_RECEBIMENTO;
			}
			List<ItemRecebimentoProvisorioVO> listaItensComPendencia = null;
			if(validaConfirmacaoRecebimentoVO!=null){
				listaItensComPendencia = validaConfirmacaoRecebimentoVO.getListaItensComPendencia(); 
			}
			
			if(listaItensComPendencia!=null && listaItensComPendencia.size()>0){
				for(ItemRecebimentoProvisorioVO item: listaItensComPendencia){
					if(item.getExisteSaldo().equals(Boolean.FALSE)){
						listaItensSemSaldo.add(item);
					} else if(item.getValorConfere().equals(Boolean.FALSE)){
						listaItensValoresDivergentes.add(item);
					}
				}
				
				return CONFIRMACAO_RECEBIMENTO;
			} else {
				this.pesquisar();
				if (notaRecebimentoProvisorio.getEslSeq() != null) {
					this.apresentarMsgNegocio(Severity.INFO, "MSG_CONFIRMACAO_RECEBIMENTO_SUCESSO");
				}else{
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CONFIRMAR_RECEBIMENTO", 
							notaRecebimentoProvisorioSelecionada.getNotaRecebimento().getSeq());
				}
		
				// imprimir relatório
				if (notaRecebimentoProvisorioSelecionada.getNotaRecebimento() != null) {
					this.imprimirNotaRecebimento(notaRecebimentoProvisorioSelecionada.getNotaRecebimento().getSeq());
				}
			} 
		
			
		} catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
			LOG.info(e.getMessage(),e);
		} catch (final JRException e) {
			this.tratarExceptionImpressao(e);
		} catch (final SystemException e) {
			this.tratarExceptionImpressao(e);
		} catch (final IOException e) {
			this.tratarExceptionImpressao(e);
		}
		return CONFIRMACAO_RECEBIMENTO;
	}
	
	private void tratarExceptionImpressao(final Exception e) {
		apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRESSAO", e.getMessage());
		LOG.error(e.getMessage(), e);
	}

	/**
	 * Impressão Direta da Nota de Recebimento
	 * 
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	private void imprimirNotaRecebimento(final Integer numeroNotaRecebimento) throws JRException, SystemException, IOException, BaseException {
		boolean imprimirRemoto = false;
		try {
			AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPRIMI_NR_LOCAL_E_REMOTA);
			imprimirRemoto = StringUtils.equalsIgnoreCase(param.getVlrTexto(), "S");
		} catch (ApplicationBusinessException e) {
			LOG.error("Parâmetro não definido: " + AghuParametrosEnum.P_IMPRIMI_NR_LOCAL_E_REMOTA.toString(), e);
		}
		this.imprimirNotaRecebimentoController.setNumNotaRec(numeroNotaRecebimento);
		this.imprimirNotaRecebimentoController.setDuasVias(DominioSimNao.N);
		this.imprimirNotaRecebimentoController.setConsiderarNotaEmpenho(false);
		this.imprimirNotaRecebimentoController.setConsiderarUnidadeFuncional(imprimirRemoto);
		this.imprimirNotaRecebimentoController.directPrint();
	}

	public void gravarItens(Boolean valida) {
		List<ItemRecebimentoProvisorioVO> listaItensAlterados = this.estoqueFacade.pesquisarItensProvisoriosAlterados(itemRecebimentoProvisorioVOs);
		try {
			this.validaConfirmacaoRecebimentoVO = this.estoqueFacade.gravarItensRecebimento(listaItensAlterados, valida);
			if (validaConfirmacaoRecebimentoVO!=null && validaConfirmacaoRecebimentoVO.isValorForaPercentualVariacaoItens()) {
				this.exibeModalItens = Boolean.TRUE;
				return;
			} else {
				this.exibeModalItens = Boolean.FALSE;
			}
			//this.estoqueFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_ITENS");
		} catch(BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void calcularVariacao(ItemRecebimentoProvisorioVO item) {
		this.estoqueFacade.calcularVariacaoItemRecebimentoProvisorio(item);
	}
	
	public void estornarRecebimento() throws ApplicationBusinessException {
		try {
			this.notaRecebimentoProvisorioSelecionada =   this.estoqueFacade.obterNotaRecebProvisorio(seqSelecionado);
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal = this.estoqueFacade.clonarNotaRecebimentoProvisorio(this.notaRecebimentoProvisorioSelecionada); 
			this.estoqueFacade.estornarRecebimento(notaRecebimentoProvisorioSelecionada, notaRecebimentoProvisorioOriginal);
			if (notaRecebimentoProvisorioSelecionada.getEslSeq() == null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNAR_RECEBIMENTO", notaRecebimentoProvisorioSelecionada.getSeq());
				
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MSG_ESTORNO_RECEBIMENTO", notaRecebimentoProvisorioSelecionada.getSeq());
				
			}
			this.pesquisar();
		} catch(BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void verificarComplemento() {
		if (this.getFiltroVO() != null && this.getFiltroVO().getLctNumero() == null) {
			this.getFiltroVO().setNroComplemento(null);
		}
	}
	
	public String redirecionarNotaFiscal(Integer seq){
		SceNotaRecebProvisorio notaRecebProvisorio =  this.estoqueFacade.obterNotaRecebProvisorio(seq);
		this.notaRecebimentoProvisorioSelecionada = notaRecebProvisorio;
		setNroFornecedorSelecionado(null);
		if (notaRecebProvisorio != null && notaRecebProvisorio.getScoAfPedido() != null && notaRecebProvisorio.getScoAfPedido().getScoAutorizacaoForn() != null
				&& notaRecebProvisorio.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor() != null
				&& notaRecebProvisorio.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor() != null) {
			nroFornecedorSelecionado = notaRecebProvisorio.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getNumero();
		}
		nroRecebimentoProvisorioSelecionado = notaRecebProvisorio.getSeq();
		return ESTOQUE_PESQUISARDOCUMENTOFISCALENTRADA;
	}
	
	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro, 100),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.contarVFornecedorPorCgcCpfRazaoSocial(parametro);
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntrada(String strPesquisa) {
		ScoFornecedor scoFornecedor = new ScoFornecedor();
		
		if (this.fornecedor!=null) {
			scoFornecedor = comprasFacade.obterFornecedorPorChavePrimaria(this.fornecedor.getNumeroFornecedor());
		} else {
			scoFornecedor = null;
		}
	
		List<SceDocumentoFiscalEntrada> listaNotaFiscalEntrada = estoqueFacade.pesquisarNotafiscalEntradaNumeroOuFornecedor(strPesquisa, scoFornecedor);

		return listaNotaFiscalEntrada;
	}
	
	
	public void limparNotaFiscal() {
		documentoFiscalEntrada = null;
		seqNota = null;
	}
	
	/*
	 * Getters e setters
	 */
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		if (documentoFiscalEntrada != null){
			this.seqNota = documentoFiscalEntrada.getSeq();
		} else {
			this.seqNota = null;
		}
		
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}
	
	public boolean isAlterandoItemNota() {
		return alterandoItemNota;
	}
	
	public void setAlterandoItemNota(boolean alterandoItemNota) {
		this.alterandoItemNota = alterandoItemNota;
	}

	public void setSeqNota(Integer seqNota) {
		this.seqNota = seqNota;
	}

	public Integer getSeqNota() {
		return seqNota;
	}

	public void setFiltroVO(ConfirmacaoRecebimentoFiltroVO filtroVO) {
		this.filtroVO = filtroVO;
	}

	public ConfirmacaoRecebimentoFiltroVO getFiltroVO() {
		return filtroVO;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setNotaRecebProvisorios(List<RecebimentosProvisoriosVO> notaRecebProvisorios) {
		this.notaRecebProvisorios = notaRecebProvisorios;
	}

	public List<RecebimentosProvisoriosVO> getNotaRecebProvisorios() {
		return notaRecebProvisorios;
	}

	public void setSeqSelecionado(Integer seqSelecionado) {
		this.seqSelecionado = seqSelecionado;
	}

	public Integer getSeqSelecionado() {
		return seqSelecionado;
	}

	public void setItemRecebimentoProvisorioVOs(List<ItemRecebimentoProvisorioVO> itemRecebimentoProvisorioVOs) {
		this.itemRecebimentoProvisorioVOs = itemRecebimentoProvisorioVOs;
	}

	public List<ItemRecebimentoProvisorioVO> getItemRecebimentoProvisorioVOs() {
		return itemRecebimentoProvisorioVOs;
	}



	public SceNotaRecebProvisorio getNotaRecebimentoProvisorioSelecionada() {
		return notaRecebimentoProvisorioSelecionada;
	}

	public void setNotaRecebimentoProvisorioSelecionada(SceNotaRecebProvisorio notaRecebimentoProvisorioSelecionada) {
		this.notaRecebimentoProvisorioSelecionada = notaRecebimentoProvisorioSelecionada;
	}

	public Integer getNroFornecedorSelecionado() {
		return nroFornecedorSelecionado;
	}

	public void setNroFornecedorSelecionado(Integer nroFornecedorSelecionado) {
		this.nroFornecedorSelecionado = nroFornecedorSelecionado;
	}

	public Integer getNroRecebimentoProvisorioSelecionado() {
		return nroRecebimentoProvisorioSelecionado;
	}

	public void setNroRecebimentoProvisorioSelecionado(Integer nroRecebimentoProvisorioSelecionado) {
		this.nroRecebimentoProvisorioSelecionado = nroRecebimentoProvisorioSelecionado;
	}

	public Boolean getExibeModal() {
		return exibeModal;
	}

	public void setExibeModal(Boolean exibeModal) {
		this.exibeModal = exibeModal;
	}

	public Boolean getExibeModalItens() {
		return exibeModalItens;
	}

	public void setExibeModalItens(Boolean exibeModalItens) {
		this.exibeModalItens = exibeModalItens;
	}

	public List<ItemRecebimentoProvisorioVO> getListaItensSemSaldo() {
		return listaItensSemSaldo;
	}

	public void setListaItensSemSaldo(
			List<ItemRecebimentoProvisorioVO> listaItensSemSaldo) {
		this.listaItensSemSaldo = listaItensSemSaldo;
	}

	public List<ItemRecebimentoProvisorioVO> getListaItensValoresDivergentes() {
		return listaItensValoresDivergentes;
	}

	public void setListaItensValoresDivergentes(List<ItemRecebimentoProvisorioVO> listaItensValoresDivergentes) {
		this.listaItensValoresDivergentes = listaItensValoresDivergentes;
	}

	public ValidaConfirmacaoRecebimentoVO getValidaConfirmacaoRecebimentoVO() {
		return validaConfirmacaoRecebimentoVO;
	}

	public void setValidaConfirmacaoRecebimentoVO(ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO) {
		this.validaConfirmacaoRecebimentoVO = validaConfirmacaoRecebimentoVO;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setIndMaterial(boolean indMaterial) {
		this.indMaterial = indMaterial;
	}

	public boolean isIndMaterial() {
		return indMaterial;
	}
	
	/**
	 * @return the exibeModalEsl
	 */
	public boolean isExibeModalEsl() {
		return exibeModalEsl;
	}

	public List<SceItemEntrSaidSemLicitacao> getListaItemRecebProvisorio() {
		return listaItemRecebProvisorio;
	}

	public void setListaItemRecebProvisorio(List<SceItemEntrSaidSemLicitacao> listaItemRecebProvisorio) {
		this.listaItemRecebProvisorio = listaItemRecebProvisorio;
	}
	
	/**
	 * @param atualizarListagem the atualizarListagem to set
	 */
	public void setAtualizarListagem(boolean atualizarListagem) {
		this.atualizarListagem = atualizarListagem;
	}
	/**
	 * @return the atualizarListagem
	 */
	public boolean isAtualizarListagem() {
		return atualizarListagem;
	}
}