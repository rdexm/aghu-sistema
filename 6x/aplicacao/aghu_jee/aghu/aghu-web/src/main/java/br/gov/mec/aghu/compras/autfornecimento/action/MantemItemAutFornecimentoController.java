package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável por controlar as ações do criação e edição de 
 * 
 */
public class MantemItemAutFornecimentoController extends ActionController {	
	
	private static final String PESQUISAR_ITEM_AUT_FORNECIMENTO = "pesquisarItemAutFornecimento";


	private static final long serialVersionUID = 1036662043732423561L;
	
	
	@EJB
	protected IAghuFacade aghuFacade;

	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
		
	private ItensAutFornVO itemAutorizacaoFornVO;
	
	/*** campos da tela ***/
	private Integer numeroAf;
	private Short numeroComplemento;
	private ScoFornecedor fornecedor;
	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
	private ScoModalidadeLicitacao modalidadeCompra;
	private Date dataGeracao;	 
	
	
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private ScoUnidadeMedida umdCodigoForn;
	private ScoUnidadeMedida unidadeMedida;
	private Integer fatorConversaoForn;
	private Double valorUnitario;
	private BigDecimal valorEfetivado;
	private BigDecimal percDescontoItem;
	private BigDecimal percDesconto;
	private BigDecimal percAcrescimoItem;
	private BigDecimal percAcrescimo;
	private BigDecimal percVarPreco;
	private BigDecimal percIpi;
	private Boolean indExclusao;
	private Boolean indEstorno;
	private Boolean indRecebimento;
	private Boolean indContrato;
	private Boolean indConsignado;
	private Boolean indProgrEntgAuto;
	private Boolean indAnaliseProgrPlanej;
	private Boolean indPreferencialCum;
	private Integer qtdeRecebida;
	private Integer qtdeSolicitada;
	private boolean desabilitarEdicaoItem;
	private boolean desabilitarItemEdicaoSS;
	private boolean desabilitarEdicaoFCForn;
	Boolean isSc;
	
	private Boolean indEstornoAnterior;
	private Boolean indConsignadoAnterior;
	
	private Boolean itemPendente;
	
