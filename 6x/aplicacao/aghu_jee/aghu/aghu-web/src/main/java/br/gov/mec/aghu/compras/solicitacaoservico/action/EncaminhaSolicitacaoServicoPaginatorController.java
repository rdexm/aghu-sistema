package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EncaminhaSolicitacaoServicoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654533755902L;
	private static final Log LOG = LogFactory.getLog(EncaminhaSolicitacaoServicoPaginatorController.class);
	
	private static final String PAGE_FASES_SOLICITACAO_SERVICO_LIST = "fasesSolicitacaoServicoList";
	private static final String PAGE_SOLICITACAO_SERVICO_CRUD = "solicitacaoServicoCRUD";
	private static final String PAGE_ENCAMINHAR_SOLICITACAO_SERVICO = "compras-encaminhaSolicitacaoServico";
	private static final String PAGE_IMPRIMIR_SOLICITACAO_SERVICO_PDF_CADASTRO = "compras-imprimirSolicitacaoDeServicoPdfCadastro";

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ImprimirSolicitacaoDeServicoController imprimirSolicitacaoDeServicoController;

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

	private Boolean desabilitaSuggestionGrupoServico;
	private Boolean desabilitaFiltros;
	private boolean desabilitaBotoes;
	private String justificativaDevolucao;
	private Boolean existeSSImpossibilitadaDevolver;

	// modal de exclusao
	private String observacaoExclusao;

	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;

	// controles checkbox
	private List<ScoSolicitacaoServico> listaChecked;
	private List<ScoSolicitacaoServico> allChecked;
	private List<ScoSolicitacaoServico> listaAtual;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.limparPesquisa();
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

	public void pesquisar() {
		if (this.getNumeroSolicitacaoServico() == null && this.getPontoParada() == null) {
			this.limparControleGrid();
			this.limparModalExclusao();
			this.limparModalEncaminhamento();
			dataModel.reiniciarPaginator();
			this.desabilitaBotoes = false;
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_VALIDACAO_CAMPOS_OBRIGATORIOS_PESQUISA_SS");
		} else {
			this.limparControleGrid();
			this.limparModalExclusao();
			this.limparModalEncaminhamento();
			dataModel.reiniciarPaginator();
			this.desabilitaBotoes = false;
			dataModel.setPesquisaAtiva(Boolean.TRUE);
		}
	}

	// métodos das suggestions

	public List<ScoGrupoServico> pesquisarGrupoServicoPorCodigoDescricao(String pesquisa) {
		return this.getComprasFacade().listarGrupoServico(pesquisa);
	}

	public List<ScoServico> pesquisarServicoPorCodigoDescricao(String param) throws BaseException {
		return this.getComprasFacade().listarServicosAtivos(param);
	}

	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}
		return this.getCentroCustoFacade().pesquisarCentroCustosPorCodigoDescricao(filtro);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro)
			throws BaseException {
		return this.getComprasCadastrosBasicosFacade().pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(parametro,
				((ScoPontoParadaSolicitacao) this.getComprasCadastrosBasicosFacade().obterPontoParadaAutorizacao()).getCodigo());
	}

	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.getRegistroColaboradorFacade().pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoEDescricao(String parametro)
			throws BaseException {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}
		return this.getComprasCadastrosBasicosFacade().pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(filtro, false);
	}

	// botões de ação

	public void encaminharItens() {
		try {
			this.getSolicitacaoServicoFacade().encaminharListaSolicitacaoServico(this.allChecked,
					this.getComprasCadastrosBasicosFacade().obterPontoParadaAutorizacao(), this.getProximoPontoParada(),
					this.getFuncionarioComprador(), false);

			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_SS_ENCAMINHADA_COM_SUCESSO", this.allChecked.get(0).getNumero());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_LISTA_SS_ENCAMINHADA_COM_SUCESSO");
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
			this.getSolicitacaoServicoFacade().inativarListaSolicitacaoServico(this.allChecked, this.observacaoExclusao);

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

	public String imprimirItens() {
		List<Integer> listaNumero = this.solicitacaoServicoFacade.obterListaNumeroSs(this.allChecked);
		
		
		imprimirSolicitacaoDeServicoController.setNumSolicServicos(listaNumero);
		imprimirSolicitacaoDeServicoController.setVoltarParaUrl(PAGE_ENCAMINHAR_SOLICITACAO_SERVICO);
		
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

	// paginator
	@Override
	public Long recuperarCount() {
		return this.getSolicitacaoServicoFacade().countSolicitacaoServicoEncaminharSs(this.montarFiltroPesquisa());
	}

	@Override
	public List<ScoSolicitacaoServico> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		List<ScoSolicitacaoServico> listaResultado =this.getSolicitacaoServicoFacade().pesquisarSolicitacaoServicoEncaminharSs(
				firstResult, maxResults, orderProperty, asc, this.montarFiltroPesquisa());
				
		listaAtual.clear();
		listaAtual.addAll(listaResultado);
		
		return listaResultado;
	}

	// metodos de auxílio

	public void atualizarPontoParada() {
		// preenche o ponto de parada caso tenha sido informado somente o numero
		// da SC
		if (this.getPontoParada() == null && this.getNumeroSolicitacaoServico() != null) {
			ScoSolicitacaoServico solServico = this.getSolicitacaoServicoFacade().obterSolicitacaoServico(
					this.getNumeroSolicitacaoServico());

			if (solServico != null) {

				if (this.getComprasCadastrosBasicosFacade().verificarPontoParadaPermitido(solServico.getPontoParada())) {
					this.setPontoParada(solServico.getPontoParada());
				}
			}
		}
	}

	public void desligarFiltros() {
		if (this.getNumeroSolicitacaoServico() != null) {			
			limparFiltros(false);			
			this.atualizarPontoParada();
			this.setDesabilitaFiltros(true);
		} else {
			this.setDesabilitaFiltros(false);
		}
	}

	public void verificarGrupoServicoHabilitado() {
		this.desabilitaSuggestionGrupoServico = ((this.getServico() != null && this.getGrupoServico() == null) ? true : false);
	}

	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(this.getComprasCadastrosBasicosFacade().verificarPontoParadaComprador(
				this.proximoPontoParada));
	}

	public void limparPesquisa() {
		this.limparFiltros(true);
		this.limparModalExclusao();
		this.limparControleGrid();
		this.limparModalEncaminhamento();
		this.setDesabilitaBotoes(false);
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		this.setExisteSSImpossibilitadaDevolver(false);
	}

	private LoteSolicitacaoServicoVO montarFiltroPesquisa() {

		LoteSolicitacaoServicoVO filtrosPesquisa = new LoteSolicitacaoServicoVO();
		filtrosPesquisa.setPontoParada(this.getPontoParada());
		filtrosPesquisa.setNumeroSolicitacaoServico(this.getNumeroSolicitacaoServico());
		filtrosPesquisa.setDataSolicitacaoServico(this.getDataSolicitacaoServico());
		filtrosPesquisa.setGrupoServico(this.getGrupoServico());
		filtrosPesquisa.setServico(this.getServico());
		filtrosPesquisa.setCentroCustoSolicitante(this.getCentroCustoSolicitante());
		filtrosPesquisa.setCentroCustoAplicacao(this.getCentroCustoAplicacao());

		return filtrosPesquisa;
	}

	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
	}

	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (ScoSolicitacaoServico ss : this.allChecked) {
			this.listaChecked.add(ss);
		}
	}
	
	public void selecionarLinha(SelectEvent event) {
		ScoSolicitacaoServico ss = (ScoSolicitacaoServico)event.getObject();
		if (this.allChecked.contains(ss)) {
			this.allChecked.remove(ss);
		} else {
			this.allChecked.add(ss);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ScoSolicitacaoServico ss = (ScoSolicitacaoServico)event.getObject();
		if (this.allChecked.contains(ss)) {
			this.allChecked.remove(ss);
		} else {
			this.allChecked.add(ss);
		}
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
	}
	
	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ScoSolicitacaoServico>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ScoSolicitacaoServico>();
		}
		this.allChecked.clear();
		this.listaChecked.clear();
		this.allChecked.clear();
	}

	public void limparModalExclusao() {
		this.observacaoExclusao = null;
		this.voltarPanel = false;
	}

	private void limparFiltros(boolean limparNumSolicitacaoServico) {
		this.setListaChecked(new ArrayList<ScoSolicitacaoServico>());
		this.allChecked = new ArrayList<ScoSolicitacaoServico>();
		this.listaAtual = new ArrayList<ScoSolicitacaoServico>();
		this.pontoParada = null;
		if (limparNumSolicitacaoServico) {
			this.numeroSolicitacaoServico = null;
		}
		this.dataSolicitacaoServico = null;
		this.grupoServico = null;
		this.servico = null;
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;
		this.desabilitaSuggestionGrupoServico = false;
		this.desabilitaFiltros = false;
	}

	public void limparModalDevolucao() {
		this.justificativaDevolucao = null;
		this.voltarPanel = false;
	}

	public void devolverItens() {
		try {
			solicitacaoServicoFacade.devolverSolicitacoesServico(this.allChecked, justificativaDevolucao);

			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_SERVICO_DEVOLVIDA_COM_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACOES_SERVICO_DEVOLVIDA_COM_SUCESSO");
			}
			this.closeDialog("modalDevolverScoWG");
			limparControleGrid();
			limparModalDevolucao();			
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	// gets and sets
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	public void setCentroCustoFacade(ICentroCustoFacade centroCustoFacade) {
		this.centroCustoFacade = centroCustoFacade;
	}

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

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public Boolean getDesabilitaSuggestionComprador() {
		return desabilitaSuggestionComprador;
	}

	public void setDesabilitaSuggestionComprador(Boolean desabilitaSuggestionComprador) {
		this.desabilitaSuggestionComprador = desabilitaSuggestionComprador;
	}

	public Boolean getDesabilitaSuggestionGrupoServico() {
		return desabilitaSuggestionGrupoServico;
	}

	public void setDesabilitaSuggestionGrupoServico(Boolean desabilitaSuggestionGrupoServico) {
		this.desabilitaSuggestionGrupoServico = desabilitaSuggestionGrupoServico;
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

	public ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}

	public void setSolicitacaoServicoFacade(ISolicitacaoServicoFacade solicitacaoServicoFacade) {
		this.solicitacaoServicoFacade = solicitacaoServicoFacade;
	}

	public String getJustificativaDevolucao() {
		return justificativaDevolucao;
	}

	public void setJustificativaDevolucao(String justificativaDevolucao) {
		this.justificativaDevolucao = justificativaDevolucao;
	}

	public Boolean getExisteSSImpossibilitadaDevolver() {
		return existeSSImpossibilitadaDevolver;
	}

	public void setExisteSSImpossibilitadaDevolver(Boolean existeSSImpossibilitadaDevolver) {
		this.existeSSImpossibilitadaDevolver = existeSSImpossibilitadaDevolver;
	}

	public DynamicDataModel<ScoSolicitacaoServico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoServico> dataModel) {
		this.dataModel = dataModel;
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