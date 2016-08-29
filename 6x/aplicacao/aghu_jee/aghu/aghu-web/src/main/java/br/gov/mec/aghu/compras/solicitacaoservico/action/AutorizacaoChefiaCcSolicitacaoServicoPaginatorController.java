package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class AutorizacaoChefiaCcSolicitacaoServicoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654533755902L;

	private static final Log LOG = LogFactory.getLog(AutorizacaoChefiaCcSolicitacaoServicoPaginatorController.class);
	
	private static final String PAGE_FASES_SOLICITACAO_SERVICO_LIST = "fasesSolicitacaoServicoList";
	private static final String PAGE_SOLICITACAO_SERVICO_CRUD = "solicitacaoServicoCRUD";
	private static final String PAGE_IMPRIMIR_SOLICITACAO_SERVICO_PDF_CADASTRO = "compras-imprimirSolicitacaoDeServicoPdfCadastro";
	private static final String PAGE_AUTORIZACAO_CHEFIA_CC_SOLICITACAO_COMPRAS = "compras-autorizacaoChefiaCcSolicitacaoCompras";
	private static final String PAGE_ANEXAR_DOCUMENTO_SOLICITAO_COMPRA = "anexarDocumentoSolicitacaoCompra";
	private static final String PAGE_AUTORIZACAO_CHEFIA_CC_ANEXOS = "suprimentos-anexarDocumentoSolicitacaoCompra";

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
		
	@Inject
	private ImprimirSolicitacaoDeServicoController imprimirSolicitacaoDeServicoController;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	
	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoServico> dataModel;
	
	// filtros
	ScoPontoParadaSolicitacao pontoParada;
	Integer numeroSolicitacaoServico;
	Date dataSolicitacaoServico;
	ScoGrupoServico grupoServico;	
	
	ScoServico servico;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;	
	DominioSimNao indUrgente;
	DominioSimNao indPrioritaria;
	DominioSimNao indExibirSolic;
	DominioSimNao indAutorizada;
	Date dataInicioAutorizacaoServico, dataFimAutorizacaoServico;
	
	private Boolean desabilitaSuggestionGrupoServico;
	private Boolean desabilitaFiltros;
	private boolean desabilitaBotoes;
	
	// modal de exclusao
	private String observacaoExclusao;
	private Boolean pontoParadaChefia;
	
	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;
	
	// modal de devolução
	private String justificativaDevolucao;
	
	// controles checkbox
	private List<ScoSolicitacaoServico> listaChecked;
	private List<ScoSolicitacaoServico> allChecked;
	private List<ScoSolicitacaoServico> listaAtual;
	
	private Boolean autorizar;	
	
	@PostConstruct
	protected void inicializar(){
		 this.begin(conversation);
		 if (this.dataModel.getRowCount() == 0){
			 this.limparPesquisa();
			 this.pesquisar();
		 }
	}

	public void pesquisar() {
		this.limparControleGrid();
		this.limparModalExclusao();
		this.limparModalEncaminhamento();
		this.desligarFiltros();
		this.dataModel.reiniciarPaginator();
		this.desabilitaBotoes = false;
	}
	
	public String gerarSS(){
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}
	
	public String visualizarSS(){
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}
	
	public String editarSS(){
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}
	
	public String fasesSolicitacaoServico(){
		return PAGE_FASES_SOLICITACAO_SERVICO_LIST;
	}
    
	public String anexosSS(){
		return PAGE_AUTORIZACAO_CHEFIA_CC_ANEXOS;
	}
	
	public String anexarDocumentoSolicitacaoCompra() {
		return PAGE_ANEXAR_DOCUMENTO_SOLICITAO_COMPRA;
	}
	
	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (ScoSolicitacaoServico ss : this.allChecked) {
			this.listaChecked.add(ss);
		}
		this.verificarPontoParadaChefia();
	}

	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(ScoSolicitacaoServico ss : listaAtual) {
				if (!this.allChecked.contains(ss)) {
					this.allChecked.add(ss);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(ScoSolicitacaoServico ss : listaAtual) {
				if (this.allChecked.contains(ss)) {
					this.allChecked.remove(ss);
				}
			}
		}
		this.verificarPontoParadaChefia();
	}
	
	public void selecionarLinha(SelectEvent event) {
		ScoSolicitacaoServico ss = (ScoSolicitacaoServico)event.getObject();
		if (this.allChecked.contains(ss)) {
			this.allChecked.remove(ss);
		} else {
			this.allChecked.add(ss);
		}
		this.verificarPontoParadaChefia();
	}

	public void desmarcarLinha(UnselectEvent event) {
		ScoSolicitacaoServico ss = (ScoSolicitacaoServico)event.getObject();
		if (this.allChecked.contains(ss)) {
			this.allChecked.remove(ss);
		} else {
			this.allChecked.add(ss);
		}
		this.verificarPontoParadaChefia();
	}
	
	public Boolean possuiAnexo(Integer slsNumero) {		
		return this.solicitacaoComprasFacade.verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SS, slsNumero);
	}
	
	// métodos das suggestions
			
	public List<ScoGrupoServico> pesquisarGrupoServicoPorCodigoDescricao(String pesquisa) {
		return comprasFacade.listarGrupoServico(pesquisa);
	}
	
	public List<ScoServico> pesquisarServicoPorCodigoDescricao(String param)
			throws BaseException {
		return comprasFacade.listarServicosAtivos(param);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filtro);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String parametro) {
		return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(parametro);
	}
	
	
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro) throws BaseException {
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(parametro, 
				((ScoPontoParadaSolicitacao) comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao()).getCodigo());
	}
	
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}
	
	// botões de ação
	
	public void encaminharItens() {
		try {
			solicitacaoServicoFacade.encaminharListaSolicitacaoServico(this.allChecked, 
					comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao(), this.getProximoPontoParada(), 
					this.getFuncionarioComprador(), this.autorizar);
			
			if (autorizar) {
				if (this.allChecked.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_SS_AUT_ENC_COM_SUCESSO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_SS_AUT_ENC_COM_SUCESSO");	
				}
			} else {
				if (this.allChecked.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_SS_ENCAMINHADA_COM_SUCESSO");
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_LISTA_SS_ENCAMINHADA_COM_SUCESSO");	
				}
			}
			
			this.limparControleGrid();
			this.limparModalEncaminhamento();
			this.closeDialog("modalEncaminharScoWG");
			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void excluirItens() {
		try {
			solicitacaoServicoFacade.inativarListaSolicitacaoServico(this.allChecked, this.observacaoExclusao);
			
			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SOL_SS_UNICA_COM_SUCESSO", this.allChecked.get(0).getNumero());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_LISTA_SS_COM_SUCESSO");	
			}
			
			this.limparModalExclusao();
			this.limparControleGrid();
			this.closeDialog("modalExclusaoScoWG");
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}
	
	public Boolean verificarDevolvida(Integer slsNumero) {
		return this.solicitacaoServicoFacade.verificarSolicitacaoDevolvidaAutorizacao(slsNumero);
	}
		
	public void autorizarItens() {
		try {			
			
			solicitacaoServicoFacade.autorizarListaSolicitacaoServico(this.allChecked);
			
			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SS_SOL_UNICA_COM_SUCESSO", this.allChecked.get(0).getNumero());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SS_SC_LISTA_COM_SUCESSO");	
			}
						
			this.limparControleGrid();
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
			this.voltarPanel = true;
		}
	}
	
	public String imprimirItens() {
		List<Integer> listaNumero = this.solicitacaoServicoFacade.obterListaNumeroSs(this.allChecked);
		imprimirSolicitacaoDeServicoController.setNumSolicServicos(listaNumero);			
		
		try {
			imprimirSolicitacaoDeServicoController.print(null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		this.limparControleGrid();
		dataModel.reiniciarPaginator();
		
		return PAGE_IMPRIMIR_SOLICITACAO_SERVICO_PDF_CADASTRO;
	}

	public String autorizarSc() {
		return PAGE_AUTORIZACAO_CHEFIA_CC_SOLICITACAO_COMPRAS;
	}
	
	public void devolverItens() {
		try {
			
			solicitacaoServicoFacade.devolverSolicitacoesServico(this.allChecked, justificativaDevolucao);
			
			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_SERVICO_DEVOLVIDA_COM_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACOES_SERVICO_DEVOLVIDA_COM_SUCESSO");	
			}
			
			limparControleGrid();
			limparModalDevolucao();
			this.closeDialog("modalDevolverScoWG");
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}
	
	// paginator
	
	@Override
	public Long recuperarCount() {
		return solicitacaoServicoFacade.countSolicitacaoServicoAutorizarSs(this.montarFiltroPesquisa());
	}

	@Override
	public List<ScoSolicitacaoServico> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		List<ScoSolicitacaoServico> listaResultado = solicitacaoServicoFacade
				.pesquisarSolicitacaoServicoAutorizarSs(firstResult, maxResults, orderProperty, 
						asc, this.montarFiltroPesquisa());
		
		listaAtual.clear();
		listaAtual.addAll(listaResultado);
		
		return listaResultado;
	}
	
	
	// metodos de auxílio
	
	public String obterCorLinha(ScoSolicitacaoServico item) {
		String ret = "";

		if (item != null) {
			if (item.getPontoParada() != null && !Objects.equals(item.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.CH)) {
				ret = "background-color:#66FF99;";
			}
			
			if (item.getIndUrgente()) {
				ret = "background-color:#F8C88D;";
			}
		}
		
		return ret;
	}
	
	
	public void desligarFiltros() {
		if (this.getNumeroSolicitacaoServico() != null) {
			Integer scId = numeroSolicitacaoServico;
			limparFiltros();
			numeroSolicitacaoServico = scId;
			this.setDesabilitaFiltros(true);
		} else {
			this.setDesabilitaFiltros(false);
		}
	}
	
	public void verificarGrupoServicoHabilitado() {
		this.desabilitaSuggestionGrupoServico = ((this.getServico() != null && this.getGrupoServico() == null) ? true : false);
	}
	
	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(comprasCadastrosBasicosFacade.verificarPontoParadaComprador(this.proximoPontoParada));
	}
	
	public void limparFiltroAut() {
		if (!this.indAutorizada.isSim()) {
			this.setDataInicioAutorizacaoServico(null);
			this.setDataFimAutorizacaoServico(null);
		} else {
			this.indExibirSolic = DominioSimNao.N;
		}
	}
	
	public void limparPanelAut() {
		if (this.indExibirSolic.isSim()) {
			this.indAutorizada = DominioSimNao.N;
			this.limparFiltroAut();
		}
	}
	
	public void limparPesquisa() {
		this.limparFiltros();
		this.limparModalExclusao();
		this.limparControleGrid();
		this.limparModalEncaminhamento();
		this.setDesabilitaBotoes(false);
		dataModel.setPesquisaAtiva(Boolean.FALSE);		
	}
	
	public String obterTitleAutorizacao() {
		String descricaoPps;
		AghParametros parametroPpsPadrao;
		try{
			parametroPpsPadrao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PPS_PADRAO_AUTORIZA_SS);
			ScoPontoParadaSolicitacao ppsPadrao = this.comprasCadastrosBasicosFacade.obterPontoParada(parametroPpsPadrao.getVlrNumerico().shortValue());
			descricaoPps = WordUtils.capitalizeFully(ppsPadrao.getDescricao());
		} catch (ApplicationBusinessException e) {
			descricaoPps = "";
		}

		return descricaoPps;
	}

	private LoteSolicitacaoServicoVO montarFiltroPesquisa () {
		
		LoteSolicitacaoServicoVO filtrosPesquisa = new LoteSolicitacaoServicoVO();
		
		try {
			filtrosPesquisa.setPontoParada(this.comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		filtrosPesquisa.setNumeroSolicitacaoServico(this.getNumeroSolicitacaoServico());
		filtrosPesquisa.setDataSolicitacaoServico(this.getDataSolicitacaoServico());
		filtrosPesquisa.setGrupoServico(this.getGrupoServico());
		filtrosPesquisa.setServico(this.getServico());
		filtrosPesquisa.setCentroCustoSolicitante(this.getCentroCustoSolicitante());
		filtrosPesquisa.setCentroCustoAplicacao(this.getCentroCustoAplicacao());
		filtrosPesquisa.setSolicitacaoUrgente(this.getIndUrgente());
		filtrosPesquisa.setSolicitacaoPrioritaria(this.getIndPrioritaria());
		filtrosPesquisa.setDevolvidasENaoDevolvidas(true);
		filtrosPesquisa.setSolicitacaoAutorizada(this.indAutorizada);
		if (this.getIndExibirSolic() != null) {
			filtrosPesquisa.setExibirScSolicitante(this.getIndExibirSolic().isSim());
		} else {
			filtrosPesquisa.setExibirScSolicitante(true);
		}
		
		if (this.dataInicioAutorizacaoServico != null) {
			filtrosPesquisa.setDataInicioAutorizacaoSolicitacaoServico(this.dataInicioAutorizacaoServico);
		}
		
		if (this.dataFimAutorizacaoServico != null) {
			filtrosPesquisa.setDataFimAutorizacaoSolicitacaoServico(this.dataFimAutorizacaoServico);
		}
		
		return filtrosPesquisa;
	}

	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
	}
	
	public void limparModalEncaminhamento(Boolean autorizar) {		
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;	
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
		this.autorizar = autorizar;
	}	
		
	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ScoSolicitacaoServico>());
		}
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ScoSolicitacaoServico>();	
		}
		this.listaChecked.clear();
		this.allChecked.clear();
	}
	
	public void limparModalExclusao() {
		this.observacaoExclusao = null;
		this.voltarPanel = false;
	}
	
	private void limparFiltros() {
		this.setListaChecked(new ArrayList<ScoSolicitacaoServico>());
		this.allChecked = new ArrayList<ScoSolicitacaoServico>();
		this.listaAtual = new ArrayList<ScoSolicitacaoServico>();
		this.pontoParada = null;
		this.numeroSolicitacaoServico = null;
		this.dataSolicitacaoServico = null;
		this.grupoServico = null;
		this.servico = null;
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;	
		this.indPrioritaria = null;
		this.indUrgente = null;
		this.indExibirSolic = DominioSimNao.N;
		this.desabilitaSuggestionGrupoServico = false;
		this.desabilitaFiltros = false;
		this.indAutorizada = DominioSimNao.N;
		this.dataFimAutorizacaoServico = null;
		this.dataInicioAutorizacaoServico = null;		
	}
	
	public void limparModalDevolucao() {
		this.justificativaDevolucao = null;
		this.voltarPanel = false;
	}
	
	private void verificarPontoParadaChefia(){
		setPontoParadaChefia(this.solicitacaoServicoFacade.verificarPontoParadaChefia(allChecked));				
	}
	
	// gets and sets
	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public Integer getNumeroSolicitacaoServico() {
		return numeroSolicitacaoServico;
	}

	public void setNumeroSolicitacaoServico(Integer numeroSolicitacaoServico) {
		this.numeroSolicitacaoServico = numeroSolicitacaoServico;
	}

	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}

	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public String getObservacaoExclusao() {
		return observacaoExclusao;
	}

	public void setObservacaoExclusao(String observacaoExclusao) {
		this.observacaoExclusao = observacaoExclusao;
	}
	
	public ScoPontoParadaSolicitacao getProximoPontoParada() {
		return proximoPontoParada;
	}

	public void setProximoPontoParada(ScoPontoParadaSolicitacao proximoPontoParada) {
		this.proximoPontoParada = proximoPontoParada;
	}
	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public String getJustificativaDevolucao() {
		return justificativaDevolucao;
	}

	public void setJustificativaDevolucao(String justificativaDevolucao) {
		this.justificativaDevolucao = justificativaDevolucao;
	}
	
	public Boolean getDesabilitaSuggestionComprador() {
		return desabilitaSuggestionComprador;
	}

	public void setDesabilitaSuggestionComprador(
			Boolean desabilitaSuggestionComprador) {
		this.desabilitaSuggestionComprador = desabilitaSuggestionComprador;
	}
	
	public Boolean getDesabilitaSuggestionGrupoServico() {
		return desabilitaSuggestionGrupoServico;
	}

	public void setDesabilitaSuggestionGrupoServico(
			Boolean desabilitaSuggestionGrupoServico) {
		this.desabilitaSuggestionGrupoServico = desabilitaSuggestionGrupoServico;
	}

	public DominioSimNao getIndUrgente() {
		return indUrgente;
	}

	public void setIndUrgente(DominioSimNao indUrgente) {
		this.indUrgente = indUrgente;
	}

	public DominioSimNao getIndPrioritaria() {
		return indPrioritaria;
	}

	public void setIndPrioritaria(DominioSimNao indPrioritaria) {
		this.indPrioritaria = indPrioritaria;
	}

	public Boolean getDesabilitaFiltros() {
		return desabilitaFiltros;
	}

	public void setDesabilitaFiltros(Boolean desabilitaFiltros) {
		this.desabilitaFiltros = desabilitaFiltros;
	}

	public boolean isDesabilitaBotoes() {
		return desabilitaBotoes;
	}

	public void setDesabilitaBotoes(boolean desabilitaBotoes) {
		this.desabilitaBotoes = desabilitaBotoes;
	}
	public Date getDataSolicitacaoServico() {
		return dataSolicitacaoServico;
	}

	public void setDataSolicitacaoServico(Date dataSolicitacaoServico) {
		this.dataSolicitacaoServico = dataSolicitacaoServico;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public Boolean getPontoParadaChefia() {
		return pontoParadaChefia;
	}

	public void setPontoParadaChefia(Boolean pontoParadaChefia) {
		this.pontoParadaChefia = pontoParadaChefia;
	}

	public Boolean getAutorizar() {
		return autorizar;
	}

	public void setAutorizar(Boolean autorizar) {
		this.autorizar = autorizar;
	}
	
	public DynamicDataModel<ScoSolicitacaoServico> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoServico> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public DominioSimNao getIndExibirSolic() {
		return indExibirSolic;
	}

	public void setIndExibirSolic(DominioSimNao indExibirSolic) {
		this.indExibirSolic = indExibirSolic;
	}

	public DominioSimNao getIndAutorizada() {
		return indAutorizada;
	}

	public void setIndAutorizada(DominioSimNao indAutorizada) {
		this.indAutorizada = indAutorizada;
	}

	public Date getDataInicioAutorizacaoServico() {
		return dataInicioAutorizacaoServico;
	}

	public void setDataInicioAutorizacaoServico(Date dataInicioAutorizacaoServico) {
		this.dataInicioAutorizacaoServico = dataInicioAutorizacaoServico;
	}

	public Date getDataFimAutorizacaoServico() {
		return dataFimAutorizacaoServico;
	}

	public void setDataFimAutorizacaoServico(Date dataFimAutorizacaoServico) {
		this.dataFimAutorizacaoServico = dataFimAutorizacaoServico;
	}

	public List<ScoSolicitacaoServico> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ScoSolicitacaoServico> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ScoSolicitacaoServico> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ScoSolicitacaoServico> allChecked) {
		this.allChecked = allChecked;
	}
}