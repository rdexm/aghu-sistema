package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.action.AutorizacaoFornecimentoController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.compras.vo.EstatisticaPacVO;
import br.gov.mec.aghu.compras.vo.PACsPendetesVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultarAndamentoProcessoCompraPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -729445225588120797L;
	
	private static final String REDIRECIONA_ITENS_PAC = "compras-consultaItensPac";

    private static final String REDIRECIONA_CONSULTA_ANDAMENTO_PROCESSO_COMPRA = "compras-consultarAndamentoProcessoCompra";

	private static final String REDIRECIONA_AUTORIZACAO_FORNECIMENTO = "compras-consultaAutorizacaoFornecimento";

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

    private Integer numeroPac;

    @Inject
    private ItemPacPaginatorController itemPacPaginatorController;

    @Inject
    private AutorizacaoFornecimentoController autorizacaoFornecimentoController;
	
	@Inject @Paginator
	private DynamicDataModel<ConsultarAndamentoProcessoCompraDataVO> dataModel;

	private Boolean togglePanel = Boolean.FALSE;
	private Boolean togglePanelSimples = Boolean.FALSE;
	private Boolean firstAccess = Boolean.TRUE;
	private ConsultarAndamentoProcessoCompraVO filtro;

	private AghParametros codigoNaturezaParam;
	private AghParametros nomeComissaoParam;

	private String fileName;
	private Boolean gerouArquivo = Boolean.FALSE;

	List<ConsultarAndamentoProcessoCompraDataVO> dados;

	private Boolean pesquisaSimples = Boolean.FALSE;;
	private Boolean pesquisaDetalhada = Boolean.FALSE;
	private static final String PERMISSAO_PESQUISA_SIMPLES = "pesquisarModoSimplesAndamentoProcessoCompras";

	private List<PACsPendetesVO> listaPacsPendentes;
	private List<EstatisticaPacVO> listaEstatisticaVO;

	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio(){

		dataModel.setDefaultMaxRow(15);
		buscaAghuParametros();

		if(firstAccess){

			this.setPesquisaSimples(this.cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_PESQUISA_SIMPLES));

			filtro = new ConsultarAndamentoProcessoCompraVO();
			filtro.setPendente(Boolean.TRUE);
			firstAccess = Boolean.FALSE;

			if(!this.getPesquisaSimples()){
				togglePanel = Boolean.TRUE;
				this.setPesquisaDetalhada(Boolean.TRUE);
			} else {
				this.setTogglePanelSimples(Boolean.TRUE);
			}
		}
	}

	public void pesquisar(){
		if(filtro.getPendente()){
			dataModel.reiniciarPaginator();
			togglePanel = Boolean.FALSE;
			this.setTogglePanelSimples(Boolean.FALSE);
		}else{
			getValidaFiltros();
		}
	}

	private void getValidaFiltros() {
		if(pacFacade.isValidForSearch(filtro)){
			dataModel.reiniciarPaginator();
			togglePanel = Boolean.FALSE;
			this.setTogglePanelSimples(Boolean.FALSE);
		}else{
			apresentarMsgNegocio(Severity.ERROR, "ANDAMENTO_PROCESSO_COMPRA_RESTRICAO_FILTROS");
		}
	}

	public void limpar(){
		filtro = new ConsultarAndamentoProcessoCompraVO();
		filtro.setPendente(Boolean.TRUE);
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		togglePanel = Boolean.TRUE;
		this.setTogglePanelSimples(Boolean.TRUE);
	}


	public void gerarArquivo() throws ApplicationBusinessException {
		dados = pacFacade.consultarAndamentoProcessoCompraForCSV(filtro);
		try {
			fileName = pacFacade.geraArquivoAndamentoProcessoCompra(dados);
			gerouArquivo = Boolean.TRUE;
			this.dispararDownload();

		} catch(IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}

	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar c1 = Calendar.getInstance(); // today
				this.download(fileName, "ARQ_HISTORICO_PROCESSOS_"+sdf.format(c1.getTime())+EXTENSAO_CSV, CONTENT_TYPE_CSV);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	public void gerarEstatisticas(){
		this.setListaPacsPendentes(this.pacFacade.obterQtdPACsPendentes());
		this.setListaEstatisticaVO(this.pacFacade.gerarEstatisticas());
	}

	public String obterCampoTruncado(String valor, int tamanhoMaximo, boolean isTruncate){
		if(valor == null || valor.isEmpty()){
			return "";
		}else{
			return truncaCampo(valor, tamanhoMaximo, isTruncate);
		}
	}

	private String truncaCampo(String valor, int tamanhoMaximo, boolean isTruncate) {
		if(isTruncate && valor.length() > tamanhoMaximo){
			return valor.substring(0, tamanhoMaximo) + "...";
		}
		return valor;
	}

	@Override
	public List<ConsultarAndamentoProcessoCompraDataVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		setValoresParametros();
		return pacFacade.consultarAndamentoProcessoCompra(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public Long recuperarCount() {
		setValoresParametros();
		return pacFacade.consultarAndamentoProcessoCompraCount(filtro);
	}

	private void setValoresParametros() {
		filtro.setCodigosNaturezaParam(codigoNaturezaParam);
		filtro.setNomeComissaoParam(nomeComissaoParam);
	}

	private void buscaAghuParametros() {
		try {
			codigoNaturezaParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGOS_NATUREZA_DESPESA_INVEST);
			nomeComissaoParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_COMISSAO_LICITACOES_PAC);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String obterHelpGestor(){
		return 	"Informe para filtrar por Processos por Gestor (Gerador do Pac, Responsável "+
				"pela AF ou "+nomeComissaoParam.getVlrTexto()+").";
	}


	public void getAlterarVisaoPesqDetalhada(){
		this.setPesquisaDetalhada(Boolean.TRUE);
		this.setPesquisaSimples(Boolean.FALSE);
		this.setTogglePanel(Boolean.TRUE);
		this.setTogglePanelSimples(Boolean.FALSE);
	}

	public String consultaItensPac() {
        itemPacPaginatorController.setNumeroPac(numeroPac);
        itemPacPaginatorController.setVoltarParaUrl(REDIRECIONA_CONSULTA_ANDAMENTO_PROCESSO_COMPRA);
        return REDIRECIONA_ITENS_PAC;
	}

	public String consultaAutorizacaoFornecimento() {
        autorizacaoFornecimentoController.setVoltarParaUrl(REDIRECIONA_CONSULTA_ANDAMENTO_PROCESSO_COMPRA);
        return REDIRECIONA_AUTORIZACAO_FORNECIMENTO;
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) throws BaseException {
		return this.returnSGWithCount(this.comprasFacade.getListaMaterialByNomeOrDescOrCodigo(objPesquisa), listarMateriaisCount(objPesquisa));
	}

	public Long listarMateriaisCount(String param) {
		return this.comprasFacade.listarScoMateriaisAtivosCount(param, null, Boolean.TRUE);
	}

	public List<ScoLocalizacaoProcesso> pesquisarLocalizacoes(String filtro) {
		return this.pacFacade.pesquisarScoLocalizacaoProcesso(filtro, 100);
	}

	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String objParam) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(objParam), null);
	}

	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa), listarCentroCustosCount(objPesquisa));
	}

	public List<FccCentroCustos> listarCentroCustos(String filter) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filter), listarCentroCustosCount(filter));
	}

	public Long listarCentroCustosCount(String filter) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(filter);
	}

	public List<ScoServico> listarServicos(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarServicos(filter), listarServicosCount(filter));
	}
	public Long listarServicosCount(String filter){
		return this.comprasFacade.listarServicosCount(filter);
	}

	public List<ScoMarcaComercial> pesquisarMarcaComercial(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricao(param), pesquisarMarcaComercialCount(param));
	}

	public Long pesquisarMarcaComercialCount(String param) {
		return this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoCount(param);
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return  this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null, 100, null, true), contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
	}

	public void setTogglePanel(Boolean togglePanel) {
		this.togglePanel = togglePanel;
	}

	public Boolean getTogglePanelSimples() {
		return togglePanelSimples;
	}

	public void setTogglePanelSimples(Boolean togglePanelSimples) {
		this.togglePanelSimples = togglePanelSimples;
	}

	public Boolean getTogglePanel() {
		return togglePanel;
	}

	public ConsultarAndamentoProcessoCompraVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ConsultarAndamentoProcessoCompraVO filtro) {
		this.filtro = filtro;
	}

	public Boolean getFirstAccess() {
		return firstAccess;
	}

	public void setFirstAccess(Boolean firstAccess) {
		this.firstAccess = firstAccess;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public Boolean getPesquisaSimples() {
		return pesquisaSimples;
	}

	public void setPesquisaSimples(Boolean pesquisaSimples) {
		this.pesquisaSimples = pesquisaSimples;
	}

	public Boolean getPesquisaDetalhada() {
		return pesquisaDetalhada;
	}

	public void setPesquisaDetalhada(Boolean pesquisaDetalhada) {
		this.pesquisaDetalhada = pesquisaDetalhada;
	}

	public List<EstatisticaPacVO> getListaEstatisticaVO() {
		return listaEstatisticaVO;
	}

	public void setListaEstatisticaVO(List<EstatisticaPacVO> listaEstatisticaVO) {
		this.listaEstatisticaVO = listaEstatisticaVO;
	}

	public List<PACsPendetesVO> getListaPacsPendentes() {
		return listaPacsPendentes;
	}

	public void setListaPacsPendentes(List<PACsPendetesVO> listaPacsPendentes) {
		this.listaPacsPendentes = listaPacsPendentes;
	}

	public DynamicDataModel<ConsultarAndamentoProcessoCompraDataVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ConsultarAndamentoProcessoCompraDataVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AghParametros getNomeComissaoParam() {
		return nomeComissaoParam;
	}

	public void setNomeComissaoParam(AghParametros nomeComissaoParam) {
		this.nomeComissaoParam = nomeComissaoParam;
	}

	public List<ConsultarAndamentoProcessoCompraDataVO> getDados() {
		return dados;
	}

	public void setDados(List<ConsultarAndamentoProcessoCompraDataVO> dados) {
		this.dados = dados;
	}

    public ItemPacPaginatorController getItemPacPaginatorController() {
        return itemPacPaginatorController;
    }

    public void setItemPacPaginatorController(ItemPacPaginatorController itemPacPaginatorController) {
        this.itemPacPaginatorController = itemPacPaginatorController;
    }

    public Integer getNumeroPac() {
        return numeroPac;
    }

    public void setNumeroPac(Integer numeroPac) {
        this.numeroPac = numeroPac;
    }
}