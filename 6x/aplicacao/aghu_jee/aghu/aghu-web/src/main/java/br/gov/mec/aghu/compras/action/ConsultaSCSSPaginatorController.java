package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.action.ConsultaSSPaginatorController;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
//import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

import com.itextpdf.text.DocumentException;

@SuppressWarnings({ "PMD.EmptyIfStmt" })
public class ConsultaSCSSPaginatorController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR = "visualizar";

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final String ASSOCIAR_SOLICITACAO_SERVICO_COMPRA = "compras-associarSolicitacaoServicoCompra";

	private static final String SOLICITACAO_SERVICO_CRUD = "compras-solicitacaoServicoCRUD";

	private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	private static final String ASSOCIAR_SOLICITACAO_COMPRA_SERVICO = "compras-associarSolicitacaoCompraServico";

	private static final String PROCESSO_ADM_COMPRA_CRUD = "compras-processoAdmCompraCRUD";

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "compras-autorizacaoFornecimentoCRUD";
	
	private static final String IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO = "compras-imprimirSolicitacaoDeComprasPdfCadastro";


	private static final long serialVersionUID = 1289998913199756967L;

	@Inject @Paginator
	private DynamicDataModel<ItensSCSSVO> dataModel;

	@Inject
	private ConsultaSSPaginatorController consultaSSPaginatorController;

	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected ICascaFacade cascaFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ImprimirSolicitacaoDeComprasController imprimirSolicitacaoDeComprasController;
	
	private static final Log LOG = LogFactory.getLog(ConsultaSCSSPaginatorController.class); 


	private FiltroConsSCSSVO filtro = new FiltroConsSCSSVO();
	private List<ItensSCSSVO> listaSC;

	private Boolean readOnlyNumero;
	private Boolean readOnlyNumeroFinal;
	private Boolean readOnlyTodosCampos;
	private Boolean readOnlyMaterial;
	private Boolean readOnlyGrupoMaterialEstocavel;
	private Boolean readOnlyServico;
	private Boolean readOnlyGrupoServico;

	private Boolean possuiPermissaoConsultaSC;
	private Boolean possuiPermissaoConsultaSS;
	private Boolean possuiPermissaoConsultaLicitacao;
	private Boolean possuiPermissaoConsultaAf;

	private Boolean erroFiltros;

	private Map<String, Boolean> listaOrdem;

	private Boolean renderedSC;
	private Boolean renderedSS;

	private VScoFornecedor vFornecedorAF;

	/**
	 * Aba corrente
	 */
	private Integer currentTabIndex = 0;

	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;

	private Integer codigoMaterial;
	
	//private List<ItensSCSSVO> itemsSelecionadosSC;

	private List<ItensSCSSVO> listaChecked;
	private List<ItensSCSSVO> allChecked;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {

		this.limparControleGrid();
		consultaSSPaginatorController.limparControleGrid();
		
		if (codigoMaterial != null) {
			ScoMaterial material = comprasFacade.obterMaterialPorId(codigoMaterial);
			filtro.setMaterial(material);
			pesquisar();
		}
	
	}
	
	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (ItensSCSSVO item : this.allChecked) {
			this.listaChecked.add(item);
		}
	}

    public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(ItensSCSSVO item : listaSC) {
				if (!this.allChecked.contains(item)) {
					this.allChecked.add(item);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(ItensSCSSVO item : listaSC) {
				if (this.allChecked.contains(item)) {
					this.allChecked.remove(item);
				}
			}
		}
	}

	public void selecionarLinha(SelectEvent event) {
		ItensSCSSVO item = (ItensSCSSVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ItensSCSSVO item = (ItensSCSSVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}

	public void setarCamposFlagFiltro() {
		this.filtro.setIndEfetivada(null);
		this.filtro.setIndExclusao(null);
		this.filtro.setIndDevolucao(null);
		this.filtro.setIndGeracaoAutomatica(null);
		this.filtro.setIndUrgente(null);
		this.filtro.setIndPrioridade(null);
		this.filtro.setIndExclusivo(null);
		this.filtro.setIndExclusaoPAC(null);
		this.filtro.setIndExclusaoAF(null);

		if (this.filtro.getEfetivada() != null) {
			this.filtro.setIndEfetivada(this.filtro.getEfetivada().isSim());
		}

		if (this.filtro.getExclusao() != null) {
			this.filtro.setIndExclusao(this.filtro.getExclusao().isSim());
		}

		if (this.filtro.getDevolucao() != null) {
			this.filtro.setIndDevolucao(this.filtro.getDevolucao().isSim());
		}

		if (this.filtro.getUrgente() != null) {
			this.filtro.setIndUrgente(this.filtro.getUrgente().isSim());
		}

		if (this.filtro.getPrioridade() != null) {
			this.filtro.setIndPrioridade(this.filtro.getPrioridade().isSim());
		}

		if (this.filtro.getExclusivo() != null) {
			this.filtro.setIndExclusivo(this.filtro.getExclusivo().isSim());
		}

		if (this.filtro.getGeracaoAutomatica() != null) {
			this.filtro.setIndGeracaoAutomatica(this.filtro.getGeracaoAutomatica().isSim());
		}

		if (this.filtro.getExclusaoPAC() != null) {
			this.filtro.setIndExclusaoPAC(this.filtro.getExclusaoPAC().isSim());
		}
		if (this.filtro.getExclusaoAF() != null) {
			this.filtro.setIndExclusaoAF(this.filtro.getExclusaoAF().isSim());
		}

	}

	public void pesquisar() {

		try {
			this.erroFiltros = Boolean.FALSE;
			this.inicializarOrdem();
			this.limparControleGrid();
			consultaSSPaginatorController.limparControleGrid();

			this.possuiPermissaoConsultaSC = this.cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(),
					"consultarSolicitacaoCompras", VISUALIZAR);
			this.possuiPermissaoConsultaSS = this.cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(),
					"consultarSolicitacaoServico", VISUALIZAR);
			this.possuiPermissaoConsultaLicitacao = this.cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "consultarPAC",
					VISUALIZAR);
			this.possuiPermissaoConsultaAf = this.cascaFacade.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "consultarAF",
					VISUALIZAR);

			this.filtro.setFornecedorAF(null);

			if (this.getvFornecedorAF() != null) {
				this.filtro.setFornecedorAF(this.comprasFacade.obterFornecedorPorChavePrimaria(this.getvFornecedorAF()
						.getNumeroFornecedor()));
			}

			this.comprasFacade.validaFiltroPesquisa(this.filtro);
			this.setarCamposFlagFiltro();
			this.comprasFacade.setaValoresDefaults(this.filtro);

			this.setCurrentTabIndex(0);

			if (this.getRenderedSC()) {
				this.getDataModel().reiniciarPaginator();
			}

			if (this.getRenderedSS()) {
				this.consultaSSPaginatorController.getDataModel().reiniciarPaginator();
			}

			if (consultaSSPaginatorController.getDataModel().getPesquisaAtiva()
					&& consultaSSPaginatorController.getDataModel().getRowCount() > 0 && this.getDataModel().getRowCount() == 0) {
				this.setCurrentTabIndex(1);
			}

		} catch (ApplicationBusinessException e) {
			this.erroFiltros = Boolean.TRUE;
			apresentarExcecaoNegocio(e);
		}

	}
	
	

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public void inicializarOrdem() {
		this.listaOrdem = new HashMap<String, Boolean>();
		this.listaOrdem.put("numero", true);
		this.listaOrdem.put("exclusao", true);
		this.listaOrdem.put("codigoMaterial", true);
		this.listaOrdem.put("nomeMaterial", true);
		this.listaOrdem.put("dtSolicitacao", true);
		this.listaOrdem.put("seqCcReq", true);
		this.listaOrdem.put("seqCcAplic", true);
		this.listaOrdem.put("seqPontoParadaProxima", true);
		this.listaOrdem.put("efetivada", true);
		this.listaOrdem.put("qtdVinculadas", true);
		this.listaOrdem.put("lctNumero", true);
		this.listaOrdem.put("itlNumero", true);
		this.listaOrdem.put("numeroEComplementoAf", true);
		consultaSSPaginatorController.setListaOrdem(new HashMap<String, Boolean>());
		consultaSSPaginatorController.getListaOrdem().put("numero", true);
		consultaSSPaginatorController.getListaOrdem().put("exclusao", true);
		consultaSSPaginatorController.getListaOrdem().put("codigoMaterial", true);
		consultaSSPaginatorController.getListaOrdem().put("nomeMaterial", true);
		consultaSSPaginatorController.getListaOrdem().put("dtSolicitacao", true);
		consultaSSPaginatorController.getListaOrdem().put("seqCcReq", true);
		consultaSSPaginatorController.getListaOrdem().put("seqCcAplic", true);
		consultaSSPaginatorController.getListaOrdem().put("seqPontoParadaProxima", true);
		consultaSSPaginatorController.getListaOrdem().put("efetivada", true);
		consultaSSPaginatorController.getListaOrdem().put("qtdVinculadas", true);
		consultaSSPaginatorController.getListaOrdem().put("lctNumero", true);
		consultaSSPaginatorController.getListaOrdem().put("itlNumero", true);
		consultaSSPaginatorController.getListaOrdem().put("numeroEComplementoAf", true);
	}

	public void limparPesquisa() {
		this.setFiltro(new FiltroConsSCSSVO());
		this.setListaSC(new ArrayList<ItensSCSSVO>());
		this.limparControleGrid();
		this.setvFornecedorAF(null);
		this.setCurrentTabIndex(0);	
		this.dataModel.setPesquisaAtiva(false);
		consultaSSPaginatorController.getDataModel().setPesquisaAtiva(false);
		consultaSSPaginatorController.limparControleGrid();
	}
	
	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<ItensSCSSVO>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<ItensSCSSVO>();
		}
		this.allChecked.clear();
	}
	

	public String voltar() {
		return voltarParaUrl;
	}

	public String imprimirItens() throws DocumentException {

		
		List<Integer> listaNumero = obterListaNumeroSc(this.allChecked);
		
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
		
		this.dataModel.reiniciarPaginator();

		return IMPRIMIR_SOLICITACAO_DE_COMPRAS_PDF_CADASTRO;
	}
	
	public List<Integer> obterListaNumeroSc(List<ItensSCSSVO> listaSc) {
		List<Integer> listaResultado = new ArrayList<Integer>();
		
		if (listaSc != null) {
			for(ItensSCSSVO sc : listaSc) {				
				listaResultado.add(sc.getNumero());
			}
		}
		
		return listaResultado;
	}
	
	public String redirecionarSolicitacaoCompra() {
		return SOLICITACAO_COMPRA_CRUD;
	}

	public String redirecionarAssociarSolicitacaoCompraServico() {
		return ASSOCIAR_SOLICITACAO_COMPRA_SERVICO;
	}

	public String redirecionarProcessoAdmCompra() {
		return PROCESSO_ADM_COMPRA_CRUD;
	}

	public String redirecionarAutorizacaoFornecimento() {
		return AUTORIZACAO_FORNECIMENTO_CRUD;
	}

	public String redirecionarSolicitacaoServico() {
		return SOLICITACAO_SERVICO_CRUD;
	}

	public String redirecionarAssociarSolicitacaoServicoCompra() {
		return ASSOCIAR_SOLICITACAO_SERVICO_COMPRA;
	}

	
	public String redirecionarAnexarDocumentoSolicitacaoCompra(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	
	public String truncarTexto(String texto) {
		return (StringUtils.isNotBlank(texto)) ? StringUtils.abbreviate(texto, 30) : "";
	}

	public List<FccCentroCustos> listarCentroCustos(String filter) {

		String srtPesquisa = (String) filter;
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(srtPesquisa),listarCentroCustosCount(filter));
	}

	public Long listarCentroCustosCount(String filter) {

		String srtPesquisa = (String) filter;
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(srtPesquisa);
	}

	public List<ScoMaterial> listarMateriais(String filter) {
		return this.returnSGWithCount(this.solicitacaoComprasFacade.listarMateriaisSC(filter),listarMateriaisCount(filter));
	}

	public Integer listarMateriaisCount(String filter) {
		return this.solicitacaoComprasFacade.listarMateriaisSCCount(filter, null);
	}

	public List<ScoGrupoMaterial> listarGrupoMateriais(String filter) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter);
	}

	public List<ScoServico> listarServicos(String filter) {
		return this.returnSGWithCount(this.comprasFacade.listarServicos(filter),listarServicosCount(filter));
	}

	public Long listarServicosCount(String filter) {
		return this.comprasFacade.listarServicosCount(filter);
	}

	public List<ScoGrupoServico> listarGrupoServico(String filter) {
		return this.comprasFacade.listarGrupoServico(filter);
	}

	//Método para carregar suggestion Ponto Parada
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(String pontoParadaSolic) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao((String) pontoParadaSolic),pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(pontoParadaSolic));
	}

	//Método para carregar suggestion ponto parada count
	public Long pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount((String) pontoParadaSolic);
	}

	/**
	 * Pesquisa modalidade licitacao
	 */
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String objParam) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(objParam);
	}

	public List<FsoNaturezaDespesa> listarTodasNaturezaDespesa(String objParam) {
		return this.returnSGWithCount(this.cadastrosBasicosOrcamentoFacade.listarTodasNaturezaDespesa(objParam),listarTodasNaturezaDespesaCount(objParam));
	}

	public Long listarTodasNaturezaDespesaCount(String objParam) {
		return this.cadastrosBasicosOrcamentoFacade.listarTodasNaturezaDespesaCount(objParam);
	}

	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam) {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}

	public List<RapServidores> pesquisarServidorPorMatriculaOuNome(String parametro) {
		return registroColaboradorFacade.pesquisarServidorPorMatriculaNome(parametro);

	}

	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro);
	}

	public Boolean getReadOnlyNumero() {
		try {
			readOnlyNumero = this.comprasFacade.verificaCamposFiltro(this.getFiltro(), "getNumero", "getNumeroFinal", "getTipoSolicitacao");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return readOnlyNumero;
	}
	
	public Boolean getReadOnlyNumeroFinal() {
		try {
			readOnlyNumeroFinal = this.comprasFacade.verificaCamposFiltro(this.getFiltro(), "getNumero", "getNumeroFinal", "getTipoSolicitacao");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return readOnlyNumeroFinal;
	}

	public Boolean getReadOnlyTodosCampos() {
		readOnlyTodosCampos = (this.filtro.getNumero() != null);
		return readOnlyTodosCampos;
	}

	public Boolean getReadOnlyMaterial() {

		if (!getReadOnlyTodosCampos()) {
			if (this.getFiltro().getTipoSolicitacao() != null) {
				readOnlyMaterial = (DominioTipoSolicitacao.SS.equals(this.getFiltro().getTipoSolicitacao()));
				return readOnlyMaterial;
			} else {
				readOnlyMaterial = (this.getFiltro().getServico() != null) || (this.getFiltro().getGrupoServico() != null);
				return readOnlyMaterial;
			}
		}
		readOnlyMaterial = getReadOnlyTodosCampos();

		return readOnlyMaterial;

	}

	public Boolean getReadOnlyGrupoMaterialEstocavel() {
		if (!this.getReadOnlyMaterial()) {
			readOnlyGrupoMaterialEstocavel = (this.getFiltro().getMaterial() != null);
			return readOnlyGrupoMaterialEstocavel;
		}
		readOnlyGrupoMaterialEstocavel = this.getReadOnlyMaterial();
		return readOnlyGrupoMaterialEstocavel;
	}

	public Boolean getReadOnlyServico() {
		if (!getReadOnlyTodosCampos()) {
			if (this.getFiltro().getTipoSolicitacao() != null) {
				readOnlyServico = (DominioTipoSolicitacao.SC.equals(this.getFiltro().getTipoSolicitacao()));
				return readOnlyServico;
			} else {
				readOnlyServico = (this.getFiltro().getMaterial() != null) || (this.getFiltro().getGrupoMaterial() != null)
						|| (this.getFiltro().getIndEstocavel() != null);
				return readOnlyServico;
			}
		}
		readOnlyServico = getReadOnlyTodosCampos();

		return readOnlyServico;
	}

	public Boolean getReadOnlyGrupoServico() {
		if (!this.getReadOnlyServico()) {
			readOnlyGrupoServico = (this.getFiltro().getServico() != null);
			return readOnlyGrupoServico;
		}
		readOnlyGrupoServico = this.getReadOnlyServico();
		return readOnlyGrupoServico;
	}

	public Boolean getReadOnlyDatas(String ignorarMetodo1, String ignorarMetodo2) {
		Boolean readOnlyDatas = getReadOnlyTodosCampos();
		if (!getReadOnlyTodosCampos()) {
			try {
				readOnlyDatas = this.comprasFacade.verificaCamposFiltroData(this.getFiltro(), ignorarMetodo1, ignorarMetodo2);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return readOnlyDatas;
	}

	public Boolean getRenderedSC() {
		if (this.getFiltro().getTipoSolicitacao() != null) {
			renderedSC = (DominioTipoSolicitacao.SC.equals(this.getFiltro().getTipoSolicitacao()));
			return renderedSC;
		} else {
			if (this.getFiltro().getMaterial() == null && this.getFiltro().getGrupoMaterial() == null
					&& this.getFiltro().getIndEstocavel() == null && this.getFiltro().getServico() == null
					&& this.getFiltro().getGrupoServico() == null) {
				renderedSC = true;
				return renderedSC;
			}
			renderedSC = (this.getFiltro().getMaterial() != null) || (this.getFiltro().getGrupoMaterial() != null)
					|| (this.getFiltro().getIndEstocavel() != null);

			return renderedSC;
		}
	}

	public Boolean getRenderedSS() {
		if (this.getFiltro().getTipoSolicitacao() != null) {
			renderedSS = (DominioTipoSolicitacao.SS.equals(this.getFiltro().getTipoSolicitacao()));
			return renderedSS;
		} else {
			if (this.getFiltro().getMaterial() == null && this.getFiltro().getGrupoMaterial() == null
					&& this.getFiltro().getIndEstocavel() == null && this.getFiltro().getServico() == null
					&& this.getFiltro().getGrupoServico() == null) {
				renderedSS = true;
				return renderedSS;
			}
			renderedSS = (this.getFiltro().getServico() != null) || (this.getFiltro().getGrupoServico() != null);
			return renderedSS;
		}
	}

	@Override
	public Long recuperarCount() {
		Long ret = null;
		if (this.erroFiltros) {
			ret = 0L;
		} else {
			ret = this.comprasFacade.listarSCItensSCSSVOCount(this.filtro);
		}

		return ret;
	}

	@Override
	public List<ItensSCSSVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		this.erroFiltros = Boolean.FALSE;
		if (this.listaSC == null) {
			this.listaSC = new ArrayList<ItensSCSSVO>();
		} else {
			this.listaSC.clear();
		}

		this.listaSC = this.comprasFacade.listarSCItensSCSSVO(firstResult, maxResults, orderProperty, asc, this.filtro);

		return listaSC;
	}

	public VScoFornecedor getvFornecedorAF() {
		return vFornecedorAF;
	}

	public void setvFornecedorAF(VScoFornecedor vFornecedorAF) {
		this.vFornecedorAF = vFornecedorAF;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public FiltroConsSCSSVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroConsSCSSVO filtro) {
		this.filtro = filtro;
	}

	public List<ItensSCSSVO> getListaSC() {
		return listaSC;
	}

	public void setListaSC(List<ItensSCSSVO> listaSC) {
		this.listaSC = listaSC;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	public Boolean getPossuiPermissaoConsultaSC() {
		return possuiPermissaoConsultaSC;
	}

	public void setPossuiPermissaoConsultaSC(Boolean possuiPermissaoConsultaSC) {
		this.possuiPermissaoConsultaSC = possuiPermissaoConsultaSC;
	}

	public Boolean getPossuiPermissaoConsultaSS() {
		return possuiPermissaoConsultaSS;
	}

	public void setPossuiPermissaoConsultaSS(Boolean possuiPermissaoConsultaSS) {
		this.possuiPermissaoConsultaSS = possuiPermissaoConsultaSS;
	}

	public Boolean getPossuiPermissaoConsultaLicitacao() {
		return possuiPermissaoConsultaLicitacao;
	}

	public void setPossuiPermissaoConsultaLicitacao(Boolean possuiPermissaoConsultaLicitacao) {
		this.possuiPermissaoConsultaLicitacao = possuiPermissaoConsultaLicitacao;
	}

	public Boolean getPossuiPermissaoConsultaAf() {
		return possuiPermissaoConsultaAf;
	}

	public void setPossuiPermissaoConsultaAf(Boolean possuiPermissaoConsultaAf) {
		this.possuiPermissaoConsultaAf = possuiPermissaoConsultaAf;
	}

	public Map<String, Boolean> getListaOrdem() {
		return listaOrdem;
	}

	public void setListaOrdem(Map<String, Boolean> listaOrdem) {
		this.listaOrdem = listaOrdem;
	}

	public Boolean getErroFiltros() {
		return erroFiltros;
	}

	public void setErroFiltros(Boolean erroFiltros) {
		this.erroFiltros = erroFiltros;
	}

	public DynamicDataModel<ItensSCSSVO> getDataModel() {
		return dataModel;
	}

	
	public void setReadOnlyNumeroFinal(Boolean readOnlyNumeroFinal) {
		this.readOnlyNumeroFinal = readOnlyNumeroFinal;
	}

	public void setDataModel(DynamicDataModel<ItensSCSSVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public List<ItensSCSSVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ItensSCSSVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ItensSCSSVO> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ItensSCSSVO> allChecked) {
		this.allChecked = allChecked;
	}

}