	private boolean bloquear = false; 
	
		
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void inicio() {
	 

	 


		ScoAutorizacaoForn scoAutorizacaoForn = null;
					
		if(this.getNumeroAf() != null && this.getNumeroComplemento() != null){
			scoAutorizacaoForn = this.autFornecimentoFacade.buscarAutFornPorNumPac(this.getNumeroAf(), this.getNumeroComplemento());
		}
		
		if (scoAutorizacaoForn != null) {
			this.setFornecedor(scoAutorizacaoForn.getPropostaFornecedor().getFornecedor());
			this.setSituacaoAf(scoAutorizacaoForn.getSituacao());
			this.setModalidadeCompra(scoAutorizacaoForn.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao());
			this.setDataGeracao(scoAutorizacaoForn.getDtGeracao());
			this.isSc = this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(scoAutorizacaoForn.getNumero()).equals(DominioTipoFaseSolicitacao.C);
		}
				
		this.setMarcaComercial(this.getItemAutorizacaoFornVO().getMarcaComercial());
		this.setModeloComercial(this.getItemAutorizacaoFornVO().getModeloComercial());
		this.setUmdCodigoForn(this.getItemAutorizacaoFornVO().getUmdCodigoForn());
		this.setUnidadeMedida(this.getItemAutorizacaoFornVO().getUnidadeMedida());
		this.setFatorConversaoForn(this.getItemAutorizacaoFornVO().getFatorConversaoForn());
		this.setValorUnitario(this.getItemAutorizacaoFornVO().getValorUnitario());
		this.setValorEfetivado(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getValorEfetivado()));
		this.setPercDescontoItem(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getPercDescontoItem()));
		this.setPercDesconto(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getPercDesconto()));
		this.setPercAcrescimoItem(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getPercAcrescimoItem()));
		this.setPercAcrescimo(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getPercAcrescimo()));
		
		if (this.getItemAutorizacaoFornVO().getPercVarPreco() != null){
		    this.setPercVarPreco(new BigDecimal(this.getItemAutorizacaoFornVO().getPercVarPreco()));
		}
		else {
			this.setPercVarPreco(BigDecimal.ZERO);
		}
		
		this.setQtdeRecebida(this.getItemAutorizacaoFornVO().getQtdeRecebida());
		this.setQtdeSolicitada(this.getItemAutorizacaoFornVO().getQtdeSolicitada());
		
		this.setPercIpi(convertDoubleBigDecimal(this.getItemAutorizacaoFornVO().getPercIpi()));
		
		this.setIndExclusao(this.getItemAutorizacaoFornVO().getIndExclusao());
		this.setIndEstorno(this.getItemAutorizacaoFornVO().getIndEstorno());
		this.setIndRecebimento(this.getItemAutorizacaoFornVO().getIndRecebimento());
		this.setIndContrato(this.getItemAutorizacaoFornVO().getIndContrato());
		this.setIndConsignado(this.getItemAutorizacaoFornVO().getIndConsignado());
		this.setIndProgrEntgAuto(this.getItemAutorizacaoFornVO().getIndProgrEntgAuto());
		this.setIndAnaliseProgrPlanej(this.getItemAutorizacaoFornVO().getIndAnaliseProgrPlanej());
		this.setIndPreferencialCum(this.getItemAutorizacaoFornVO().getIndPreferencialCum());
		this.setDesabilitarEdicaoItem(this.getItemAutorizacaoFornVO().isDesabilitarEdicaoItem() || bloquear);
		this.setDesabilitarItemEdicaoSS(this.getItemAutorizacaoFornVO().desabilitarItemEdicaoSS());
		
		this.setIndEstornoAnterior(this.getIndEstorno());
		this.setIndConsignadoAnterior(this.getIndConsignado());
		this.setItemPendente(this.getItemAutorizacaoFornVO().getPendente());
		
		this.validarUnidadeFatorConversao(false);		
		
		//this.//setIgnoreInitPageConfig(true);
		
	
	}
	
	
	public BigDecimal convertDoubleBigDecimal(Double valor){
		if (valor != null){
			return new BigDecimal(valor);
		}
		return BigDecimal.ZERO;
	}
	
	public Double convertBigDecimalDouble(BigDecimal valor){
		if (valor != null){
			return valor.doubleValue();
		}
		return new Double(0);
	}
	

	public String alterar() {			
		
			
		//this.getItemAutorizacaoFornVO().setIndExclusao(true);
		
		this.getItemAutorizacaoFornVO().setMarcaComercial(this.getMarcaComercial());
		this.getItemAutorizacaoFornVO().setModeloComercial(this.getModeloComercial());
		this.getItemAutorizacaoFornVO().setUmdCodigoForn(this.getUmdCodigoForn());
		this.getItemAutorizacaoFornVO().setUnidadeMedida(this.getUnidadeMedida());
		this.getItemAutorizacaoFornVO().setFatorConversaoForn(this.getFatorConversaoForn());
		
		this.getItemAutorizacaoFornVO().setValorUnitario(this.getValorUnitario());
		this.getItemAutorizacaoFornVO().setValorEfetivado(convertBigDecimalDouble(this.getValorEfetivado()));
		this.getItemAutorizacaoFornVO().setPercDescontoItem(convertBigDecimalDouble(this.getPercDescontoItem()));
		this.getItemAutorizacaoFornVO().setPercDesconto(convertBigDecimalDouble(this.getPercDesconto()));
		this.getItemAutorizacaoFornVO().setPercAcrescimoItem(convertBigDecimalDouble(this.getPercAcrescimoItem()));
		this.getItemAutorizacaoFornVO().setPercAcrescimo(convertBigDecimalDouble(this.getPercAcrescimo()));
		
		if (this.getPercVarPreco() !=null){
		    this.getItemAutorizacaoFornVO().setPercVarPreco(this.getPercVarPreco().floatValue());
		}
		else {
			this.getItemAutorizacaoFornVO().setPercVarPreco(new Float(0));
		}
		
		this.getItemAutorizacaoFornVO().setPercIpi(convertBigDecimalDouble(this.getPercIpi()));
		
		this.getItemAutorizacaoFornVO().setIndExclusao(this.getIndExclusao());
		this.getItemAutorizacaoFornVO().setIndEstorno(this.getIndEstorno());
		this.getItemAutorizacaoFornVO().setIndRecebimento(this.getIndRecebimento());
		this.getItemAutorizacaoFornVO().setIndContrato(this.getIndContrato());
		this.getItemAutorizacaoFornVO().setIndConsignado(this.getIndConsignado());
		this.getItemAutorizacaoFornVO().setIndProgrEntgAuto(this.getIndProgrEntgAuto());
		this.getItemAutorizacaoFornVO().setIndAnaliseProgrPlanej(this.getIndAnaliseProgrPlanej());
		this.getItemAutorizacaoFornVO().setIndPreferencialCum(this.getIndPreferencialCum());
		this.setPendenteItem(true);
		
		return PESQUISAR_ITEM_AUT_FORNECIMENTO;
	}
	/**
	 * Método que realiza a ação do botão cancelar 
	 */
	public String cancelar() {		
		if (!this.getItemPendente()){
		    this.setPendenteItem(false);
		    this.getItemAutorizacaoFornVO().setIndConsignado(this.getIndConsignadoAnterior());
		    this.getItemAutorizacaoFornVO().setIndEstorno(this.getIndEstornoAnterior());
		}
		return PESQUISAR_ITEM_AUT_FORNECIMENTO;
	}
	
	
	
	public void setPendenteItem(Boolean pendente){
		this.getItemAutorizacaoFornVO().setPendente(pendente);		
	}
	
	public void limparModeloComercial() {
		this.setModeloComercial(null);
	}
	
	public Integer obterQtdeSaldoItemAF(){
		return this.autFornecimentoFacade.obterQtdeSaldoItemAF(this.getQtdeSolicitada(), this.getQtdeRecebida());		
	}
	
	public Double obterValorBrutoItemAF(){
		return this.autFornecimentoFacade.obterValorBrutoItemAF(this.getValorUnitario().doubleValue(), this.getQtdeSolicitada(), this.getQtdeRecebida(), this.getValorEfetivado(), this.isSc);
	}
	
	public Double obterValorDescontoItemAF(){
		return this.autFornecimentoFacade.obterValorDescontoItemAF(this.getPercDesconto().doubleValue(), this.getPercDescontoItem().doubleValue(),
		   		                                                   this.getValorUnitario().doubleValue(), this.getQtdeSolicitada(), this.getQtdeRecebida(), this.getValorEfetivado(), this.isSc);
	}
	
	public Double obterValorAcrescimoItemAF(){
		return this.autFornecimentoFacade.obterValorAcrescimoItemAF(this.getPercAcrescimo().doubleValue(), this.getPercAcrescimoItem().doubleValue(),
		   		                                                    this.getValorUnitario().doubleValue(), this.getQtdeSolicitada(), this.getQtdeRecebida(), this.getValorEfetivado(), this.isSc);		
	}
	
	public Double obterValorIpiItemAF(){
		return this.autFornecimentoFacade.obterValorIpiItemAF(this.getPercIpi().doubleValue(), this.getPercAcrescimo().doubleValue(), this.getPercAcrescimoItem().doubleValue(),
		   		                                              this.getPercDesconto().doubleValue(), this.getPercDescontoItem().doubleValue(), 
		   		                                              this.getValorUnitario().doubleValue(), this.getQtdeSolicitada(), this.getQtdeRecebida(), this.getValorEfetivado(), this.isSc);
		
	}
	
	public Double obterValorSaldoItemAF(){
		return this.autFornecimentoFacade.obterValorSaldoItemAF(this.getPercIpi().doubleValue(), this.getPercAcrescimo().doubleValue(), this.getPercAcrescimoItem().doubleValue(),
		   		                                                this.getPercDesconto().doubleValue(), this.getPercDescontoItem().doubleValue(), 
		    		                                            this.getValorUnitario().doubleValue(), this.getQtdeSolicitada(), this.getQtdeRecebida(), this.getValorEfetivado(), this.isSc);		    		
		
	}
	
	public Double obterValorTotalItemAF(){
		return this.autFornecimentoFacade.obterValorTotalItemAF(this.getValorEfetivado().doubleValue(), this.getPercIpi().doubleValue(), this.getPercAcrescimo().doubleValue(), 
		   		                                                this.getPercAcrescimoItem().doubleValue(), this.getPercDesconto().doubleValue(), 
		   		                                                this.getPercDescontoItem().doubleValue(),  this.getValorUnitario().doubleValue(), 
		   		                                                this.getQtdeSolicitada(), this.getQtdeRecebida(), this.isSc);		    		
		
	}
		
	/*public boolean desabilitaGerarEfetivada(){		
		return this.autFornecimentoFacade.desabilitaPermisaoAFEfetivada(obterLoginUsuarioLogado(), "gerarAF", this.getItemAutorizacaoFornVO().getIndSituacao());				
	}*/

	public String obterCodigoDescricaoMaterialServico(){
		StringBuffer codigoDescricao = new StringBuffer("");
		if(this.getItemAutorizacaoFornVO()!=null){
			
			MaterialServicoVO materialServicoVo = this.autFornecimentoFacade.obterDadosMaterialServico(this.getItemAutorizacaoFornVO().getScoFaseSolicitacao());
			Integer codigo =  materialServicoVo.getCodigoMatServ();
			String descricao = materialServicoVo.getNomeMatServ(); 
			
			if(codigo != null) {
			    codigoDescricao.append(codigo.toString());
			}
			codigoDescricao.append(" - ");
			if (descricao != null){	
			    codigoDescricao.append(descricao);
			}    
			
			
		}
		return codigoDescricao.toString();
	}
	
    // Metodo pesquisa sugestion
	public List<ScoMarcaComercial> pesquisarMarcaComercial(String param) {
		return this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricao(param);
	}
	
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(String param) {
		return this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricao(param, this.getMarcaComercial(), true);
	}
	
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(String parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaAtivaPorCodigoDescricao(parametro);
	}
	
	public void validarEstornoItemAF(Boolean pendente){	
		
		try {
			this.getItemAutorizacaoFornVO().setIndEstorno(this.getIndEstorno());
			this.getItemAutorizacaoFornVO().setPendente(pendente);	
		    this.autFornecimentoFacade.validarExclusaoEstornoItemAF(this.getItemAutorizacaoFornVO());
		  
		} catch (ApplicationBusinessException e) {
			this.setIndEstorno(this.getIndEstornoAnterior());
			this.getItemAutorizacaoFornVO().setIndEstorno(this.getIndEstornoAnterior());
			apresentarExcecaoNegocio(e);
		}     
		
	}
	
   public void validarConsignadoItemAF(Boolean pendente){	
		
		try {
			this.getItemAutorizacaoFornVO().setIndConsignado(this.getIndConsignado());
			this.getItemAutorizacaoFornVO().setPendente(pendente);	
		    this.autFornecimentoFacade.validaIndConsignadoItemAF(this.getItemAutorizacaoFornVO());	
		} catch (BaseException e) {
			this.setIndConsignado(this.getIndConsignadoAnterior());
			this.getItemAutorizacaoFornVO().setIndConsignado(this.getIndConsignadoAnterior());
			apresentarExcecaoNegocio(e);
		}     
		
	}
	
	public void validarUnidadeFatorConversao(Boolean setFC){
		if (umdCodigoForn != null && unidadeMedida != null && umdCodigoForn.equals(unidadeMedida)){			
			desabilitarEdicaoFCForn = true;
			
			if (setFC){
				fatorConversaoForn = 1;	
			}
			
		} else {
			desabilitarEdicaoFCForn = false;
		}
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

	public BigDecimal getPercDescontoItem() {
		return percDescontoItem;
	}

	public void setPercDescontoItem(BigDecimal percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}

	public BigDecimal getPercDesconto() {
		return percDesconto;
	}

	public void setPercDesconto(BigDecimal percDesconto) {
		this.percDesconto = percDesconto;
	}

	public BigDecimal getPercAcrescimoItem() {
		return percAcrescimoItem;
	}

	public void setPercAcrescimoItem(BigDecimal percAcrescimoItem) {
		this.percAcrescimoItem = percAcrescimoItem;
	}

	public BigDecimal getPercAcrescimo() {
		return percAcrescimo;
	}

	public void setPercAcrescimo(BigDecimal percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}

	public BigDecimal getPercVarPreco() {
		return percVarPreco;
	}

	public void setPercVarPreco(BigDecimal percVarPreco) {
		this.percVarPreco = percVarPreco;
	}

	public BigDecimal getPercIpi() {
		return percIpi;
	}

	public void setPercIpi(BigDecimal percIpi) {
		this.percIpi = percIpi;
	}

	
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	public ScoUnidadeMedida getUmdCodigoForn() {
		return umdCodigoForn;
	}

	public void setUmdCodigoForn(ScoUnidadeMedida umdCodigoForn) {
		this.umdCodigoForn = umdCodigoForn;
		validarUnidadeFatorConversao(true);
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getFatorConversaoForn() {
		return fatorConversaoForn;
	}

	public void setFatorConversaoForn(Integer fatorConversaoForn) {
		this.fatorConversaoForn = fatorConversaoForn;
	}

	

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(BigDecimal valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Boolean getIndExclusao() {
		return indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}

	public Boolean getIndEstorno() {
		return indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	public Boolean getIndRecebimento() {
		return indRecebimento;
	}

	public void setIndRecebimento(Boolean indRecebimento) {
		this.indRecebimento = indRecebimento;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Boolean getIndConsignado() {
		return indConsignado;
	}

	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}

	public Boolean getIndProgrEntgAuto() {
		return indProgrEntgAuto;
	}

	public void setIndProgrEntgAuto(Boolean indProgrEntgAuto) {
		this.indProgrEntgAuto = indProgrEntgAuto;
	}

	public Boolean getIndAnaliseProgrPlanej() {
		return indAnaliseProgrPlanej;
	}

	public void setIndAnaliseProgrPlanej(Boolean indAnaliseProgrPlanej) {
		this.indAnaliseProgrPlanej = indAnaliseProgrPlanej;
	}

	public Boolean getIndPreferencialCum() {
		return indPreferencialCum;
	}

	public void setIndPreferencialCum(Boolean indPreferencialCum) {
		this.indPreferencialCum = indPreferencialCum;
	}
	

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	

	public ItensAutFornVO getItemAutorizacaoFornVO() {
		return itemAutorizacaoFornVO;
	}

	public void setItemAutorizacaoFornVO(ItensAutFornVO itemAutorizacaoFornVO) {
		this.itemAutorizacaoFornVO = itemAutorizacaoFornVO;
	}

	public boolean isDesabilitarEdicaoItem() {
		return desabilitarEdicaoItem;
	}

	public void setDesabilitarEdicaoItem(boolean desabilitarEdicaoItem) {
		this.desabilitarEdicaoItem = desabilitarEdicaoItem;
	}

	public Boolean getIndEstornoAnterior() {
		return indEstornoAnterior;
	}

	public void setIndEstornoAnterior(Boolean indEstornoAnterior) {
		this.indEstornoAnterior = indEstornoAnterior;
	}

	public Boolean getIndConsignadoAnterior() {
		return indConsignadoAnterior;
	}

	public void setIndConsignadoAnterior(Boolean indConsignadoAnterior) {
		this.indConsignadoAnterior = indConsignadoAnterior;
	}

	public boolean isDesabilitarItemEdicaoSS() {
		return desabilitarItemEdicaoSS;
	}

	public void setDesabilitarItemEdicaoSS(boolean desabilitarItemEdicaoSS) {
		this.desabilitarItemEdicaoSS = desabilitarItemEdicaoSS;
	}

	public Boolean getItemPendente() {
		return itemPendente;
	}

	public void setItemPendente(Boolean itemPendente) {
		this.itemPendente = itemPendente;
	}

	public boolean isDesabilitarEdicaoFCForn() {
		return desabilitarEdicaoFCForn;
	}

	public void setDesabilitarEdicaoFCForn(boolean desabilitarEdicaoFCForn) {
		this.desabilitarEdicaoFCForn = desabilitarEdicaoFCForn;
	}

	public Boolean getIsSc() {
		return isSc;
	}

	public boolean isBloquear() {
		return bloquear;
	}

	public void setBloquear(boolean bloquear) {
		this.bloquear = bloquear;
	}

	public void setIsSc(Boolean isSc) {
		this.isSc = isSc;
	}
}
