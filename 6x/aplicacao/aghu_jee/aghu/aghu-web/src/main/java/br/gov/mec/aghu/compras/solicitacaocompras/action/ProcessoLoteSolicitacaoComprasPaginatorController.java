package br.gov.mec.aghu.compras.solicitacaocompras.action;

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

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.action.ImprimirSolicitacaoDeComprasController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;

public class ProcessoLoteSolicitacaoComprasPaginatorController extends ActionController implements ActionPaginator {

	private static final String IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO = "compras-imprimirSolicitacaoDeComprasPdfCadastro";

	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoDeCompra> dataModel;

	private static final Log LOG = LogFactory.getLog(ProcessoLoteSolicitacaoComprasPaginatorController.class); // MECPaginatorController
																												// {
	private static final long serialVersionUID = 6311943654533755902L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

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
	private ImprimirSolicitacaoDeComprasController imprimirSolicitacaoDeComprasController;

	// filtros
	ScoPontoParadaSolicitacao pontoParada;
	Integer numeroSolicitacaoCompra;
	Date dataSolicitacaoCompra;
	ScoGrupoMaterial grupoMaterial;
	ScoMaterial material;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;
	private Boolean desabilitaSuggestionGrupoMaterial;
	private boolean desabilitaBotoes;

	// modal de exclusao
	private String observacaoExclusao;

	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;

	// controle checkbox
	private List<ScoSolicitacaoDeCompra> listaChecked;
	private List<ScoSolicitacaoDeCompra> allChecked;
	private List<ScoSolicitacaoDeCompra> listaAtual;
	
