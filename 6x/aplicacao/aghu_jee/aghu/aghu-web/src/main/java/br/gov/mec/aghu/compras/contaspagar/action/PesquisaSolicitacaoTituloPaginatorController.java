package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.action.PesquisaGeralTitulosController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroSolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloSemLicitacaoVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoTitulo;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da Tela de Pesquisa de Solcitações
 * 
 * @author rafael.silvestre
 */
public class PesquisaSolicitacaoTituloPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -9193900983987649982L;
	
	private static final String PAGINA_CADASTRO_SOLICITACAO_COMPRAS = "compras-solicitacaoCompraCRUD";
	private static final String PAGINA_CADASTRO_SOLICITACAO_SERVICO = "compras-solicitacaoServicoCRUD";
	private static final String PAGINA_PESQUISA_SOLICITACOES_TITULO = "compras-pesquisarSolicitacoesTitulo";
	private static final String PAGINA_CADASTRO_TITULO_SEM_LICITACAO = "compras-gerarTitulosSemLicitacao";
	private static final String PAGINA_PESQUISA_TITULOS = "compras-pesquisaGeralTitulos";
	
	private static final String TITLE_NOME = "Nome: ";
	private static final String TITLE_DESCRICAO = "Descrição: ";
	private static final String TITLE_APLICACAO = "Aplicação: ";
	private static final String TITLE_VERBA_GESTAO = "Verba de Gestão: ";
	private static final String BARRA_N = "\n";
	
	private static final String MSG_INFO_SOLICITACOES_ADICIONADAS_COM_SUCESSO = "MSG_INFO_SOLICITACOES_TITULO_ADICIONADAS_COM_SUCESSO";
	private static final String MSG_INFO_SOLICITACAO_REMOVIDA_COM_SUCESSO = "MSG_INFO_SOLICITACAO_TITULO_REMOVIDA_COM_SUCESSO";
	private static final String MSG_ERRO_QUANTIDADE_PARCELAS_INSUFICIENTE = "MSG_ERRO_QUANTIDADE_PARCELAS_INSUFICIENTE";
	private static final String MSG_ERRO_NENHUMA_SOLICITACAO_ADICIONADA = "MSG_ERRO_NENHUMA_SOLICITACAO_TITULO_ADICIONADA";
	private static final String MSG_ERRO_NENHUMA_SOLICITACAO_SELECIONADA = "MSG_ERRO_NENHUMA_SOLICITACAO_TITULO_SELECIONADA";
	private static final String MSG_ERRO_PERIODO_INCORRETO = "MSG_ERRO_PERIODO_INCORRETO_SOLICITACOES_TITULO";
	
	private static final String MSG_INFO_TITULO_GERADO_COM_SUCESSO = "MSG_INFO_TITULO_GERADO_COM_SUCESSO";
	private static final String MSG_INFO_TITULOS_GERADOS_COM_SUCESSO = "MSG_INFO_TITULOS_GERADOS_COM_SUCESSO";
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
    private IParametroFacade parametroFacade;
	
	@Inject @Paginator
	private DynamicDataModel<SolicitacaoTituloVO> dataModel;
	
	@Inject
	private PesquisaGeralTitulosController pesquisaGeralTitulosController;
	
	private Long count;
	private List<SolicitacaoTituloVO> listaPaginada;
	
	private List<SolicitacaoTituloVO> listaCompleta;
	private List<SolicitacaoTituloVO> listaAdicionados;
	private List<SolicitacaoTituloVO> listaSelecionados;
	
	private FiltroSolicitacaoTituloVO filtro;
	private FiltroSolicitacaoTituloVO filtroPesquisa;
	private TituloSemLicitacaoVO titulo;
	
	private SolicitacaoTituloVO voSelecionado;
	private SolicitacaoTituloVO voAdicionado;
	
	private Short pontoParada;

	private boolean allChecked;
	
	private boolean exibirBotaoVoltar;
	
	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}
	
	/**
	 * Metodo invocado ao carregar a tela.
	 */
	public void iniciar() {
		try {
			this.pontoParada = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_PONTO_PARADA_GER_TIT);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if (this.filtro == null) {
			this.filtro = new FiltroSolicitacaoTituloVO();
			this.filtro.setTipo(DominioTipoSolicitacaoTitulo.SS);
		}
	}
	
	/**
	 * Ação do botão Pesquisar (Tela de Pesquisa de Solicitações)
	 */
	public void pesquisar() {
		if (permitirPesquisar()) {
			this.allChecked = false;
			this.voSelecionado = null;
			this.count = null;
			this.listaPaginada = new ArrayList<SolicitacaoTituloVO>();
			this.listaCompleta = new ArrayList<SolicitacaoTituloVO>();
			this.listaSelecionados = new ArrayList<SolicitacaoTituloVO>();
			this.carregarFiltrosPesquisa();
			this.dataModel.reiniciarPaginator();
		}
	}
	
	private void carregarFiltrosPesquisa() {
		this.filtroPesquisa = new FiltroSolicitacaoTituloVO();
		this.filtroPesquisa.setDataGeracaoFinal(this.filtro.getDataGeracaoFinal());
		this.filtroPesquisa.setDataGeracaoInicial(this.filtro.getDataGeracaoInicial());
		this.filtroPesquisa.setGrupoMaterial(this.filtro.getGrupoMaterial());
		this.filtroPesquisa.setGrupoNaturezaDespesa(this.filtro.getGrupoNaturezaDespesa());
		this.filtroPesquisa.setGrupoServico(this.filtro.getGrupoServico());
		this.filtroPesquisa.setMaterial(this.filtro.getMaterial());
		this.filtroPesquisa.setNaturezaDespesa(this.filtro.getNaturezaDespesa());
		this.filtroPesquisa.setServico(this.filtro.getServico());
		this.filtroPesquisa.setSolicitacao(this.filtro.getSolicitacao());
		this.filtroPesquisa.setTipo(this.filtro.getTipo());
		this.filtroPesquisa.setVerbaGestao(this.filtro.getVerbaGestao());
	}

	private boolean permitirPesquisar() {
		boolean permitir = true;
		if (this.filtro != null && this.filtro.getDataGeracaoInicial() != null && this.filtro.getDataGeracaoFinal() != null) {
			if (!CoreUtil.isMenorOuIgualDatas(this.filtro.getDataGeracaoInicial(), this.filtro.getDataGeracaoFinal())) {
				apresentarMsgNegocio(Severity.ERROR, MSG_ERRO_PERIODO_INCORRETO);
				permitir = false;
			}
		}
		return permitir;
	}
	
	/**
	 * Ação do botão Limpar (Tela de Pesquisa de Solicitações)
	 */
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.allChecked = false;
		this.filtro = new FiltroSolicitacaoTituloVO();
		this.filtro.setTipo(DominioTipoSolicitacaoTitulo.SS);
		this.count = null;
		this.listaPaginada = new ArrayList<SolicitacaoTituloVO>();
		this.listaCompleta = new ArrayList<SolicitacaoTituloVO>();
		this.listaSelecionados = new ArrayList<SolicitacaoTituloVO>();
		this.dataModel.limparPesquisa();
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 */
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	/**
	 * Ação do botão Adicionar ao Titulo Sem Licitação (Tela de Pesquisa de Solicitações)
	 */
	public String adicionarTituloSemLicitacao() {
		if (!this.exibirBotaoVoltar) {
			this.titulo = new TituloSemLicitacaoVO();
		}
		if (this.listaSelecionados != null && !this.listaSelecionados.isEmpty()) {
			if (this.listaAdicionados == null) {
				this.listaAdicionados = new ArrayList<SolicitacaoTituloVO>();
			}
			if (this.listaAdicionados.isEmpty()) {
				this.listaAdicionados.addAll(listaSelecionados);
			} else {
				for (SolicitacaoTituloVO solicitacao : this.listaSelecionados) {
					if (!possuiSolicitacaoSelecionada(solicitacao, this.listaAdicionados)) {
						this.listaAdicionados.add(solicitacao);
					}
				}
			}
			apresentarMsgNegocio(Severity.INFO, MSG_INFO_SOLICITACOES_ADICIONADAS_COM_SUCESSO);
		} else {
			apresentarMsgNegocio(Severity.ERROR, MSG_ERRO_NENHUMA_SOLICITACAO_SELECIONADA);
			return null;
		}
		return PAGINA_CADASTRO_TITULO_SEM_LICITACAO;
	}
	
	private boolean possuiSolicitacaoSelecionada(SolicitacaoTituloVO item, List<SolicitacaoTituloVO> lista) {
		for (SolicitacaoTituloVO solicitacao : lista) {
			if (solicitacao.getSolicitacao().equals(item.getSolicitacao())) {
				return true;
			}
		} 
		return false;
	}

	/**
	 * Ação do botão Voltar (Tela de Pesquisa de Solicitações)
	 */
	public String voltar() {
		return PAGINA_CADASTRO_TITULO_SEM_LICITACAO;
	}
	
	/**
	 * Ação do botão Adicionar (Tela de Geração de Titulos)
	 */
	public String adicionar() {
		this.exibirBotaoVoltar = true;
		this.allChecked = false;
		this.listaSelecionados = new ArrayList<SolicitacaoTituloVO>();
		return PAGINA_PESQUISA_SOLICITACOES_TITULO;
	}
	
	/**
	 * Ação do botão Gerar Titulos (Tela de Geração de Titulos)
	 */
	public String gerarTitulos() {
		if (this.titulo != null && this.listaAdicionados != null && permitirGerarTitulos()) {
			try {
				List<FcpTitulo> listaTitulos = this.comprasFacade.gerarTitulosSemLicitacao(this.titulo, this.listaAdicionados);
				this.pesquisaGeralTitulosController.setListaTitulos(obterListaConsultaGeralTitulo(listaTitulos));
				this.pesquisaGeralTitulosController.setExibeGrid(true);
				return PAGINA_PESQUISA_TITULOS;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}

	private List<ConsultaGeralTituloVO> obterListaConsultaGeralTitulo(List<FcpTitulo> listaTitulos) {
		FiltroConsultaGeralTituloVO filtro = new FiltroConsultaGeralTituloVO();
		List<Integer> listaTtlSeq = new ArrayList<Integer>(); 
		if (listaTitulos != null && !listaTitulos.isEmpty()) {
			for (FcpTitulo titulo : listaTitulos) {
				if (titulo.getSeq() != null) {
					listaTtlSeq.add(titulo.getSeq());
				}
			}
		}
		if (!listaTtlSeq.isEmpty()) {
			if (listaTtlSeq.size() > 1) {
				apresentarMsgNegocio(Severity.INFO, MSG_INFO_TITULOS_GERADOS_COM_SUCESSO);
			} else {
				apresentarMsgNegocio(Severity.INFO, MSG_INFO_TITULO_GERADO_COM_SUCESSO);
			}
			filtro.setArrayTtlSeq((Integer[]) listaTtlSeq.toArray(new Integer[listaTtlSeq.size()]));
			return this.comprasFacade.consultaGeralTitulos(filtro);
		}
		return null;
	}

	private boolean permitirGerarTitulos() {
		boolean permissao = true;
		if (this.titulo.getQtdeParcelas() <= 0 ) {
			apresentarMsgNegocio(Severity.ERROR, MSG_ERRO_QUANTIDADE_PARCELAS_INSUFICIENTE);
			permissao = false;
		}
		if (this.listaAdicionados.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, MSG_ERRO_NENHUMA_SOLICITACAO_ADICIONADA);
			permissao = false;
		}
		return permissao;
	}
	
	/**
	 * Ação do botão Cancelar (Tela de Geração de Titulos)
	 */
	public String cancelar() {
		this.titulo = new TituloSemLicitacaoVO();
		this.listaAdicionados = new ArrayList<SolicitacaoTituloVO>();
		this.exibirBotaoVoltar = false;
		this.allChecked = false;
		this.listaSelecionados = new ArrayList<SolicitacaoTituloVO>();
		return PAGINA_PESQUISA_SOLICITACOES_TITULO;
	}
	
	/**
	 * Ação do link Remover Solicitação (Tela de Geração de Titulos)
	 */
	public void removerSolicitacao() {
		if (voAdicionado != null) {
			this.listaAdicionados.remove(voAdicionado);
			apresentarMsgNegocio(Severity.INFO, MSG_INFO_SOLICITACAO_REMOVIDA_COM_SUCESSO);
		}
	}
	
	@Override
	public List<SolicitacaoTituloVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.listaPaginada = this.comprasFacade.recuperarListaPaginadaSolicitacaoTitulo(this.filtroPesquisa, this.pontoParada, firstResult, maxResult, orderProperty, asc);
		if (listaSelecionados != null && !listaSelecionados.isEmpty()) {
			alterarSelecaoItemListaVO();
		}
		return this.listaPaginada;
	}
	
	@Override
	public Long recuperarCount() {
		this.listaCompleta = this.comprasFacade.recuperarListaCompletaSolicitacaoTitulo(this.filtroPesquisa, this.pontoParada);
		if (listaCompleta != null && !listaCompleta.isEmpty()) {
			this.count = Long.valueOf(listaCompleta.size());
		} else {
			this.count = 0l;
		}
		return this.count;
	}
	
	/**
	 * Atualiza a seleção do item na lista.
	 */
	private void alterarSelecaoItemListaVO() {
		if (this.listaPaginada != null) {
			for (SolicitacaoTituloVO vo : this.listaPaginada) {
				if (this.listaSelecionados.contains(vo)) {
					vo.setSelecionado(true);
				} else {
					vo.setSelecionado(false);
				}
			}
		}
	}
	
	/**
	 * Método invocado ao marcar/desmarcar o checkbox do cabeçalho da grid.
	 */
	public void checkAll() {
		this.listaSelecionados = new ArrayList<SolicitacaoTituloVO>();
		if (this.listaCompleta != null) {
			for (SolicitacaoTituloVO vo : this.listaCompleta) {
				if (this.allChecked) {
					this.listaSelecionados.add(vo);
					vo.setSelecionado(true);
				} else {
					vo.setSelecionado(false);
				}
			}
		}
	}
	
	/**
	 * Método invocado ao marcar/desmarcar o checkbox de um item da grid.
	 */
	public void checkItem(SolicitacaoTituloVO item) {
		if (possuiSolicitacaoSelecionada(item, this.listaSelecionados)) {
			this.listaSelecionados.remove(item);
		} else {
			this.listaSelecionados.add(item);
		}
		this.allChecked = (this.listaSelecionados.size() == this.count.intValue());
		this.voSelecionado = item;
	}
	
	/**
	 * Reseta os valores da suggestion box de Natureza de Despesa
	 */
	public void limparSuggestionNaturezaDespesa() {
		this.filtro.setNaturezaDespesa(null);
	}
	
	/**
	 * Metodo invocado ao mudar seleção no combobox do Tipo Solicitação
	 */
	public void selecionarServicoMaterial() {
		if (this.filtro != null && this.filtro.getTipo() != null) {
			switch (this.filtro.getTipo()) {
			case SS:
				resetarSuggestionBoxServico();
				break;
			case SC:
				resetarSuggestionBoxMaterial();
				break;
			default:
				break;
			}
		}
	}
	
	private void resetarSuggestionBoxServico() {
		if (this.filtro != null) {
			this.filtro.setSolicitacao(null);
			this.filtro.setGrupoServico(null);
			this.filtro.setServico(null);
		}
	}

	private void resetarSuggestionBoxMaterial() {
		if (this.filtro != null) {
			this.filtro.setSolicitacao(null);
			this.filtro.setGrupoMaterial(null);
			this.filtro.setMaterial(null);
		}
	}
	
	/**
	 * Metodo que retorna o title para o suggestion box de Material 
	 */
	public String getResultTitleMaterial() {
		if (filtro != null && filtro.getMaterial() != null) {
			StringBuilder sb = new StringBuilder(60);
			sb.append(TITLE_NOME).append(filtro.getMaterial().getNome());
			if (StringUtils.isNotBlank(filtro.getMaterial().getDescricao())) {
				sb.append(BARRA_N).append(TITLE_DESCRICAO).append(filtro.getMaterial().getDescricao());
			}
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * Metodo que retorna o title para o link de Solicitação na grid
	 */
	public String getTitleLinkSolicitacao(SolicitacaoTituloVO item) {
		if (item != null) {
			StringBuilder sb = new StringBuilder(60);
			sb.append(TITLE_DESCRICAO);
			if (StringUtils.isNotBlank(item.getDescricao())) {
				sb.append(item.getDescricao());
			}
			sb.append(BARRA_N).append(TITLE_APLICACAO);
			if (StringUtils.isNotBlank(item.getAplicacao())) {
				sb.append(item.getAplicacao());
			}
			sb.append(BARRA_N).append(TITLE_VERBA_GESTAO);
			if (StringUtils.isNotBlank(item.getVerbaGestao())) {
				sb.append(item.getVerbaGestao());
			}
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * Encaminha para a pagina de cadastro de solicitação de compras.
	 */
	public String redirecionarSolicitacaoCompra() {
		return PAGINA_CADASTRO_SOLICITACAO_COMPRAS;
	}
	
	/**
	 * Encaminha para a pagina de cadastro de solicitação de serviço.
	 */
	public String redirecionarSolicitacaoServico() {
		return PAGINA_CADASTRO_SOLICITACAO_SERVICO;
	}
	
	/**
	 * Obtem descrição truncada.
	 */
	public String obterDescricaoTruncada(String descricao, Integer tamanho) {
		if (descricao.length() > tamanho) {
			return StringUtils.substring(descricao, 0, tamanho).concat("...");
		}
		return descricao;
	}
	
	/**
	 * CNPJ/CPF formatado
	 */
	public static String getCpfCnpjFormatado(ScoFornecedor item) {
		if (item.getCpf() == null) {
			if (item.getCgc() == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(item.getCgc());
		}
		return CoreUtil.formataCPF(item.getCpf());
	}
	
	/**
	 * Obtem lista para o combobox de Modalidade Empenho
	 */
	public DominioModalidadeEmpenho[] listarDominioModalidadeEmpenho() {
		List<DominioModalidadeEmpenho> listaModalidadeEmpenho = new LinkedList<DominioModalidadeEmpenho>(Arrays.asList(DominioModalidadeEmpenho.values()));
		return listaModalidadeEmpenho.toArray(new DominioModalidadeEmpenho[listaModalidadeEmpenho.size()]);
	}
	
	/**
	 * Suggestion Box de Grupo Natureza de Despesa
	 */
	public List<FsoGrupoNaturezaDespesa> listarGrupoNaturezaDespesa(String filter) {
		return this.returnSGWithCount(this.cadastrosBasicosOrcamentoFacade.obterListaGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(filter),
									  this.cadastrosBasicosOrcamentoFacade.obterCountGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(filter));
	}
	
	/**
	 * Suggestion Box de Natureza de Despesa
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesa(String filter) {
		return this.returnSGWithCount(this.cadastrosBasicosOrcamentoFacade.obterListaNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(this.filtro.getGrupoNaturezaDespesa(), filter),
									  this.cadastrosBasicosOrcamentoFacade.obterCountNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(this.filtro.getGrupoNaturezaDespesa(), filter));
	}
	
	/**
	 * Suggestion Box de Grupo Serviço
	 */
	public List<ScoGrupoServico> listarGrupoServico(String filter) {
		return this.returnSGWithCount(this.comprasFacade.obterListaGrupoServicoPorCodigoOuDescricao(filter),
				  					  this.comprasFacade.obterCountGrupoServicoPorCodigoOuDescricao(filter));
	}

	/**
	 * Suggestion Box de Grupo Material
	 */
	public List<ScoGrupoMaterial> listarGrupoMaterial(String filter) {
		return this.returnSGWithCount(this.comprasFacade.obterListaGrupoMaterialPorCodigoOuDescricao(filter),
									  this.comprasFacade.obterCountGrupoMaterialPorCodigoOuDescricao(filter));
	}
	
	/**
	 * Suggestion Box de Serviço
	 */
	public List<ScoServico> listarServico(String filter) {
		return this.returnSGWithCount(this.comprasFacade.obterListaServicoAtivosPorCodigoOuNome(filter),
									  this.comprasFacade.obterCountServicoAtivosPorCodigoOuNome(filter));
	}
	
	/**
	 * Suggestion Box de Material
	 */
	public List<ScoMaterial> listarMaterial(String filter) {
		return this.returnSGWithCount(this.comprasFacade.obterListaMaterialAtivosPorCodigoNomeOuDescricao(filter),
									  this.comprasFacade.obterCountMaterialAtivosPorCodigoNomeOuDescricao(filter));
	}
	
	/**
	 * Suggestion Box de Verba de Gestão
	 */
	public List<FsoVerbaGestao> listarVerbaGestao(String filter) {
		return this.returnSGWithCount(this.cadastrosBasicosOrcamentoFacade.obterListaVerbaGestaoAtivosPorSeqOuDescricao(filter),
				  					  this.cadastrosBasicosOrcamentoFacade.obterCountVerbaGestaoAtivosPorSeqOuDescricao(filter));
	}
	
	/**
	 * Suggestion Box de Credor
	 */
	public List<ScoFornecedor> listarFornecedor(String filter) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(filter, 0, 100, null, true),
									  this.comprasFacade.listarFornecedoresAtivosCount(filter));
	}
	
	/**
	 * Suggestion Box de Classificação
	 */
	public List<FcpClassificacaoTitulo> listarClassificacao(String filter) {
		return this.returnSGWithCount(this.comprasFacade.obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(filter),
									  this.comprasFacade.obterCountClassificacaoTituloAtivosPorCodigoOuDescricao(filter));
	}

	/**
	 * 
	 * GETTERs and SETTERs
	 * 
	 */
	public DynamicDataModel<SolicitacaoTituloVO> getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DynamicDataModel<SolicitacaoTituloVO> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public List<SolicitacaoTituloVO> getListaSelecionados() {
		return listaSelecionados;
	}

	public void setListaSelecionados(List<SolicitacaoTituloVO> listaSelecionados) {
		this.listaSelecionados = listaSelecionados;
	}

	public List<SolicitacaoTituloVO> getListaCompleta() {
		return listaCompleta;
	}

	public void setListaCompleta(List<SolicitacaoTituloVO> listaCompleta) {
		this.listaCompleta = listaCompleta;
	}

	public List<SolicitacaoTituloVO> getListaAdicionados() {
		return listaAdicionados;
	}

	public void setListaAdicionados(List<SolicitacaoTituloVO> listaAdicionados) {
		this.listaAdicionados = listaAdicionados;
	}

	public FiltroSolicitacaoTituloVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroSolicitacaoTituloVO filtro) {
		this.filtro = filtro;
	}

	public Short getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(Short pontoParada) {
		this.pontoParada = pontoParada;
	}

	public TituloSemLicitacaoVO getTitulo() {
		return titulo;
	}

	public void setTitulo(TituloSemLicitacaoVO titulo) {
		this.titulo = titulo;
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public List<SolicitacaoTituloVO> getListaPaginada() {
		return listaPaginada;
	}

	public void setListaPaginada(List<SolicitacaoTituloVO> listaPaginada) {
		this.listaPaginada = listaPaginada;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public SolicitacaoTituloVO getVoSelecionado() {
		return voSelecionado;
	}

	public void setVoSelecionado(SolicitacaoTituloVO voSelecionado) {
		this.voSelecionado = voSelecionado;
	}

	public SolicitacaoTituloVO getVoAdicionado() {
		return voAdicionado;
	}

	public void setVoAdicionado(SolicitacaoTituloVO voAdicionado) {
		this.voAdicionado = voAdicionado;
	}

	public FiltroSolicitacaoTituloVO getFiltroPesquisa() {
		return filtroPesquisa;
	}

	public void setFiltroPesquisa(FiltroSolicitacaoTituloVO filtroPesquisa) {
		this.filtroPesquisa = filtroPesquisa;
	}
}
