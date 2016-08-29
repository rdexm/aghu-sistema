package br.gov.mec.aghu.compras.solicitacaocompras.action;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.action.ImprimirSolicitacaoDeComprasController;
import br.gov.mec.aghu.compras.action.ImprimirSolicitacaoDeEstocaveisController;
import br.gov.mec.aghu.compras.autfornecimento.action.AutorizacaoFornecimentoController;
import br.gov.mec.aghu.compras.autfornecimento.action.PesquisaProgrEntregaItensAFController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.PlanejamentoCompraVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.suprimentos.vo.ScoPlanejamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import java.text.ParseException;
import java.util.*;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class PlanejamentoSolicitacaoComprasPaginatorController extends ActionController implements ActionPaginator {

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "compras-autorizacaoFornecimentoCRUD";
	private static final String PESQUISAR_PROG_ENTREGA_ITENS_AF = "compras-pesquisarProgEntregaItensAF";
	private static final String PESQUISA_LOG_REPOSICAO_AUTOMATICA = "pesquisaLogReposicaoAutomatica";
	private static final String COMPRAS_IMPRIMIR_SOLICITACAO_DE_ESTOCAVEIS = "compras-imprimirSolicitacaoDeEstocaveis";
	private static final String FASES_SOLICITACAO_COMPRA_LIST = "fasesSolicitacaoCompraList";
	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String SOLICITACAO_COMPRA_CRUD = "solicitacaoCompraCRUD";
	private static final String PLANEJAMENTO_COMPRA = "compras-planejamentoSolicitacaoCompras";
	private static final String IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO = "compras-imprimirSolicitacaoDeComprasPdfCadastro";
	private static final String REPOSICAO_MATERIAL = "reposicaoMaterial";

	@Inject
	private PesquisaProgrEntregaItensAFController pesquisaProgrEntregaItensAFController;
	
	@Inject
	private AutorizacaoFornecimentoController autorizacaoFornecimentoController;
	
	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoDeCompra> dataModel;
	
	private static final Log LOG = LogFactory.getLog(PlanejamentoSolicitacaoComprasPaginatorController.class); //MECPaginatorController {
	
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
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ImprimirSolicitacaoDeComprasController imprimirSolicitacaoDeComprasController;

	@Inject
	private ImprimirSolicitacaoDeEstocaveisController imprimirSolicitacaoDeEstocaveisController;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;	
	
	@Inject
	private SecurityController securityController;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	ScoPontoParadaSolicitacao pontoParada;
	Integer numeroSolicitacaoCompra;
	Date dataInicioSolicitacaoCompra;
	Date dataFimSolicitacaoCompra;
	Date dataInicioAnaliseSolicitacaoCompra;
	Date dataFimAnaliseSolicitacaoCompra;
	Date dataInicioAutorizacaoSolicitacaoCompra;
	Date dataFimAutorizacaoSolicitacaoCompra;
	ScoGrupoMaterial grupoMaterial;
	ScoMaterial material;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;
	DominioSimNao indUrgente;
	DominioSimNao indPrioritaria;
	DominioSimNao repAutomatica;
	DominioSimNao indContrato;
	DominioSimNao matEstocavel;
	RapServidores filtroComprador;
	FsoVerbaGestao verbaGestao;
	Date dataUltimaExecucao;
	Boolean pesquisarScMaterialEstocavel;
	
	private Boolean desabilitaSuggestionGrupoMaterial;
	private Boolean desabilitaFiltros;
	private boolean desabilitaBotoes;
	private Boolean alteracaoPendente;
	
	// modal de exclusao
	private String observacaoExclusao;
	
	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;
	
	// modal de geracao automatica
	private SceAlmoxarifado almoxarifadoGeracao;
	private DominioSimNao indGeracaoAutomatica;
	private Date horaAgendamento;
	private String mensagemConfirmacaoGeracao;
	
	// controles checkbox
	private List<ScoSolicitacaoDeCompra> listaChecked;
	private List<ScoSolicitacaoDeCompra> allChecked;
	private List<ScoSolicitacaoDeCompra> listaAtual;
	private List<PlanejamentoCompraVO> listaControle;
	private Map<Integer, Boolean> hasLibRef;
	private Map<Integer, Boolean> hasLibAss;
	private List<Integer> nroLibRefs;
	private List<Integer> nroLibAss;
	private ScoSolicitacaoDeCompra scoSelecionada;
	
	// controles grid editavel
	private List<ScoPlanejamentoVO> listAlteracoes;
	private ScoItemAutorizacaoForn itemAfSelecionada;
	private String voltarParaUrl;
	
	private Boolean temPermissaoCadastrarSc;
	private Boolean temPermissaoPlanejamento;
	private Boolean temPermissaoComprador;
	private Boolean temPermissaoAreasEspec;
	private Boolean temPermissaoChefia;
	private Boolean temPermissaoGerarScAutomatica;
	private Boolean temPermissaoEncaminhar;
	private Boolean temPermissaoConsultaEstatisticaConsumo;
	private Boolean temPermissaoLiberarParcelasAf;
	private Boolean temPermissaoConsultarParcelasAf;
	private Boolean temPermissaoGerarAf;
	private Boolean temPermissaoConsultarAf;
	private Boolean temPermissaoConsultarSc;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		iniciar();
	}
	
	public void iniciar() {
		final String GRAVAR = "gravar";
		final String VISUALIZAR = "visualizar";
		this.temPermissaoComprador = securityController.usuarioTemPermissao("cadastrarSCComprador",GRAVAR,true);
		this.temPermissaoPlanejamento= securityController.usuarioTemPermissao("cadastrarSCPlanejamento",GRAVAR,true);
		this.temPermissaoEncaminhar = securityController.usuarioTemPermissao("encaminharSolicitacaoCompras",GRAVAR,true);
		this.temPermissaoCadastrarSc = securityController.usuarioTemPermissao("cadastrarSolicitacaoCompras",GRAVAR,true);  
		this.temPermissaoChefia = securityController.usuarioTemPermissao("cadastrarSCChefias",GRAVAR,true);     
		this.temPermissaoAreasEspec = securityController.usuarioTemPermissao("cadastrarSCAreasEspecificas",GRAVAR,true);
		this.temPermissaoGerarScAutomatica = securityController.usuarioTemPermissao("gerarSolicitacaoAutomaticaCompras",GRAVAR,true);
		this.temPermissaoConsultaEstatisticaConsumo = securityController.usuarioTemPermissao("consultaEstatiscaConsumo","consultar",true);
		this.temPermissaoLiberarParcelasAf = securityController.usuarioTemPermissao("liberarParcelasAF",GRAVAR,true);
		this.temPermissaoConsultarParcelasAf = securityController.usuarioTemPermissao("consultarParcelasAF",VISUALIZAR,true);
		this.temPermissaoGerarAf = securityController.usuarioTemPermissao("gerarAF",GRAVAR,true);  
		this.temPermissaoConsultarAf = securityController.usuarioTemPermissao("consultarAF",VISUALIZAR,true); 
		this.setTemPermissaoConsultarSc(securityController.usuarioTemPermissao("consultarSC",VISUALIZAR,true));
		if(numeroSolicitacaoCompra!=null){
			pesquisar();
		}else {
			this.limparPesquisa();
			pesquisarScMaterialEstocavel = Boolean.FALSE;
		}

		if (material != null) {
			this.pesquisar();
		}

	}
	
	// métodos de controle da grid editável
	public Boolean verificarHabilitacaoCamposAf(ScoSolicitacaoDeCompra item, Boolean verificarParcela, Boolean verificarLibRef, Boolean verificarAf, Boolean verificarLibAss, Boolean protegerQtde) {
		return this.solicitacaoComprasFacade.verificarHabilitacaoCamposAf(this.listaControle, item, verificarParcela, verificarLibRef, verificarAf, verificarLibAss, protegerQtde);
	}
	
	public void atualizarListaEdicoes(ScoSolicitacaoDeCompra item) {
		ScoPlanejamentoVO planVO = this.solicitacaoComprasFacade.montarItemObjetoVO(item);
		Integer index = listAlteracoes.indexOf(planVO);
		// se já existe na lista só atualiza
		if (index >= 0) {
			((ScoPlanejamentoVO) listAlteracoes.get(index)).setQtdeAprovada(item.getQtdeAprovada());
			((ScoPlanejamentoVO) listAlteracoes.get(index)).setQtdeReforco(item.getQtdeReforco());
			((ScoPlanejamentoVO) listAlteracoes.get(index)).setDataAnaliseCompra(item.getDtAnalise());
		} else {
			listAlteracoes.add(planVO);
		}
		if (!this.allChecked.contains(item)) {
			this.listaChecked.add(item);
			this.allChecked.add(item);
		}
		setAlteracaoPendente(true);
	}

	private Date obterDataAnaliseEditada(ScoSolicitacaoDeCompra item) {
		if (verificarRegistroEditado(item)) {
			return ((Date) this.listAlteracoes.get(this.solicitacaoComprasFacade.obterIndiceLista(item, this.listAlteracoes)).getDataAnaliseCompra());
		} else {
			return item.getDtAnalise();
		}
	}

	private Long obterQtdeAprovadaEditada(ScoSolicitacaoDeCompra item) {
		if (verificarRegistroEditado(item)) {
			return ((Long) this.listAlteracoes.get(this.solicitacaoComprasFacade.obterIndiceLista(item, this.listAlteracoes)).getQtdeAprovada());
		} else {
			return item.getQtdeAprovada();
		}
	}

	private Long obterQtdeReforcoEditada(ScoSolicitacaoDeCompra item) {
		if (verificarRegistroEditado(item)) {
			return ((Long) this.listAlteracoes.get(this.solicitacaoComprasFacade.obterIndiceLista(item, this.listAlteracoes)).getQtdeReforco());
		} else {
			return item.getQtdeReforco();
		}
	}

	private Boolean verificarRegistroEditado(ScoSolicitacaoDeCompra item) {
		return this.solicitacaoComprasFacade.obterIndiceLista(item, listAlteracoes) >= 0;
	}

	// métodos de controle dos checkbox
	public void selecionarLinha(SelectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
			ScoPlanejamentoVO planVO = this.solicitacaoComprasFacade.montarItemObjetoVO(sc);
			this.listAlteracoes.remove(planVO);
		} else {
			this.allChecked.add(sc);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra)event.getObject();
		if (this.allChecked.contains(sc)) {
			this.allChecked.remove(sc);
			ScoPlanejamentoVO planVO = this.solicitacaoComprasFacade.montarItemObjetoVO(sc);
			this.listAlteracoes.remove(planVO);
		} else {
			this.allChecked.add(sc);
		}
	}
	
	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
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

	public void addLibAss(Integer numeroSolicitacao) {
		if (this.hasLibAss == null) {
			this.hasLibAss = new HashMap<Integer, Boolean>();
		}
		if (this.nroLibAss == null) {
			this.nroLibAss = new ArrayList<Integer>();
		}
		if (this.nroLibAss.contains(numeroSolicitacao)) {
			this.nroLibAss.remove(numeroSolicitacao);
			this.hasLibAss.remove(numeroSolicitacao);
		} else {
			this.nroLibAss.add(numeroSolicitacao);
			this.hasLibAss.put(numeroSolicitacao, true);
			ScoSolicitacaoDeCompra sc = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numeroSolicitacao);
			if (sc != null) {
				this.listaChecked.add(sc);
				this.allChecked.add(sc);
			}
			
			atualizarListaEdicoes(this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numeroSolicitacao));
		}
	}

	public void addLibRef(Integer numeroSolicitacao) {
		if (this.hasLibRef == null) {
			this.hasLibRef = new HashMap<Integer, Boolean>();
		}
		if (this.nroLibRefs == null) {
			this.nroLibRefs = new ArrayList<Integer>();
		}
		if (this.nroLibRefs.contains(numeroSolicitacao)) {
			this.nroLibRefs.remove(numeroSolicitacao);
			this.hasLibRef.remove(numeroSolicitacao);
		} else {
			this.nroLibRefs.add(numeroSolicitacao);
			this.hasLibRef.put(numeroSolicitacao, true);
			ScoSolicitacaoDeCompra sc = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numeroSolicitacao);
			if (sc != null) {
				this.listaChecked.add(sc);
				this.allChecked.add(sc);
			}
			atualizarListaEdicoes(this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(numeroSolicitacao));
		}
		if (this.nroLibAss.contains(numeroSolicitacao)) {
			this.nroLibAss.remove(numeroSolicitacao);
			this.hasLibAss.remove(numeroSolicitacao);			
		}
		setAlteracaoPendente(true);		
	}
	
	// métodos das suggestions
	public List<FsoVerbaGestao> pesquisarVerbasGestao(String parametro) {
		return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(parametro);
	}

	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro);
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(objPesquisa, null, true),listarMateriaisCount(objPesquisa));
	}

	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.contarScoMateriaisGrupoAtiva(param, this.getGrupoMaterial(), false);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}
		return this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filtro);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro) throws BaseException {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos(filtro);
	}

	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoEDescricao(Object parametro) throws BaseException {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro.toString();
		}
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(filtro, false);
	}
	
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	// botões de ação
	public void pesquisar() {
		this.limparControleGrid();
		this.limparModalExclusao();
		this.limparModalEncaminhamento();
		this.limparModalGeracaoAutomatica();
		this.setPesquisarScMaterialEstocavel(Boolean.FALSE);
		this.dataModel.reiniciarPaginator();
		this.desabilitaBotoes = false;
		this.setDataUltimaExecucao(this.solicitacaoComprasFacade.obterDataUltimaExecucao());
	}

	public void encaminharItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.solicitacaoComprasFacade.encaminharListaSolicitacaoCompras(listaNumero,this.comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao(), this.getProximoPontoParada(), this.getFuncionarioComprador(), false);
			if (listaNumero.size() == 1) {
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

	public void excluirItens() {
		try {
			List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
			this.solicitacaoComprasFacade.inativarListaSolicitacaoCompras(listaNumero, this.observacaoExclusao);
			if (listaNumero.size() == 1) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SOL_UNICA_COM_SUCESSO", listaNumero.get(0));
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_EXCLUSAO_LISTA_COM_SUCESSO");
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

	public String imprimirItens() {
		List<Integer> listaNumero = this.solicitacaoComprasFacade.obterListaNumeroSc(this.allChecked);
		imprimirSolicitacaoDeComprasController.setNumSolicComps(listaNumero);
		try {
			imprimirSolicitacaoDeComprasController.print(null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		this.limparControleGrid();
		this.dataModel.reiniciarPaginator();
		return IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO;
	}
	
	public String imprimirSolicitacaoDeEstocaveis() {
		return COMPRAS_IMPRIMIR_SOLICITACAO_DE_ESTOCAVEIS;
	}

	public String gerarSc() {
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	public String redirecionarSolicitacaoCompra(){
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	public String redirecionarEstatisticaConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}

	public String redirecionarFasesSolicitacaoCompra(){
		return FASES_SOLICITACAO_COMPRA_LIST;
	}

	public void gerarScRepAutomatica() {
		try {
			List<String> resultadoProcessamento = this.solicitacaoComprasFacade.gerarScRepAutomatica(new Date(), almoxarifadoGeracao);
			this.limparModalGeracaoAutomatica();
			this.setDataUltimaExecucao(this.solicitacaoComprasFacade.obterDataUltimaExecucao());
			if (resultadoProcessamento != null && resultadoProcessamento.size() > 0) {
				
				for (String item : resultadoProcessamento) {
		        	this.apresentarMsgNegocio(Severity.INFO,
		    				item);
		        }
			}
			this.atualizarUltimaExecucaoGeracaoAutomaticaSolCompras();
			this.almoxarifadoGeracao = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void gravarEdicoes() {
		if (this.listAlteracoes.size() == 0 && this.nroLibRefs.size() == 0) {
			this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_PLANEJAMENTO_M04");
		} else {
			try {
				this.solicitacaoComprasFacade.atualizarPlanejamentoSco(this.listAlteracoes, this.nroLibRefs, this.nroLibAss);
				if (this.listAlteracoes.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_UPDATE_SOLCOMPRAS",this.allChecked.get(0).getNumero());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_UPDATE_SOLCOMPRAS_ITENS");
				}
				this.limparControleGrid();
				this.limparModalEncaminhamento();
				this.pesquisar();
				setAlteracaoPendente(false);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras() {
		if (this.indGeracaoAutomatica == DominioSimNao.S && this.horaAgendamento == null) {
			this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_AGENDAMENTO_M07");
		} else {
			try {	
				// atualiza a hora do parâmetro no banco e já agenda/reagenda/remove no quartz, conforme necessário
				this.solicitacaoComprasFacade.atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras(horaAgendamento);				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MODAL_GERACAOAUTOMATICA_M03");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException esr) {
				apresentarExcecaoNegocio(esr);
			}
		}
	}	
	
	public String redirecionarLinks(ScoSolicitacaoDeCompra item, Boolean redirecionarParcela, Boolean redirecionarAf) {
		String ret = "";
		Boolean achou = Boolean.FALSE;
		Integer index = this.solicitacaoComprasFacade.obterIndiceListaControle(item, this.listaControle);
		if (index >= 0) {
			if (this.listaControle.get(index).getSlcNumero().equals(item.getNumero())) {
				this.itemAfSelecionada = this.listaControle.get(index).getItemAf();
				achou = Boolean.TRUE;
			}
		}
		if (itemAfSelecionada != null && achou) {
			if (redirecionarParcela) {
				pesquisaProgrEntregaItensAFController.setVoltarParaUrl(PLANEJAMENTO_COMPRA);
				pesquisaProgrEntregaItensAFController.setNumeroAf(itemAfSelecionada.getId().getAfnNumero());
				pesquisaProgrEntregaItensAFController.setNumeroItem(itemAfSelecionada.getId().getNumero().shortValue());
				ret = PESQUISAR_PROG_ENTREGA_ITENS_AF;
			}
			if (redirecionarAf) {
				autorizacaoFornecimentoController.setVoltarParaUrl(PLANEJAMENTO_COMPRA);
				autorizacaoFornecimentoController.setNumeroAf(itemAfSelecionada.getAutorizacoesForn().getPropostaFornecedor().getId().getLctNumero());
				autorizacaoFornecimentoController.setNumeroComplemento(itemAfSelecionada.getAutorizacoesForn().getNroComplemento());
				ret = AUTORIZACAO_FORNECIMENTO_CRUD;
			}
		}
		return ret;
	}
	
	public void atualizarUltimaExecucaoGeracaoAutomaticaSolCompras() {
		try {
			StringBuilder msgConfirmacao = new StringBuilder(this.getBundle().getString("MENSAGEM_ULTIMA_EXECUCAO")).append((new Date()).toString());
			this.solicitacaoComprasFacade.atualizarUltimaExecucaoGeracaoAutomaticaSolCompras(msgConfirmacao.toString());	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void carregarHorarioAgendamentoGeracaoAutomaticaSolCompras() {
		try {
			this.horaAgendamento =  this.solicitacaoComprasFacade.carregarHorarioAgendamentoGeracaoAutomaticaSolCompras();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (ParseException eps) {
			LOG.error("Hora em Formato Incorreto", eps);
		}
	}
	
	public void pesquisarSCAutomatica(){
		this.limparControleGrid();
		this.limparModalExclusao();
		this.limparModalEncaminhamento();
		this.limparModalGeracaoAutomatica();
		this.setPesquisarScMaterialEstocavel(Boolean.TRUE);
		this.dataModel.reiniciarPaginator();
		this.desabilitaBotoes = false;
		this.setDataUltimaExecucao(this.solicitacaoComprasFacade.obterDataUltimaExecucao());
	}
	
	public Boolean verificarItemAF(ScoSolicitacaoDeCompra item) {
		Boolean ret = Boolean.FALSE;
		Integer index = this.solicitacaoComprasFacade.obterIndiceListaControle(item, this.listaControle);
		if (index >= 0) {
			if (this.listaControle.get(index).getSlcNumero().equals(item.getNumero()) && this.listaControle.get(index).getItemAf() != null) {
				ret = Boolean.TRUE;
			}
		}
		return ret;
	}

	@Override
	public Long recuperarCount() {
		return this.solicitacaoComprasFacade.countSolicitacaoComprasPlanejamentoSc(this.montarFiltroPesquisa(), this.pesquisarScMaterialEstocavel);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<ScoSolicitacaoDeCompra> result = this
				.solicitacaoComprasFacade
				.pesquisarSolicitacaoComprasPlanejamentoSc(firstResult,
						maxResults, orderProperty, asc,
						this.montarFiltroPesquisa(), this.pesquisarScMaterialEstocavel);

		listaAtual.clear();
		
		for (ScoSolicitacaoDeCompra scoItem : result) {
			ScoSolicitacaoDeCompra solCompras = new ScoSolicitacaoDeCompra();
			solCompras.setNumero(Integer.valueOf(scoItem.getNumero()));
			if (scoItem.getDtAnalise() != null) {
				solCompras.setDtAnalise((Date)scoItem.getDtAnalise().clone());
			} else {
				solCompras.setDtAnalise(null);
			}
			this.listaControle.add(this.solicitacaoComprasFacade.preencherControleVO(scoItem));
			this.listaAtual.add(scoItem);

			if (verificarRegistroEditado(scoItem)) {
				scoItem.setDtAnalise(obterDataAnaliseEditada(scoItem));
				scoItem.setQtdeAprovada(obterQtdeAprovadaEditada(scoItem));
				scoItem.setQtdeReforco(obterQtdeReforcoEditada(scoItem));
			}
		}
		
		setAlteracaoPendente(false);		
		
		if (result.size() == 0) {
			this.desabilitaBotoes = true;
		} else {
			this.desabilitaBotoes = false;
		}

		return result;
	}
	
	// metodos de auxílio
	public Integer obterQtdSaldoParcelas(ScoSolicitacaoDeCompra item) {
		return this.solicitacaoComprasFacade.obterQtdSaldoParcelas(item, this.listaControle);
	}

	public void prepararExibicaoModalConfirmacao() {
		if (this.almoxarifadoGeracao == null) {
			this.mensagemConfirmacaoGeracao = this.getBundle().getString("MENSAGEM_MODAL_GERACAOAUTOMATICA_M01");	
		} else {
			StringBuilder msgConfirmacao = new StringBuilder(this.getBundle().getString("MENSAGEM_MODAL_GERACAOAUTOMATICA_M02")).append(this.almoxarifadoGeracao.getSeqDescricao()).append(this.getBundle().getString("LABEL_INTERROGACAO"));
			this.mensagemConfirmacaoGeracao =  msgConfirmacao.toString();		
		}
	}

	public void apagarHoraAgendamento() {
		if (this.indGeracaoAutomatica == DominioSimNao.N) {
			this.horaAgendamento = null;
		}
	}
	
	public String obterDescricaoPontoParada(ScoSolicitacaoDeCompra item, Boolean proximo) {
		try {
			return this.solicitacaoComprasFacade.obterDescricaoPontoParada(item, proximo);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return "";
	}

	public void desligarFiltros() {
		if (this.getNumeroSolicitacaoCompra() != null) {
			this.limparFiltros(false);
			this.setDesabilitaFiltros(true);
		} else {
			this.setDesabilitaFiltros(false);
		}
	}

	public void verificarGrupoMaterialHabilitado() {
		this.desabilitaSuggestionGrupoMaterial = ((this.getMaterial() != null && this.getGrupoMaterial() == null) ? true : false);
	}

	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(this.comprasCadastrosBasicosFacade.verificarPontoParadaComprador(this.proximoPontoParada));
	}

	public void limparPesquisa() {
		this.dataModel.setPesquisaAtiva(false);
		this.limparFiltros(true);
		this.limparModalExclusao();
		this.limparControleGrid();
		this.limparModalEncaminhamento();
		this.limparModalGeracaoAutomatica();
		this.setDesabilitaBotoes(false);
		this.setDataUltimaExecucao(this.solicitacaoComprasFacade.obterDataUltimaExecucao());
	}

	private PesqLoteSolCompVO montarFiltroPesquisa() {
		PesqLoteSolCompVO filtrosPesquisa = new PesqLoteSolCompVO();
		filtrosPesquisa.setPontoParada(this.pontoParada);
		if (this.getNumeroSolicitacaoCompra() != null){
			this.limparFiltros(false);
		}
		filtrosPesquisa.setNumeroSolicitacaoCompra(this.numeroSolicitacaoCompra);
		filtrosPesquisa.setDataInicioSolicitacaoCompra(this.dataInicioSolicitacaoCompra);
		filtrosPesquisa.setDataFimSolicitacaoCompra(this.dataFimSolicitacaoCompra);
		filtrosPesquisa.setDataInicioAnaliseSolicitacaoCompra(this.dataInicioAnaliseSolicitacaoCompra);
		filtrosPesquisa.setDataFimAnaliseSolicitacaoCompra(this.dataFimAnaliseSolicitacaoCompra);
		filtrosPesquisa.setDataInicioAutorizacaoSolicitacaoCompra(this.dataInicioAutorizacaoSolicitacaoCompra);
		filtrosPesquisa.setDataFimAutorizacaoSolicitacaoCompra(this.dataFimAutorizacaoSolicitacaoCompra);
		filtrosPesquisa.setGrupoMaterial(this.grupoMaterial);
		filtrosPesquisa.setMaterial(this.material);
		filtrosPesquisa.setCentroCustoSolicitante(this.centroCustoSolicitante);
		filtrosPesquisa.setIndContrato(this.indContrato);
		filtrosPesquisa.setCentroCustoAplicacao(this.centroCustoAplicacao);
		filtrosPesquisa.setSolicitacaoUrgente(this.indUrgente);
		filtrosPesquisa.setSolicitacaoPrioritaria(this.indPrioritaria);
		filtrosPesquisa.setRepAutomatica(this.repAutomatica);
		filtrosPesquisa.setMatEstocavel(this.matEstocavel);
		filtrosPesquisa.setFiltroComprador(this.filtroComprador);
		filtrosPesquisa.setVerbaGestao(this.verbaGestao);
		filtrosPesquisa.setDataUltimaExecucao(this.dataUltimaExecucao);

		return filtrosPesquisa;
	}

	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;
		this.setDesabilitaSuggestionComprador(false);
		this.voltarPanel = false;
	}

	public void limparModalRelatorioEstocavel() {
		imprimirSolicitacaoDeEstocaveisController.setDataInicioGeracao(null);
		imprimirSolicitacaoDeEstocaveisController.setDataFimGeracao(null);
		imprimirSolicitacaoDeEstocaveisController.setNumSCEstocavel(null);
	}
	
	private void limparControleGrid() {
		this.hasLibRef = new HashMap<Integer, Boolean>();
		this.hasLibAss = new HashMap<Integer, Boolean>();
		this.nroLibRefs = new ArrayList<Integer>();
		this.nroLibAss = new ArrayList<Integer>();
		this.listAlteracoes = new ArrayList<ScoPlanejamentoVO>();
		this.listaControle = new ArrayList<PlanejamentoCompraVO>();
		this.setListaChecked(new ArrayList<ScoSolicitacaoDeCompra>());
		this.allChecked = new ArrayList<ScoSolicitacaoDeCompra>();
		this.listaAtual = new ArrayList<ScoSolicitacaoDeCompra>();
	}

	public void limparModalExclusao() {
		this.observacaoExclusao = null;
		this.voltarPanel = false;
	}

	public void limparModalGeracaoAutomatica() {
		this.almoxarifadoGeracao = null;
		this.carregarHorarioAgendamentoGeracaoAutomaticaSolCompras();
		if (this.horaAgendamento != null) {
			this.indGeracaoAutomatica = DominioSimNao.S;
		} else {
			this.indGeracaoAutomatica = DominioSimNao.N;
		}
	}
	
	private void limparFiltros(Boolean limparNumero) {
		if (limparNumero) {
			this.numeroSolicitacaoCompra = null;
			this.pontoParada = this.comprasCadastrosBasicosFacade.obterPontoParadaPorTipo(DominioTipoPontoParada.PL);
			this.setMatEstocavel(DominioSimNao.S);
		} else {
			this.pontoParada = null;
			this.setMatEstocavel(null);
		}
		this.grupoMaterial = null;
		this.material = null;
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;
		this.indPrioritaria = null;
		this.indUrgente = null;
		this.indContrato = null;
		this.desabilitaSuggestionGrupoMaterial = false;
		this.desabilitaFiltros = false;
		this.setRepAutomatica(null);
		this.setDataInicioSolicitacaoCompra(null);
		this.setFiltroComprador(null);
		this.setVerbaGestao(null);
		this.setDataFimSolicitacaoCompra(null);
		this.setDataInicioAnaliseSolicitacaoCompra(null);
		this.setDataFimAnaliseSolicitacaoCompra(null);
		this.setDataInicioAutorizacaoSolicitacaoCompra(null);
		this.setDataFimAutorizacaoSolicitacaoCompra(null);
	}
	
	public String getURLPlanejarCompras() {
		try {
			return this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_URL_PLANEJAR_COMPRA_AGHU_5);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
	
	public String visualizarLogReposicaoAutomatica() {
		return PESQUISA_LOG_REPOSICAO_AUTOMATICA;
	}

	public String redirecionarReposicaoPlanejamento(){
		return REPOSICAO_MATERIAL;
	}
	
	// gets and sets
	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public DominioSimNao getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(DominioSimNao indContrato) {
		this.indContrato = indContrato;
	}

	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
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

	public Boolean getDesabilitaSuggestionGrupoMaterial() {
		return desabilitaSuggestionGrupoMaterial;
	}

	public void setDesabilitaSuggestionGrupoMaterial(Boolean desabilitaSuggestionGrupoMaterial) {
		this.desabilitaSuggestionGrupoMaterial = desabilitaSuggestionGrupoMaterial;
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

	public Date getDataInicioSolicitacaoCompra() {
		return dataInicioSolicitacaoCompra;
	}

	public void setDataInicioSolicitacaoCompra(Date dataInicioSolicitacaoCompra) {
		this.dataInicioSolicitacaoCompra = dataInicioSolicitacaoCompra;
	}

	public Date getDataFimSolicitacaoCompra() {
		return dataFimSolicitacaoCompra;
	}

	public void setDataFimSolicitacaoCompra(Date dataFimSolicitacaoCompra) {
		this.dataFimSolicitacaoCompra = dataFimSolicitacaoCompra;
	}

	public Date getDataInicioAnaliseSolicitacaoCompra() {
		return dataInicioAnaliseSolicitacaoCompra;
	}

	public void setDataInicioAnaliseSolicitacaoCompra(Date dataInicioAnaliseSolicitacaoCompra) {
		this.dataInicioAnaliseSolicitacaoCompra = dataInicioAnaliseSolicitacaoCompra;
	}

	public Date getDataFimAnaliseSolicitacaoCompra() {
		return dataFimAnaliseSolicitacaoCompra;
	}

	public void setDataFimAnaliseSolicitacaoCompra(Date dataFimAnaliseSolicitacaoCompra) {
		this.dataFimAnaliseSolicitacaoCompra = dataFimAnaliseSolicitacaoCompra;
	}

	public Date getDataInicioAutorizacaoSolicitacaoCompra() {
		return dataInicioAutorizacaoSolicitacaoCompra;
	}

	public void setDataInicioAutorizacaoSolicitacaoCompra(Date dataInicioAutorizacaoSolicitacaoCompra) {
		this.dataInicioAutorizacaoSolicitacaoCompra = dataInicioAutorizacaoSolicitacaoCompra;
	}

	public Date getDataFimAutorizacaoSolicitacaoCompra() {
		return dataFimAutorizacaoSolicitacaoCompra;
	}

	public void setDataFimAutorizacaoSolicitacaoCompra(Date dataFimAutorizacaoSolicitacaoCompra) {
		this.dataFimAutorizacaoSolicitacaoCompra = dataFimAutorizacaoSolicitacaoCompra;
	}

	public DominioSimNao getMatEstocavel() {
		return matEstocavel;
	}

	public void setMatEstocavel(DominioSimNao matEstocavel) {
		this.matEstocavel = matEstocavel;
	}

	public RapServidores getFiltroComprador() {
		return filtroComprador;
	}

	public void setFiltroComprador(RapServidores filtroComprador) {
		this.filtroComprador = filtroComprador;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public DominioSimNao getRepAutomatica() {
		return repAutomatica;
	}

	public void setRepAutomatica(DominioSimNao repAutomatica) {
		this.repAutomatica = repAutomatica;
	}

	public List<ScoPlanejamentoVO> getListAlteracoes() {
		return listAlteracoes;
	}

	public void setGridData(List<ScoPlanejamentoVO> listAlteracoes) {
		this.listAlteracoes = listAlteracoes;
	}

	public ScoSolicitacaoDeCompra getScoSelecionada() {
		return scoSelecionada;
	}

	public void setScoSelecionada(ScoSolicitacaoDeCompra scoSelecionada) {
		this.scoSelecionada = scoSelecionada;
	}

	public SceAlmoxarifado getAlmoxarifadoGeracao() {
		return almoxarifadoGeracao;
	}

	public void setAlmoxarifadoGeracao(SceAlmoxarifado almoxarifadoGeracao) {
		this.almoxarifadoGeracao = almoxarifadoGeracao;
	}

	public DominioSimNao getIndGeracaoAutomatica() {
		return indGeracaoAutomatica;
	}

	public void setIndGeracaoAutomatica(DominioSimNao indGeracaoAutomatica) {
		this.indGeracaoAutomatica = indGeracaoAutomatica;
	}

	public Date getHoraAgendamento() {
		return horaAgendamento;
	}

	public void setHoraAgendamento(Date horaAgendamento) {
		this.horaAgendamento = horaAgendamento;
	}

	public String getMensagemConfirmacaoGeracao() {
		return mensagemConfirmacaoGeracao;
	}

	public void setMensagemConfirmacaoGeracao(String mensagemConfirmacaoGeracao) {
		this.mensagemConfirmacaoGeracao = mensagemConfirmacaoGeracao;
	}

	public ScoItemAutorizacaoForn getItemAfSelecionada() {
		return itemAfSelecionada;
	}

	public void setItemAfSelecionada(ScoItemAutorizacaoForn itemAfSelecionada) {
		this.itemAfSelecionada = itemAfSelecionada;
	}

	public List<PlanejamentoCompraVO> getListaControle() {
		return listaControle;
	}

	public void setListaControle(List<PlanejamentoCompraVO> listaControle) {
		this.listaControle = listaControle;
	}

	public Map<Integer, Boolean> getHasLibRef() {
		return hasLibRef;
	}

	public void setHasLibRef(Map<Integer, Boolean> hasLibRef) {
		this.hasLibRef = hasLibRef;
	}

	public List<Integer> getNroLibRefs() {
		return nroLibRefs;
	}

	public void setNroLibRefs(List<Integer> nroLibRefs) {
		this.nroLibRefs = nroLibRefs;
	}

	public Map<Integer, Boolean> getHasLibAss() {
		return hasLibAss;
	}

	public void setHasLibAss(Map<Integer, Boolean> hasLibAss) {
		this.hasLibAss = hasLibAss;
	}

	public List<Integer> getNroLibAss() {
		return nroLibAss;
	}

	public void setNroLibAss(List<Integer> nroLibAss) {
		this.nroLibAss = nroLibAss;
	}

	public Date getDataUltimaExecucao() {
		return dataUltimaExecucao;
	}

	public void setDataUltimaExecucao(Date dataUltimaExecucao) {
		this.dataUltimaExecucao = dataUltimaExecucao;
	}

	public Boolean getPesquisarScMaterialEstocavel() {
		return pesquisarScMaterialEstocavel;
	}

	public void setPesquisarScMaterialEstocavel(Boolean pesquisarScMaterialEstocavel) {
		this.pesquisarScMaterialEstocavel = pesquisarScMaterialEstocavel;
	}

	public Boolean getAlteracaoPendente() {
		return alteracaoPendente;
	}

	public void setAlteracaoPendente(Boolean alteracaoPendente) {
		this.alteracaoPendente = alteracaoPendente;
	}
	
	public DynamicDataModel<ScoSolicitacaoDeCompra> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoDeCompra> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ScoSolicitacaoDeCompra> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ScoSolicitacaoDeCompra> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ScoSolicitacaoDeCompra> getListaAtual() {
		return listaAtual;
	}

	public void setListaAtual(List<ScoSolicitacaoDeCompra> listaAtual) {
		this.listaAtual = listaAtual;
	}

	public List<ScoSolicitacaoDeCompra> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ScoSolicitacaoDeCompra> allChecked) {
		this.allChecked = allChecked;
	}

	public Boolean getTemPermissaoCadastrarSc() {
		return temPermissaoCadastrarSc;
	}

	public void setTemPermissaoCadastrarSc(Boolean temPermissaoCadastrarSc) {
		this.temPermissaoCadastrarSc = temPermissaoCadastrarSc;
	}

	public Boolean getTemPermissaoPlanejamento() {
		return temPermissaoPlanejamento;
	}

	public void setTemPermissaoPlanejamento(Boolean temPermissaoPlanejamento) {
		this.temPermissaoPlanejamento = temPermissaoPlanejamento;
	}

	public Boolean getTemPermissaoComprador() {
		return temPermissaoComprador;
	}

	public void setTemPermissaoComprador(Boolean temPermissaoComprador) {
		this.temPermissaoComprador = temPermissaoComprador;
	}

	public Boolean getTemPermissaoAreasEspec() {
		return temPermissaoAreasEspec;
	}

	public void setTemPermissaoAreasEspec(Boolean temPermissaoAreasEspec) {
		this.temPermissaoAreasEspec = temPermissaoAreasEspec;
	}

	public Boolean getTemPermissaoChefia() {
		return temPermissaoChefia;
	}

	public void setTemPermissaoChefia(Boolean temPermissaoChefia) {
		this.temPermissaoChefia = temPermissaoChefia;
	}

	public Boolean getTemPermissaoGerarScAutomatica() {
		return temPermissaoGerarScAutomatica;
	}

	public void setTemPermissaoGerarScAutomatica(
			Boolean temPermissaoGerarScAutomatica) {
		this.temPermissaoGerarScAutomatica = temPermissaoGerarScAutomatica;
	}

	public Boolean getTemPermissaoEncaminhar() {
		return temPermissaoEncaminhar;
	}

	public void setTemPermissaoEncaminhar(Boolean temPermissaoEncaminhar) {
		this.temPermissaoEncaminhar = temPermissaoEncaminhar;
	}

	public Boolean getTemPermissaoConsultaEstatisticaConsumo() {
		return temPermissaoConsultaEstatisticaConsumo;
	}

	public void setTemPermissaoConsultaEstatisticaConsumo(
			Boolean temPermissaoConsultaEstatisticaConsumo) {
		this.temPermissaoConsultaEstatisticaConsumo = temPermissaoConsultaEstatisticaConsumo;
	}

	public Boolean getTemPermissaoLiberarParcelasAf() {
		return temPermissaoLiberarParcelasAf;
	}

	public void setTemPermissaoLiberarParcelasAf(
			Boolean temPermissaoLiberarParcelasAf) {
		this.temPermissaoLiberarParcelasAf = temPermissaoLiberarParcelasAf;
	}

	public Boolean getTemPermissaoConsultarParcelasAf() {
		return temPermissaoConsultarParcelasAf;
	}

	public void setTemPermissaoConsultarParcelasAf(
			Boolean temPermissaoConsultarParcelasAf) {
		this.temPermissaoConsultarParcelasAf = temPermissaoConsultarParcelasAf;
	}

	public Boolean getTemPermissaoGerarAf() {
		return temPermissaoGerarAf;
	}

	public void setTemPermissaoGerarAf(Boolean temPermissaoGerarAf) {
		this.temPermissaoGerarAf = temPermissaoGerarAf;
	}

	public Boolean getTemPermissaoConsultarAf() {
		return temPermissaoConsultarAf;
	}

	public void setTemPermissaoConsultarAf(Boolean temPermissaoConsultarAf) {
		this.temPermissaoConsultarAf = temPermissaoConsultarAf;
	}

	public Boolean getTemPermissaoConsultarSc() {
		return temPermissaoConsultarSc;
	}

	public void setTemPermissaoConsultarSc(Boolean temPermissaoConsultarSc) {
		this.temPermissaoConsultarSc = temPermissaoConsultarSc;
	}
}