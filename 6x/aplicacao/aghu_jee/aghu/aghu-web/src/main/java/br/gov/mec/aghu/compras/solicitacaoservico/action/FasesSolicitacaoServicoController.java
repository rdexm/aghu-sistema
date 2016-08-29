package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import java.util.Objects;

import br.gov.mec.aghu.compras.autfornecimento.action.AutorizacaoFornecimentoController;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.action.ProcessoAdmComprasController;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class FasesSolicitacaoServicoController extends ActionController {

	private static final long serialVersionUID = -5861595457653656517L;
	
	private static final String PAGE_ACOES_PONTO_PARADA_SS = "acoesPontoParadaSs";
	private static final String PAGE_PAC_CRUD = "compras-processoAdmCompraCRUD";
	private static final String PAGE_AF_CRUD = "compras-autorizacaoFornecimentoCRUD";

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ProcessoAdmComprasController processoAdmComprasController;
	
	@Inject
	private AutorizacaoFornecimentoController autorizacaoFornecimentoController;
	
	// Objetos
	private List<ScoSsJn> listFasesSolicitacao;
	private ScoFaseSolicitacao dadosLicitacao = new ScoFaseSolicitacao();
		
	// Numero da Solicitacao de Servico
	private Integer numero;

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
	private String unidade;
	private Date previsaoEntrega;
	private String situacao;
	private BigDecimal valorUnitario;
	private BigDecimal valorEntregue;
	private BigDecimal valorSaldo;
	
	private Boolean mostrarMsg;	
	private Boolean mostrarBotaoVoltar;
	private String voltarPara;
	private Boolean origemMenu;

	private ScoSolicitacaoServico solicitacaoServico;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void pesquisar() {		
		this.solicitacaoServico = this.solicitacaoServicoFacade.obterSolicitacaoServico(numero);
		if (this.solicitacaoServico == null) {
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

	public void iniciar() {
	 

	 

		this.setOrigemMenu(true);
		if (this.numero != null && this.voltarPara != null) {
			this.setOrigemMenu(false);
			this.pesquisar();
		}
	
	}
	
	
	public String acoesPontoParada(){
		return PAGE_ACOES_PONTO_PARADA_SS;
	}
	
	public String redirecionarAf() {
		autorizacaoFornecimentoController.setVoltarParaUrl("compras-fasesSolicitacaoServicoList");
		autorizacaoFornecimentoController.setNumeroAf(this.nroAF);
		autorizacaoFornecimentoController.setNumeroComplemento(this.nroComplemento);
		return PAGE_AF_CRUD;
	}
	
	public String redirecionarPac() {
		processoAdmComprasController.setVoltarParaUrl("compras-fasesSolicitacaoServicoList");
		processoAdmComprasController.setNumeroPac(this.nroLicitacao);
		return PAGE_PAC_CRUD;
	}
	
	public Boolean habilitarBotaoResgatar() {
		Boolean habilita = Boolean.FALSE;
		
		try {
			
			RapServidores servidor = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());
			
			if (listFasesSolicitacao != null && listFasesSolicitacao.size() > 0) {
				RapServidores servidorUltimoEnc = registroColaboradorFacade.obterServidorAtivoPorUsuario(listFasesSolicitacao.get(0).getNomeUsuario());
			
				if (listFasesSolicitacao.size() > 1 && Objects.equals(servidor, servidorUltimoEnc) &&
						!this.solicitacaoServicoFacade.verificarAcoesPontoParadaSs(this.numero, listFasesSolicitacao.get(0).getDataAlteracao())) {
					habilita = Boolean.TRUE;
				}
			}
					
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return habilita;
	}
	
	public void resgatarSs() {
		try {
			solicitacaoServicoFacade.resgatarSs(this.numero);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_RESGATADA_COM_SUCESSO");
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa() {		
		this.inicializaVariaveis();
		this.numero = null;
	}
	
	public void inicializaVariaveis() {
		
		this.setListFasesSolicitacao(new ArrayList<ScoSsJn>());		
		
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
		this.unidade = null;
		this.previsaoEntrega = null;
		this.situacao = null;
		this.valorEntregue = BigDecimal.ZERO;
		this.valorSaldo = BigDecimal.ZERO;
		this.valorUnitario = BigDecimal.ZERO;
		
		this.setMostrarMsg(false);
	}

	private void buscaDadosLicitacao() {

		// Dados licitação - Nro, Item - SQL3
		List<ScoFaseSolicitacao> resultDadosLicitacao = this.solicitacaoServicoFacade
				.listarDadosLicitacaoSs(numero);
		if (resultDadosLicitacao.size() > 0) {			
			
			if (resultDadosLicitacao.get(0).getItemLicitacao().getId().getLctNumero() != null){
				this.setNroLicitacao(resultDadosLicitacao.get(0).getItemLicitacao().getId().getLctNumero());
			}
			
			if (resultDadosLicitacao.get(0).getItemLicitacao().getId().getNumero() != null){
				this.setItem(resultDadosLicitacao.get(0).getItemLicitacao().getId().getNumero());
			}
			
			// Busca Dados Licitação - Data Digitacao, Data Publicacao
			ScoItemLicitacao licitacaoDataDigPub = this.solicitacaoComprasFacade
					.obterDataDigitacaoPublicacaoLicitacao(nroLicitacao, item);

			if (licitacaoDataDigPub != null) {
				if (licitacaoDataDigPub.getLicitacao().getDtDigitacao() != null) {
					this.setDataDigitacao(licitacaoDataDigPub.getLicitacao().getDtDigitacao());							
				}

				if (licitacaoDataDigPub.getLicitacao().getDtPublicacao() != null) {
					this.setDataPublicacao(licitacaoDataDigPub.getLicitacao().getDtPublicacao());
				}
			}

			// Busca Dados Licitação - Data Proposta
			List<ScoPropostaFornecedor> licitacaoDataProposta = this.solicitacaoComprasFacade
					.listarDataDigitacaoPropostaForn(nroLicitacao);

			if (licitacaoDataProposta.size() > 0) {
				if (licitacaoDataProposta.get(0).getDtDigitacao() != null) {
					this.setDataProposta(licitacaoDataProposta.get(0).getDtDigitacao());
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
		ScoFaseSolicitacao resultDadosAF = this.solicitacaoServicoFacade
				.obterDadosAutorizacaoFornecimentoSs(numero);
				
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
				 
				if (resultDadosItens.getValorUnitario() != null) {
					 this.setValorUnitario(new BigDecimal(resultDadosItens.getValorUnitario()));
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
				 
				if (resultDadosItens.getValorEfetivado() != null) {
					 this.setValorEntregue(new BigDecimal(resultDadosItens.getValorEfetivado()));
				}	 
				
				this.setValorSaldo(this.valorUnitario.subtract(this.valorEntregue));
			 }		
	    }	 
	}

	public Boolean exibirEdicao(ScoSsJn item) {
		Boolean exibir = Boolean.FALSE;
			
		ScoPontoParadaSolicitacao pps = this.comprasCadastrosBasicosFacade.obterPontoParada(item.getPpsCodigo());
		if (pps != null) {
			exibir = item.getEtapa().equalsIgnoreCase("Atual") && this.comprasCadastrosBasicosFacade.verificarPontoParadaPermitido(pps);
		}

		return exibir;
	}

	public String voltar() {
		return voltarPara;
	}
		
	public Long countFasesSolicitacao() {
		return this.solicitacaoServicoFacade.countPesquisaFasesSolicitacaoServico(numero);
	}

	public List<ScoSsJn> listFasesSolicitacao() {       
		 
		List<ScoSsJn> result = this.solicitacaoServicoFacade.listarPesquisaFasesSolicitacaoServico(numero);

		if (result.size() == 0) {
			result = new ArrayList<ScoSsJn>();
			this.setMostrarMsg(true);				
		}
		else {
			this.setMostrarMsg(false);
		}			

		return result;
	}

	public String obterDescricaoPontoParada(ScoSsJn fase) {
		return comprasCadastrosBasicosFacade.obterPontoParada(fase.getPpsCodigo()).getCodigoDescricao();
	}
	
	public String obterNomeServidorAutorizador(ScoSsJn fase) {
		return (fase.getSerMatriculaAutorizada() == null ? "" : registroColaboradorFacade.obterNomePessoaServidor(fase.getSerVinCodigoAutorizada(), fase.getSerMatriculaAutorizada()));
	}

	public String obterRamalServidorAutorizador(ScoSsJn fase) {
		String ramal = "";
		
		RapServidores servidor = this.registroColaboradorFacade.obterServidor(fase.getSerVinCodigoAutorizada(), fase.getSerMatriculaAutorizada());
		
		if (servidor != null && servidor.getRamalTelefonico() != null) {
			ramal = servidor.getRamalTelefonico().getNumeroRamal().toString();
		}
		
		return ramal;
	}
	
	// ### GETs e SETs ###

	public void setListFasesSolicitacao(List<ScoSsJn> listFasesSolicitacao) {
		this.listFasesSolicitacao = listFasesSolicitacao;
	}

	public List<ScoSsJn> getListFasesSolicitacao() {
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

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorEntregue() {
		return valorEntregue;
	}

	public void setValorEntregue(BigDecimal valorEntregue) {
		this.valorEntregue = valorEntregue;
	}

	public BigDecimal getValorSaldo() {
		return valorSaldo;
	}

	public void setValorSaldo(BigDecimal valorSaldo) {
		this.valorSaldo = valorSaldo;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	
}
