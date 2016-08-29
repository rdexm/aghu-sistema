package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Classe responsável por controlar as ações do criação e edição de 
 * 
 */


public class PesquisaItemAutFornecimentoController extends ActionController {
	
	
	private static final String PESQUISAR_PROG_ENTREGA_ITENS_AF = "pesquisarProgEntregaItensAF";
	private static final String MANTER_ITEM_AUT_FORNECIMENTO = "manterItemAutFornecimento";
	private static final Log LOG = LogFactory.getLog(PesquisaItemAutFornecimentoController.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5504326168179477927L;	

	public enum PesquisarItemAutFornecimentoControllerExceptionCode implements BusinessExceptionCode {
		MSG_ALTERADA_SUCESSO_ITENS_AF_M12, ERRO_NAO_EXISTE_FASE_MSG14}

		
	private Boolean clicouEditar = false;

	@EJB
	protected IAghuFacade aghuFacade;

	@EJB
	IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected IEstoqueFacade estoqueFacade;

	private boolean pendenteItens = false;
	
	private boolean modalGerarSolicitacao = false;
	
	private String voltarParaUrl;

	/*** campos da tela ***/
	private Integer numeroAf;
	private Short numeroComplemento;
	private ScoFornecedor fornecedor;
	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
	private ScoModalidadeLicitacao modalidadeCompra;
	private Date dataGeracao;
	private ScoAutorizacaoForn scoAutorizacaoForn= new ScoAutorizacaoForn();
	private List<ItensAutFornVO> listaItensAutorizacaoVO = new ArrayList<ItensAutFornVO>();	
	private List<ScoUnidadeMedida> listaUnidadeMedida = new ArrayList<ScoUnidadeMedida>();
	private ItensAutFornVO itemAutFornVoAtual = null;
	Boolean isSc;
	private String labelSolicitacaoOriginal;
	
	private Boolean gravou;
	
	private Boolean bloquear;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 
			
		gravou = false;
		
		if(this.getNumeroAf() != null && this.getNumeroComplemento()!=null){
			scoAutorizacaoForn = this.autFornecimentoFacade.buscarAutFornPorNumPac(this.getNumeroAf(), this.getNumeroComplemento());			
		}
		else{
			scoAutorizacaoForn = null;
		}
		
		if (scoAutorizacaoForn != null) {
			this.setFornecedor(scoAutorizacaoForn.getPropostaFornecedor().getFornecedor());
			this.setSituacaoAf(scoAutorizacaoForn.getSituacao());
			this.setModalidadeCompra(scoAutorizacaoForn.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao());
			this.setDataGeracao(scoAutorizacaoForn.getDtGeracao());
			this.isSc = this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(scoAutorizacaoForn.getNumero()).equals(DominioTipoFaseSolicitacao.C);
			obterAndamento();
			inicializarLabelSolicOriginal();
		   
		
		
			if (clicouEditar == false){
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}
				
				this.listaItensAutorizacaoVO = new ArrayList<ItensAutFornVO>();
				this.getListaUnidadeMedida().addAll(this.pesquisarUnidadeMedidaPorCodigoDescricao(null));
				
				for (ScoItemAutorizacaoForn itemAutForn: scoAutorizacaoForn.getItensAutorizacaoForn()){
					
					ItensAutFornVO itemAutFornVO = new ItensAutFornVO();
					
					itemAutFornVO.setAfnNumero(itemAutForn.getId().getAfnNumero());
					itemAutFornVO.setNumero(itemAutForn.getId().getNumero());
					itemAutFornVO.setMarcaComercial(itemAutForn.getMarcaComercial());
					itemAutFornVO.setModeloComercial(itemAutForn.getModeloComercial());
					itemAutFornVO.setUmdCodigoForn(itemAutForn.getUmdCodigoForn());
					itemAutFornVO.setUnidadeMedida(itemAutForn.getUnidadeMedida());
					itemAutFornVO.setFatorConversao(itemAutForn.getFatorConversao());
					itemAutFornVO.setFatorConversaoForn(itemAutForn.getFatorConversaoForn());
					itemAutFornVO.setValorUnitario(itemAutForn.getValorUnitario());
					itemAutFornVO.setValorEfetivado(itemAutForn.getValorEfetivado());
					itemAutFornVO.setPercDescontoItem(itemAutForn.getPercDescontoItem());
					itemAutFornVO.setPercDesconto(itemAutForn.getPercDesconto());
					itemAutFornVO.setPercAcrescimoItem(itemAutForn.getPercAcrescimoItem());
					itemAutFornVO.setPercAcrescimo(itemAutForn.getPercAcrescimo());
					itemAutFornVO.setPercVarPreco(itemAutForn.getPercVarPreco());
					itemAutFornVO.setPercIpi(itemAutForn.getPercIpi());
					itemAutFornVO.setIndSituacao(itemAutForn.getIndSituacao());
					
					itemAutFornVO.setIndExclusao((Boolean) CoreUtil.nvl(itemAutForn.getIndExclusao(),false));
					itemAutFornVO.setIndEstorno((Boolean) CoreUtil.nvl(itemAutForn.getIndEstorno(),false));
					itemAutFornVO.setIndRecebimento((Boolean) CoreUtil.nvl(itemAutForn.getIndRecebimento(),false));
					itemAutFornVO.setIndContrato((Boolean) CoreUtil.nvl(itemAutForn.getIndContrato(),false));
					itemAutFornVO.setIndConsignado((Boolean) CoreUtil.nvl(itemAutForn.getIndConsignado(),false));
					itemAutFornVO.setIndProgrEntgAuto((Boolean) CoreUtil.nvl(itemAutForn.getIndProgrEntgAuto(),false));
					itemAutFornVO.setIndAnaliseProgrPlanej((Boolean) CoreUtil.nvl(itemAutForn.getIndAnaliseProgrPlanej(),false));
					itemAutFornVO.setIndPreferencialCum((Boolean) CoreUtil.nvl(itemAutForn.getIndPreferencialCum(),false));				
					
					if(itemAutForn.getItemPropostaFornecedor() != null){
					    if (itemAutForn.getItemPropostaFornecedor().getItemLicitacao() != null){ 	
					       itemAutFornVO.setNumeroLicitacao(itemAutForn.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero());
					    }
					}
					
					if (itemAutForn.getQtdeRecebida() == null){
						itemAutFornVO.setQtdeRecebida(0);
					}	
					else {
					   itemAutFornVO.setQtdeRecebida(itemAutForn.getQtdeRecebida());
					}
					
					itemAutFornVO.setQtdeSolicitada(itemAutForn.getQtdeSolicitada());
					
					//if (verificarMaterialFase(itemAutForn.getScoFaseSolicitacao())){
					   itemAutFornVO.setQtdeRp(estoqueFacade.somarQtdeItensNotaRecebProvisorio(
							itemAutForn, QtdeRpVO.MAX_RPS + 1));
					//}
					//else {
					//	 QtdeRpVO qtdeRpSS = new QtdeRpVO();
					//	 qtdeRpSS.setQuantidade(0);
					//	 itemAutFornVO.setQtdeRp(qtdeRpSS);
					//}
					
					/*if (itemAutForn.getScoFaseSolicitacao() == null){
						this.autFornecimentoFacade.obter
					}*/
					/*this.logInfo("BUSCAR FASES => " + itemAutForn.getScoFaseSolicitacao());*/
					itemAutFornVO.setScoFaseSolicitacao(itemAutForn.getScoFaseSolicitacao());
					itemAutFornVO.setPendente(false);
					itemAutFornVO.setDesabilitarEdicaoItem(this.autFornecimentoFacade.desabilitarPermisaoSituacao(obterLoginUsuarioLogado(), "gerarAF","gravar", itemAutForn.getIndSituacao()));
					itemAutFornVO.setNomeMicrocomputador(nomeMicrocomputador);
	
					itemAutFornVO.setHintQtdAF(this.autFornecimentoFacade.obterDescricaoQtdItemAF(itemAutForn, itemAutForn.getQtdeSolicitada()));
					itemAutFornVO.setHintEmbalagemFornecedor(obterHintEmbalagemFornecedor(itemAutFornVO));
					itemAutFornVO.setHintValorUnitario(obterValorUnitario(itemAutFornVO));  
					itemAutFornVO.setValorTotal(setValorTotal(itemAutForn));
					
					itemAutFornVO.setMaterialServicoVO(this.autFornecimentoFacade.obterDadosMaterialServico(itemAutForn.getScoFaseSolicitacao()));				
					itemAutFornVO.setValorSaldo(itemAutForn.getValorUnitario() - (Double) CoreUtil.nvl(itemAutForn.getValorEfetivado(),0.00));
					itemAutFornVO.setNumSolicitacaoOriginal(comprasFacade.obterNumeroSolicitacaoCompraOriginal(itemAutForn.getId(), this.getIsSc()));
					
					this.getListaItensAutorizacaoVO().add(itemAutFornVO);
					
			    }
			}
			
		}
		
	
	}
	
	
	private void inicializarLabelSolicOriginal(){
		StringBuilder label = new StringBuilder();
		if (this.isSc){
			label.append(this.getBundle().getString("LABEL_TOOLTIP_SC_ORIGINAL"));			
		} else {
			label.append(this.getBundle().getString("LABEL_TOOLTIP_SS_ORIGINAL"));	
		}
		
		labelSolicitacaoOriginal = label.toString();
	}
	
	
	private String obterValorUnitario(ItensAutFornVO itemAf){
		BigDecimal vlUnitarioEmbalagem = new BigDecimal(itemAf.getValorUnitario()).multiply(new BigDecimal(itemAf.getFatorConversaoForn()));
		vlUnitarioEmbalagem = vlUnitarioEmbalagem.setScale(4, RoundingMode.HALF_UP);
		Locale locBR = new Locale("pt", "BR"); 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,###,##0.0000####", dfSymbols);
        format.format(vlUnitarioEmbalagem);
        return format.format(vlUnitarioEmbalagem);
	}
	
	private String obterHintEmbalagemFornecedor(ItensAutFornVO itemAF){
		if (isSc && itemAF.getUmdCodigoForn() != null && itemAF.getFatorConversaoForn() != null) {
			StringBuilder toolTip = new StringBuilder();
	
			toolTip.append(itemAF.getUmdCodigoForn().getCodigo())
			.append(' ' + getBundle().getString("LABEL_TOOLTIP_QTDE_C") + ' ')
			.append(itemAF.getFatorConversaoForn())
			.append(' ' + itemAF.getUnidadeMedida().getCodigo());
			
			return toolTip.toString();
		} else {
			return null;
		}
	}
	
	private Double setValorTotal(ScoItemAutorizacaoForn itemAutForn){
		Double valorTotal;
		
		valorTotal = this.autFornecimentoFacade.obterValorTotalItemAF(itemAutForn.getValorEfetivado(), itemAutForn.getPercIpi(), itemAutForn.getPercAcrescimo(), 
				                                                      itemAutForn.getPercAcrescimoItem(), itemAutForn.getPercDesconto(), 
				                                                      itemAutForn.getPercDescontoItem(),  itemAutForn.getValorUnitario(), 
				                                                      itemAutForn.getQtdeSolicitada(), itemAutForn.getQtdeRecebida(), this.isSc);		    		
		
		return valorTotal;
	}
		
	public String editar(){
		this.setClicouEditar(true);
		return MANTER_ITEM_AUT_FORNECIMENTO;
	}
	
	public void verificarGerarSolicitacaoItem(ItensAutFornVO item){
		
		Boolean flagExisteSaldo = this.autFornecimentoFacade.verificarExisteSaldo(item);		
		this.setItemAutFornVoAtual(item);		
		
		if (flagExisteSaldo != null){
			this.setModalGerarSolicitacao(flagExisteSaldo);
			if (!flagExisteSaldo){
				this.alterarSituacaoItem();
			}
			if (modalGerarSolicitacao) {
				super.openDialog("modalGerarSolicitacaoWG");
			}
		}
		else {			
			this.setModalGerarSolicitacao(false);
			this.setPendenteItens(false);
			desfazerAlteracoes(item);
			this.apresentarMsgNegocio(Severity.INFO,
					PesquisarItemAutFornecimentoControllerExceptionCode.ERRO_NAO_EXISTE_FASE_MSG14.toString());			
			
		}
		
	}
	
	public void gerarSolicitacaoItem(){
		ItensAutFornVO item = this.getItemAutFornVoAtual();
		if (item != null) {
			try {
				this.autFornecimentoFacade.processarSolicitacaoCompra(item);			
				this.autFornecimentoFacade.alterarSituacaoItem(item);
				item.setPendente(true);
				this.setPendenteItens(true);
				//this.autFornecimentoFacade.flush();
				this.setSituacaoAf(scoAutorizacaoForn.getSituacao());
				this.setModalGerarSolicitacao(false);			
			} catch (BaseException e) {
				desfazerAlteracoes(item);
				this.setPendenteItens(false);
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
   public void alterarSituacaoItem(){
		ItensAutFornVO item = this.getItemAutFornVoAtual();		
		if (item != null){
				try {
					item.setPendente(true);
					this.setPendenteItens(true);
					this.autFornecimentoFacade.alterarSituacaoItem(item);
					item.setDesabilitarEdicaoItem(this.autFornecimentoFacade.desabilitarPermisaoSituacao(obterLoginUsuarioLogado(), "gerarAF","gravar", item.getIndSituacao()));
					//this.autFornecimentoFacade.flush();
					this.setSituacaoAf(scoAutorizacaoForn.getSituacao());
				} catch (BaseException e) {
					desfazerAlteracoes(item);
					this.setPendenteItens(false);
					apresentarExcecaoNegocio(e);
				}
				
			
		}
		
		
	}
	
	public void desfazerAlteracoes(ItensAutFornVO itemAutFornVO ){		
		if (itemAutFornVO != null){
			Integer index = this.getListaItensAutorizacaoVO().indexOf(itemAutFornVO);			
			if (index > -1){
				ScoItemAutorizacaoForn itemAutFornBanco = this.getScoAutorizacaoForn().getItensAutorizacaoForn().get(index);
								
				itemAutFornVO.setUmdCodigoForn(itemAutFornBanco.getUmdCodigoForn());
				itemAutFornVO.setUnidadeMedida(itemAutFornBanco.getUnidadeMedida());
				itemAutFornVO.setFatorConversao(itemAutFornBanco.getFatorConversao());
				itemAutFornVO.setFatorConversaoForn(itemAutFornBanco.getFatorConversaoForn());
				itemAutFornVO.setValorUnitario(itemAutFornBanco.getValorUnitario());
				itemAutFornVO.setValorEfetivado(itemAutFornBanco.getValorEfetivado());
				itemAutFornVO.setPercDescontoItem(itemAutFornBanco.getPercDescontoItem());
				itemAutFornVO.setPercDesconto(itemAutFornBanco.getPercDesconto());
				itemAutFornVO.setPercAcrescimoItem(itemAutFornBanco.getPercAcrescimoItem());
				itemAutFornVO.setPercAcrescimo(itemAutFornBanco.getPercAcrescimo());
				itemAutFornVO.setPercVarPreco(itemAutFornBanco.getPercVarPreco());
				itemAutFornVO.setPercIpi(itemAutFornBanco.getPercIpi());
				itemAutFornVO.setIndSituacao(itemAutFornBanco.getIndSituacao());
				itemAutFornVO.setIndExclusao(itemAutFornBanco.getIndExclusao());
				itemAutFornVO.setIndEstorno(itemAutFornBanco.getIndEstorno());
				itemAutFornVO.setIndRecebimento(itemAutFornBanco.getIndRecebimento());
				itemAutFornVO.setIndContrato(itemAutFornBanco.getIndContrato());
				itemAutFornVO.setIndConsignado(itemAutFornBanco.getIndConsignado());
				itemAutFornVO.setIndProgrEntgAuto(itemAutFornBanco.getIndProgrEntgAuto());
				itemAutFornVO.setIndAnaliseProgrPlanej(itemAutFornBanco.getIndAnaliseProgrPlanej());
				itemAutFornVO.setIndPreferencialCum(itemAutFornBanco.getIndPreferencialCum());
				
				if (itemAutFornBanco.getQtdeRecebida() == null){
					itemAutFornVO.setQtdeRecebida(0);
				}	
				else {
				   itemAutFornVO.setQtdeRecebida(itemAutFornBanco.getQtdeRecebida());
				}
				
				itemAutFornVO.setQtdeSolicitada(itemAutFornBanco.getQtdeSolicitada());
				itemAutFornVO.setScoFaseSolicitacao(itemAutFornBanco.getScoFaseSolicitacao());
				itemAutFornVO.setPendente(false);				
				itemAutFornVO.setDesabilitarEdicaoItem(this.autFornecimentoFacade.desabilitarPermisaoSituacao(obterLoginUsuarioLogado(), "gerarAF","gravar", itemAutFornVO.getIndSituacao()));
				
				
			}
		} 
		
		
	}
	
	public void gravar(){
		//boolean flagItensPendentes = false;	
		this.setPendenteItens(false);
		this.setModalGerarSolicitacao(false);
		gravou = true;
		try {
		for (ItensAutFornVO item: this.getListaItensAutorizacaoVO()){
			if (item.getPendente()){
					this.autFornecimentoFacade.alterarItemAF(item);					
					item.setPendente(false);
					//flagItensPendentes = true;	
			}			
		}
			
		this.apresentarMsgNegocio(Severity.INFO,
				PesquisarItemAutFornecimentoControllerExceptionCode.MSG_ALTERADA_SUCESSO_ITENS_AF_M12.toString());						
		} catch (BaseException e) {			
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	public Integer obterQtdeSaldoItemAF(ItensAutFornVO itemAutfornVO ){
		if (itemAutfornVO != null) {
		    return this.autFornecimentoFacade.obterQtdeSaldoItemAF(itemAutfornVO.getQtdeSolicitada(), itemAutfornVO.getQtdeRecebida());
		}
		return 0;
	}
	
	public String validarItensPendentes(String ret) {
		this.setClicouEditar(false);
		
		int index = 0;
		boolean isValidaPendente = false;
		while (index < this.getListaItensAutorizacaoVO().size()
			   && isValidaPendente == false) {

			ItensAutFornVO itemAutFornVO = this.getListaItensAutorizacaoVO().get(index);
 
			if (itemAutFornVO.getPendente()){
				isValidaPendente = true;
			}				
			index = index + 1;
	   }
			
		this.setPendenteItens(isValidaPendente);

		if (!isValidaPendente){
			return ret;
		} else {
			return null;
		}
	}
	
	public void validarValorUnitario(ItensAutFornVO item, Boolean pendente){		
		item.setPendente(pendente);
		this.setPendenteItens(pendente);
		try {
			this.autFornecimentoFacade.validarValorUnitarioItemAF(item);	
			Double valorTotal;
			valorTotal =  this.autFornecimentoFacade.obterValorTotalItemAF(item.getValorEfetivado(), item.getPercIpi(), item.getPercAcrescimo(), 
																		   item.getPercAcrescimoItem(), item.getPercDesconto(), 
																		   item.getPercDescontoItem(),  item.getValorUnitario(), 
																		   item.getQtdeSolicitada(), item.getQtdeRecebida(), this.isSc);	
			item.setValorTotal(valorTotal);
				
		} catch (ApplicationBusinessException e) {
			Integer index = this.getListaItensAutorizacaoVO().indexOf(item);			
			if (index > -1){
				ScoItemAutorizacaoForn itemAutFornBanco = this.getScoAutorizacaoForn().getItensAutorizacaoForn().get(index);
				item.setValorUnitario(itemAutFornBanco.getValorUnitario());
				item.setValorTotal(setValorTotal(itemAutFornBanco));
				item.setPendente(false);				
				this.setPendenteItens(false);				
			}
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void validarQuantidadeAF(ItensAutFornVO item, Boolean pendente){
		item.setPendente(pendente);
		this.setPendenteItens(pendente);
		try {
		   this.autFornecimentoFacade.validarItemEntregue(item.getScoFaseSolicitacao(), item.getQtdeRecebida(), item.getQtdeSolicitada(),
				                                          item.getValorUnitario(), item.getValorEfetivado());
		   		   
		   Double valorTotal;
			
		   valorTotal =   this.autFornecimentoFacade.obterValorTotalItemAF(item.getValorEfetivado(), item.getPercIpi(), item.getPercAcrescimo(), 
			 														       item.getPercAcrescimoItem(), item.getPercDesconto(), 
																		   item.getPercDescontoItem(),  item.getValorUnitario(), 
																		   item.getQtdeSolicitada(), item.getQtdeRecebida(), this.isSc);	
																	
		   item.setValorTotal(valorTotal);
		   
		} catch (ApplicationBusinessException e) {
			Integer index = this.getListaItensAutorizacaoVO().indexOf(item);			
			if (index > -1){
				ScoItemAutorizacaoForn itemAutFornBanco = this.getScoAutorizacaoForn().getItensAutorizacaoForn().get(index);
				item.setQtdeSolicitada(itemAutFornBanco.getQtdeSolicitada());
				item.setValorTotal(setValorTotal(itemAutFornBanco));
			}
			apresentarExcecaoNegocio(e);
		}   
		
	}
	
	public void validarExclusaoItemAF(ItensAutFornVO item, Boolean pendente){
		item.setPendente(pendente);
		this.setPendenteItens(pendente);
		try {
		  this.autFornecimentoFacade.validarExclusaoEstornoItemAF(item);
		  
		} catch (ApplicationBusinessException e) {
			Integer index = this.getListaItensAutorizacaoVO().indexOf(item);			
			if (index > -1){
				ScoItemAutorizacaoForn itemAutFornBanco = this.getScoAutorizacaoForn().getItensAutorizacaoForn().get(index);
				item.setIndExclusao(itemAutFornBanco.getIndExclusao());				
			}
			apresentarExcecaoNegocio(e);
		}     
		
	}
	
	public void setPendenteItem(ItensAutFornVO item, Boolean pendente){
		item.setPendente(pendente);
		this.setPendenteItens(pendente);
	}
	
	public boolean validarSituacaoAF(){
		
		int index = 0;
		boolean isValidaPendente = false;
		while (index < this.getListaItensAutorizacaoVO().size()
			   && isValidaPendente == false) {

			ItensAutFornVO itemAutFornVO = this.getListaItensAutorizacaoVO().get(index);
 
			if (itemAutFornVO.getPendente()){
				isValidaPendente = true;
			}				
			index = index + 1;
	   }		
	   if(this.getScoAutorizacaoForn() != null && this.getScoAutorizacaoForn().getSituacao() != null){
		   return (!this.getScoAutorizacaoForn().getSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EF) || isValidaPendente);
	   }
	   return false;
	}
	
	/*public boolean desabilitaGerarEfetivada(DominioSituacaoAutorizacaoFornecedor situacao){		
		return this.autFornecimentoFacade.desabilitaPermisaoAFEfetivada(obterLoginUsuarioLogado(), "gerarAF","gravar", situacao);				
	}*/
	public boolean desabilitarCheckExclusao(ItensAutFornVO itemAutForn){
	    return this.autFornecimentoFacade.desabilitarCheckExclusao(itemAutForn.getIndSituacao()) ||
	    		itemAutForn.isDesabilitarEdicaoItem();
	}
	
	public boolean verificarEfetivado(ItensAutFornVO itemAF){
		return (itemAF.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF) ||
				itemAF.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EP) ||
				itemAF.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EX));	
	} 
	
	public void validarSCContrato(ItensAutFornVO itemAutorizacaoForn){
		try {
		    this.autFornecimentoFacade.validaIndConsignado(itemAutorizacaoForn);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			try {
			   this.autFornecimentoFacade.validaIndConsignadoItemAF(itemAutorizacaoForn);
			} catch (BaseException ex) {
				apresentarExcecaoNegocio(ex);
			}
		}
	
		try {
			this.autFornecimentoFacade.validarScContrato(itemAutorizacaoForn);
			itemAutorizacaoForn.setPendente(true);
			this.setPendenteItens(true);
		} catch (BaseException e) {			
			Integer index = this.getListaItensAutorizacaoVO().indexOf(itemAutorizacaoForn);			
			if (index > -1){
				ScoItemAutorizacaoForn itemAutFornBanco = this.getScoAutorizacaoForn().getItensAutorizacaoForn().get(index);
				itemAutorizacaoForn.setIndContrato(itemAutFornBanco.getIndContrato());
			}			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionaParcelamentoEntregaAF(ItensAutFornVO itemAutorizacaoForn){	
		this.setItemAutFornVoAtual(itemAutorizacaoForn);
		String retorno = validarItensPendentes(PESQUISAR_PROG_ENTREGA_ITENS_AF);
		if (pendenteItens && !clicouEditar) {
			retorno = null;
			super.openDialog("modalConfirmacaoPendenciaParcelasWG");
		}
	    return retorno;
	}
	
	public String cancelarParcelas() {
		
		int index = 0;		
		while (index < this.getListaItensAutorizacaoVO().size()) {
			ItensAutFornVO itemAutFornVO = this.getListaItensAutorizacaoVO().get(index);
			 
			if (itemAutFornVO.getPendente()){
				desfazerAlteracoes(itemAutFornVO);
			}
			index = index + 1;
		}
		this.setClicouEditar(true);
		return PESQUISAR_PROG_ENTREGA_ITENS_AF;
	}
	
	public String voltar(){
		String ret = validarItensPendentes(this.voltarParaUrl);
		
		if (pendenteItens && !clicouEditar) {
			ret = null;
			super.openDialog("modalConfirmacaoPendenciaWG");
		}
		
		return ret;
	}
	
	public String cancelar(){
		return this.voltarParaUrl;
	}
	
	public Boolean verificarMaterialFase(List<ScoFaseSolicitacao> fases){
		return this.autFornecimentoFacade.verificarMaterialFase(fases);
	}	
	
	public String obterDescricaoSolicitacao(ItensAutFornVO item) {
		return this.autFornecimentoFacade.obterDescricaoSolicitacao(item.getScoFaseSolicitacao());
	}
	
	public String obterToolTipQtdeFornecedorVlrUnitario(ItensAutFornVO item, Integer valor){
		
		StringBuilder toolTip = new StringBuilder();
		
		BigDecimal qtd = new BigDecimal(valor);
		qtd = qtd.divide(new BigDecimal(item.getFatorConversaoForn()), 2, RoundingMode.HALF_UP);  
		BigDecimal qtdInteira = new BigDecimal(qtd.intValue());			
		if (qtd.compareTo(qtdInteira) == 0){
			qtd = qtdInteira;
		}		
		toolTip.append(qtd.toString())
		
		.append(' ')
		.append(obterHintEmbalagemFornecedor(item))
					
		.append(". ")
		.append(getBundle().getString("LABEL_TOOLTIP_QTDE_VALOR_UNITARIO"))
		.append(": ")

		.append(obterValorUnitario(item));
				
		return toolTip.toString();
	}
	
	public void obterAndamento(){
		bloquear = false;
		if (this.getScoAutorizacaoForn() != null){
			bloquear = DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA.equals(this.autFornecimentoFacade.obterAndamentoAutorizacaoFornecimento(this.getScoAutorizacaoForn().getPropostaFornecedor().getId().getLctNumero(),this.getScoAutorizacaoForn().getNroComplemento()));
		}		
	}
	
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaAtivaPorCodigoDescricao(parametro);
	}
	// ### GETs e SETs ###
	
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

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAf() {
		return situacaoAf;
	}

	public void setSituacaoAf(DominioSituacaoAutorizacaoFornecimento situacaoAf) {
		this.situacaoAf = situacaoAf;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
	public ScoAutorizacaoForn getScoAutorizacaoForn() {
		return scoAutorizacaoForn;
	}

	public void setScoAutorizacaoForn(ScoAutorizacaoForn scoAutorizacaoForn) {
		this.scoAutorizacaoForn = scoAutorizacaoForn;
	}

	public Boolean getClicouEditar() {
		return clicouEditar;
	}

	public void setClicouEditar(Boolean clicouEditar) {
		this.clicouEditar = clicouEditar;
	}	

	public List<ItensAutFornVO> getListaItensAutorizacaoVO() {
		return listaItensAutorizacaoVO;
	}

	public void setListaItensAutorizacaoVO(
			List<ItensAutFornVO> listaItensAutorizacaoVO) {
		this.listaItensAutorizacaoVO = listaItensAutorizacaoVO;
	}

	public boolean isPendenteItens() {
		return pendenteItens;
	}

	public void setPendenteItens(boolean isPendenteItens) {
		this.pendenteItens = isPendenteItens;
	}

	public List<ScoUnidadeMedida> getListaUnidadeMedida() {
		return listaUnidadeMedida;
	}

	public void setListaUnidadeMedida(List<ScoUnidadeMedida> listaUnidadeMedida) {
		this.listaUnidadeMedida = listaUnidadeMedida;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public boolean isModalGerarSolicitacao() {
		return modalGerarSolicitacao;
	}

	public void setModalGerarSolicitacao(boolean modalGerarSolicitacao) {
		this.modalGerarSolicitacao = modalGerarSolicitacao;
	}

	public ItensAutFornVO getItemAutFornVoAtual() {
		return itemAutFornVoAtual;
	}

	public void setItemAutFornVoAtual(ItensAutFornVO itemAutFornVoAtual) {
		this.itemAutFornVoAtual = itemAutFornVoAtual;
	}
	
	public Boolean getIsSc() {
		return isSc;
	}

	public void setIsSc(Boolean isSc) {
		this.isSc = isSc;
	}
	
	public String getLabelSolicitacaoOriginal() {
		return labelSolicitacaoOriginal;
	}

	public void setLabelSolicitacaoOriginal(String labelSolicitacaoOriginal) {
		this.labelSolicitacaoOriginal = labelSolicitacaoOriginal;
	}

	public Boolean getGravou() {
		return gravou;
	}

	public void setGravou(Boolean gravou) {
		this.gravou = gravou;
	}

	public Boolean getBloquear() {
		return bloquear;
	}

	public void setBloquear(Boolean bloquear) {
		this.bloquear = bloquear;
	}	
}
