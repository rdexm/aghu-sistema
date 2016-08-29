package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.action.AutorizacaoFornecimentoController;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.action.ProcessoAdmComprasController;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class FasesSolicitacaoCompraController extends ActionController {

	

	private static final String ACOES_PONTO_PARADA = "compras-acoesPontoParada";

	/**
	 * Variáveis
	 */
	private static final long serialVersionUID = -5861595457653656517L;

	private static final String PAGE_ACOES_PONTO_PARADA_SC = "acoesPontoParadaSc";
	private static final String PAGE_PAC_CRUD = "compras-processoAdmCompraCRUD";
	private static final String PAGE_AF_CRUD = "compras-autorizacaoFornecimentoCRUD";
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private ProcessoAdmComprasController processoAdmComprasController;
	
	@Inject
	private AutorizacaoFornecimentoController autorizacaoFornecimentoController;
	
	// Objetos
	private List<ScoScJn> listFasesSolicitacao;
	private ScoFaseSolicitacao dadosLicitacao = new ScoFaseSolicitacao();
		
	// Numero da Solicitacao de Compra
	private Integer numero;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	
	
	// Dados da Licitacao
	private Integer nroLicitacao;
	private Short item;
	private Date dataDigitacao;
	private Date dataPublicacao;
	private Date dataProposta;

	// Dados da AF
	private Integer nroAF;
	private Short nroComplemento;
	private Date dataGeracao;
	private Integer quantidade;
	private String unidade;
	private Date previsaoEntrega;
	private String situacao;
	private Integer qtdEntregue;
	private Integer qtdSaldo;

	private Boolean mostrarMsg;	
	private Boolean mostrarBotaoVoltar;
	private String voltarPara;
	private Boolean origemMenu;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		this.setOrigemMenu(true);
		if (this.numero != null && this.voltarPara != null) {
			this.pesquisar();
			this.setOrigemMenu(false);
		}
	
	}
	
	
	public void pesquisar() {		
		this.solicitacaoCompra = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numero);
		if (this.solicitacaoCompra == null) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_NAO_ENCONTRADA");
			this.inicializaVariaveis();
		} else {
			this.inicializaVariaveis();
			this.setListFasesSolicitacao(this.listFasesSolicitacao());
			if (countFasesSolicitacao() > 0) {
				this.buscaDadosLicitacao();
				this.buscaDadosAF();
			}
		}
	}

	public String acoesPontoParada(){
		return PAGE_ACOES_PONTO_PARADA_SC;
	}
	
	public String redirecionarAf() {
		autorizacaoFornecimentoController.setVoltarParaUrl("compras-fasesSolicitacaoCompraList");
		autorizacaoFornecimentoController.setNumeroAf(this.nroAF);
		autorizacaoFornecimentoController.setNumeroComplemento(this.nroComplemento);
		return PAGE_AF_CRUD;
	}
	
	public String redirecionarPac() {
		processoAdmComprasController.setVoltarParaUrl("compras-fasesSolicitacaoCompraList");
		processoAdmComprasController.setNumeroPac(this.nroLicitacao);
		return PAGE_PAC_CRUD;
	}
	
	public Boolean exibirEdicao(ScoScJn item) {
		if (item != null) {
			return item.getEtapa().equalsIgnoreCase("Atual") && this.comprasCadastrosBasicosFacade.verificarPontoParadaPermitido(item.getPontoParadaSolicitacao());
		} else {
			return false;
		}
	}
	
	public void limparPesquisa() {		
		this.inicializaVariaveis();
		this.numero = null;
	}
	
	public Boolean habilitarBotaoResgatar() {
		Boolean habilita = Boolean.FALSE;
		
		try {
			RapServidores servidor = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());
			
			if (listFasesSolicitacao != null && 
					listFasesSolicitacao.size() > 1 && 
					servidor.equals(listFasesSolicitacao.get(0).getServidor()) &&
					!this.solicitacaoComprasFacade.verificarAcoesPontoParadaSc(this.numero, listFasesSolicitacao.get(0).getDataAlteracao())) {
				habilita = Boolean.TRUE;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return habilita;
	}
	
	public void resgatarSc() {
		try {
			solicitacaoComprasFacade.resgatarSc(this.numero);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_RESGATADA_COM_SUCESSO");
			
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void inicializaVariaveis() {
		
		this.setListFasesSolicitacao(new ArrayList<ScoScJn>());		
		
		// Dados da Licitacao
		this.nroLicitacao = null;
		this.item = null;
		this.dataDigitacao = null;
		this.dataPublicacao = null;
		this.dataProposta = null;

		// Dados da AF
		this.nroAF = null;
		this.nroComplemento = null;
		this.dataGeracao = null;
		this.quantidade = null;
		this.unidade = null;
		this.previsaoEntrega = null;
		this.situacao = null;
		this.setQtdEntregue(null);
		this.setQtdSaldo(null);
		
		this.setMostrarMsg(false);
	}

	private void buscaDadosLicitacao() {

		// Dados licitação - Nro, Item - SQL3
		List<ScoFaseSolicitacao> resultDadosLicitacao = this.solicitacaoComprasFacade
				.listarDadosLicitacao(numero);
		if (resultDadosLicitacao.size() > 0) {			
			
			if (resultDadosLicitacao.get(0).getItemLicitacao().getId().getLctNumero() != null){
				this.setNroLicitacao(resultDadosLicitacao.get(0).getItemLicitacao().getId().getLctNumero());
			}
			
			if (resultDadosLicitacao.get(0).getItemLicitacao().getId().getNumero() != null){
				this.setItem(resultDadosLicitacao.get(0).getItemLicitacao().getId().getNumero());
			}
			
			// Busca Dados Licitação - Data Digitacao, Data Publicacao
			ScoItemLicitacao resultDadosLicitacao1 = this.solicitacaoComprasFacade
					.obterDataDigitacaoPublicacaoLicitacao(nroLicitacao, item);

			if (resultDadosLicitacao1 != null) {
				if (resultDadosLicitacao1.getLicitacao().getDtDigitacao() != null) {
					this.setDataDigitacao(resultDadosLicitacao1.getLicitacao().getDtDigitacao());							
				}

				if (resultDadosLicitacao1.getLicitacao().getDtPublicacao() != null) {
					this.setDataPublicacao(resultDadosLicitacao1.getLicitacao().getDtPublicacao());
				}
			}

			// Busca Dados Licitação - Data Proposta
			List<ScoPropostaFornecedor> resultDadosLicitacao2 = this.solicitacaoComprasFacade
					.listarDataDigitacaoPropostaForn(nroLicitacao);

			if (resultDadosLicitacao2.size() > 0) {
				if (resultDadosLicitacao2.get(0).getDtDigitacao() != null) {
					this.setDataProposta(resultDadosLicitacao2.get(0).getDtDigitacao());
				}
			}			
		}

	}

	private void buscaDadosAF() {
		Integer afnNumero;
		Integer auxNumero;

		afnNumero = null;
		auxNumero = null;

		// Busca dados AF parte 1
		ScoFaseSolicitacao resultDadosAF = this.solicitacaoComprasFacade
				.obterDadosAutorizacaoFornecimento(numero);
				
		if (resultDadosAF != null) {			
			afnNumero = resultDadosAF.getItemAutorizacaoForn().getId().getAfnNumero();
			auxNumero = resultDadosAF.getItemAutorizacaoForn().getId().getNumero();

			//Busca dados AF parte 2 
			ScoItemAutorizacaoForn resultDadosItens =  this.solicitacaoComprasFacade
					.obterDadosItensAutorizacaoFornecimento(afnNumero, auxNumero);
			  
			 if (resultDadosItens != null) { 
				 if (resultDadosItens.getAutorizacoesForn().getPropostaFornecedor().getId().getLctNumero()!= null) {
					 this.setNroAF(resultDadosItens.getAutorizacoesForn().getPropostaFornecedor().getId().getLctNumero());
				}
				 
				if (resultDadosItens.getAutorizacoesForn().getNroComplemento() != null) {
					 this.setNroComplemento(resultDadosItens.getAutorizacoesForn().getNroComplemento());
				}
				
				if (resultDadosItens.getAutorizacoesForn().getDtGeracao() != null) {
					 this.setDataGeracao(resultDadosItens.getAutorizacoesForn().getDtGeracao()); 
				}
				 
				if (resultDadosItens.getQtdeSolicitada() != null) {
					 this.setQuantidade(resultDadosItens.getQtdeSolicitada());
				} else {
					 this.setQuantidade(0);
				}
				 
				if (resultDadosItens.getUnidadeMedida() != null) {
					this.setUnidade(resultDadosItens.getUnidadeMedida()
							.getCodigo());
				}
				 
				if (resultDadosItens.getAutorizacoesForn().getDtPrevEntrega() != null) {
					 this.setPrevisaoEntrega (resultDadosItens.getAutorizacoesForn().getDtPrevEntrega()); 
				}
				 
				if (resultDadosItens.getAutorizacoesForn().getSituacao().toString() != null) {
					 this.setSituacao(resultDadosItens.getAutorizacoesForn().getSituacao().toString());
				}
				 
				if (resultDadosItens.getQtdeRecebida() != null) {
					 this.setQtdEntregue(resultDadosItens.getQtdeRecebida());
				} else {
					this.setQtdEntregue(0);
				}
				
				this.setQtdSaldo(this.quantidade - this.qtdEntregue);
			 }		
	    }	 
	}

	public String voltar() {
		return voltarPara;
	}
	
	public String redirecionarAcoesPontoParada(){
		return ACOES_PONTO_PARADA;
	}
		
	public Long countFasesSolicitacao() {
		return solicitacaoComprasFacade.countPesquisaFasesSolicitacaoCompra(numero);
	}

	
	public List<ScoScJn> listFasesSolicitacao() {       
		 
		List<ScoScJn> result = this.solicitacaoComprasFacade.listarPesquisaFasesSolicitacaoCompra(numero);

		if (result.size() == 0) {
			result = new ArrayList<ScoScJn>();
			this.setMostrarMsg(true);				
		}
		else {
			this.setMostrarMsg(false);
		}			

		return result;
	}

	// ### GETs e SETs ###

	public void setListFasesSolicitacao(List<ScoScJn> listFasesSolicitacao) {
		this.listFasesSolicitacao = listFasesSolicitacao;
	}

	public List<ScoScJn> getListFasesSolicitacao() {
		return listFasesSolicitacao;
	}

	public ScoFaseSolicitacao getDadosLicitacao() {
		return dadosLicitacao;
	}

	public void setDadosLicitacao(ScoFaseSolicitacao dadosLicitacao) {
		this.dadosLicitacao = dadosLicitacao;
	}

	public void setNumero(final Integer numero) {
		this.numero = numero;
	}

	public Integer getNumero() {
		return numero;
	}

	public Integer getNroLicitacao() {
		return nroLicitacao;
	}

	public void setNroLicitacao(Integer nroLicitacao) {
		this.nroLicitacao = nroLicitacao;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public Date getDataDigitacao() {
		return dataDigitacao;
	}

	public void setDataDigitacao(Date dataDigitacao) {
		this.dataDigitacao = dataDigitacao;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public Date getDataProposta() {
		return dataProposta;
	}

	public void setDataProposta(Date dataProposta) {
		this.dataProposta = dataProposta;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Date getPrevisaoEntrega() {
		return previsaoEntrega;
	}

	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Boolean getMostrarMsg() {
		return mostrarMsg;
	}

	public void setMostrarMsg(Boolean mostrarMsg) {
		this.mostrarMsg = mostrarMsg;
	}

	public Boolean getMostrarBotaoVoltar() {
		return mostrarBotaoVoltar;
	}

	public void setMostrarBotaoVoltar(Boolean mostrarBotaoVoltar) {
		this.mostrarBotaoVoltar = mostrarBotaoVoltar;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getOrigemMenu() {
		return origemMenu;
	}

	public void setOrigemMenu(Boolean origemMenu) {
		this.origemMenu = origemMenu;
	}

	public Integer getQtdSaldo() {
		return qtdSaldo;
	}

	public void setQtdSaldo(Integer qtdSaldo) {
		this.qtdSaldo = qtdSaldo;
	}

	public Integer getQtdEntregue() {
		return qtdEntregue;
	}

	public void setQtdEntregue(Integer qtdEntregue) {
		this.qtdEntregue = qtdEntregue;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

}
