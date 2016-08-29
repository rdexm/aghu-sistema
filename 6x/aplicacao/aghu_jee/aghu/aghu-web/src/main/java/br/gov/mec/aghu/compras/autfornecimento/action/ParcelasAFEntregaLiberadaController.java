package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.vo.ParcelasAutFornPedidoVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ParcelasAFEntregaLiberadaController extends ActionController {
	
	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "autorizacaoFornecimentoCRUD";

	private static final long serialVersionUID = -1252724787813172342L;
	
	private Integer numeroAF;
	private Integer numeroAFP;
	private Integer numeroAFN;
	private Integer codFornecedor;

	private Short numeroComplemento;

	private String nomeFornecedor;
	private String voltarParaUrl;
	
	private DominioTipoFaseSolicitacao tipoSolicitacao;
	
	private ScoAutorizacaoForn autFornPedido = new ScoAutorizacaoForn();
	
	List<ParcelasAutFornPedidoVO> listaParcelasAFP = new ArrayList<ParcelasAutFornPedidoVO>();
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		
		if (this.numeroAF != null && this.numeroAFP != null) {
			autFornPedido = this.autFornecimentoFacade.buscarAutFornPorNumPac(this.numeroAF, this.numeroComplemento);
		} 
		
		if (autFornPedido == null) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_AF_NAO_ENCONTRADA", this.numeroAF);
		}
		else {
				tipoSolicitacao = autFornPedido.getItensAutorizacaoForn().get(0).getScoFaseSolicitacao().get(0).getTipo();
				numeroAFN = autFornPedido.getNumero();
				codFornecedor = autFornPedido.getPropostaFornecedor().getFornecedor().getNumero();
				nomeFornecedor = autFornPedido.getPropostaFornecedor().getFornecedor().getRazaoSocial();
				
				listaParcelasAFP = autFornecimentoFacade.pesquisarParcelasAfpPorFiltro(numeroAFN, numeroAFP, tipoSolicitacao);
		}
	
	}
	
	
	public Boolean verificarDtPrevEntrega(ParcelasAutFornPedidoVO parcela) {
		
		Date hoje = new Date();
		
		//Verifica se a data de entrega está atrasada
		if (parcela != null && DateUtil.truncaData(parcela.getDataPrevEntrega()).compareTo(DateUtil.truncaData(hoje)) < 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//botões
	public String voltar() {
		return voltarParaUrl;
	}
	
	public String redirecionarAutorizacaoFornecimento(){
		return AUTORIZACAO_FORNECIMENTO_CRUD;
	}
	
	public String visualizarEstatisticasConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}
	
	//gets and sets
	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	
	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
	
	public Integer getNumeroAFP() {
		return numeroAFP;
	}

	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}
	
	public Integer getNumeroAFN() {
		return numeroAFN;
	}

	public void setNumeroAFN(Integer numeroAFN) {
		this.numeroAFN = numeroAFN;
	}
	
	public Integer getCodFornecedor() {
		return codFornecedor;
	}

	public void setCodFornecedor(Integer codFornecedor) {
		this.codFornecedor = codFornecedor;
	}
	
	public DominioTipoFaseSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoFaseSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	
	public ScoAutorizacaoForn getAutFornPedido() {
		return autFornPedido;
	}

	public void setAutFornPedido(ScoAutorizacaoForn autFornPedido) {
		this.autFornPedido = autFornPedido;
	}
	
	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ParcelasAutFornPedidoVO> getListaParcelasAFP() {
		return listaParcelasAFP;
	}

	public void setListaParcelasAFP(List<ParcelasAutFornPedidoVO> listaParcelasAFP) {
		this.listaParcelasAFP = listaParcelasAFP;
	}
}