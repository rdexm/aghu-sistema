package br.gov.mec.aghu.compras.autfornecimento.action;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioVizAutForn;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import java.text.Collator;
import java.util.*;


public class PesquisarPlanjProgEntregaItensAFController extends ActionController implements ActionPaginator {

	

	private static final String CONSULTA_ITENS_AF_PROGRAMACAO_ENTREGA = "consultaItensAFProgramacaoEntrega";
	private static final String PROGRAMACAO_ENTREGA_ITEM_AF = "programacaoEntregaItemAF";
	private static final String EXCLUIR_PROGRAMACAO = "excluirProgramacao";
	private static final String TIPO_CONSULTA_BTN_LIB_ENT_ITEM="liberarEntregasPorItem";
	private static final String TIPO_CONSULTA_BTN_ALT_PROG="alterarProgramacao";
	private static final String VOLTAR_CONSULTA = "/pages/compras/autfornecimento/pesquisarPlanjProgEntregaItensAF.xhtml";


	@Inject @Paginator
	private DynamicDataModel<PesquisarPlanjProgrEntregaItensAfVO> dataModel;

	
	private PesquisarPlanjProgrEntregaItensAfVO selecionado;
	
	private static final Log LOG = LogFactory.getLog(PesquisarPlanjProgEntregaItensAFController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3672912971943538394L;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;


	@Inject
	private ExcluirProgramacaoEntregaItensAfController excluirProgramacaoEntregaItensAfController;

	@Inject
	private ProgramacaoManualParcelasEntregaAFController programacaoManualParcelasEntregaAFController;

	@Inject
	protected ConsultarParcelaEntregaMaterialController consultarParcelaEntregaMaterialController;
	
	@Inject
	private ProgramacaoEntregaItemAFController programacaoEntregaItemAFController;
	
	private PesquisarPlanjProgrEntregaItensAfFiltroVO filtro = new PesquisarPlanjProgrEntregaItensAfFiltroVO(DominioSimNao.N, DominioSimNao.S, DominioSimNao.N);
	private PesquisarPlanjProgrEntregaItensAfFiltroVO filtroPesquisa = new PesquisarPlanjProgrEntregaItensAfFiltroVO();
	
	private List<PesquisarPlanjProgrEntregaItensAfVO> listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
	private List<PesquisarPlanjProgrEntregaItensAfVO> listaChecked;
	private List<PesquisarPlanjProgrEntregaItensAfVO> allChecked;


//	private Boolean realizarPesquisaPadrao = true;
private Boolean realizarPesquisaPadrao = false;
	private Boolean realizarPesquisa = false;
	private Boolean exibirLista = false;
	private Boolean habilitarBotoes = false;
	private Boolean habilitarProgramacaoPendenteGeral = false;
	private Boolean selecionarTodos = false;
	private Integer numeroAF;
	
	private Integer numeroAf;
	private Short numeroComplemento;
	
	private Date dataLiberacaoEntrega;
	private PesquisarPlanjProgrEntregaItensAfVO afSelecionada;
	private String voltarParaUrl;
	private Boolean habilitarExcluirProgramacao = false;
	private Boolean habilitarParcelasLiberar = false;
	private Boolean habilitarAlterarProgramacao = false;
	private Boolean habilitarProgManual = false;
	private Boolean habilitarProgAutomatica = false;
	private Boolean habilitarBase = false;
	private boolean enviarParametros = false;
	private Boolean togglePanel = false;
	private Boolean pesquisaNova = false;

	private Comparator<PesquisarPlanjProgrEntregaItensAfVO> currentComparator;
	private String currentSortProperty;
	
	
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void inicio() {

		if (listaPlanjProgEntregaItensAFs == null || listaPlanjProgEntregaItensAFs.size()==0) {
			setMaxResults(13);
 			//REALIZAR PESQUISA DEFAULT
			this.getLimpar();
			exibirLista = false;
			togglePanel = false;
			listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);

			realizarPesquisaPadrao = false;
			realizarPesquisa=true;
			this.filtro.setEfetivada(DominioSimNao.N);
			this.filtro.setEstocavel(DominioSimNao.S);
			this.filtro.setVisualizarAutForn(DominioVizAutForn.P);
			this.currentComparator = null;
			habilitarBotoes=false;
			pesquisar();
		}

	}
	
	
	public void getLimpar() {
		filtro = new PesquisarPlanjProgrEntregaItensAfFiltroVO();
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		exibirLista = false;
		selecionarTodos = false;
		afSelecionada = null;
		this.filtro.setEfetivada(DominioSimNao.N);
		this.filtro.setEstocavel(DominioSimNao.S);
		this.filtro.setVisualizarAutForn(DominioVizAutForn.P);
		habilitarBotoes = false;

		listaChecked = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();
		allChecked = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();

		this.dataModel.setPesquisaAtiva(false);

		limparControleGrid();
	}

	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();
		}
		if (this.listaPlanjProgEntregaItensAFs == null) {
			this.listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();
		}
		this.allChecked.clear();
	}

	public String getVoltar() {
		return voltarParaUrl;
	}

	public void liberarEntrega() {
		try {

			// prepara os dados para liberaçao de entrega (botao 'liberar entrega por item')
			for (PesquisarPlanjProgrEntregaItensAfVO vo: listaChecked) {
				consultarParcelaEntregaMaterialController.setNumeroAf(vo.getNumeroAF());
				consultarParcelaEntregaMaterialController.setNumeroComplemento(vo.getComplemento());
				consultarParcelaEntregaMaterialController.setTipoConsulta(TIPO_CONSULTA_BTN_LIB_ENT_ITEM);
				consultarParcelaEntregaMaterialController.setVoltarParaUrl(VOLTAR_CONSULTA);
				consultarParcelaEntregaMaterialController.setIniciou(Boolean.FALSE);

				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				autFornecimentoFacade.liberarEntrega(listaPlanjProgEntregaItensAFs, dataLiberacaoEntrega, servidorLogado);
			}
			this.apresentarMsgNegocio(Severity.INFO, 
					"PROG_ENTREG_LIBERAR_ENTREGA_SUCESSO");
//			this.getPesquisarCorrente();
//			habilitarBotoes = false;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	/**
	 * Ação do botão PROGRAMAR AUTOMÁTICA
	 */
	public void getProgramacaoAutomatica() {
		try {

			List<Integer> listaAFs = new ArrayList<Integer>();
			for (PesquisarPlanjProgrEntregaItensAfVO vo: listaChecked){
				listaAFs.add(vo.getNumeroAF());

			}
			comprasFacade.programacaoAutomaticaParcelasAF(listaAFs);
			this.apresentarMsgNegocio(Severity.INFO, "PROGRAMACAO_REALIZADA_SUCESSO");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void botaoLiberarEntrega() {
		try {
			dataLiberacaoEntrega = autFornecimentoFacade.obterDataLiberacaoEntrega(filtro.getDataPrevisaoEntrega());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (PesquisarPlanjProgrEntregaItensAfVO item : this.allChecked) {
			this.listaChecked.add(item);
		}
	}
	
	public void processarNumeroAF() {
		if(filtro.getNumeroAF() == null) {
			filtro.setComplemento(null);
		}
	}
	
	public void processarAutForn() {
		if(!DominioVizAutForn.E.equals(filtro.getVisualizarAutForn())) {
			filtro.setDataPrevisaoEntrega(null);
		}
	}

	public String getProgPendente() {
		return "listarItensAutorizacaoFornecimento";
	}

	public String getProgGeral() {
		return "listarItensAutorizacaoFornecimento";
	}
	
	public String irParaBaseProgramacao() {

		for(PesquisarPlanjProgrEntregaItensAfVO vo: listaChecked) {
			programacaoEntregaItemAFController.setNumeroAF(vo.getNumeroAF());
		}

		return PROGRAMACAO_ENTREGA_ITEM_AF;
	}
	
	public String irConsultaItensAFProgramacaoEntrega(){
		return CONSULTA_ITENS_AF_PROGRAMACAO_ENTREGA;
	}
	
	public String liberarEntregasPorItem() {

		// prepara os dados para liberaçao de entrega (botao 'liberar entrega por item')
		for (PesquisarPlanjProgrEntregaItensAfVO vo: listaChecked) {
			consultarParcelaEntregaMaterialController.setNumeroAf(vo.getNumeroAF());
			consultarParcelaEntregaMaterialController.setNumeroComplemento(vo.getComplemento());
			consultarParcelaEntregaMaterialController.setTipoConsulta(TIPO_CONSULTA_BTN_LIB_ENT_ITEM);
			consultarParcelaEntregaMaterialController.setVoltarParaUrl(VOLTAR_CONSULTA);
			consultarParcelaEntregaMaterialController.setIniciou(Boolean.FALSE);
		}

		return "consultarParcelaEntregaMaterial";
	}
	
	public String alterarProgramacao() {
		consultarParcelaEntregaMaterialController.setNumeroAf(afSelecionada.getNumeroLicitacao());
		consultarParcelaEntregaMaterialController.setNumeroComplemento(afSelecionada.getComplemento());
		consultarParcelaEntregaMaterialController.setTipoConsulta(TIPO_CONSULTA_BTN_ALT_PROG);
		consultarParcelaEntregaMaterialController.setTipoConsulta(TIPO_CONSULTA_BTN_ALT_PROG);
		consultarParcelaEntregaMaterialController.setVoltarParaUrl(VOLTAR_CONSULTA);
		consultarParcelaEntregaMaterialController.setIniciou(Boolean.FALSE);
		return "consultarParcelaEntregaMaterial";
	}
	
	public String getParcelasLiberar() {
		return "consultarParcelaEntregaMaterial";
	}
	
	public void pesquisar() {
		if(DominioVizAutForn.E == filtro.getVisualizarAutForn()){
			if(filtro.getDataPrevisaoEntrega() == null){
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_INFORME_DATA_LIMITE_PREVISAO_ENTREGA");
			}
		} else {
			limparControleGrid();
			habilitarBotoes = false;
			habilitarParcelasLiberar = false;
			habilitarAlterarProgramacao = false;
			habilitarProgramacaoPendenteGeral =false;
			habilitarProgManual = false;
			habilitarProgAutomatica = false;
			habilitarExcluirProgramacao = false;
			habilitarBase = false;
			exibirLista = true;
			togglePanel = false;
			pesquisaNova = true;
			listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
			setMaxResults(13);
			dataModel.reiniciarPaginator();
		}

	}
	
	public void getPesquisaRedirect() {
		setMaxResults(13);
		exibirLista = true;
		togglePanel = false;
		pesquisaNova = true;
		realizarPesquisa = false;
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		this.dataModel.reiniciarPaginator();

	}
	
	
	public void getPesquisarCorrente() {
		exibirLista = true;
		togglePanel = false;
//		realizarPesquisa = true;
		realizarPesquisa = false;
		habilitarBotoes = false;
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		this.dataModel.reiniciarPaginator();
		
	}
	
	public DominioVizAutForn[] getVizAutForn() {
		return DominioVizAutForn.values();
	}
	
	public DominioModalidadeEmpenho[] getModalidadesEmpenho() {
		return new DominioModalidadeEmpenho[] { DominioModalidadeEmpenho.CONTRATO, DominioModalidadeEmpenho.ESTIMATIVA};
	}
	
	public List<ScoGrupoMaterial> listarGrupoMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter),listarGrupoMateriaisCount(filter));
	}
	
	public Long listarGrupoMateriaisCount(String filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}

	public List<ScoMaterial> listarMateriais(String filter){
		return this.returnSGWithCount(this.solicitacaoComprasFacade.listarMateriaisSC(filter),listarMateriaisCount(filter));
	}

	public Long listarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}

	public List<FccCentroCustos> listarCentroCustos(String filter) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filter),listarCentroCustosCount(filter));
	}
	
	public Long listarCentroCustosCount(String filter) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(filter);
	}
	
	public List<ScoModalidadeLicitacao> listarModalidades(String filter) {
		return this.returnSGWithCount(comprasCadastrosBasicosFacade.listarModalidadeLicitacao(filter), listarModalidadesCount(filter));
	}

	public Long listarModalidadesCount(String filter){
		return this.comprasCadastrosBasicosFacade.listarModalidadeCount(filter);
	}

	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro){		
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro), pesquisarFornecedoresPorCgcCpfRazaoSocialCount(parametro));
	}

	public Long pesquisarFornecedoresPorCgcCpfRazaoSocialCount(String filter){
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocialCount(filter);
	}

	public List<PesquisarPlanjProgrEntregaItensAfVO> getListaPlanjProgEntregaItensAFs() {
		return listaPlanjProgEntregaItensAFs;
	}

	public void setListaPlanjProgEntregaItensAFs(
			List<PesquisarPlanjProgrEntregaItensAfVO> listaPlanjProgEntregaItensAFs) {
		this.listaPlanjProgEntregaItensAFs = listaPlanjProgEntregaItensAFs;
	}

	public Boolean getRealizarPesquisaPadrao() {
		return realizarPesquisaPadrao;
	}

	public void setRealizarPesquisaPadrao(Boolean realizarPesquisaPadrao) {
		this.realizarPesquisaPadrao = realizarPesquisaPadrao;
	}

	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();

		afSelecionada = null;

		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			allChecked.clear();
			for(PesquisarPlanjProgrEntregaItensAfVO item : listaPlanjProgEntregaItensAFs) {
				if (!this.allChecked.contains(item)) {
					this.allChecked.add(item);
				}
			}
			habilitarBotoes = false;
			habilitarProgramacaoPendenteGeral = false;
			habilitarExcluirProgramacao = false;
			habilitarParcelasLiberar = true;
			habilitarAlterarProgramacao = false;
			habilitarProgManual = false;
			habilitarBase = false;
			habilitarProgAutomatica = true;
			enviarParametros = true;
		} else if (this.listaChecked.size() == 0) {
			for(PesquisarPlanjProgrEntregaItensAfVO item : listaPlanjProgEntregaItensAFs) {
				if (this.allChecked.contains(item)) {
					this.allChecked.remove(item);
				}
			}
			habilitarProgramacaoPendenteGeral = false;
			habilitarExcluirProgramacao = false;
			habilitarParcelasLiberar = false;
			habilitarAlterarProgramacao = false;
			habilitarProgManual = false;
			habilitarBase = false;
			habilitarProgAutomatica = false;
			enviarParametros = false;
		}
	}

	public void selecionarLinha(SelectEvent event) {
		PesquisarPlanjProgrEntregaItensAfVO item = (PesquisarPlanjProgrEntregaItensAfVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
			habilitarBotoes = true;
			habilitarProgramacaoPendenteGeral = true;
			habilitarExcluirProgramacao = true;
			habilitarParcelasLiberar = true;
			habilitarAlterarProgramacao = true;
			habilitarProgManual = true;
			habilitarBase = true;
			habilitarProgAutomatica = true;
			enviarParametros = true;
			afSelecionada = item;
		}

		if (allChecked.size() > 1) {
			afSelecionada = null;
			habilitarProgramacaoPendenteGeral = false;
			habilitarExcluirProgramacao = false;
			habilitarParcelasLiberar = false;
			habilitarAlterarProgramacao = false;
			habilitarProgManual = false;
			habilitarBase = false;
			habilitarProgAutomatica = true;
			enviarParametros = false;
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		PesquisarPlanjProgrEntregaItensAfVO item = (PesquisarPlanjProgrEntregaItensAfVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}

		if (allChecked.size() == 1) {
			habilitarProgramacaoPendenteGeral = true;
			habilitarExcluirProgramacao = true;
			habilitarParcelasLiberar = true;
			habilitarAlterarProgramacao = true;
			habilitarProgManual = true;
			habilitarBase = true;
			habilitarProgAutomatica = true;
			enviarParametros = true;
			afSelecionada = allChecked.get(0);
		} else if (allChecked.size() == 0) {
			habilitarBotoes = false;
			habilitarProgramacaoPendenteGeral = false;
			habilitarExcluirProgramacao = false;
			habilitarParcelasLiberar = false;
			habilitarAlterarProgramacao = false;
			habilitarProgManual = false;
			habilitarBase = false;
			habilitarProgAutomatica = false;
			afSelecionada = null;
		}
	}

	public PesquisarPlanjProgrEntregaItensAfFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) {
		this.filtro = filtro;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Boolean getHabilitarBotoes() {
		return habilitarBotoes;
	}

	public void setHabilitarBotoes(Boolean habilitarBotoes) {
		this.habilitarBotoes = habilitarBotoes;
	}

	public Date getDataLiberacaoEntrega() {
		return dataLiberacaoEntrega;
	}

	public void setDataLiberacaoEntrega(Date dataLiberacaoEntrega) {
		this.dataLiberacaoEntrega = dataLiberacaoEntrega;
	}

	public Boolean getExibirLista() {
		return exibirLista;
	}

	public void setExibirLista(Boolean exibirLista) {
		this.exibirLista = exibirLista;
	}

	public Boolean getSelecionarTodos() {
		return selecionarTodos;
	}

	public void setSelecionarTodos(Boolean selecionarTodos) {
		this.selecionarTodos = selecionarTodos;
	}

	public Boolean getHabilitarProgramacaoPendenteGeral() {
		return habilitarProgramacaoPendenteGeral;
	}

	public void setHabilitarProgramacaoPendenteGeral(
			Boolean habilitarProgramacaoPendenteGeral) {
		this.habilitarProgramacaoPendenteGeral = habilitarProgramacaoPendenteGeral;
	}

	public PesquisarPlanjProgrEntregaItensAfVO getAfSelecionada() {
		return afSelecionada;
	}

	public void setAfSelecionada(PesquisarPlanjProgrEntregaItensAfVO afSelecionada) {
		this.afSelecionada = afSelecionada;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setHabilitarExcluirProgramacao(
			Boolean habilitarExcluirProgramacao) {
		this.habilitarExcluirProgramacao = habilitarExcluirProgramacao;
	}

	public Boolean getHabilitarExcluirProgramacao() {
		return habilitarExcluirProgramacao;
	}
	
	public Boolean getHabilitarParcelasLiberar() {
		return habilitarParcelasLiberar;
	}

	public void setHabilitarParcelasLiberar(Boolean habilitarParcelasLiberar) {
		this.habilitarParcelasLiberar = habilitarParcelasLiberar;
	}

	public Boolean getHabilitarAlterarProgramacao() {
		return habilitarAlterarProgramacao;
	}

	public void setHabilitarAlterarProgramacao(Boolean habilitarAlterarProgramacao) {
		this.habilitarAlterarProgramacao = habilitarAlterarProgramacao;
	}

	public boolean isEnviarParametros() {
		return enviarParametros;
	}

	public void setEnviarParametros(boolean enviarParametros) {
		this.enviarParametros = enviarParametros;
	}

	public String irParaExclusaoProgramacao(){

		// prepara dados para exclusão
		for (PesquisarPlanjProgrEntregaItensAfVO vo: listaChecked){
			excluirProgramacaoEntregaItensAfController.setNumeroAF(vo.getNumeroAF());
			excluirProgramacaoEntregaItensAfController.setNumeroLicitacao(vo.getNumeroLicitacao());
			excluirProgramacaoEntregaItensAfController.setComplemento(vo.getComplemento());
			excluirProgramacaoEntregaItensAfController.setCgc((long) vo.getNumeroFornecedor());
			excluirProgramacaoEntregaItensAfController.setRazaoSocial(vo.getNomeFornecedor());
		}

		return EXCLUIR_PROGRAMACAO;
	}
	
	public String getIrParaProgramarManualBotao() {

		programacaoManualParcelasEntregaAFController.setComplemento(afSelecionada.getComplemento());
		programacaoManualParcelasEntregaAFController.setLctNumero(afSelecionada.getPfrLctNumero());
		programacaoManualParcelasEntregaAFController.setNumeroAF(afSelecionada.getNumeroAF());
		programacaoManualParcelasEntregaAFController.setNumeroFornecedor(afSelecionada.getNumeroFornecedor());
		programacaoManualParcelasEntregaAFController.setPrimeiraConsulta(Boolean.TRUE);
		programacaoManualParcelasEntregaAFController.setIndExibeFiltros(Boolean.TRUE);
		return "programacaoManualParcelasEntregaAF";
	}
	
	public String getFornecedorTruncated(Integer numeroFornecedor, String nomeFornecedor){
		return numeroFornecedor + " - " + StringUtil.trunc(nomeFornecedor, true, 20l);
	}

	public boolean isTogglePanel() {
		return togglePanel;
	}

	public void setTogglePanel(Boolean togglePanel) {
		this.togglePanel = togglePanel;
	}
	
	public Boolean isSliderAberto() {
		return togglePanel;
	}
	
	public void collapseTogglePesquisa() {
		if (Boolean.TRUE.equals(togglePanel)) {
			togglePanel = Boolean.FALSE;
		} else {
			togglePanel = Boolean.TRUE;
		}
	}

	@Override
	public Long recuperarCount() {
		try {
			if(pesquisaNova){
				filtroPesquisa = filtro;
			}
			Long count = this.autFornecimentoFacade.pesquisarProgrEntregaItensAfCount(filtroPesquisa);
			return count;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public List<PesquisarPlanjProgrEntregaItensAfVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) { 
		List<PesquisarPlanjProgrEntregaItensAfVO> listaPaginada = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();
		try {
			if(pesquisaNova){
				filtroPesquisa = filtro;
			}

			if(DominioVizAutForn.T.equals(filtroPesquisa.getVisualizarAutForn()) && !pesquisaNova){
				Integer indiceFinal = firstResult + maxResults;
				for (int i = firstResult; i < indiceFinal; i++) {
					if(i < listaPlanjProgEntregaItensAFs.size()){
						listaPaginada.add(listaPlanjProgEntregaItensAFs.get(i));
					}
				}
				pesquisaNova = false;
				return listaPaginada;
			}else{

				listaPlanjProgEntregaItensAFs = autFornecimentoFacade.pesquisarProgrEntregaItensAf(filtroPesquisa, firstResult, maxResults, orderProperty, asc);
				pesquisaNova = false;


				return listaPlanjProgEntregaItensAFs;

			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaPaginada;
	}

	public PesquisarPlanjProgrEntregaItensAfFiltroVO getFiltroPesquisa() {
		return filtroPesquisa;
	}

	public void setFiltroPesquisa(
			PesquisarPlanjProgrEntregaItensAfFiltroVO filtroPesquisa) {
		this.filtroPesquisa = filtroPesquisa;
	}

	/**
	 * Ordena o resultado da pesquisa
	 * @param propriedade
	 */
	public void ordenar(String propriedade) {
		Comparator<PesquisarPlanjProgrEntregaItensAfVO> comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaPlanjProgEntregaItensAFs, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	private static final Comparator<Object> PT_BR_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};

	public Boolean getPesquisaNova() {
		return pesquisaNova;
	}

	public void setPesquisaNova(Boolean pesquisaNova) {
		this.pesquisaNova = pesquisaNova;
	}

	public Boolean getRealizarPesquisa() {
		return realizarPesquisa;
	}

	public void setRealizarPesquisa(Boolean realizarPesquisa) {
		this.realizarPesquisa = realizarPesquisa;
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
	
	public void setMaxResults(Integer results){
		this.dataModel.setDefaultMaxRow(results);
	}

	public DynamicDataModel<PesquisarPlanjProgrEntregaItensAfVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisarPlanjProgrEntregaItensAfVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public PesquisarPlanjProgrEntregaItensAfVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(PesquisarPlanjProgrEntregaItensAfVO selecionado) {
		this.selecionado = selecionado;
	}

	public static Log getLog() {
		return LOG;
	}

	public List<PesquisarPlanjProgrEntregaItensAfVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<PesquisarPlanjProgrEntregaItensAfVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<PesquisarPlanjProgrEntregaItensAfVO> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<PesquisarPlanjProgrEntregaItensAfVO> allChecked) {
		this.allChecked = allChecked;
	}


	public Boolean getHabilitarProgManual() {
		return habilitarProgManual;
	}

	public void setHabilitarProgManual(Boolean habilitarProgManual) {
		this.habilitarProgManual = habilitarProgManual;
	}

	public Boolean getHabilitarProgAutomatica() {
		return habilitarProgAutomatica;
	}

	public void setHabilitarProgAutomatica(Boolean habilitarProgAutomatica) {
		this.habilitarProgAutomatica = habilitarProgAutomatica;
	}

	public Boolean getHabilitarBase() {
		return habilitarBase;
	}

	public void setHabilitarBase(Boolean habilitarBase) {
		this.habilitarBase = habilitarBase;
	}
}