package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.IPlanejamentoFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.CriterioReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.dominio.DominioBaseAnaliseReposicao;
import br.gov.mec.aghu.dominio.DominioBaseReposicao;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioInclusaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoConsumo;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public class ReposicaoMaterialPaginatorController extends ActionController implements ActionPaginator {
	
	@Inject @Paginator
	private DynamicDataModel<ItemReposicaoMaterialVO> dataModel;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IPlanejamentoFacade planejamentoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private SolicitacaoCompraController solicitacaoCompraController;
	
	private static final String MAT_CODIGO = "matCodigo";
	private static final String INCLUSAO_MANUAL_REPOSICAO_AUTOMATICA = "inclusaoManualReposicaoAutomatica";
	private static final String LOTE_REPOSICAO_MATERIAL_LIST = "loteReposicaoMaterialList";
	private static final String SOLICITACAO_COMPRA_CRUD = "solicitacaoCompraCRUD";
	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String REPOSICAO_MATERIAL = "reposicaoMaterial";
	
	private static final long serialVersionUID = 6311943654533755902L;
		
	private List<ItemReposicaoMaterialVO> listaInclusaoPontual;

	private CriterioReposicaoMaterialVO criterioReposicao;
	private DominioTipoMaterial tipoMaterial = DominioTipoMaterial.E;
	private DominioBaseAnaliseReposicao base;
	private ScoGrupoMaterial grupoMaterial;
	private VScoClasMaterial classificacaoMaterial;
	private List<DominioClassifABC> listaClassAbc;
	private FccCentroCustos centroCustoAplicacao;
	private DominioSimNao indProducaoInterna;
	private DominioSimNao indPontoPedido;
	private DominioSimNao indComLicitacao;
	private DominioSimNao indAfContrato;
	private DominioSimNao indAfVencida;
	private DominioSimNao indItemContrato;
	private Integer cobertura;
	private Date dataVigencia;
	private ScoModalidadeLicitacao modalidade;
	private Date dataInicio;
	private Date dataFim;
	private Integer frequencia;
	private BigDecimal vlrInicial;
	private BigDecimal vlrFinal;
	private Boolean somentePesquisa = Boolean.TRUE;
	private DominioBaseReposicao baseReposicao;
	private Date dataInicioReposicao;
	private Date dataFimReposicao;
	private DominioTipoConsumo tipoConsumo;
	private BigDecimal fatorSeguranca;
	private String nomeLote;
	private Integer seqLote;
	private String titleFaixaValor;
	private String descricaoLote;
	private Date dataGeracao;
	private String usuarioGeracao;
	private Boolean mostrarDadosLote;
	private Boolean mostrarTipoConsumo;
	private ScoLoteReposicao loteReposicao;
	private Integer seqItemExclusao;
	private List<ItemReposicaoMaterialVO> listaChecked;
	private List<ItemReposicaoMaterialVO> allChecked;
	private List<ItemReposicaoMaterialVO> listaAtual;
	private List<Integer> nroDesmarcados;
	List<ItemReposicaoMaterialVO> listaAlteracoes;
	List<ItemReposicaoMaterialVO> listaCompleta;
	private String voltarParaUrl;
	private boolean indSemClassificacaoABC;
	private DominioSimNao indEmSc = DominioSimNao.N;
	private DominioSimNao indEmPac = DominioSimNao.N;
	private DominioSimNao indEmAf = DominioSimNao.N;

	@PostConstruct
	protected void inicializar(){	
		this.begin(conversation);
		this.limpar(false, true);
	}
	
	public void iniciar() {

		this.limpar(false,false);
		if (this.listaInclusaoPontual != null && !this.listaInclusaoPontual.isEmpty()) {
			try {
				this.planejamentoFacade.inserirInclusaoManualLoteReposicao(this.loteReposicao,
						this.listaInclusaoPontual);
				this.listaInclusaoPontual = null;
				this.dataModel.reiniciarPaginator();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INCLUSAO_PONTUAL_COM_SUCESSO", loteReposicao.getSeq());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		if (this.seqLote != null) {
			this.loteReposicao = this.planejamentoFacade.obterLoteReposicaoPorSeq(seqLote);
			this.descricaoLote = loteReposicao.getDescricao();
			this.dataGeracao = loteReposicao.getDtGeracao();
			this.usuarioGeracao = loteReposicao.getServidorGeracao().getCodigoVinculoNomeServidor();
			this.mostrarDadosLote = Boolean.TRUE;
			this.nomeLote = this.loteReposicao.getDescricao();
			this.seqLote = null;
			this.somentePesquisa = Boolean.FALSE;
			this.limpar(false, true);		
			montarFiltrosAnalise();
			
			this.dataModel.reiniciarPaginator();
		} else {
			if (loteReposicao == null) {
				this.loteReposicao = null;
				this.descricaoLote = null;
				this.dataGeracao = null;
				this.usuarioGeracao = null;
				this.mostrarDadosLote = Boolean.FALSE;
			}
		}
	}
	
	public void selecionarLinha(SelectEvent event) {
		ItemReposicaoMaterialVO item = (ItemReposicaoMaterialVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
		if (!this.nroDesmarcados.contains(item.getMatCodigo())) {
			this.nroDesmarcados.add(item.getMatCodigo());
		} else {
			this.nroDesmarcados.remove(item.getMatCodigo());
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		ItemReposicaoMaterialVO item = (ItemReposicaoMaterialVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
		if (!this.nroDesmarcados.contains(item.getMatCodigo())) {
			this.nroDesmarcados.add(item.getMatCodigo());
		} else {
			this.nroDesmarcados.remove(item.getMatCodigo());
		}
	}
	
	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		if (listaChecked == null) {
			this.listaChecked = new ArrayList<ItemReposicaoMaterialVO>();
		}
		this.listaChecked.clear();
		for (ItemReposicaoMaterialVO item : this.allChecked) {
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
			for(ItemReposicaoMaterialVO item : listaAtual) {
				if (!this.allChecked.contains(item)) {
					this.allChecked.add(item);
					this.nroDesmarcados.remove(item.getMatCodigo());
				}
			}
		} else if (this.listaChecked.size() == 0) {
			for(ItemReposicaoMaterialVO item : listaAtual) {
				if (this.allChecked.contains(item)) {
					this.allChecked.remove(item);
					this.nroDesmarcados.add(item.getMatCodigo());
				}
			}
		}
	}
	
	public void atualizarListaEdicoes(ItemReposicaoMaterialVO item) {
		Integer index = listaAlteracoes.indexOf(item);
		if (index >= 0) {
			((ItemReposicaoMaterialVO) listaAlteracoes.get(index)).setQtd(item.getQtd()); 
		} else {
			listaAlteracoes.add(item);
		}

		if (!this.allChecked.contains(item.getMatCodigo())) {
			this.allChecked.add(item);
			this.listaChecked.add(item);
			this.nroDesmarcados.remove(item.getMatCodigo());
		}
	}
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro);
	}

	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, this.grupoMaterial.getCodigo().shortValue());
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		return this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(parametro);
	}
	
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(String parametro) {
		return comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(parametro);
	}
	
	public void gerarReposicao(Boolean simular) {
		try {
			if (!this.somentePesquisa) {
				this.criterioReposicao = montarFiltroReposicao();
				this.planejamentoFacade.validarCamposObrigatorios(this.criterioReposicao, simular);
			}
			if (simular) {
				this.limpar(false, true);
				this.dataModel.reiniciarPaginator();
			} else {
				this.loteReposicao = this.planejamentoFacade.criarLote(this.montarFiltroPesquisa(), this.getCriterioReposicao(), 
						listaAlteracoes, nroDesmarcados);
				
				if (this.loteReposicao != null) {
					this.mostrarDadosLote = Boolean.TRUE;
					this.descricaoLote = loteReposicao.getDescricao();
					this.dataGeracao = loteReposicao.getDtGeracao();
					loteReposicao.setServidorGeracao(this.registroColaboradorFacade.obterServidor(loteReposicao.getServidorGeracao().getId()));
					loteReposicao.getServidorGeracao().setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(loteReposicao.getServidorGeracao().getPessoaFisica().getCodigo()));
					this.usuarioGeracao = loteReposicao.getServidorGeracao().getId().getVinCodigo() + " - " + loteReposicao.getServidorGeracao().getPessoaFisica().getNome();
					
					this.dataModel.reiniciarPaginator();
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REPOSICAO_REALIZADA_COM_SUCESSO");
				} else {
					this.mostrarDadosLote = Boolean.FALSE;
				}
			}
		} catch (BaseException e) {
			dataModel.setPesquisaAtiva(Boolean.FALSE);
			apresentarExcecaoNegocio(e);
		}	
	}
	
	public void excluirCesta() {
		try {
			if (getLoteReposicao() != null) {
				this.planejamentoFacade.excluirLote(getLoteReposicao());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LOTE_REPOSICAO_EXCLUIDO_SUCESSO", getLoteReposicao().getSeq(), getLoteReposicao().getDescricao());
				this.limpar(true, true);
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SEM_LOTE_REPOSICAO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}	
	}
	
	public void excluirItem() {
		try {
			if (getLoteReposicao() != null) {
				this.planejamentoFacade.excluirItemLote(getLoteReposicao(), this.seqItemExclusao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_LOTE_REPOSICAO_EXCLUIDO_SUCESSO", getLoteReposicao().getSeq(), getLoteReposicao().getDescricao());				
				this.dataModel.reiniciarPaginator();
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_SEM_LOTE_REPOSICAO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void confirmarItem(ItemReposicaoMaterialVO item) {
		item.setConfirmada(true);
	}
	
	@Override
	public Long recuperarCount() {
		if (this.loteReposicao != null) {
			return this.planejamentoFacade.pesquisarMaterialReposicaoCount(this.loteReposicao);
		} else {
			return this.solicitacaoComprasFacade.pesquisarMaterialReposicaoCount(this.montarFiltroPesquisa());
		}
	}

	@Override
	public List<ItemReposicaoMaterialVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {
		if(orderProperty==null){
			orderProperty = MAT_CODIGO;
		}
		if (this.loteReposicao != null) {
			this.pesquisarListaItensLoteGerado(firstResult, maxResults, orderProperty, asc);
		} else {
			this.pesquisarListaSimulacao(firstResult, maxResults, orderProperty, asc);
		}
		return this.listaCompleta;
	}
	
	private void pesquisarListaItensLoteGerado(Integer firstResult, Integer maxResults, String orderProperty,	boolean asc) {
		this.listaCompleta = this.planejamentoFacade.pesquisarMaterialReposicao(firstResult,maxResults, orderProperty, asc,	this.loteReposicao);	
	}
	
	private void pesquisarListaSimulacao(Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {
		this.listaCompleta = this.planejamentoFacade.pesquisarMaterialReposicao(firstResult,
						maxResults, orderProperty, asc, this.montarFiltroPesquisa(), this.criterioReposicao);
		
		this.listaAtual.clear();
		this.listaAtual.addAll(listaCompleta);
		
		for (ItemReposicaoMaterialVO item : this.listaCompleta) {
			Integer indexDesmarcado = nroDesmarcados.indexOf(item.getMatCodigo());
			if (indexDesmarcado < 0) {
				this.listaChecked.add(item);
			} else {
				this.listaChecked.remove(item);
			}
			if (item.getIndInclusao() != null && !item.getIndInclusao().equals(DominioInclusaoLoteReposicao.AT)) {
				continue;
			}
			item.setConfirmada(true);
			item.setIndInclusao(DominioInclusaoLoteReposicao.AT);	
			item.setQtdOriginal(item.getQtd());
			Integer index = listaAlteracoes.indexOf(item);
			if (index >= 0) {
				item.setQtd(((Integer) this.listaAlteracoes.get(index).getQtd()));
				item.setIndInclusao(((DominioInclusaoLoteReposicao) this.listaAlteracoes.get(index).getIndInclusao()));
			}
		}
		
		for (ItemReposicaoMaterialVO item : this.listaAlteracoes) {
			if (item.getIndInclusao() != null && !item.getIndInclusao().equals(DominioInclusaoLoteReposicao.AT)) {
				this.allChecked.add(item);
				this.listaCompleta.add(item);
			}
		}
	}
	
	public void atualizarCombos(Integer tipo) {
		switch(tipo) {
			case 0:
				if (indEmSc != null && !indEmSc.isSim()) {
					this.indEmPac = DominioSimNao.N;
					this.indEmAf = DominioSimNao.N;
				}
				break;
			case 1:
				if (indEmPac != null) {
					if (indEmPac.isSim()) {
						this.indEmSc = DominioSimNao.S;
					} else {
						this.indEmAf = DominioSimNao.N;
					}
				}
				break;
			case 2:
				if (indEmAf != null && indEmAf.isSim()) {
					this.indEmPac = DominioSimNao.S;
					this.indEmSc = DominioSimNao.S;
				}
				break;
			default:
				break;
		}
	}
	
	public void atualizarBaseAnalise(Integer tipo) {
		switch(tipo) {
		case 0:
			String msgTitleValorReposicao = getBundle().getString("TITLE_FAIXA_VALOR_REPOSICAO");
			
			if (base != null) {
				if (base.equals(DominioBaseAnaliseReposicao.CA)) {
					this.titleFaixaValor = MessageFormat.format(msgTitleValorReposicao, DominioBaseAnaliseReposicao.CA.getDescricao());
				} else {
					this.titleFaixaValor = MessageFormat.format(msgTitleValorReposicao, DominioBaseAnaliseReposicao.CO.getDescricao());
				}
			} else {
				this.dataInicio = null;
				this.dataFim = null;
				this.frequencia = null;
				this.vlrInicial = null;
				this.vlrFinal = null;
			}
			break;
		case 1:
			if (this.getBaseReposicao() != null && this.getBaseReposicao().equals(DominioBaseReposicao.HC)) {
				this.mostrarTipoConsumo = Boolean.FALSE;
			} else {
				this.mostrarTipoConsumo = Boolean.TRUE;
			}
			break;
		}
	}
	
	public Boolean verificarOriginal(ItemReposicaoMaterialVO item) {
		return item.getIndInclusao().equals(DominioInclusaoLoteReposicao.AT);
	}
	
	public String obterListaScs(ItemReposicaoMaterialVO item) {
		return this.planejamentoFacade.getListaScs(item);
	}
	
	private CriterioReposicaoMaterialVO montarFiltroReposicao() {
		CriterioReposicaoMaterialVO criterio = new CriterioReposicaoMaterialVO();
		criterio.setBaseReposicao(this.baseReposicao);
		criterio.setDataInicioReposicao(dataInicioReposicao);
		criterio.setDataFimReposicao(dataFimReposicao);
		criterio.setFatorSeguranca(fatorSeguranca);
		criterio.setNomeLote(nomeLote);
		criterio.setTipoConsumo(tipoConsumo);
		return criterio;
	}
	
	private FiltroReposicaoMaterialVO montarFiltroPesquisa () {
		FiltroReposicaoMaterialVO filtrosPesquisa = new FiltroReposicaoMaterialVO();
		filtrosPesquisa.setSemClassificacaoAbc(indSemClassificacaoABC);
		filtrosPesquisa.setTipoMaterial(this.tipoMaterial);
		filtrosPesquisa.setBase(this.base);
		filtrosPesquisa.setGrupoMaterial(this.grupoMaterial);
		filtrosPesquisa.setClassificacaoMaterial(this.classificacaoMaterial);
		if (this.listaClassAbc != null && !this.listaClassAbc.isEmpty()) {
			filtrosPesquisa.setListaClassAbc(getListaClassAbc());
		}
		filtrosPesquisa.setCentroCustoAplicacao(this.centroCustoAplicacao);
		filtrosPesquisa.setIndProducaoInterna(this.indProducaoInterna);
		filtrosPesquisa.setDataVigencia(this.dataVigencia);
		filtrosPesquisa.setModalidade(this.modalidade);
		filtrosPesquisa.setDataInicio(this.dataInicio);
		filtrosPesquisa.setDataFim(this.dataFim);
		filtrosPesquisa.setFrequencia(this.frequencia);
		filtrosPesquisa.setVlrInicial(this.vlrInicial);
		filtrosPesquisa.setVlrFinal(this.vlrFinal);
		filtrosPesquisa.setSomentePesquisa(this.somentePesquisa);
		filtrosPesquisa.setIndEmAf(indEmAf);
		filtrosPesquisa.setIndEmPac(indEmPac);
		filtrosPesquisa.setIndEmSc(indEmSc);
		filtrosPesquisa.setIndPontoPedido(indPontoPedido);
		filtrosPesquisa.setCobertura(cobertura);
		filtrosPesquisa.setIndComLicitacao(indComLicitacao);
		filtrosPesquisa.setIndAfContrato(indAfContrato);
		filtrosPesquisa.setIndAfVencida(indAfVencida);
		filtrosPesquisa.setIndItemContrato(indItemContrato);

		try {
			filtrosPesquisa.setFornecedorPadrao(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO));
			filtrosPesquisa.setCompetencia(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA));
			filtrosPesquisa.setAlmoxarifadoPadrao(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return filtrosPesquisa;
	}

	private void montarFiltroClassAbc() {
		if (this.loteReposicao.getClassificacaoAbc() != null) {
			if (this.listaClassAbc == null) {
				this.listaClassAbc = new ArrayList<DominioClassifABC>();
			}
			if (this.loteReposicao.getClassificacaoAbc().contains("A")) {
				this.listaClassAbc.add(DominioClassifABC.A);
			}
			if (this.loteReposicao.getClassificacaoAbc().contains("B")) {
				this.listaClassAbc.add(DominioClassifABC.B);
			}
			if (this.loteReposicao.getClassificacaoAbc().contains("C")) {
				this.listaClassAbc.add(DominioClassifABC.C);
			}
		}
	}

	public void montarFiltrosAnalise() {
		this.base = null;
		if (this.loteReposicao.getBaseAnalise() != null) {
			this.base = this.loteReposicao.getBaseAnalise();
		}
		this.centroCustoAplicacao = null;
		if (this.loteReposicao.getCentroCustoAplicacao() != null) {
			this.centroCustoAplicacao = this.loteReposicao.getCentroCustoAplicacao();
		}
		this.dataFim = null;
		if (this.loteReposicao.getDtFimBaseAnalise() != null) {
			this.dataFim = this.loteReposicao.getDtFimBaseAnalise();
		}
		this.dataInicio = null;
		if (this.loteReposicao.getDtInicioBaseAnalise() != null) {
			this.dataInicio = this.loteReposicao.getDtInicioBaseAnalise();
		}
		if (this.loteReposicao.getCobertura() != null) {
			this.setCobertura(this.loteReposicao.getCobertura());
		}
		this.fatorSeguranca = this.loteReposicao.getFatorSeguranca();
		this.baseReposicao = this.loteReposicao.getBaseReposicao();
		this.dataFimReposicao = this.loteReposicao.getDataFimReposicao();
		this.dataInicioReposicao = this.loteReposicao.getDataInicioReposicao();
		this.descricaoLote = this.loteReposicao.getDescricao();
		montarFiltroClassAbc();
		montarFiltrosAnaliseAdicional();
		montarFiltroPlanejamento();
	}
	
	public void montarFiltroPlanejamento() {
		this.indProducaoInterna = null;
		if (this.loteReposicao.getIndProducaoInterna() != null) {
			this.setIndProducaoInterna(DominioSimNao.getInstance(this.loteReposicao.getIndProducaoInterna()));
	}
		this.indComLicitacao = null;
		if (this.loteReposicao.getIndComLicitacao() != null) {
			this.setIndComLicitacao(DominioSimNao.getInstance(this.loteReposicao.getIndComLicitacao()));
		}
		this.indEmSc = null;
		if (this.loteReposicao.getIndEmSc() != null) {
			this.setIndEmSc(DominioSimNao.getInstance(this.loteReposicao.getIndEmSc()));
		}
		this.indEmPac = null;
		if (this.loteReposicao.getIndEmLicitacao() != null) {
			this.setIndEmPac(DominioSimNao.getInstance(this.loteReposicao.getIndEmLicitacao()));
		}
		this.indEmAf = null;
		if (this.loteReposicao.getIndEmAf() != null) {
			this.setIndEmAf(DominioSimNao.getInstance(this.loteReposicao.getIndEmAf()));
		}
		this.indAfContrato = null;
		if (this.loteReposicao.getIndAfContrato() != null) {
			this.setIndAfContrato(DominioSimNao.getInstance(this.loteReposicao.getIndAfContrato()));
		}
		this.indAfVencida = null;
		if (this.loteReposicao.getIndAfVencida() != null) {
			this.setIndAfVencida(DominioSimNao.getInstance(this.loteReposicao.getIndAfVencida()));
		}
		this.indItemContrato = null;
		if (this.loteReposicao.getIndItemContrato() != null) {
			this.setIndItemContrato(DominioSimNao.getInstance(this.loteReposicao.getIndItemContrato()));
		}
	} 
	
	public void montarFiltrosAnaliseAdicional() {
		this.classificacaoMaterial = null;
		if (this.loteReposicao.getCn5Numero() != null) {
			this.classificacaoMaterial = this.comprasFacade.obterVScoClasMaterialPorNumero(this.loteReposicao.getCn5Numero());
		}
		this.dataVigencia = null;
		if (this.loteReposicao.getDtVigenciaContrato() != null) {
			this.dataVigencia = this.loteReposicao.getDtVigenciaContrato();
		}
		this.frequencia = null;
		if (this.loteReposicao.getFrequenciaBaseAnalise() != null) {
			this.frequencia = this.loteReposicao.getFrequenciaBaseAnalise();
		}
		this.grupoMaterial = null;
		if (this.loteReposicao.getGrupoMaterial() != null) {
			this.grupoMaterial = this.loteReposicao.getGrupoMaterial();
		}
		this.modalidade = null;
		if (this.loteReposicao.getModalidade() != null) {
			this.modalidade = this.loteReposicao.getModalidade();
		}
		this.tipoConsumo = null;
		if (this.loteReposicao.getTipoConsumo() != null) {
			this.tipoConsumo = this.loteReposicao.getTipoConsumo();
		}
		this.tipoMaterial = null;
		if (this.loteReposicao.getTipoMaterial() != null) {
			this.tipoMaterial = this.loteReposicao.getTipoMaterial();
		}
		if (this.loteReposicao.getIndPontoPedido() != null) {
			this.setIndPontoPedido(DominioSimNao.getInstance(this.loteReposicao.getIndPontoPedido()));
		}
	}
	
	public void limpar(Boolean filtro, Boolean grid) {
		if (filtro) {
			this.indSemClassificacaoABC = Boolean.FALSE;
			this.indEmAf = DominioSimNao.N;
			this.indEmPac = DominioSimNao.N;
			this.indEmSc = DominioSimNao.N;
			this.tipoMaterial = DominioTipoMaterial.E;
			this.loteReposicao = null;
			this.base = null;
			this.grupoMaterial = null;
			this.classificacaoMaterial = null;
			this.listaClassAbc = null;
			this.cobertura = null;
			this.indPontoPedido = null;
			this.centroCustoAplicacao = null;
			this.indProducaoInterna = null;
			this.dataVigencia = null;
			this.modalidade = null;
			this.dataInicio = null;
			this.dataFim = null;
			this.indComLicitacao = null;
			this.frequencia = null;
			this.vlrInicial = null;
			this.vlrFinal = null;
			this.baseReposicao = null;
			this.indItemContrato = null;
			this.dataInicioReposicao = null;
			this.dataFimReposicao = null;
			this.tipoConsumo = null;
			this.mostrarTipoConsumo = Boolean.TRUE;
			this.somentePesquisa = Boolean.TRUE;
			this.fatorSeguranca = null;
			this.nomeLote = null;
			this.titleFaixaValor = "";
			this.descricaoLote = "";
			this.dataGeracao = null;
			this.usuarioGeracao = null;
			this.seqLote = null;
			this.listaInclusaoPontual = null;
			this.mostrarDadosLote = Boolean.FALSE;
		}
		
		if (grid) {
			this.allChecked = new ArrayList<ItemReposicaoMaterialVO>();
			this.listaAtual = new ArrayList<ItemReposicaoMaterialVO>();
			this.listaChecked = new ArrayList<ItemReposicaoMaterialVO>();
			this.nroDesmarcados = new ArrayList<Integer>();
			this.listaAlteracoes = new ArrayList<ItemReposicaoMaterialVO>();
			this.listaCompleta = new ArrayList<ItemReposicaoMaterialVO>();
			
			dataModel.setPesquisaAtiva(Boolean.FALSE);
		}
	}

	public void limparClassificacao() {
		this.setClassificacaoMaterial(null);
	}
	
	public String voltar() {
		return this.voltarParaUrl;
	}

	public String redirecionarEstatisticaConsumo() {
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}

	public String redirecionarSolicitacaoCompraCRUD() {
		return SOLICITACAO_COMPRA_CRUD;
	}

	public String redirecionaNovaSc() throws ApplicationBusinessException {
		solicitacaoCompraController.setNumero(null);
		solicitacaoCompraController.setCentroCusto(null);
		solicitacaoCompraController.setVoltarParaUrl(REPOSICAO_MATERIAL);
		solicitacaoCompraController.inicio();
		
		return SOLICITACAO_COMPRA_CRUD;
	}

	public String consultarCesta() {
		return LOTE_REPOSICAO_MATERIAL_LIST;
	}

	public String redirecionaInclusaoPontual() {
		return INCLUSAO_MANUAL_REPOSICAO_AUTOMATICA;
	}

	public DominioTipoMaterial getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(DominioTipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public DynamicDataModel<ItemReposicaoMaterialVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ItemReposicaoMaterialVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}

	public void setSolicitacaoComprasFacade(
			ISolicitacaoComprasFacade solicitacaoComprasFacade) {
		this.solicitacaoComprasFacade = solicitacaoComprasFacade;
	}

	public List<ItemReposicaoMaterialVO> getListaInclusaoPontual() {
		return listaInclusaoPontual;
	}

	public void setListaInclusaoPontual(
			List<ItemReposicaoMaterialVO> listaInclusaoPontual) {
		this.listaInclusaoPontual = listaInclusaoPontual;
	}

	public CriterioReposicaoMaterialVO getCriterioReposicao() {
		return criterioReposicao;
	}

	public void setCriterioReposicao(
			CriterioReposicaoMaterialVO criterioReposicao) {
		this.criterioReposicao = criterioReposicao;
	}

	public DominioBaseAnaliseReposicao getBase() {
		return base;
	}

	public void setBase(DominioBaseAnaliseReposicao base) {
		this.base = base;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}

	public List<DominioClassifABC> getListaClassAbc() {
		return listaClassAbc;
	}

	public void setListaClassAbc(List<DominioClassifABC> listaClassAbc) {
		this.listaClassAbc = listaClassAbc;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public DominioSimNao getIndProducaoInterna() {
		return indProducaoInterna;
	}

	public void setIndProducaoInterna(DominioSimNao indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}

	public DominioSimNao getIndPontoPedido() {
		return indPontoPedido;
	}

	public void setIndPontoPedido(DominioSimNao indPontoPedido) {
		this.indPontoPedido = indPontoPedido;
	}

	public DominioSimNao getIndComLicitacao() {
		return indComLicitacao;
	}

	public void setIndComLicitacao(DominioSimNao indComLicitacao) {
		this.indComLicitacao = indComLicitacao;
	}

	public DominioSimNao getIndAfContrato() {
		return indAfContrato;
	}

	public void setIndAfContrato(DominioSimNao indAfContrato) {
		this.indAfContrato = indAfContrato;
	}

	public DominioSimNao getIndAfVencida() {
		return indAfVencida;
	}

	public void setIndAfVencida(DominioSimNao indAfVencida) {
		this.indAfVencida = indAfVencida;
	}

	public DominioSimNao getIndItemContrato() {
		return indItemContrato;
	}

	public void setIndItemContrato(DominioSimNao indItemContrato) {
		this.indItemContrato = indItemContrato;
	}

	public Integer getCobertura() {
		return cobertura;
	}

	public void setCobertura(Integer cobertura) {
		this.cobertura = cobertura;
	}

	public Date getDataVigencia() {
		return dataVigencia;
	}

	public void setDataVigencia(Date dataVigencia) {
		this.dataVigencia = dataVigencia;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public BigDecimal getVlrInicial() {
		return vlrInicial;
	}

	public void setVlrInicial(BigDecimal vlrInicial) {
		this.vlrInicial = vlrInicial;
	}

	public BigDecimal getVlrFinal() {
		return vlrFinal;
	}

	public void setVlrFinal(BigDecimal vlrFinal) {
		this.vlrFinal = vlrFinal;
	}

	public Boolean getSomentePesquisa() {
		return somentePesquisa;
	}

	public void setSomentePesquisa(Boolean somentePesquisa) {
		this.somentePesquisa = somentePesquisa;
	}

	public DominioBaseReposicao getBaseReposicao() {
		return baseReposicao;
	}

	public void setBaseReposicao(DominioBaseReposicao baseReposicao) {
		this.baseReposicao = baseReposicao;
	}

	public Date getDataInicioReposicao() {
		return dataInicioReposicao;
	}

	public void setDataInicioReposicao(Date dataInicioReposicao) {
		this.dataInicioReposicao = dataInicioReposicao;
	}

	public Date getDataFimReposicao() {
		return dataFimReposicao;
	}

	public void setDataFimReposicao(Date dataFimReposicao) {
		this.dataFimReposicao = dataFimReposicao;
	}

	public DominioTipoConsumo getTipoConsumo() {
		return tipoConsumo;
	}

	public void setTipoConsumo(DominioTipoConsumo tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}

	public BigDecimal getFatorSeguranca() {
		return fatorSeguranca;
	}

	public void setFatorSeguranca(BigDecimal fatorSeguranca) {
		this.fatorSeguranca = fatorSeguranca;
	}

	public String getNomeLote() {
		return nomeLote;
	}

	public void setNomeLote(String nomeLote) {
		this.nomeLote = nomeLote;
	}

	public Integer getSeqLote() {
		return seqLote;
	}

	public void setSeqLote(Integer seqLote) {
		this.seqLote = seqLote;
	}

	public String getTitleFaixaValor() {
		return titleFaixaValor;
	}

	public void setTitleFaixaValor(String titleFaixaValor) {
		this.titleFaixaValor = titleFaixaValor;
	}

	public String getDescricaoLote() {
		return descricaoLote;
	}

	public void setDescricaoLote(String descricaoLote) {
		this.descricaoLote = descricaoLote;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public String getUsuarioGeracao() {
		return usuarioGeracao;
	}

	public void setUsuarioGeracao(String usuarioGeracao) {
		this.usuarioGeracao = usuarioGeracao;
	}

	public Boolean getMostrarDadosLote() {
		return mostrarDadosLote;
	}

	public void setMostrarDadosLote(Boolean mostrarDadosLote) {
		this.mostrarDadosLote = mostrarDadosLote;
	}

	public Boolean getMostrarTipoConsumo() {
		return mostrarTipoConsumo;
	}

	public void setMostrarTipoConsumo(Boolean mostrarTipoConsumo) {
		this.mostrarTipoConsumo = mostrarTipoConsumo;
	}

	public ScoLoteReposicao getLoteReposicao() {
		return loteReposicao;
	}

	public void setLoteReposicao(ScoLoteReposicao loteReposicao) {
		this.loteReposicao = loteReposicao;
	}

	public Integer getSeqItemExclusao() {
		return seqItemExclusao;
	}

	public void setSeqItemExclusao(Integer seqItemExclusao) {
		this.seqItemExclusao = seqItemExclusao;
	}

	public List<Integer> getNroDesmarcados() {
		return nroDesmarcados;
	}

	public void setNroDesmarcados(List<Integer> nroDesmarcados) {
		this.nroDesmarcados = nroDesmarcados;
	}

	public List<ItemReposicaoMaterialVO> getListaAlteracoes() {
		return listaAlteracoes;
	}

	public void setListaAlteracoes(List<ItemReposicaoMaterialVO> listaAlteracoes) {
		this.listaAlteracoes = listaAlteracoes;
	}

	public List<ItemReposicaoMaterialVO> getListaCompleta() {
		return listaCompleta;
	}

	public void setListaCompleta(List<ItemReposicaoMaterialVO> listaCompleta) {
		this.listaCompleta = listaCompleta;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public boolean isIndSemClassificacaoABC() {
		return indSemClassificacaoABC;
	}

	public void setIndSemClassificacaoABC(boolean indSemClassificacaoABC) {
		this.indSemClassificacaoABC = indSemClassificacaoABC;
	}

	public DominioSimNao getIndEmSc() {
		return indEmSc;
	}

	public void setIndEmSc(DominioSimNao indEmSc) {
		this.indEmSc = indEmSc;
	}

	public DominioSimNao getIndEmPac() {
		return indEmPac;
	}

	public void setIndEmPac(DominioSimNao indEmPac) {
		this.indEmPac = indEmPac;
	}

	public List<ItemReposicaoMaterialVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ItemReposicaoMaterialVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<ItemReposicaoMaterialVO> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<ItemReposicaoMaterialVO> allChecked) {
		this.allChecked = allChecked;
	}

	public List<ItemReposicaoMaterialVO> getListaAtual() {
		return listaAtual;
	}

	public void setListaAtual(List<ItemReposicaoMaterialVO> listaAtual) {
		this.listaAtual = listaAtual;
	}

	public DominioSimNao getIndEmAf() {
		return indEmAf;
	}

	public void setIndEmAf(DominioSimNao indEmAf) {
		this.indEmAf = indEmAf;
	}
}