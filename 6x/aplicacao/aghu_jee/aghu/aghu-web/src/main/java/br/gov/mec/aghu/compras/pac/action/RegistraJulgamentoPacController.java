package br.gov.mec.aghu.compras.pac.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RegistraJulgamentoPacController extends ActionController {

	private static final long serialVersionUID = 6311943154533755902L;

	private static final String PAGE_ESCOLHE_CONDICAO_PAGAMENTO_JULGAMENTO_PAC = "escolheCondicaoPagamentoJulgamentoPac";
	private static final String PAGE_REGISTRA_JULGAMENTO_LOTE = "registraJulgamentoPacLote";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private Integer numeroCondicaoPagamentoSelecionado;

	// parametros
	private Integer numeroPac;

	// cabecalho
	private String descricaoPac;
	private ScoModalidadeLicitacao modalidadePac;
	private Date dataPublicacao;

	private ScoFaseSolicitacaoVO faseSolicitacao;
	private DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento;
	private DominioSituacaoJulgamento pendentePor;
	private String razaoSocial;
	private ScoMarcaComercial marcaComercial;
	private Date dataEscolha;
	private ScoUnidadeMedida unidadeSolicitacao;
	private ScoCriterioEscolhaProposta criterioEscolha;
	private ScoCondicaoPagamentoPropos condicaoPagamento;
	private Integer qtdeParcelas;
	private BigDecimal percAcrescimo;
	private BigDecimal percDesconto;
	private Integer numeroFornecedor;
	private Short numeroItemPac;
	private DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao;

	// URL e demais atributos para botão voltar
	private String voltarParaUrl;

	// grid
	private List<ScoItemPropostaFornecedor> listaItensProposta;
	private ScoItemPropostaFornecedor itemEmEdicao;
	private Boolean propostaEmEdicao;
	private Short numeroItemProposta;

	// controles de tela
	private Boolean itemPacCancelado;
	private Boolean itemPacJulgado;
	private Boolean itemPendente;
	private Boolean itemDesclassificado;
	private Boolean itemPacPossuiPropostaEscolhida;
	private Boolean estadoOriginal;
	private Boolean modalConfirmacao;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	// botões
	public void gravar(Boolean forceUpdate) {
		if (this.itemDesclassificado != null && this.itemDesclassificado && this.criterioEscolha != null && !forceUpdate) {
			this.modalConfirmacao = Boolean.TRUE;
		} else {
			if ((this.motivoCancelamento == null && !this.verificarPendenciaSelecionada() && this.criterioEscolha == null
					&& this.motivoDesclassificacao == null && this.itemPacJulgado == Boolean.FALSE
					&& this.itemPacCancelado == Boolean.FALSE && this.itemPendente == Boolean.FALSE && this.itemDesclassificado == Boolean.FALSE)
					|| (this.estadoOriginal)
					|| this.listaItensProposta.isEmpty()
					&& !this.verificarPendenciaSelecionada()
					&& this.itemPendente == Boolean.FALSE
					&& this.itemPacCancelado == Boolean.FALSE
					&& this.motivoCancelamento == null) {
				if (this.listaItensProposta.isEmpty()) {
					apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_JULGAMENTO_MSG011a");
				} else {
					apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_JULGAMENTO_MSG011");
				}
			} else {
				try {
					this.pacFacade.registrarJulgamentoPac(this.itemEmEdicao, this.faseSolicitacao, this.condicaoPagamento,
							this.motivoCancelamento, this.pendentePor, this.criterioEscolha, this.motivoDesclassificacao);

					this.mostrarMensagemJulgamento();
					this.mostrarMensagemCancelamento();
					this.mostrarMensagemDesclassificacao();
					this.mostrarMensagemItemPendente();

					this.inicializarControles();
					this.limparDadosCabecalhoItem();
					this.atualizarDadosCabecalhoItem();
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	public String escolheCondicaoPagamentoJulgamentoPac() {
		return PAGE_ESCOLHE_CONDICAO_PAGAMENTO_JULGAMENTO_PAC;
	}
	
	public String acessarJulgamentoLote(){
		return PAGE_REGISTRA_JULGAMENTO_LOTE;
	}

	public void editarItemProposta(ScoItemPropostaFornecedor item) {
		this.itemEmEdicao = item;
		if (item.getPropostaFornecedor() != null && item.getPropostaFornecedor().getFornecedor() != null) {
			this.razaoSocial = this.comprasFacade.montarRazaoSocialFornecedor(item.getPropostaFornecedor().getFornecedor());
			this.numeroFornecedor = item.getPropostaFornecedor().getFornecedor().getNumero();
		} else {
			this.razaoSocial = null;
			this.numeroFornecedor = null;
		}

		this.numeroItemProposta = item.getId().getNumero();
		this.marcaComercial = item.getMarcaComercial();
		if (item.getDtEscolha() == null) {
			this.dataEscolha = new Date();
		} else {
			this.dataEscolha = item.getDtEscolha();
		}
		this.dataEscolha = item.getDtEscolha();
		this.motivoDesclassificacao = item.getMotDesclassif();
		if (this.motivoDesclassificacao != null) {
			this.itemDesclassificado = Boolean.TRUE;
		} else {
			this.itemDesclassificado = Boolean.FALSE;
		}
		if (item.getIndEscolhido() == Boolean.TRUE) {
			this.criterioEscolha = item.getCriterioEscolhaProposta();
		} else {
			this.criterioEscolha = null;
		}
		if (this.criterioEscolha != null) {
			this.itemPacJulgado = Boolean.TRUE;
		} else {
			this.itemPacJulgado = Boolean.FALSE;
		}
		this.preencherCondicaoProposta(item);
	}

	public List<ScoCriterioEscolhaProposta> listarCriterioEscolhaProposta() {
		return comprasCadastrosBasicosFacade.pesquisarCriterioEscolhaProposta();
		
	}
	
	private void preencherCondicaoProposta(ScoItemPropostaFornecedor item) {
		if (item.getCondicaoPagamentoPropos() != null) {
			this.condicaoPagamento = item.getCondicaoPagamentoPropos();
			this.percAcrescimo = this.getDefaultPercentual(item.getCondicaoPagamentoPropos().getPercAcrescimo(), BigDecimal.ZERO);
			this.percDesconto = this.getDefaultPercentual(item.getCondicaoPagamentoPropos().getPercDesconto(), BigDecimal.ZERO);
			this.qtdeParcelas = this.obterQtdeParcelas(item);
			/*if (item.getCondicaoPagamentoPropos().getParcelas() != null
					&& item.getCondicaoPagamentoPropos().getParcelas().size() > 0) {
				this.qtdeParcelas = item.getCondicaoPagamentoPropos().getParcelas().size();
			} else {
				this.qtdeParcelas = 1;
			}*/
		} else {
			List<ScoCondicaoPagamentoPropos> listaFormasPagamento;
			listaFormasPagamento = this.pacFacade.pesquisarCondicaoPagamentoProposta(this.numeroFornecedor, this.numeroPac,
					this.numeroItemProposta);

			if (listaFormasPagamento != null && listaFormasPagamento.size() == 1) {
				this.condicaoPagamento = listaFormasPagamento.get(0);
				this.percAcrescimo = this.getDefaultPercentual(listaFormasPagamento.get(0).getPercAcrescimo(), BigDecimal.ZERO);
				this.percDesconto = this.getDefaultPercentual(listaFormasPagamento.get(0).getPercDesconto(), BigDecimal.ZERO);

				if (listaFormasPagamento.get(0).getParcelas() != null && listaFormasPagamento.get(0).getParcelas().size() > 0) {
					this.qtdeParcelas = listaFormasPagamento.get(0).getParcelas().size();
				} else {
					this.qtdeParcelas = 1;
				}
			} else {
				this.condicaoPagamento = null;
				this.percAcrescimo = null;
				this.percDesconto = null;
				this.qtdeParcelas = null;
			}
		}
	}

	public String voltar() {
		this.numeroCondicaoPagamentoSelecionado = null;
		return voltarParaUrl;
	}

	// suggestions

	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(String param) {
		return this.pacFacade.pesquisarItemLicitacao(param, this.numeroPac);
	}

	// métodos de auxílio

	private Boolean verificarPendenciaSelecionada() {
		return (this.pendentePor != null && this.pendentePor != DominioSituacaoJulgamento.SJ && this.pendentePor != DominioSituacaoJulgamento.JU);
	}

	private void mostrarMensagemJulgamento() {
		if (this.itemPacJulgado == Boolean.FALSE && this.criterioEscolha != null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG001");
		}
		if (this.itemPacJulgado == Boolean.TRUE && this.criterioEscolha == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG005");
		}
	}

	private void mostrarMensagemCancelamento() {
		if (!this.itemPacCancelado && this.motivoCancelamento != null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG002");
		}
		if (this.itemPacCancelado && this.motivoCancelamento == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG003");
		}
	}

	private void mostrarMensagemItemPendente() {
		if (!this.itemPendente && this.verificarPendenciaSelecionada()) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG007");
		}
		if (this.itemPendente && !this.verificarPendenciaSelecionada()) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG013");
		}
	}

	private void mostrarMensagemDesclassificacao() {
		if (this.itemDesclassificado != null) {
			if ((!this.itemDesclassificado && this.motivoDesclassificacao != null)
					|| (this.itemDesclassificado && this.motivoDesclassificacao == null)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG010");
			}
		}
	}

	public void atualizarCombos(int caller) {
		switch (caller) {
		case 0:
			this.pendentePor = null;
			this.criterioEscolha = null;
			this.motivoDesclassificacao = null;
			this.estadoOriginal = Boolean.FALSE;
			break;
		case 1:
			this.motivoCancelamento = null;
			this.criterioEscolha = null;
			this.motivoDesclassificacao = null;
			this.estadoOriginal = Boolean.FALSE;
			break;
		case 2:
			this.pendentePor = null;
			this.motivoCancelamento = null;
			this.motivoDesclassificacao = null;
			this.estadoOriginal = Boolean.FALSE;
			break;
		case 3:
			this.pendentePor = null;
			this.motivoCancelamento = null;
			this.criterioEscolha = null;
			this.estadoOriginal = Boolean.FALSE;
			break;
		}
	}

	public BigDecimal getDefaultPercentual(BigDecimal vlr, BigDecimal defaultVlr) {
		if (vlr != null && vlr.compareTo(BigDecimal.ZERO) > 0) {
			return vlr;
		} else {
			return defaultVlr;
		}
	}

	public String obterDocFormatado(ScoFornecedor fornecedor) {
		return this.comprasFacade.obterCnpjCpfFornecedorFormatado(fornecedor);
	}

	public String obterParecerTecnicoItemProposta(ScoItemPropostaFornecedor item) {
		return this.pacFacade.obterParecerTecnicoItemProposta(item);
	}

	public Boolean verificarItemPropostaFornecedorEmAf(ScoItemPropostaVO item) {
		return this.pacFacade.verificarItemPropostaFornecedorEmAf(this.numeroPac, item.getNumeroItemProposta(),
				item.getFornecedorProposta());
	}

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public void atualizarDadosCabecalhoItem() {
		if (this.getFaseSolicitacao() != null) {
			this.motivoCancelamento = this.getFaseSolicitacao().getItemLicitacao().getMotivoCancel();
			if (this.motivoCancelamento != null) {
				this.itemPacCancelado = Boolean.TRUE;
			} else {
				this.itemPacCancelado = Boolean.FALSE;
			}
			this.pendentePor = this.getFaseSolicitacao().getItemLicitacao().getSituacaoJulgamento();
			if (this.verificarPendenciaSelecionada()) {
				this.itemPendente = Boolean.TRUE;
			} else {
				this.itemPendente = Boolean.FALSE;
			}

			this.numeroItemPac = this.getFaseSolicitacao().getItemLicitacao().getId().getNumero();
			if (this.getFaseSolicitacao().getSolicitacaoCompra() != null) {
				this.unidadeSolicitacao = this.getFaseSolicitacao().getSolicitacaoCompra().getUnidadeMedida();
			} else {
				this.unidadeSolicitacao = this.comprasFacade.obterScoUnidadeMedidaOriginal("UN");
			}
			this.listaItensProposta = this.pacFacade.pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(this.numeroPac,
					this.numeroItemPac);
			this.itemPacPossuiPropostaEscolhida = this.pacFacade.verificarItemPacPossuiPropostaEscolhida(this.numeroPac,
					this.numeroItemPac);
			this.estadoOriginal = Boolean.TRUE;
		} else {
			this.limparDadosCabecalhoItem();
		}
	}

	public Boolean obterDadosLicitacao() {
		ScoLicitacao licitacao = this.pacFacade.obterLicitacao(this.numeroPac);

		if (licitacao != null) {
			this.descricaoPac = licitacao.getNumeroDescricao();
			this.modalidadePac = licitacao.getModalidadeLicitacao();
			this.dataPublicacao = licitacao.getDtPublicacao();
		} else {
			this.descricaoPac = null;
			this.modalidadePac = null;
			this.dataPublicacao = null;
		}

		if (this.listaItensProposta == null) {
			this.listaItensProposta = new ArrayList<ScoItemPropostaFornecedor>();
		}
		return (licitacao != null);
	}

	private void inicializarControles() {
		this.itemPacCancelado = Boolean.FALSE;
		this.itemPacJulgado = Boolean.FALSE;
		this.itemPendente = Boolean.FALSE;
		this.itemPacPossuiPropostaEscolhida = Boolean.FALSE;
		this.itemDesclassificado = Boolean.FALSE;
		this.itemEmEdicao = null;
		this.modalConfirmacao = Boolean.FALSE;
	}

	private void limparDadosCabecalhoItem() {
		this.motivoCancelamento = null;
		this.pendentePor = null;
		this.unidadeSolicitacao = null;
		if (this.listaItensProposta == null) {
			this.listaItensProposta = new ArrayList<ScoItemPropostaFornecedor>();
		} else {
			this.listaItensProposta.clear();
		}
		this.itemEmEdicao = null;
		this.motivoDesclassificacao = null;
		this.razaoSocial = null;
		this.numeroFornecedor = null;
		this.numeroItemProposta = null;
		this.marcaComercial = null;
		this.dataEscolha = null;
		this.criterioEscolha = null;
		this.condicaoPagamento = null;
		this.percAcrescimo = null;
		this.percDesconto = null;
		this.qtdeParcelas = null;
		this.itemPacPossuiPropostaEscolhida = Boolean.FALSE;
		this.estadoOriginal = Boolean.TRUE;
	}

	public void iniciar() {
	 

	 

		this.modalConfirmacao = Boolean.FALSE;
		if (this.numeroCondicaoPagamentoSelecionado == null) {
			if (this.listaItensProposta != null && this.listaItensProposta.isEmpty()) {
				this.inicializarControles();
			}

			if (this.numeroPac != null) {
				this.obterDadosLicitacao();
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAMETROS_INVALIDOS");
			}
		} else {
			this.condicaoPagamento = this.pacFacade
					.obterCondicaoPagamentoPropostaPorNumero(this.numeroCondicaoPagamentoSelecionado);
			this.percAcrescimo = this.getDefaultPercentual(this.condicaoPagamento.getPercAcrescimo(), BigDecimal.ZERO);
			this.percDesconto = this.getDefaultPercentual(this.condicaoPagamento.getPercDesconto(), BigDecimal.ZERO);
			if (this.condicaoPagamento.getParcelas() != null && this.condicaoPagamento.getParcelas().size() > 0) {
				this.qtdeParcelas = this.condicaoPagamento.getParcelas().size();
			} else {
				this.qtdeParcelas = 1;
			}
		}
	
	}
	
	public Integer obterQtdeParcelas(ScoItemPropostaFornecedor item){
		if (item.getCondicaoPagamentoPropos() != null){
			Long parcelas = this.comprasFacade.contarParcelasPagamentoProposta(item.getCondicaoPagamentoPropos());
			if (parcelas != null && parcelas > 0){
				return parcelas.intValue();
			}
			else {
				return Integer.valueOf(1);
			}
			
		}
		return Integer.valueOf(0);
	}

	// gets and sets

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoItemPropostaFornecedor getItemEmEdicao() {
		return itemEmEdicao;
	}

	public void setItemEmEdicao(ScoItemPropostaFornecedor itemEmEdicao) {
		this.itemEmEdicao = itemEmEdicao;
	}

	public Boolean getPropostaEmEdicao() {
		return propostaEmEdicao;
	}

	public void setPropostaEmEdicao(Boolean propostaEmEdicao) {
		this.propostaEmEdicao = propostaEmEdicao;
	}

	public Short getNumeroItemProposta() {
		return numeroItemProposta;
	}

	public void setNumeroItemProposta(Short numeroItemProposta) {
		this.numeroItemProposta = numeroItemProposta;
	}

	public ScoModalidadeLicitacao getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(ScoModalidadeLicitacao modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public DominioMotivoCancelamentoComissaoLicitacao getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public DominioSituacaoJulgamento getPendentePor() {
		return pendentePor;
	}

	public void setPendentePor(DominioSituacaoJulgamento pendentePor) {
		this.pendentePor = pendentePor;
	}

	public Date getDataEscolha() {
		return dataEscolha;
	}

	public void setDataEscolha(Date dataEscolha) {
		this.dataEscolha = dataEscolha;
	}

	public ScoCriterioEscolhaProposta getCriterioEscolha() {
		return criterioEscolha;
	}

	public void setCriterioEscolha(ScoCriterioEscolhaProposta criterioEscolha) {
		this.criterioEscolha = criterioEscolha;
	}

	public ScoCondicaoPagamentoPropos getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(ScoCondicaoPagamentoPropos condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public Integer getQtdeParcelas() {
		return qtdeParcelas;
	}

	public void setQtdeParcelas(Integer qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	public BigDecimal getPercAcrescimo() {
		return percAcrescimo;
	}

	public void setPercAcrescimo(BigDecimal percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}

	public BigDecimal getPercDesconto() {
		return percDesconto;
	}

	public void setPercDesconto(BigDecimal percDesconto) {
		this.percDesconto = percDesconto;
	}

	public ScoUnidadeMedida getUnidadeSolicitacao() {
		return unidadeSolicitacao;
	}

	public void setUnidadeSolicitacao(ScoUnidadeMedida unidadeSolicitacao) {
		this.unidadeSolicitacao = unidadeSolicitacao;
	}

	public ScoFaseSolicitacaoVO getFaseSolicitacao() {
		return faseSolicitacao;
	}

	public void setFaseSolicitacao(ScoFaseSolicitacaoVO faseSolicitacao) {
		this.faseSolicitacao = faseSolicitacao;
	}

	public List<ScoItemPropostaFornecedor> getListaItensProposta() {
		return listaItensProposta;
	}

	public void setListaItensProposta(List<ScoItemPropostaFornecedor> listaItensProposta) {
		this.listaItensProposta = listaItensProposta;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}

	public Boolean getItemPacCancelado() {
		return itemPacCancelado;
	}

	public void setItemPacCancelado(Boolean itemPacCancelado) {
		this.itemPacCancelado = itemPacCancelado;
	}

	public Boolean getItemPacJulgado() {
		return itemPacJulgado;
	}

	public void setItemPacJulgado(Boolean itemPacJulgado) {
		this.itemPacJulgado = itemPacJulgado;
	}

	public Boolean getItemPendente() {
		return itemPendente;
	}

	public void setItemPendente(Boolean itemPendente) {
		this.itemPendente = itemPendente;
	}

	public Boolean getItemPacPossuiPropostaEscolhida() {
		return itemPacPossuiPropostaEscolhida;
	}

	public void setItemPacPossuiPropostaEscolhida(Boolean itemPacPossuiPropostaEscolhida) {
		this.itemPacPossuiPropostaEscolhida = itemPacPossuiPropostaEscolhida;
	}

	public DominioMotivoDesclassificacaoItemProposta getMotivoDesclassificacao() {
		return motivoDesclassificacao;
	}

	public void setMotivoDesclassificacao(DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) {
		this.motivoDesclassificacao = motivoDesclassificacao;
	}

	public Boolean getItemDesclassificado() {
		return itemDesclassificado;
	}

	public void setItemDesclassificado(Boolean itemDesclassificado) {
		this.itemDesclassificado = itemDesclassificado;
	}

	public Boolean getEstadoOriginal() {
		return estadoOriginal;
	}

	public void setEstadoOriginal(Boolean estadoOriginal) {
		this.estadoOriginal = estadoOriginal;
	}

	public Boolean getModalConfirmacao() {
		return modalConfirmacao;
	}

	public void setModalConfirmacao(Boolean modalConfirmacao) {
		this.modalConfirmacao = modalConfirmacao;
	}
}
