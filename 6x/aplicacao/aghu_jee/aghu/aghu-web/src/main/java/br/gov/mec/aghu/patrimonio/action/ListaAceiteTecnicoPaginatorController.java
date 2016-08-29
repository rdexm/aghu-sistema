package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.cadastroapoio.AnexosArquivosListPaginatorController;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.FiltroAceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da tela ListarAceitesTecnicos
 * 
 * @author rafael.silvestre
 */
public class ListaAceiteTecnicoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8048301995011668076L;
	
	private static final String ACAO_EXECUTAR = "executar";
	private static final String PESQUISAR = "pesquisar";
	private static final String MENSAGEM_TEC_AREA_TEC_DIFERENTES = "MENSAGEM_TEC_AREA_TEC_DIFERENTES";
	private static final String PAGINA_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	private static final String PAGINA_DESIGNAR_TECNICO_RESPONSAVEL = "patrimonio-designarTecnicoResponsavel";
	private static final String PERMISSAO_LISTAR_ACEITES_TECNICOS_CHEFIA = "listarAceitesTecnicosChefia";
	private static final String PERMISSAO_LISTAR_ACEITES_TECNICOS_TEC_ESPECIALISTA = "listarAceitesTecnicosTecEspecialista";
	private static final String PERMISSAO_LISTAR_ACEITE_TECNICO_CHEFE_AREA_TEC = "listarAceiteTecnicoChefeAreaTec";
	private static final String PERMISSAO_CHEFE_AREA_TECNICA_AVALIACAO = "chefeAreaTecnicaAvaliacao";
	private static final String PERMISSAO_CHEFIA_PATRIMONIO_AREA_TECNICA_AVALIACAO = "chefiaPatrimonioAreaTecnicaAvaliacao";
	private static final String PERMISSAO_DESIGNAR_TECNICO_RESPONSAVEL_AVALIACAO = "designarTecnicoResponsavelAvaliacao";
	private static final String PERMISSAO_VISUALIZAR_TECNICO_RESPONSAVEL_AVALIACAO = "visualizarTecnicoResponsavelAvaliacao";
	private static final String PERMISSAO_ALTERAR_TECNICO_RESPONSAVEL_AVALIACAO = "alterarTecnicoResponsavelAvaliacao";
	private static final String PERMISSAO_REGISTRAR_RETIRADA_BEM_PERMANENTE = "registrarRetiraBemPermanente";
	private static final String ATENDER_ACEITE_TECNICO = "atenderAceiteTecnico";
	private static final String PAGINA_CONSULTAR_NOTIFICACOES_TECNICAS = "listaNotificacaoTecnica";
	private static final String PAGINA_CADASTRAR_NOTIFICACOES_TECNICAS = "cadastroNotificacaoTecnica";
	private static final String LISTAR_NOTIFICACOES_TECNICAS = "listaNotificacaoTecnica";
	private static final String CADASTRAR_NOTIFICACOES_TECNICAS = "cadastroNotificacaoTecnica";
	private static final String PERMISSAO_REGISTRAR_DEVOLUCAO="registrarDevolucaoBemPermanente";
	private static final String PERMISSAO_LISTAR_ACEITES_TECNICOS_TEC_CHEFIA_CC = "listarAceitesTecnicosChefiaCC";
	private static final String PAGE_PESQUISAR_ACEITE_TECNICO = "patrimonio-registrarAceiteTecnicoList";
	private static final String PERMISSAO_PESQUISAR_ACEITE_TECNICO="pesquisarAceiteTecnico";
	private static final String EM_AVALIACAO_TECNICA = "Em Avaliação Técnica";
	private static final String ANEXOS_ARQUIVOS_LIST = "patrimonio-anexosArquivosList";
	private static final String MENSAGEM_SELECAO_UNICA_ITEM_RECEBIMENTO = "MENSAGEM_SELECAO_UNICA_ITEM_RECEBIMENTO";
	private static final String MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO = "MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO";
	private static final String ITEM_RECEBIMENTO = "patrimonio-itemRecebimento";
	
	@EJB
	private IPermissionService permissionService;

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EncaminharSolicitacaoAnaliseTecnicaController encaminharSolicitacaoAnaliseTecnicaController;

	@Inject
	private ItemRecebimentoController itemRecebimentoController;
	
	@Inject
	private DesignaTecnicoResponsavelController designaTecnicoResponsavelController;
	
	@Inject
	private CadastroNotificacaoTecnicaController cadastroNotificacaoTecnicaController;
	
	@Inject
	private ListaNotificacaoTecnicaController listaNotificacaoTecnicaController;
	
	@Inject
	private RegistrarAceiteTecnicoPaginatorController registrarAceiteTecnicoPaginatorController;
	
	@Inject
	private AnexosArquivosListPaginatorController anexosArquivosListPaginatorController;
	
	@Inject 
	@Paginator
	private DynamicDataModel<AceiteTecnicoParaSerRealizadoVO> dataModel;
	
	@Inject
	private RelatorioNotifTecRecebimentoProvisorioController relatorioNotifTecRecebimentoProvisorioController;
	
	private FiltroAceiteTecnicoParaSerRealizadoVO filtro;
	private AceiteTecnicoParaSerRealizadoVO aceiteSelecionado;
	private AceiteTecnicoParaSerRealizadoVO visualizaAceiteSelecionado;
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoVO;
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosVO;
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosAuxVO;
	private boolean allChecked;
	private boolean pm01;
	private boolean pm02;
	private boolean pm03;
	private boolean permissaoChefe;
	private boolean permissaoChefiaPatrimonio;
	private boolean permissaoDesignarTecnico;
	private boolean permissaoVisualizarTecnico;
	private boolean permissaoAlterarTecnico;
	private boolean possuiValorNumeroAf;
	private boolean permissaoAtenderAceiteTecnico;
	private boolean permissaoRetiradaBemPermanente;
	private Boolean botaoNotasTecnicas;
	private PtmItemRecebProvisorios irpSeq;
	private List<PtmNotificacaoTecnica> listaNotificacoesTecnicas;
	private Boolean permissaoConsultarNotificacoesTecnicas;
	private Boolean permissaoCadastrarNotificacoesTecnicas;
	private boolean permissaoRegistrarDevolucaoBemPermanente;
	private boolean permissaoListarAceitesTecnicosChefiaCC;
	private boolean permissaoPesquisarAceiteTecnico;
	private boolean habilitaBotao;
	private boolean botaoEncaminharAceiteTec;
	private boolean botaoVisualizarNotaTec;
	
	@PostConstruct
	protected void inicializar() {
		begin(conversation);
		botaoNotasTecnicas = true;
	}
	
	/*** Metodo invocado ao carregar a tela.*/
	public void iniciar() {
		resetarVariaveis();
		carregarPermissoes();
		preencherFiltro();
		habilitaBotao = false;
		botaoEncaminharAceiteTec = false;
		botaoNotasTecnicas = habilitarBotaoNotasTecnicas();
	}

	/*** Inicializa variaveis da tela.*/
	private void resetarVariaveis() {
		if (this.filtro == null) {
			this.filtro = new FiltroAceiteTecnicoParaSerRealizadoVO();
		}
		if (this.listaAceiteTecnicoVO == null) {
			this.listaAceiteTecnicoVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		}
		this.allChecked = false;
		this.listaAceiteTecnicoSelecionadosVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		this.listaAceiteTecnicoSelecionadosAuxVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
	}
	
	/**
	 * Verifica as permissões que o usuario possui.
	 */
	private void carregarPermissoes() {
		this.pm01 = usuarioTemPermissao(PERMISSAO_LISTAR_ACEITES_TECNICOS_CHEFIA);
		this.pm02 = usuarioTemPermissao(PERMISSAO_LISTAR_ACEITES_TECNICOS_TEC_ESPECIALISTA);
		this.pm03 = usuarioTemPermissao(PERMISSAO_LISTAR_ACEITE_TECNICO_CHEFE_AREA_TEC);
		this.permissaoChefe = usuarioTemPermissao(PERMISSAO_CHEFE_AREA_TECNICA_AVALIACAO);
		this.permissaoChefiaPatrimonio = usuarioTemPermissao(PERMISSAO_CHEFIA_PATRIMONIO_AREA_TECNICA_AVALIACAO);
		this.permissaoDesignarTecnico = usuarioTemPermissao(PERMISSAO_DESIGNAR_TECNICO_RESPONSAVEL_AVALIACAO);
		this.permissaoVisualizarTecnico = usuarioTemPermissao(PERMISSAO_VISUALIZAR_TECNICO_RESPONSAVEL_AVALIACAO);
		this.permissaoAlterarTecnico = usuarioTemPermissao(PERMISSAO_ALTERAR_TECNICO_RESPONSAVEL_AVALIACAO);
		this.permissaoAtenderAceiteTecnico = usuarioTemPermissao(ATENDER_ACEITE_TECNICO);
		this.permissaoRetiradaBemPermanente = usuarioTemPermissao(PERMISSAO_REGISTRAR_RETIRADA_BEM_PERMANENTE);
		this.permissaoConsultarNotificacoesTecnicas = usuarioTemPermissao(LISTAR_NOTIFICACOES_TECNICAS);
		this.permissaoCadastrarNotificacoesTecnicas = usuarioTemPermissao(CADASTRAR_NOTIFICACOES_TECNICAS);
		this.permissaoRegistrarDevolucaoBemPermanente = usuarioTemPermissao(PERMISSAO_REGISTRAR_DEVOLUCAO);
		this.permissaoListarAceitesTecnicosChefiaCC = usuarioTemPermissao(PERMISSAO_LISTAR_ACEITES_TECNICOS_TEC_CHEFIA_CC);
		this.permissaoPesquisarAceiteTecnico = this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_PESQUISAR_ACEITE_TECNICO, PESQUISAR);
	}
	
	/*** Invoca metodo da arquitetura para validar permissão do usuario.*/
	private boolean usuarioTemPermissao(String componente) {
		return this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), componente, ACAO_EXECUTAR);
	}
	
	/*** Carrega o filtro com base nas permissões retornadas. */
	private void preencherFiltro() {
		if (this.dataModel == null || !this.dataModel.getPesquisaAtiva()) {
			if (this.pm02 && !permissaoListarAceitesTecnicosChefiaCC) {
				this.filtro.setResponsavelTecnico(this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(
						obterLoginUsuarioLogado()));
			}
			if (this.pm03) {
				this.filtro.setAreaTecnicaAvaliacao(this.patrimonioFacade.obterAreaTecnicaPorServidor(
						this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())));
			}
		}
	}
	
	/*** Habilita campo Complemento AF quando informado valor no campo Numero AF.*/
	public void habilitarCampoComplementoAf() {
		if (this.filtro != null && this.filtro.getNumeroAf() != null) {
			this.possuiValorNumeroAf = true;
		} else {
			this.possuiValorNumeroAf = false;
		}
	}
	
	/*** Ação do botão Pesquisar */
	public void pesquisar() {
		resetarVariaveis();
		this.dataModel.reiniciarPaginator();
	}
	
	public boolean desabilitaSuggestionTecnicoResponsavel(){
		if(pm02 && !pm03 && !pm01){
			return true;
		}
		return false;
	}
	
	/*** Ação do botão Limpar*/
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.limparPesquisa();
		resetarVariaveis();
		this.possuiValorNumeroAf = false;
		this.filtro.setResponsavelTecnico(null);
		this.filtro.setNumeroRecebimento(null);
		this.filtro.setAreaTecnicaAvaliacao(null);
		preencherFiltro();
		this.filtro.setNumeroAf(null);
		this.filtro.setComplementoAf(null);
		this.filtro.setMaterial(null);
		this.filtro.setStatus(null);
		this.filtro.setFornecedor(null);
		
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
	 * Suggestion Box de Material.
	 */
	public List<ScoMaterial> listarMaterial(String parametro) {
		return returnSGWithCount(this.comprasFacade.obterMateriaisPorCodigoOuDescricao(parametro), 
								 this.comprasFacade.obterMateriaisPorCodigoOuDescricaoCount(parametro));
	}
	
	/**
	 * Suggestion Box de Responsavel Tecnico.
	 */
	public List<RapServidores> listarResponsavelTecnico(String parametro) {
		return returnSGWithCount(this.registroColaboradorFacade.obterServidorPorMatriculaVinculoOuNome(parametro),
								 this.registroColaboradorFacade.obterServidorPorMatriculaVinculoOuNomeCount(parametro));
	}

	/**
	 * Suggestion Box de Area Tecnica de Avaliação.
	 */
	public List<PtmAreaTecAvaliacao> listarAreaTecnicaAvaliacao(String parametro) {
		if(!pm02 && pm03){
			return returnSGWithCount(this.patrimonioFacade.pesquisarListaAreaTecnicaSuggestionBox(parametro, 
					this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())),
					this.patrimonioFacade.pesquisarListaAreaTecnicaSuggestionBoxCount(parametro, 
							this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())));
		}else if(pm02 && !pm03){
			return returnSGWithCount(this.patrimonioFacade.pesquisarListaAreaTecnicaPorResponsavel(parametro, 
					this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())),
					this.patrimonioFacade.pesquisarListaAreaTecnicaPorResponsavelCount(parametro, 
							this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())));
		}else if(pm02 && pm03){
			return returnSGWithCount(this.patrimonioFacade.pesquisarAreaTecnicaPorPermissoes(parametro, 
					this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())),
					this.patrimonioFacade.pesquisarAreaTecnicaPorPermissoesCount(parametro, 
							this.registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(obterLoginUsuarioLogado())));
		}else{
			return returnSGWithCount(this.patrimonioFacade.obterAreaTecAvaliacaoPorCodigoOuNome(parametro),
					this.patrimonioFacade.obterAreaTecAvaliacaoPorCodigoOuNomeCount(parametro));
		}
	}
	
	/**
	 * Suggestion Box de Fornecedor.
	 */
	public List<FornecedorVO> listarFornecedor(String parametro) {
		return returnSGWithCount(this.comprasFacade.pesquisarFornecedoresAtivosVO(parametro, 0, 100, null, false),
				this.comprasFacade.listarFornecedoresAtivosCount(parametro));
	}
	
	/**
	 * Obtem o nome da Area Tecnica de Avaliação. 
	 */
	public String obterNomeAreaTecAvaliacao(Integer areaTecAvaliacao, Integer tamanho) {
		String retorno = "";
		if (areaTecAvaliacao != null) {
			PtmAreaTecAvaliacao ata = this.patrimonioFacade.obterAreaTecAvaliacaoPorChavePrimaria(areaTecAvaliacao);
			if (ata != null && ata.getNomeAreaTecAvaliacao() != null) {
				retorno = ata.getNomeAreaTecAvaliacao();
				if (retorno.length() > tamanho) {
					retorno = StringUtils.abbreviate(retorno, tamanho);
				}
			}
		}
		return retorno;
	}
	
	/**
	 * Obtem o nome do Tecnico Responsavel. 
	 */
	public String obterNomeTecnicoResponsavel(Integer tecnicoResponsavel, Short codigoTecnicoResponsavel, Integer tamanho) {
		String retorno = "";
		if (tecnicoResponsavel != null) {
			retorno = this.registroColaboradorFacade.buscarNomeResponsavelPorMatricula(codigoTecnicoResponsavel, tecnicoResponsavel);
			if (retorno.length() > tamanho) {
				retorno = StringUtils.abbreviate(retorno, tamanho);
			}
		}
		return retorno;
	}
	
	/**
	 * Obtem descrição truncada para o status do aceite técnico.
	 */
	public String obterDescricaoDominioStatusTruncado(Integer value, Integer tamanho) {
		String descricao = obterDescricaoDominioStatus(value);
		if (descricao.length() > tamanho) {
			descricao = StringUtils.abbreviate(descricao, tamanho);
		}
		return descricao;
	}
	
	/**
	 * Obtem descrição truncada para o fornecedor.
	 */
	public String obterFornecedorTruncado(String descricao, Integer tamanho) {
		if (descricao.length() > tamanho) {
			descricao = StringUtils.abbreviate(descricao, tamanho);
		}
		return descricao;
	}
	
	/**
	 * Obtem descrição para o status do aceite técnico.
	 */
	public String obterDescricaoDominioStatus(Integer value) {
		return DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(value).getDescricao();
	}
	
	public void encaminharSolicitacao() {
		this.encaminharSolicitacaoAnaliseTecnicaController.setAceiteTecnicoParaSerRealizadoVO(this.aceiteSelecionado);
		this.encaminharSolicitacaoAnaliseTecnicaController.iniciar();
	}
	
	public String visualizarNotificacaoTecnica(){		
		this.listaNotificacaoTecnicaController.montarDados(visualizaAceiteSelecionado);
		return PAGINA_CONSULTAR_NOTIFICACOES_TECNICAS;
	}
	
	@Override
	public List<AceiteTecnicoParaSerRealizadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.listaAceiteTecnicoVO = this.patrimonioFacade.recuperarListaPaginadaAceiteTecnico(firstResult, maxResult, orderProperty, asc, this.filtro);
		alterarSelecaoItemListaVO();
		return this.listaAceiteTecnicoVO;
	}

	@Override
	public Long recuperarCount() {
		return this.patrimonioFacade.recuperarCountAceiteTecnico(this.filtro);
	}
	
	public String redirecionarSolicitacaoCompra() {
		return PAGINA_SOLICITACAO_COMPRA_CRUD;
	}
	
	/**
	 * Habilita ou desabilita o ícone da grid. Estória 43449.
	 */
	public boolean habilitarIconeGrid(AceiteTecnicoParaSerRealizadoVO item) {
		if(DominioStatusAceiteTecnico.RECEBIDO.getCodigo() != item.getStatus()){//Alteracao solicitada por Lucas Inocencio atraves de email, ainda nao esta documentado. 
			return false;														//Realizada para impedir internal server error ao entrar no Designar Tecnico.
		}
		if (this.permissaoChefiaPatrimonio) {
			return true;
		} else if (this.permissaoChefe) {
			try {
				
//				List<PtmAreaTecAvaliacao> listaAreaTecnicaResponsavel = 
//						this.patrimonioFacade.obterListaAreaTecnicaPorServidor(
//								this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
//				for (PtmAreaTecAvaliacao ptmAreaTecAvaliacao : listaAreaTecnicaResponsavel) {
//					if (ptmAreaTecAvaliacao.getSeq().equals(item.getAreaTecAvaliacao())) {
//						return true;
//					}
//				}
				if(item.getAreaTecAvaliacao()==null){
					return false;
				}
				RapServidores responsavel = this.patrimonioFacade.obterResponsavelAreaTecnicaPorSeqArea(item.getAreaTecAvaliacao());
				if(responsavel != null && responsavel.getId() != null) {
					RapServidores servidorlogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
					if(servidorlogado.getId().getMatricula().equals(responsavel.getId().getMatricula())
							&& servidorlogado.getId().getVinCodigo().equals(responsavel.getId().getVinCodigo())){
						return true;
					}
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return false;
	}
	
	/**
	 * Método invocado ao marcar/desmarcar o checkbox do cabeçalho da grid.
	 */
	public void checkAll() {
		this.listaAceiteTecnicoSelecionadosVO = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		for (AceiteTecnicoParaSerRealizadoVO vo : this.listaAceiteTecnicoVO) {
			if (this.allChecked) {
				this.listaAceiteTecnicoSelecionadosVO.add(vo);
				if (listaAceiteTecnicoSelecionadosVO.size() == 1) {
					botaoEncaminharAceiteTec = this.habilitarIconeGrid(vo);
				}
				vo.setSelecionado(true);
				if(listaAceiteTecnicoSelecionadosVO != null && !listaAceiteTecnicoSelecionadosVO.isEmpty()){
					for (AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO : dataModel) {
						try {
							List<PtmTecnicoItemRecebimento> lista = consultarTecnicoItemRecebimento(aceiteTecnicoParaSerRealizadoVO);
							if(lista != null && !lista.isEmpty()){
								habilitaBotao = true;
							}else{
								habilitaBotao = false;
								break;
							}
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
					}
				}
			} else {
				vo.setSelecionado(false);
				habilitaBotao = false;
			}
		}
		botaoNotasTecnicas = this.habilitarBotaoNotasTecnicas();
	}
	
	/**
	 * Método invocado ao marcar/desmarcar o checkbox de um item da grid.
	 */
	public void checkItem(AceiteTecnicoParaSerRealizadoVO item) {
		if (this.listaAceiteTecnicoSelecionadosVO.contains(item)) {
			this.listaAceiteTecnicoSelecionadosVO.remove(item);
			validarUsuarioLogadoItemRecebimento(item, false);
			habilitarBotaoRemoveItemSelecionado();
		} else {
			this.listaAceiteTecnicoSelecionadosVO.add(item);
			validarUsuarioLogadoItemRecebimento(item, true);
			botaoVisualizarNotaTec = this.verificarNotificacoes(item);
			setVisualizaAceiteSelecionado(item);
		}
		this.allChecked = (listaAceiteTecnicoSelecionadosVO.size() == listaAceiteTecnicoVO.size());
		botaoNotasTecnicas = this.habilitarBotaoNotasTecnicas();
		botaoEncaminharAceiteTec = this.habilitarIconeGrid(item);
		setAceiteSelecionado(item);
	}

	private void habilitarBotaoRemoveItemSelecionado() {
		if(listaAceiteTecnicoSelecionadosVO != null && !listaAceiteTecnicoSelecionadosVO.isEmpty()){
			for (AceiteTecnicoParaSerRealizadoVO selecionados : listaAceiteTecnicoSelecionadosVO) {
				if(!this.verificarNotificacoes(selecionados)){
					botaoVisualizarNotaTec = false;
					break;
				}else{
					botaoVisualizarNotaTec = true;
					setVisualizaAceiteSelecionado(selecionados);
				}
			}
		}
	}
	
	private void validarUsuarioLogadoItemRecebimento(
			AceiteTecnicoParaSerRealizadoVO item, boolean adicao) {
		try {
			List<PtmTecnicoItemRecebimento> lista = consultarTecnicoItemRecebimento(item);
			if(lista != null && !lista.isEmpty()){
				habilitaBotao = true;
			}else{
				if(adicao){					
					listaAceiteTecnicoSelecionadosAuxVO.add(item);	
				}else{
					listaAceiteTecnicoSelecionadosAuxVO.remove(item);
				}
			}
			if(listaAceiteTecnicoSelecionadosAuxVO != null && !listaAceiteTecnicoSelecionadosAuxVO.isEmpty()){				
				habilitaBotao = false;
			}else{
				habilitaBotao = true;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private List<PtmTecnicoItemRecebimento> consultarTecnicoItemRecebimento(
			AceiteTecnicoParaSerRealizadoVO item)
			throws ApplicationBusinessException {
		List<PtmTecnicoItemRecebimento> lista = this.patrimonioFacade.obterPorTecnicoItemRecebimento(item.getRecebimento(), 
				item.getItemRecebimento(), 
				this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
		return lista;
	}
	
	/**
	 * Atualiza a seleção do item na lista.
	 */
	private void alterarSelecaoItemListaVO() {
		for (AceiteTecnicoParaSerRealizadoVO vo : this.listaAceiteTecnicoVO) {
			if (this.listaAceiteTecnicoSelecionadosVO.contains(vo)) {
				vo.setSelecionado(true);
			} else {
				vo.setSelecionado(false);
			}
		}
		botaoNotasTecnicas = this.habilitarBotaoNotasTecnicas();
	}
	
	public List<AceiteTecnicoParaSerRealizadoVO> obterTodosAceitesTecnicosSelecionados() {
		List<AceiteTecnicoParaSerRealizadoVO> lista = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		for (AceiteTecnicoParaSerRealizadoVO vo : this.listaAceiteTecnicoVO) {
			if (this.listaAceiteTecnicoSelecionadosVO.contains(vo)) {
				lista.add(vo);
			}
		}
		return lista;
	}
	
	/**
	 * Ação do botão Visualizar Detalhes Solicitação Analise Tecnica. 
	 */
	public String designarTecnicoResponsavelSingle() {
		if(listaAceiteTecnicoSelecionadosVO.size() > 1){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SELECAO_UNICA_ITEM_RECEBIMENTO);
			return null;
		}
		if(aceiteSelecionado != null && aceiteSelecionado.getStatus() != 7){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ACEITE_TECNICO_STATUS_CONCLUIDO);
			return null;
		}else if(aceiteSelecionado == null && listaAceiteTecnicoSelecionadosVO.size() == 1){
			this.designaTecnicoResponsavelController.setAceiteTecnico(listaAceiteTecnicoSelecionadosVO.get(0));
			this.designaTecnicoResponsavelController.setModoEdicao(false);
			return PAGINA_DESIGNAR_TECNICO_RESPONSAVEL;
		}
		this.designaTecnicoResponsavelController.setAceiteTecnico(aceiteSelecionado);
		this.designaTecnicoResponsavelController.setModoEdicao(false);
		return PAGINA_DESIGNAR_TECNICO_RESPONSAVEL;
	}
	
	/**
	 * Ação do botão Notas Técnicas 
	 * @throws ApplicationBusinessException 
	 */
	public String cadastrarNotificacoesTecnicas() throws ApplicationBusinessException{
		String resultado=null;
		List<Long> lista = new ArrayList<Long>();
		List<PtmItemRecebProvisorios> ptmItemRecebProvisorio = new ArrayList<PtmItemRecebProvisorios>();
		for (AceiteTecnicoParaSerRealizadoVO vo : this.listaAceiteTecnicoSelecionadosVO) {
			ptmItemRecebProvisorio.addAll(this.patrimonioFacade.pesquisarItemRecebProvisorios(vo.getRecebimento(), vo.getItemRecebimento(), null));		
		}
		for(PtmItemRecebProvisorios ptmItem: ptmItemRecebProvisorio){
			lista.add(ptmItem.getSeq());			
		}
		Integer  item = this.patrimonioFacade.verificarResponsavelAceiteTecnico(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()), lista);
		if(item == 0){
			cadastroNotificacaoTecnicaController.setListaAceiteTecnicoCadastroNotificacaoVO(listaAceiteTecnicoSelecionadosVO);
			resultado = PAGINA_CADASTRAR_NOTIFICACOES_TECNICAS;			
		}else if(item == 1){
			this.apresentarMsgNegocio(Severity.INFO, "USUARIO_NAO_RESP_NOTIFICACAO");
		}else if(item == 2){
			this.apresentarMsgNegocio(Severity.INFO, "ITEM_RECEB_DISTINTOS_USUARIOS");
		}
		botaoNotasTecnicas = true;
		return resultado;
	}	
	
	/**
	 * Ação do link de consultar notificações tecnicas 
	 */
	public String consultarNotificacoesTecnicas(){
		return PAGINA_CONSULTAR_NOTIFICACOES_TECNICAS;
	}
	
	/**
	 * Ação de confirmação do modal de alteração de tecnico responsavel pela avaliação do bem  
	 */
	public String designarTecnicoResponsavelMultiple() {
		this.designaTecnicoResponsavelController.setListaAceiteTecnico(this.listaAceiteTecnicoSelecionadosVO);
		this.designaTecnicoResponsavelController.setAceiteTecnico(this.listaAceiteTecnicoSelecionadosVO.get(0));
		this.designaTecnicoResponsavelController.setModoEdicao(true);
		return PAGINA_DESIGNAR_TECNICO_RESPONSAVEL;
	}

	/**
	 * Ação do botão Designar Tecnico.
	 */
	public String designarTecnico() {
		boolean possuemMesmaAreaTecnica = true;
		if (this.listaAceiteTecnicoSelecionadosVO != null && !this.listaAceiteTecnicoSelecionadosVO.isEmpty()) {
			AceiteTecnicoParaSerRealizadoVO aceiteVO = this.listaAceiteTecnicoSelecionadosVO.get(0);
			for (AceiteTecnicoParaSerRealizadoVO aceite : this.listaAceiteTecnicoSelecionadosVO) {
				if (aceite.getAreaTecAvaliacao() != null && !aceite.getAreaTecAvaliacao().equals(aceiteVO.getAreaTecAvaliacao())) {
					possuemMesmaAreaTecnica = false;
					break;
				}
			}
		}
		if (possuemMesmaAreaTecnica) {
			return validarTecnicoDesignado();
		} else {
			apresentarMsgNegocio(Severity.ERROR, MENSAGEM_TEC_AREA_TEC_DIFERENTES);
			return null;
		}
	}

	private String validarTecnicoDesignado() {
		boolean possuiTecnicoDesignado = false;
		for (AceiteTecnicoParaSerRealizadoVO aceite : this.listaAceiteTecnicoSelecionadosVO) {
			List<RapServidores> lista = this.registroColaboradorFacade.obterServidorTecnicoPorItemRecebimento(
					aceite.getRecebimento(), aceite.getItemRecebimento());
			if (lista != null && !lista.isEmpty()) {
				possuiTecnicoDesignado = true;
				break;
			}
		}
		if (possuiTecnicoDesignado) {
			RequestContext.getCurrentInstance().execute("PF('modalConfirmacaoAlteracaoTecnico').show()");
			return null;
		} else {
			return designarTecnicoResponsavelMultiple();
		}
	}
	
	/**
	 * Ação de confirmação do modal de atender aceite técnico.  
	 */
	public String atenderAceiteTecnico(){
		
		if(listaAceiteTecnicoSelecionadosVO != null && !listaAceiteTecnicoSelecionadosVO.isEmpty()){
			for (AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO : this.listaAceiteTecnicoSelecionadosVO) {
				this.patrimonioFacade.atenderAceiteTecnico(aceiteTecnicoParaSerRealizadoVO.getRecebimento(), 
						aceiteTecnicoParaSerRealizadoVO.getItemRecebimento());
			}
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ASSUMIR_TAREFA");
		}
		return null;
	}
	
	public boolean consultarItemRecebimento(AceiteTecnicoParaSerRealizadoVO item){
		boolean concluido = false;
		try {
			List<PtmItemRecebProvisorios> listaItensProvisorios = this.patrimonioFacade.pesquisarItemRecebProvisorios(item.getRecebimento(), item.getItemRecebimento(), 
					this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
			if(listaItensProvisorios != null && !listaItensProvisorios.isEmpty()){
				for (PtmItemRecebProvisorios ptmItemRecebProvisorios : listaItensProvisorios) {
					if(ptmItemRecebProvisorios.getStatus().equals(DominioStatusAceiteTecnico.CONCLUIDA.getCodigo())){
						concluido = true;
					}else{
						concluido = false;
					}
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return concluido;
	}
	
	public Boolean habilitarBotaoNotasTecnicas(){
		Boolean emAvaliacao = true;
        if (permissaoCadastrarNotificacoesTecnicas && listaAceiteTecnicoSelecionadosVO != null) {
        	RapServidores servidor = null;
            try {
            	servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
            } catch (ApplicationBusinessException e) {
            	apresentarExcecaoNegocio(e);
            }
            for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnicoSelecionadosVO) {
            	if (servidor != null && servidor.getId() != null && 
            			item.getStatus() != null && 
            			DominioStatusAceiteTecnico.EM_AVALIACAO_TECNICA.getDescricao().equals(obterDescricaoDominioStatus(item.getStatus()))) {
            		emAvaliacao = false;
                 } else {
                	 emAvaliacao = true;
                     break;
                 }
             }
        }
        return emAvaliacao;
	}
	
	public Boolean verificarNotificacoes(AceiteTecnicoParaSerRealizadoVO item){
		Boolean mostraIcone = false;
		listaNotificacoesTecnicas = new ArrayList<PtmNotificacaoTecnica>();
		irpSeq = this.patrimonioFacade.pesquisarIrpSeq(item.getRecebimento(), item.getItemRecebimento(), null);
		listaNotificacoesTecnicas = this.patrimonioFacade.pesquisarNotificacoesTecnica(irpSeq.getSeq());
		if(listaNotificacoesTecnicas != null && !listaNotificacoesTecnicas.isEmpty()){
			mostraIcone = true;
		}else{
			mostraIcone = false;
		}
		return mostraIcone;
	}
	
	public String registrarAceiteTecnico(){
		if (getListaAceiteTecnicoSelecionadosVO() != null && !getListaAceiteTecnicoSelecionadosVO().isEmpty()) {
			if(getListaAceiteTecnicoSelecionadosVO().size() > 1){
				apresentarMsgNegocio(Severity.WARN, "REAL_ACEITE_APENAS_1_ITEM");
				return null;
			}else if(getListaAceiteTecnicoSelecionadosVO().size() == 1){
				RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
				PtmItemRecebProvisorios pirp = patrimonioFacade.pesquisarItemRecebSeq(getListaAceiteTecnicoSelecionadosVO().get(0).getRecebimento(),
						getListaAceiteTecnicoSelecionadosVO().get(0).getItemRecebimento());
				if(this.patrimonioFacade.verificarUsuarioLogadoResponsavelPorAceite(pirp.getSeq(), servidor)){
					return verificarStatusRealizarAceite();
				}else{
					RapServidoresVO obj2 = this.patrimonioFacade.verificarUsuarioLogadoResponsavelPorAreaTecnica(
							getListaAceiteTecnicoSelecionadosVO().get(0).getItemRecebimento(), getListaAceiteTecnicoSelecionadosVO().get(0).getRecebimento());
					if(obj2 != null){
						if(obj2.getMatricula() != null && obj2.getVinculo() != null){
							if(CoreUtil.igual(obj2.getMatricula(), servidor.getId().getMatricula()) &&
									CoreUtil.igual(obj2.getVinculo(), servidor.getId().getVinCodigo())){
								return verificarStatusRealizarAceite();
							}else{
								apresentarMsgNegocio(Severity.WARN, "ACAO_NAO_PERMITIDA");
								return null;
							}
						}
					}
				}
			}
		}else{
			return PAGE_PESQUISAR_ACEITE_TECNICO;
		}
		return null;
	}

	private String verificarStatusRealizarAceite() {
		if(!obterDescricaoDominioStatus(
				getListaAceiteTecnicoSelecionadosVO().get(0).getStatus()).equals(EM_AVALIACAO_TECNICA)){
			apresentarMsgNegocio(Severity.WARN, "STATUS_NAO_VALIDO");
			return null;
		}else{
			this.registrarAceiteTecnicoPaginatorController.setAceiteTecRealizadoVO(getListaAceiteTecnicoSelecionadosVO().get(0));
			return PAGE_PESQUISAR_ACEITE_TECNICO;
		}
	}
	
	public String btAnexarDocumentos(){
		PtmItemRecebProvisorios ptmItemRecebProvisorios = new PtmItemRecebProvisorios();
		ptmItemRecebProvisorios.setNrpSeq(this.listaAceiteTecnicoSelecionadosVO.get(0).getRecebimento());
		ptmItemRecebProvisorios.setNroItem(this.listaAceiteTecnicoSelecionadosVO.get(0).getItemRecebimento());
		this.anexosArquivosListPaginatorController.setRecebimentoItem(ptmItemRecebProvisorios);
		return ANEXOS_ARQUIVOS_LIST;
	}
	
	public String imprimirNotificacoes() throws ApplicationBusinessException {
		if (listaAceiteTecnicoSelecionadosVO == null || listaAceiteTecnicoSelecionadosVO.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MS01_48204");
			return null;
		}
		if (listaAceiteTecnicoSelecionadosVO.size() > 1) {
			apresentarMsgNegocio(Severity.ERROR, "MS02_48204");
			return null;
		}
		relatorioNotifTecRecebimentoProvisorioController.setRecebimento(this.listaAceiteTecnicoSelecionadosVO.get(0).getRecebimento());
		relatorioNotifTecRecebimentoProvisorioController.setItemRecebimento(this.listaAceiteTecnicoSelecionadosVO.get(0).getItemRecebimento());
		return relatorioNotifTecRecebimentoProvisorioController.visualizarImpressao();
	}	
	public String detalharItemReceb() {
		if (listaAceiteTecnicoSelecionadosVO == null || listaAceiteTecnicoSelecionadosVO.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MS01_DETALHAR_ITEM_RECEBIMENTO");
			return null;
		}		
		if (listaAceiteTecnicoSelecionadosVO.size() > 1) {
			apresentarMsgNegocio(Severity.ERROR, "MS02_DETALHAR_ITEM_RECEBIMENTO");
			return null;
		}
		setAceiteSelecionado(listaAceiteTecnicoSelecionadosVO.get(0));
		this.itemRecebimentoController.setAceiteTecnicoParaSerRealizadoVO(this.aceiteSelecionado);
		return ITEM_RECEBIMENTO;
	}
	/**
	 * 
	 * GETs and SETs 
	 * 
	 */
	public DynamicDataModel<AceiteTecnicoParaSerRealizadoVO> getDataModel() {
		return dataModel;
	}
	public void setDataModel(DynamicDataModel<AceiteTecnicoParaSerRealizadoVO> dataModel) {
		this.dataModel = dataModel;
	}
	public FiltroAceiteTecnicoParaSerRealizadoVO getFiltro() {
		return filtro;
	}
	public void setFiltro(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		this.filtro = filtro;
	}
	public boolean isPm01() {
		return pm01;
	}
	public void setPm01(boolean pm01) {
		this.pm01 = pm01;
	}
	public boolean isPm02() {
		return pm02;
	}
	public void setPm02(boolean pm02) {
		this.pm02 = pm02;
	}
	public boolean isPm03() {
		return pm03;
	}
	public void setPm03(boolean pm03) {
		this.pm03 = pm03;
	}
	public boolean isPossuiValorNumeroAf() {
		return possuiValorNumeroAf;
	}
	public void setPossuiValorNumeroAf(boolean possuiValorNumeroAf) {
		this.possuiValorNumeroAf = possuiValorNumeroAf;
	}
	public AceiteTecnicoParaSerRealizadoVO getAceiteSelecionado() {
		return aceiteSelecionado;
	}
	public void setAceiteSelecionado(AceiteTecnicoParaSerRealizadoVO aceiteSelecionado) {
		this.aceiteSelecionado = aceiteSelecionado;
	}
	public boolean isAllChecked() {
		return allChecked;
	}
	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}
	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnicoVO() {
		return listaAceiteTecnicoVO;
	}
	public void setListaAceiteTecnicoVO(List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoVO) {
		this.listaAceiteTecnicoVO = listaAceiteTecnicoVO;
	}
	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnicoSelecionadosVO() {
		return listaAceiteTecnicoSelecionadosVO;
	}
	public void setListaAceiteTecnicoSelecionadosVO(List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosVO) {
		this.listaAceiteTecnicoSelecionadosVO = listaAceiteTecnicoSelecionadosVO;
	}
	public boolean isPermissaoChefe() {
		return permissaoChefe;
	}
	public void setPermissaoChefe(boolean permissaoChefe) {
		this.permissaoChefe = permissaoChefe;
	}
	public boolean isPermissaoChefiaPatrimonio() {
		return permissaoChefiaPatrimonio;
	}
	public void setPermissaoChefiaPatrimonio(boolean permissaoChefiaPatrimonio) {
		this.permissaoChefiaPatrimonio = permissaoChefiaPatrimonio;
	}
	public boolean isPermissaoDesignarTecnico() {
		return permissaoDesignarTecnico;
	}
	public void setPermissaoDesignarTecnico(boolean permissaoDesignarTecnico) {
		this.permissaoDesignarTecnico = permissaoDesignarTecnico;
	}
	public boolean isPermissaoVisualizarTecnico() {
		return permissaoVisualizarTecnico;
	}
	public void setPermissaoVisualizarTecnico(boolean permissaoVisualizarTecnico) {
		this.permissaoVisualizarTecnico = permissaoVisualizarTecnico;
	}
	public boolean isPermissaoAlterarTecnico() {
		return permissaoAlterarTecnico;
	}
	public void setPermissaoAlterarTecnico(boolean permissaoAlterarTecnico) {
		this.permissaoAlterarTecnico = permissaoAlterarTecnico;
	}
	public boolean isPermissaoAtenderAceiteTecnico() {
		return permissaoAtenderAceiteTecnico;
	}
	public void setPermissaoAtenderAceiteTecnico(
			boolean permissaoAtenderAceiteTecnico) {
		this.permissaoAtenderAceiteTecnico = permissaoAtenderAceiteTecnico;
	}
	public boolean isPermissaoRetiradaBemPermanente() {
		return permissaoRetiradaBemPermanente;
	}
	public void setPermissaoRetiradaBemPermanente(
			boolean permissaoRetiradaBemPermanente) {
		this.permissaoRetiradaBemPermanente = permissaoRetiradaBemPermanente;
	}
	public boolean isPermissaoRegistrarDevolucaoBemPermanente() {
		return permissaoRegistrarDevolucaoBemPermanente;
	}
	public Boolean getBotaoNotasTecnicas() {
		return botaoNotasTecnicas;
	}
	public void setBotaoNotasTecnicas(Boolean botaoNotasTecnicas) {
		this.botaoNotasTecnicas = botaoNotasTecnicas;
	}
	public PtmItemRecebProvisorios getIrpSeq() {
		return irpSeq;
	}
	public void setIrpSeq(PtmItemRecebProvisorios irpSeq) {
		this.irpSeq = irpSeq;
	}
	public List<PtmNotificacaoTecnica> getListaNotificacoesTecnicas() {
		return listaNotificacoesTecnicas;
	}
	public void setListaNotificacoesTecnicas(List<PtmNotificacaoTecnica> listaNotificacoesTecnicas) {
		this.listaNotificacoesTecnicas = listaNotificacoesTecnicas;
	}
	public Boolean getPermissaoConsultarNotificacoesTecnicas() {
		return permissaoConsultarNotificacoesTecnicas;
	}
	public void setPermissaoConsultarNotificacoesTecnicas(Boolean permissaoConcultarNotificacoesTecnicas) {
		this.permissaoConsultarNotificacoesTecnicas = permissaoConcultarNotificacoesTecnicas;
	}
	public Boolean getPermissaoCadastrarNotificacoesTecnicas() {
		return permissaoCadastrarNotificacoesTecnicas;
	}
	public void setPermissaoCadastrarNotificacoesTecnicas(Boolean permissaoCadastrarNotificacoesTecnicas) {
		this.permissaoCadastrarNotificacoesTecnicas = permissaoCadastrarNotificacoesTecnicas;
	}
	public void setPermissaoRegistrarDevolucaoBemPermanente(
			boolean permissaoRegistrarDevolucaoBemPermanente) {
		this.permissaoRegistrarDevolucaoBemPermanente = permissaoRegistrarDevolucaoBemPermanente;
	}
	public boolean isHabilitaBotao() {
		return habilitaBotao;
	}
	public void setHabilitaBotao(boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}
	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnicoSelecionadosAuxVO() {
		return listaAceiteTecnicoSelecionadosAuxVO;
	}
	public void setListaAceiteTecnicoSelecionadosAuxVO(
			List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnicoSelecionadosAuxVO) {
		this.listaAceiteTecnicoSelecionadosAuxVO = listaAceiteTecnicoSelecionadosAuxVO;
	}
	public boolean isPermissaoPesquisarAceiteTecnico() {
		return permissaoPesquisarAceiteTecnico;
	}
	public void setPermissaoPesquisarAceiteTecnico(boolean permissaoPesquisarAceiteTecnico) {
		this.permissaoPesquisarAceiteTecnico = permissaoPesquisarAceiteTecnico;
	}
	public boolean isPermissaoListarAceitesTecnicosChefiaCC() {
		return permissaoListarAceitesTecnicosChefiaCC;
	}
	public void setPermissaoListarAceitesTecnicosChefiaCC(
			boolean permissaoListarAceitesTecnicosChefiaCC) {
		this.permissaoListarAceitesTecnicosChefiaCC = permissaoListarAceitesTecnicosChefiaCC;
	}
	public boolean isBotaoEncaminharAceiteTec() {
		return botaoEncaminharAceiteTec;
	}
	public void setBotaoEncaminharAceiteTec(boolean botaoEncaminharAceiteTec) {
		this.botaoEncaminharAceiteTec = botaoEncaminharAceiteTec;
	}
	public void setAceiteTecnicoParaSerRealizadoVO(AceiteTecnicoParaSerRealizadoVO aceiteSelecionado) {
		this.aceiteSelecionado = aceiteSelecionado;
	}

	public boolean isBotaoVisualizarNotaTec() {
		return botaoVisualizarNotaTec;
	}

	public void setBotaoVisualizarNotaTec(boolean botaoVisualizarNotaTec) {
		this.botaoVisualizarNotaTec = botaoVisualizarNotaTec;
	}

	public AceiteTecnicoParaSerRealizadoVO getVisualizaAceiteSelecionado() {
		return visualizaAceiteSelecionado;
	}

	public void setVisualizaAceiteSelecionado(
			AceiteTecnicoParaSerRealizadoVO visualizaAceiteSelecionado) {
		this.visualizaAceiteSelecionado = visualizaAceiteSelecionado;
	}
}
