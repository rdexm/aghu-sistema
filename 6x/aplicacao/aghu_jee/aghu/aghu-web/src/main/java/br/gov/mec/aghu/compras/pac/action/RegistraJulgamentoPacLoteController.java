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
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RegistraJulgamentoPacLoteController extends ActionController {

	private static final long serialVersionUID = 6311943154533755902L;

	private static final String PAGE_ESCOLHE_COND_PAG_JULGAMENTO_PAC = "escolheCondicaoPagamentoJulgamentoPac";
	private static final String PAGE_REGISTRA_JULGAMENTO_PAC = "registraJulgamentoPac";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private Integer numeroCondicaoPagamentoSelecionado;

	// parametros
	private Integer numeroPac;

	// cabecalho pac
	private String descricaoPac;
	private DominioSituacaoLicitacao situacaoPac;
	private ScoModalidadeLicitacao modalidadePac;
	private Date dataPublicacao;
	private ScoFornecedor fornecedor;
	private String razaoSocial;
	private DominioSimNaoTodos mostraEscolhido;
	private ScoCriterioEscolhaProposta criterioEscolha;
	private ScoCondicaoPagamentoPropos condicaoPagamento;
	private Integer qtdeParcelas;
	private BigDecimal percAcrescimo;
	private BigDecimal percDesconto;

	// URL e demais atributos para botão voltar
	private String voltarParaUrl;

	// grid
	private List<ScoItemPropostaVO> listaItensProposta;
	private Short numeroItemProposta;
	

	// controles de tela
	private Boolean allChecked;
	private Boolean disableCheckAll;
	
	private List<ScoItemPropostaVO> listaItensPropostaSelecionados;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	// botões
	public void limpar() {
		if (this.listaItensProposta == null) {
			this.listaItensProposta = new ArrayList<ScoItemPropostaVO>();
		} else {
			this.listaItensProposta.clear();
		}

		this.fornecedor = null;
		this.mostraEscolhido = DominioSimNaoTodos.NAO;
		this.razaoSocial = null;
		this.criterioEscolha = null;
		this.condicaoPagamento = null;
		this.percAcrescimo = null;
		this.percDesconto = null;
		this.qtdeParcelas = null;
		this.allChecked = Boolean.FALSE;
	}

	public void pesquisar() {
		this.listaItensProposta = this.pacFacade.pesquisarPropostaFornecedorParaJulgamentoLote(this.numeroPac, this.fornecedor,
				this.mostraEscolhido);
		this.disableCheckAll = this.checkAllDisabled();
	}

	public String escolheCondicaoPagamentoJulgamentoPac() {
		return PAGE_ESCOLHE_COND_PAG_JULGAMENTO_PAC;
	}
	
	public String acessarJulgamentoItem(){
		return PAGE_REGISTRA_JULGAMENTO_PAC;
	}

	public void gravar() {
		try {
			 List<ScoItemPropostaVO> listaItensPropostaGravar = new ArrayList<ScoItemPropostaVO>();
			 for(ScoItemPropostaVO itemPropVo: this.listaItensProposta){
				 Integer index = this.listaItensPropostaSelecionados.indexOf(itemPropVo);
				 if(index > -1){
					 itemPropVo.setMarcadoJulgamento(Boolean.TRUE);
				 }
				 else {
					 itemPropVo.setMarcadoJulgamento(Boolean.FALSE); 
				 }
				 
				 listaItensPropostaGravar.add(itemPropVo);				
			 }
						
			this.pacFacade.registrarJulgamentoPacLote(listaItensPropostaGravar, this.condicaoPagamento, this.criterioEscolha);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_LOTE_MSG001");

			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		this.numeroCondicaoPagamentoSelecionado = null;
		this.descricaoPac = null;
		return voltarParaUrl;
	}
	
	public List<ScoCriterioEscolhaProposta> listarCriterioEscolhaProposta() {
		return comprasCadastrosBasicosFacade.pesquisarCriterioEscolhaProposta();	
	}


	public void checkAll() {
		if (this.allChecked) {
			for (ScoItemPropostaVO item : this.listaItensProposta) {
				if (!item.getDesabilitaCheckboxJulgamentoLote()) {
					item.setMarcadoJulgamento(Boolean.FALSE);
				}
			}
			this.allChecked = Boolean.FALSE;
		} else {
			for (ScoItemPropostaVO item : this.listaItensProposta) {
				if (!item.getDesabilitaCheckboxJulgamentoLote()) {
					item.setMarcadoJulgamento(Boolean.TRUE);
				}
			}
			this.allChecked = Boolean.TRUE;
		}
	}

	public Boolean checkAllDisabled() {
		Boolean ret = Boolean.TRUE;

		for (ScoItemPropostaVO item : this.listaItensProposta) {
			if (!item.getDesabilitaCheckboxJulgamentoLote()) {
				ret = Boolean.FALSE;
			}
		}

		return ret;
	}

	// suggestions

	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.comprasFacade.listarFornecedoresComProposta(param, 0, 100, null, false, this.numeroPac);
	}

	// métodos de auxílio

	public Boolean verificarTipoSc(ScoItemPropostaVO item) {
		return (item.getTipoSolicitacao().equals(DominioTipoSolicitacao.SC));
	}

	private void preencherCondicaoProposta() {
		List<ScoCondicaoPagamentoPropos> listaFormasPagamento;
		listaFormasPagamento = this.pacFacade.pesquisarCondicaoPagamentoProposta(this.fornecedor.getNumero(), this.numeroPac,
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

	public void atualizarDados() {
		if (this.fornecedor != null) {
			this.razaoSocial = this.comprasFacade.montarRazaoSocialFornecedor(this.fornecedor);
			this.preencherCondicaoProposta();
			this.pesquisar();
		} else {
			this.limpar();
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

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public Boolean obterDadosLicitacao() {
		ScoLicitacao licitacao = this.pacFacade.obterLicitacao(this.numeroPac);

		if (licitacao != null) {
			this.descricaoPac = licitacao.getNumeroDescricao();
			this.modalidadePac = licitacao.getModalidadeLicitacao();
			this.situacaoPac = licitacao.getSituacao();
			this.dataPublicacao = licitacao.getDtPublicacao();
		} else {
			this.descricaoPac = null;
			this.modalidadePac = null;
			this.situacaoPac = null;
			this.dataPublicacao = null;
		}

		if (this.listaItensProposta == null) {
			this.listaItensProposta = new ArrayList<ScoItemPropostaVO>();
		}
		return (licitacao != null);
	}

	public void iniciar() {
	 

	 

		if (this.numeroCondicaoPagamentoSelecionado == null && this.descricaoPac == null) {
			this.limpar();

			if (this.numeroPac != null) {
				this.obterDadosLicitacao();
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAMETROS_INVALIDOS");
			}
		} else {
			if (this.numeroCondicaoPagamentoSelecionado != null) {
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

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
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

	public List<ScoItemPropostaVO> getListaItensProposta() {
		return listaItensProposta;
	}

	public void setListaItensProposta(List<ScoItemPropostaVO> listaItensProposta) {
		this.listaItensProposta = listaItensProposta;
	}

	public DominioSituacaoLicitacao getSituacaoPac() {
		return situacaoPac;
	}

	public void setSituacaoPac(DominioSituacaoLicitacao situacaoPac) {
		this.situacaoPac = situacaoPac;
	}

	public DominioSimNaoTodos getMostraEscolhido() {
		return mostraEscolhido;
	}

	public void setMostraEscolhido(DominioSimNaoTodos mostraEscolhido) {
		this.mostraEscolhido = mostraEscolhido;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public Boolean getDisableCheckAll() {
		return disableCheckAll;
	}

	public void setDisableCheckAll(Boolean disableCheckAll) {
		this.disableCheckAll = disableCheckAll;
	}

	public List<ScoItemPropostaVO> getListaItensPropostaSelecionados() {
		return listaItensPropostaSelecionados;
	}

	public void setListaItensPropostaSelecionados(
			List<ScoItemPropostaVO> listaItensPropostaSelecionados) {
		this.listaItensPropostaSelecionados = listaItensPropostaSelecionados;
	}
}