	// modal de devolução
	private String justificativaDevolucao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.limparPesquisa();
	}

	public void pesquisar() {
		if (this.getNumeroSolicitacaoCompra() == null && this.getPontoParada() == null) {
			this.desabilitaBotoes = false;
			this.limparControleGrid();
			this.limparModalExclusao();
			this.limparModalEncaminhamento();
			this.limparModalDevolucao();
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_VALIDACAO_CAMPOS_OBRIGATORIOS_PESQUISA");
		} else {
			this.desabilitaBotoes = false;
			this.limparControleGrid();
			this.limparModalExclusao();
			this.limparModalEncaminhamento();
			this.limparModalDevolucao();
			this.dataModel.reiniciarPaginator();
		}
	}

	// métodos das suggestions

	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.getComprasFacade().pesquisarGrupoMaterialPorFiltro(parametro);
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(objPesquisa, null, true),listarMateriaisCount(objPesquisa));
	}

	public Long listarMateriaisCount(String filter) {
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String parametro) {
		return this.getCentroCustoFacade().pesquisarCentroCustoUsuarioGerarSCSuggestion(parametro);
	}

	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoEDescricao(String parametro) throws BaseException {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}

		return this.getComprasCadastrosBasicosFacade().pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(filtro, false);
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

	// botões de ação

	public void encaminharItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.getSolicitacaoComprasFacade().encaminharListaSolicitacaoCompras(listaNumero, this.getPontoParada(),
					this.getProximoPontoParada(), this.getFuncionarioComprador(), false);

			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_ENCAMINHADA_COM_SUCESSO", listaNumero.get(0));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_ENCAMINHADA_COM_SUCESSO");
			}

			this.limparControleGrid();
			this.limparModalEncaminhamento();
			this.closeDialog("modalEncaminharScoWG");
			this.dataModel.reiniciarPaginator();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void devolverItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);

			this.getSolicitacaoComprasFacade().devolverSolicitacoesCompras(listaNumero, justificativaDevolucao);

			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACAO_COMPRAS_DEVOLVIDA_COM_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOLICITACOES_COMPRAS_DEVOLVIDA_COM_SUCESSO");
			}

			limparControleGrid();
			limparModalDevolucao();
			this.closeDialog("modalDevolverScoWG");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void excluirItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.getSolicitacaoComprasFacade().inativarListaSolicitacaoCompras(listaNumero, this.observacaoExclusao);

			if (this.allChecked.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SOL_UNICA_COM_SUCESSO", listaNumero.get(0));
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_LISTA_COM_SUCESSO");
			}

			this.limparModalExclusao();
			this.limparControleGrid();
			this.closeDialog("modalExclusaoScoWG");
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public String imprimirItens() throws DocumentException {

		List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
		
		this.imprimirSolicitacaoDeComprasController.setNumSolicComps(listaNumero);

		try {
			this.imprimirSolicitacaoDeComprasController.print(null);
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
		this.dataModel.reiniciarPaginator();

		return IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO;
	}

	// paginator

	@Override
	public Long recuperarCount() {
		return this.getSolicitacaoComprasFacade().countLoteSolicitacaoCompras(this.montarFiltroPesquisa());
	}

	@Override
	public List<ScoSolicitacaoDeCompra> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		List<ScoSolicitacaoDeCompra> listaResultado = this.getSolicitacaoComprasFacade().pesquisarLoteSolicitacaoCompras(firstResult, maxResults,
				orderProperty, asc, this.montarFiltroPesquisa());
		listaAtual.clear();
		listaAtual.addAll(listaResultado);
		
		return listaResultado;
	}
	
	
	// metodos de auxílio
	public void atualizarAllChecked() {
		this.listaChecked.clear();
		for (ScoSolicitacaoDeCompra sc : this.allChecked) {
			this.listaChecked.add(sc);
		}
	}

	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(ScoSolicitacaoDeCompra sc : listaAtual) {
				if (!this.allChecked.contains(sc)) {
					this.allChecked.add(sc);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(ScoSolicitacaoDeCompra sc : listaAtual) {
				if (this.allChecked.contains(sc)) {
					this.allChecked.remove(sc);
				}
			}
		}
	}

	public void selecionarLinha(SelectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
		} else {
			this.allChecked.add(sc);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
		} else {
			this.allChecked.add(sc);
		}
	}

	public void atualizarPontoParada() {
		// preenche o ponto de parada caso tenha sido informado somente o numero
		// da SC
		if (this.getPontoParada() == null && this.getNumeroSolicitacaoCompra() != null) {
			ScoSolicitacaoDeCompra solCompras = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(
					this.getNumeroSolicitacaoCompra());

			if (solCompras != null) {
				if (this.getComprasCadastrosBasicosFacade().verificarPontoParadaPermitido(solCompras.getPontoParadaProxima())) {
					this.setPontoParada(solCompras.getPontoParadaProxima());
				}
			}
		}
	}

	public void verificarGrupoMaterialHabilitado() {
		this.desabilitaSuggestionGrupoMaterial = ((this.getMaterial() != null && this.getGrupoMaterial() == null) ? true : false);
	}

	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(this.getComprasCadastrosBasicosFacade()
				.verificarPontoParadaComprador(this.proximoPontoParada));
	}

	public void limparPesquisa() {
		this.limparFiltros();
		this.limparModalExclusao();
		this.limparControleGrid();
		this.limparModalEncaminhamento();
		this.dataModel.setPesquisaAtiva(false);
		this.setDesabilitaBotoes(false);
	}

	private PesqLoteSolCompVO montarFiltroPesquisa() {

		PesqLoteSolCompVO filtrosPesquisa = new PesqLoteSolCompVO();

		filtrosPesquisa.setPontoParada(this.getPontoParada());
		filtrosPesquisa.setNumeroSolicitacaoCompra(this.getNumeroSolicitacaoCompra());
		filtrosPesquisa.setDataSolicitacaoCompra(this.getDataSolicitacaoCompra());
		filtrosPesquisa.setGrupoMaterial(this.getGrupoMaterial());
		filtrosPesquisa.setMaterial(this.getMaterial());
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

	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ScoSolicitacaoDeCompra>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ScoSolicitacaoDeCompra>();
		}
		this.allChecked.clear();
	}

	public void limparModalExclusao() {
		this.observacaoExclusao = null;
		this.voltarPanel = false;
	}

	public void limparModalDevolucao() {
		this.justificativaDevolucao = null;
		this.voltarPanel = false;
	}

	private void limparFiltros() {
		this.setListaChecked(new ArrayList<ScoSolicitacaoDeCompra>());
		this.allChecked = new ArrayList<ScoSolicitacaoDeCompra>();
		this.listaAtual = new ArrayList<ScoSolicitacaoDeCompra>();
		this.pontoParada = null;
		this.numeroSolicitacaoCompra = null;
		this.dataSolicitacaoCompra = null;
		this.grupoMaterial = null;
		this.material = null;
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;
		this.desabilitaSuggestionGrupoMaterial = false;
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

	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}

	public Date getDataSolicitacaoCompra() {
		return dataSolicitacaoCompra;
	}

	public void setDataSolicitacaoCompra(Date dataSolicitacaoCompra) {
		this.dataSolicitacaoCompra = dataSolicitacaoCompra;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
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

	public ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}

	public void setSolicitacaoComprasFacade(ISolicitacaoComprasFacade solicitacaoComprasFacade) {
		this.solicitacaoComprasFacade = solicitacaoComprasFacade;
	}

	public Boolean getDesabilitaSuggestionGrupoMaterial() {
		return desabilitaSuggestionGrupoMaterial;
	}

	public void setDesabilitaSuggestionGrupoMaterial(Boolean desabilitaSuggestionGrupoMaterial) {
		this.desabilitaSuggestionGrupoMaterial = desabilitaSuggestionGrupoMaterial;
	}

	public boolean isDesabilitaBotoes() {
		return desabilitaBotoes;
	}

	public void setDesabilitaBotoes(boolean desabilitaBotoes) {
		this.desabilitaBotoes = desabilitaBotoes;
	}

	public String getJustificativaDevolucao() {
		return justificativaDevolucao;
	}

	public void setJustificativaDevolucao(String justificativaDevolucao) {
		this.justificativaDevolucao = justificativaDevolucao;
	}

	public DynamicDataModel<ScoSolicitacaoDeCompra> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoDeCompra> dataModel) {
		this.dataModel = dataModel;
	}

	public List<ScoSolicitacaoDeCompra> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ScoSolicitacaoDeCompra> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ScoSolicitacaoDeCompra> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ScoSolicitacaoDeCompra> allChecked) {
		this.allChecked = allChecked;
	}
}