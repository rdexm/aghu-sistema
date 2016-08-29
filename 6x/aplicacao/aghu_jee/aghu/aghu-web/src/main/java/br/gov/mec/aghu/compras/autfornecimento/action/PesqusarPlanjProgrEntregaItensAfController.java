package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioVizAutForn;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class PesqusarPlanjProgrEntregaItensAfController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PesquisarPlanjProgrEntregaItensAfVO> dataModel;

	private static final Log LOG = LogFactory.getLog(PesqusarPlanjProgrEntregaItensAfController.class);

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
	
	@Inject
	protected ConsultarParcelaEntregaMaterialController consultarParcelaEntregaMaterialController;
	
	private PesquisarPlanjProgrEntregaItensAfFiltroVO filtro = new PesquisarPlanjProgrEntregaItensAfFiltroVO(DominioSimNao.N, DominioSimNao.S, DominioSimNao.N);
	private PesquisarPlanjProgrEntregaItensAfFiltroVO filtroPesquisa = new PesquisarPlanjProgrEntregaItensAfFiltroVO();
	
	private List<PesquisarPlanjProgrEntregaItensAfVO> listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
	private Boolean realizarPesquisaPadrao = true;
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
	private boolean enviarParametros = false;
	private Boolean togglePanel = false;
	private Boolean pesquisaNova = false;
	
	@Inject
	protected ExcluirProgramacaoEntregaItensAfController excluirProgramacaoEntregaItensAfController;
	

	
	public void inicio() {
		setMaxResults(13);
		//REALIZAR PESQUISA DEFAULT
		this.getLimpar();
		this.filtro.setVisualizarAutForn(null);
		exibirLista = true;
		togglePanel = false;
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		dataModel.reiniciarPaginator();
		realizarPesquisaPadrao = false;
		this.filtro.setVisualizarAutForn(DominioVizAutForn.T);
	}
	
	public void getLimpar() {
		filtro = new PesquisarPlanjProgrEntregaItensAfFiltroVO();
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		exibirLista = false;
		selecionarTodos = false;
		afSelecionada = null;
		//togglePanel = false;
	}
	
	public String getVoltar() {
		return "voltar";
	}

	public void liberarEntrega() {
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			autFornecimentoFacade.liberarEntrega(listaPlanjProgEntregaItensAFs, dataLiberacaoEntrega, servidorLogado);
			this.apresentarMsgNegocio(Severity.INFO, 
					"PROG_ENTREG_LIBERAR_ENTREGA_SUCESSO");
			this.getPesquisarCorrente();
			habilitarBotoes = false;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	/**
	 * Ação do botão PROGRAMAR AUTOMÁTICA
	 */
	public void getProgramacaoAutomatica() {
		try {
			List<Integer> listaAFs = this.getListaAFs(listaPlanjProgEntregaItensAFs);

			comprasFacade.programacaoAutomaticaParcelasAF(listaAFs);

			this.apresentarMsgNegocio(Severity.INFO, "PROGRAMACAO_REALIZADA_SUCESSO");

			this.getPesquisarCorrente();

			habilitarBotoes = false;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Busca as AFs selecionadas
	 * 
	 * @param listaPlanjProgEntregaItensAFs
	 * @return
	 */
	private List<Integer> getListaAFs(List<PesquisarPlanjProgrEntregaItensAfVO> listaPlanjProgEntregaItensAFs) {
		List<Integer> listaAFs = new ArrayList<Integer>();
		for (PesquisarPlanjProgrEntregaItensAfVO pesquisarPlanjProgrEntregaItensAfVO : listaPlanjProgEntregaItensAFs) {
			if(Boolean.TRUE.equals(pesquisarPlanjProgrEntregaItensAfVO.getSelecionado())){
				listaAFs.add(pesquisarPlanjProgrEntregaItensAfVO.getNumeroAF());
			}
		}
		return listaAFs;
	}


	public void botaoLiberarEntrega() {
		try {
			dataLiberacaoEntrega = autFornecimentoFacade.obterDataLiberacaoEntrega(filtro.getDataPrevisaoEntrega());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarTodosCheckBox() {
		habilitarProgramacaoPendenteGeral = false;
		habilitarExcluirProgramacao = false;
		enviarParametros = false;
		
		if(Boolean.TRUE.equals(selecionarTodos)) {
			habilitarBotoes = true;
		}
		else {
			habilitarBotoes = false;
			habilitarExcluirProgramacao = false;
		}
		for(PesquisarPlanjProgrEntregaItensAfVO vo : listaPlanjProgEntregaItensAFs) {
			vo.setSelecionado(selecionarTodos);
		}
 	}
	
	public void processarCheckBox() {
		habilitarBotoes = false;
		habilitarProgramacaoPendenteGeral = false;
		habilitarExcluirProgramacao = false;
		habilitarParcelasLiberar = false;
		habilitarAlterarProgramacao = false;
		enviarParametros = false;
		int qtdSelecionados = 0;
		for(PesquisarPlanjProgrEntregaItensAfVO vo : listaPlanjProgEntregaItensAFs) {
			if(Boolean.TRUE.equals(vo.getSelecionado())) {
				habilitarBotoes = true;
				afSelecionada = vo;
				qtdSelecionados++;
			}
		}
		
		if(qtdSelecionados == 1) {
			habilitarProgramacaoPendenteGeral = true;
			habilitarExcluirProgramacao = true;
			habilitarParcelasLiberar = true;
			habilitarAlterarProgramacao = true;
			enviarParametros = true;
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
		return "progPendente";
	}

	public String getProgGeral() {
		return "progGeral";
	}
	
	public String irParaBaseProgramacao() {
		return "progEntregaItensAF";
	}
	
	public String irConsultaItensAFProgramacaoEntrega(){
		return "consultaItensAFProgramacaoEntrega";
	}
	
	public String liberarEntregasPorItem() {
		consultarParcelaEntregaMaterialController.setNumeroAf(null);
		consultarParcelaEntregaMaterialController.setNumeroComplemento(null);
		return "liberarEntregasPorItem";
	}
	
	public String alterarProgramacao() {
		for(PesquisarPlanjProgrEntregaItensAfVO vo : listaPlanjProgEntregaItensAFs) {
			if (vo.getSelecionado() != null && vo.getSelecionado()) {
				consultarParcelaEntregaMaterialController.setNumeroAf(vo.getNumeroLicitacao());
				consultarParcelaEntregaMaterialController.setNumeroComplemento(vo.getComplemento());
				break;
			}
		}
		return "alterarProgramacao";
	}
	
	public String getParcelasLiberar() {
		for(PesquisarPlanjProgrEntregaItensAfVO vo : listaPlanjProgEntregaItensAFs) {
			if (vo.getSelecionado() != null && vo.getSelecionado()) {
				consultarParcelaEntregaMaterialController.setNumeroAf(vo.getNumeroLicitacao());
				consultarParcelaEntregaMaterialController.setNumeroComplemento(vo.getComplemento());
				break;
			}
		}
		return "parcelasLiberar";
	}
	
	public void pesquisar() {
		if(DominioVizAutForn.E == filtro.getVisualizarAutForn()){
			if(filtro.getDataPrevisaoEntrega() == null){
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_INFORME_DATA_LIMITE_PREVISAO_ENTREGA");
			}
		} else {
			exibirLista = true;
			togglePanel = false;
			pesquisaNova = true;
			listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
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
		dataModel.reiniciarPaginator();
	}
	
	
	public void getPesquisarCorrente() {
		exibirLista = true;
		togglePanel = false;
		listaPlanjProgEntregaItensAFs = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
		dataModel.reiniciarPaginator();
	}
	
	public DominioVizAutForn[] getVizAutForn() {
		return DominioVizAutForn.values();
	}
	
	public DominioModalidadeEmpenho[] getModalidadesEmpenho() {
		return new DominioModalidadeEmpenho[] { DominioModalidadeEmpenho.CONTRATO, DominioModalidadeEmpenho.ESTIMATIVA};
	}
	
	public List<ScoGrupoMaterial> listarGrupoMateriais(Object filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter);
	}
	
	public Long listarGrupoMateriaisCount(Object filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}

	public List<ScoMaterial> listarMateriais(Object filter){
		return this.comprasFacade.listarScoMateriais(filter, null, true);
	}

	public Long listarMateriaisCount(Object filter){
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}

	public List<FccCentroCustos> listarCentroCustos(Object filter) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String)filter);
	}
	
	public Long listarCentroCustosCount(Object filter) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount((String)filter);
	}
	
	public List<ScoModalidadeLicitacao> listarModalidades(Object filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacao(filter);
	}

	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(Object parametro){		
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro);
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
		
		if(afSelecionada !=null){
			excluirProgramacaoEntregaItensAfController.setRazaoSocial(afSelecionada.getNomeFornecedor());
		}
		
		return "exclusaoProgramacao";
	}
	
	public String getIrParaProgramarManualBotao() {
		if (this.enviarParametros) {
			return "programarManualBotaoComAF";
		}
		return "programarManualBotaoSemAF";
	}
	
	public String getFornecedorTruncated(String fornecedor){
		return StringUtil.trunc(fornecedor, true, 15l);
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
}