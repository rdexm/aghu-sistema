package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaProgrEntregaItensAFController  extends ActionController {

	private static final String PROG_ENTREGA_ITENS_AFCRUD = "compras-progEntregaItensAFCRUD";


	private static final long serialVersionUID = -729441125588120797L;

	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPacFacade pacFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IPermissionService permissionService;

	// parametros
	private Integer numeroAf;	
	private Short numeroItem;
	
	
	// campos tela	
	private Integer numeroProposta;
	private Short numeroComplemento;
	private ScoFornecedor fornecedor;
	private ScoModalidadeLicitacao modalidadeCompra;
	private DominioTipoSolicitacao tipoSolicitacao;
	private String descricaoSolicitacao;
	private Integer qtdeAf;
	private Integer qtdeProgramada;
	private Integer qtdeLiberar;
	private Integer qtdeEfetivada;
	private BigDecimal valorProgramado;
	private BigDecimal valorEfetivado;
	private BigDecimal valorLiberar;
	private BigDecimal valorAf;
	private List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas;
	private Map<ScoProgEntregaItemAutorizacaoFornecimento, QtdeRpVO> qtdesRps;
	private Boolean ativo;
	private Boolean itemAfFechado;
		
	// URL para botão voltar
	private String voltarParaUrl;
	
	// controles para modais
	private ScoProgEntregaItemAutorizacaoFornecimento itemExclusao;
	
	private Short numeroItemLicitacao;


	private boolean isItemAfGeradoAutomaticamente = false;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void iniciar() {
	 

	 
	
		if (this.numeroAf != null && this.numeroItem != null) {
			this.limpar(false);
			this.pesquisar();			
		}
	
	}
	
	
	
	public String redirecionarProgEntregaItensAF(){
		return PROG_ENTREGA_ITENS_AFCRUD;
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
	
	// botoes
			
	public void limpar(Boolean tudo) {
		if (tudo) {
			this.numeroAf = null;
			this.numeroComplemento = null;
			this.numeroItem = null;
		}
		
		this.numeroProposta = null;
		this.fornecedor = null;
		this.modalidadeCompra = null;
		this.tipoSolicitacao = null;
		this.descricaoSolicitacao = null;
		this.qtdeAf = null;
		this.qtdeProgramada = null;
		this.qtdeLiberar = null;
		this.qtdeEfetivada = null;
		this.valorProgramado = BigDecimal.ZERO;
		this.valorEfetivado = BigDecimal.ZERO;
		this.valorLiberar = BigDecimal.ZERO;
		this.valorAf = BigDecimal.ZERO;
		this.ativo = Boolean.FALSE;
		this.itemAfFechado = Boolean.FALSE;
	}
	
	public void prepararExclusaoItem(ScoProgEntregaItemAutorizacaoFornecimento item) {	
		this.itemExclusao = item;
	}
	
	public void excluir() {
		try {		
			this.autFornecimentoFacade.excluirProgEntregaItemAf(this.itemExclusao);
			this.itemExclusao = null;
			obterListaProgEntrega();
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_EXCLUSAO_PARCELA_AF_OK");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void liberarAssinatura(ScoProgEntregaItemAutorizacaoFornecimento item) {
		try {
			this.autFornecimentoFacade.liberarAssinaturaProgEntregaItemAf(item);
			this.pesquisar();
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_LIBERACAO_ASS_PARCELA_AF_OK");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	// metodos auxilio

	
	public void pesquisar() {
		this.popularCamposAf();
		this.obterListaProgEntrega();
	}
	
	/**
	 * Indica se possui permissão para liberar parcelas.
	 * 
	 * @return Flag
	 */
	public boolean hasPermissionToLiberarParcelas() {
		if (!Boolean.TRUE.equals(isItemAfGeradoAutomaticamente)) {
			if (this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "liberarParcelasAF", "gravar")) {
				if(tipoSolicitacao != null){
					switch (tipoSolicitacao) {
						case SC: return qtdeLiberar > 0;
						case SS: return valorLiberar.compareTo(BigDecimal.ZERO) > 0;
						default: throw new IllegalArgumentException(tipoSolicitacao.toString());
					}
				}
			} 
		}
		return false;	
	}
	
	private void popularCamposAf() {
		ScoAutorizacaoForn af = this.autFornecimentoFacade.obterAfDetalhadaByNumero(this.numeroAf);
		
		if (af != null) {
			this.numeroComplemento = af.getNroComplemento();
			this.fornecedor = af.getPropostaFornecedor().getFornecedor();
			this.modalidadeCompra = af.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao();		
			this.numeroProposta = af.getPropostaFornecedor().getId().getLctNumero();
		}
		
		ScoItemAutorizacaoForn itemAf = this.solicitacaoComprasFacade.obterDadosItensAutorizacaoFornecimento(this.numeroAf, Integer.valueOf(this.numeroItem));
		numeroItemLicitacao = itemAf.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero();
		if (itemAf != null) {
			if (Objects.equals(this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(itemAf.getId().getAfnNumero()), DominioTipoFaseSolicitacao.C)) {
				this.tipoSolicitacao = DominioTipoSolicitacao.SC;
			} else {
				this.tipoSolicitacao = DominioTipoSolicitacao.SS;
			}
			
			this.qtdeAf = itemAf.getQtdeSolicitada();
			this.descricaoSolicitacao = this.pacFacade.obterNomeMaterialServico(itemAf.getItemPropostaFornecedor().getItemLicitacao(), true);
			this.valorAf = new BigDecimal(itemAf.getValorUnitario());
			this.itemAfFechado = Boolean.FALSE;
			
			if (itemAf.getIndSituacao() != null) {
				if (itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EX) || 
						itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EP) ||	
						itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF)) {
					this.itemAfFechado = Boolean.TRUE;
				}
			}
		}
	}		
	
	private void obterListaProgEntrega() {
		this.ativo = Boolean.TRUE;
		
		this.listaParcelas = this.comprasFacade.pesquisaProgEntregaItemAf(this.numeroAf, Integer.valueOf(this.numeroItem), false, false);
		if (this.listaParcelas != null) {
			if (this.tipoSolicitacao == DominioTipoSolicitacao.SC) {
				this.qtdeProgramada = this.autFornecimentoFacade.obterQtdeProgramadaProgEntregaItemAf(this.listaParcelas);			
				this.qtdeEfetivada = this.autFornecimentoFacade.obterQtdeEfetivadaProgEntregaItemAf(this.listaParcelas);
				this.qtdeLiberar = this.qtdeAf - this.qtdeProgramada;
			} else {
				this.valorProgramado = this.autFornecimentoFacade.obterValorProgramadoProgEntregaItemAf(this.listaParcelas);
				this.valorEfetivado = this.autFornecimentoFacade.obterValorTotalProgEntregaItemAf(this.listaParcelas);
				this.valorLiberar = this.valorAf.subtract(this.valorProgramado).setScale(2, RoundingMode.HALF_UP);
			}
		}
		
		//  QtdeRpVO.MAX_RPS + 1)
		// Mapeia quantidade de notas de recebimento provisório;
		qtdesRps = new HashMap<ScoProgEntregaItemAutorizacaoFornecimento, QtdeRpVO>();
		
		// Descobre se o item de AF foi gerado automaticamente.
		PARCELAS_LOOP: for (ScoProgEntregaItemAutorizacaoFornecimento parcela : listaParcelas) {
			QtdeRpVO qtde = estoqueFacade.somarQtdeItensNotaRecebProvisorio(
					parcela, QtdeRpVO.MAX_RPS + 1);
			
			qtdesRps.put(parcela, qtde);			
			
			if (parcela.getSolicitacoesProgEntrega() != null) {
				parcela.setSolicitacoesProgEntrega(new HashSet<ScoSolicitacaoProgramacaoEntrega>(this.autFornecimentoFacade.pesquisarSolicitacaoProgEntregaPorItemProgEntrega(parcela.getId().getIafAfnNumero(), 
						                                                                     parcela.getId().getIafNumero(), 
						                                                                     parcela.getId().getSeq(), 
						                                                                     parcela.getId().getParcela())));
								
				for (ScoSolicitacaoProgramacaoEntrega entrega : parcela.getSolicitacoesProgEntrega()) {
					if (entrega.getItemAfOrigem() != null) {
						isItemAfGeradoAutomaticamente = true;
						break PARCELAS_LOOP;
					}
				}
			}
		}
	}
	
	//55 CX c/ 2 L. Valor Unitário da Embalagem: R$60.2468 
	
	public String obterHintQtdLiberada(ScoProgEntregaItemAutorizacaoFornecimento item) {
		StringBuilder sb = new StringBuilder("");
		ScoItemAutorizacaoForn itemAf = this.solicitacaoComprasFacade.obterDadosItensAutorizacaoFornecimento(this.numeroAf, Integer.valueOf(this.numeroItem));
		if (itemAf != null) {
			Integer fatorConversaoForn = (Integer) CoreUtil.nvl(itemAf.getFatorConversaoForn(),1);
			
			BigDecimal qtd = new BigDecimal(item.getQtde());
			qtd = qtd.divide(new BigDecimal(fatorConversaoForn), 2, RoundingMode.HALF_UP);  
			BigDecimal qtdInteira = new BigDecimal(qtd.intValue());			
			if (qtd.compareTo(qtdInteira) == 0){
				qtd = qtdInteira;
			}
			
			sb.append(qtd.toString()).append(' ');
			if (itemAf.getUmdCodigoForn() != null) {
				sb.append(itemAf.getUmdCodigoForn().getCodigo()).append(' ');
			}
			sb.append(getBundle().getString("LABEL_TOOLTIP_QTDE_C")).append(' ');
			if (itemAf.getFatorConversaoForn() != null) {
				sb.append(itemAf.getFatorConversaoForn()).append(' ');
			}
			sb.append(itemAf.getUnidadeMedida().getCodigo()).append(". ")
			.append(getBundle().getString("LABEL_TOOLTIP_QTDE_VALOR_UNITARIO")).append(": ");
			
			BigDecimal vlUnitarioEmbalagem = new BigDecimal(itemAf.getValorUnitario()).multiply(new BigDecimal(fatorConversaoForn)); 			
			Locale locBR = new Locale("pt", "BR");
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat format = new DecimalFormat("#,###,###,###,###,##0.0000####", dfSymbols);			
			sb.append(format.format(vlUnitarioEmbalagem));
		}
		
		return sb.toString();
	}
	
	public Boolean isSc() {
		return this.tipoSolicitacao == DominioTipoSolicitacao.SC;
	}
	
	public QtdeRpVO getQtdeRp(ScoProgEntregaItemAutorizacaoFornecimento item) {
		return qtdesRps.get(item);
	}
	
	// Getters/Setters
	
	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IPacFacade getPacFacade() {
		return pacFacade;
	}

	public void setPacFacade(IPacFacade pacFacade) {
		this.pacFacade = pacFacade;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}

	public void setSolicitacaoComprasFacade(ISolicitacaoComprasFacade solicitacaoComprasFacade) {
		this.solicitacaoComprasFacade = solicitacaoComprasFacade;
	}

	public ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}

	public void setSolicitacaoServicoFacade(ISolicitacaoServicoFacade solicitacaoServicoFacade) {
		this.solicitacaoServicoFacade = solicitacaoServicoFacade;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}

	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}

	public Integer getQtdeAf() {
		return qtdeAf;
	}

	public void setQtdeAf(Integer qtdeAf) {
		this.qtdeAf = qtdeAf;
	}

	public Integer getQtdeProgramada() {
		return qtdeProgramada;
	}

	public void setQtdeProgramada(Integer qtdeProgramada) {
		this.qtdeProgramada = qtdeProgramada;
	}

	public Integer getQtdeLiberar() {
		return qtdeLiberar;
	}

	public void setQtdeLiberar(Integer qtdeLiberar) {
		this.qtdeLiberar = qtdeLiberar;
	}

	public Integer getQtdeEfetivada() {
		return qtdeEfetivada;
	}

	public void setQtdeEfetivada(Integer qtdeEfetivada) {
		this.qtdeEfetivada = qtdeEfetivada;
	}

	public List<ScoProgEntregaItemAutorizacaoFornecimento> getListaParcelas() {
		return listaParcelas;
	}

	public void setListaParcelas(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		this.listaParcelas = listaParcelas;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public BigDecimal getValorProgramado() {
		return valorProgramado;
	}

	public void setValorProgramado(BigDecimal valorProgramado) {
		this.valorProgramado = valorProgramado;
	}

	public BigDecimal getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(BigDecimal valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public BigDecimal getValorLiberar() {
		return valorLiberar;
	}

	public void setValorLiberar(BigDecimal valorLiberar) {
		this.valorLiberar = valorLiberar;
	}

	public BigDecimal getValorAf() {
		return valorAf;
	}

	public void setValorAf(BigDecimal valorAf) {
		this.valorAf = valorAf;
	}

	public Integer getNumeroProposta() {
		return numeroProposta;
	}

	public void setNumeroProposta(Integer numeroProposta) {
		this.numeroProposta = numeroProposta;
	}

	public ScoProgEntregaItemAutorizacaoFornecimento getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(ScoProgEntregaItemAutorizacaoFornecimento itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}

	public boolean isItemAfGeradoAutomaticamente() {
		return isItemAfGeradoAutomaticamente;
	}

	public Boolean getItemAfFechado() {
		return itemAfFechado;
	}

	public void setItemAfFechado(Boolean itemAfFechado) {
		this.itemAfFechado = itemAfFechado;
	}
}