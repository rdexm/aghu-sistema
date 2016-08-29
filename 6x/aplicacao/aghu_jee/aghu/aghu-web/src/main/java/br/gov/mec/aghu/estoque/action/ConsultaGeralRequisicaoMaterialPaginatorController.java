package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ConsultaGeralRequisicaoMaterialPaginatorController extends ActionController implements ActionPaginator {
	
	private static final String EXCECAO_CAPUTADA = "Exceção caputada:";

	private static final long serialVersionUID = 9089840032121263323L;

	@Inject @Paginator
	private DynamicDataModel<SceReqMaterial> dataModel;
	private static final Log LOG = LogFactory.getLog(ConsultaGeralRequisicaoMaterialPaginatorController.class);
	

	public enum EnumTargetConsultaGeralRequisicaoMaterial {
		CONSULTAR_GERAL_REQUISICAO_MATERIAL, LIMPAR, EDITAR_GERACAO_REQUISICAO_MATERIAL, VISUALIZAR_ITENS_REQUISICAO_MATERIAL;
	}

	private Boolean consultaGeral = Boolean.FALSE; // Define se a "tela" é a
													// Consulta Geral

	// requisição utilizada para filtro de pesquisa
	private SceReqMaterial requisicao = new SceReqMaterial();
	// requisicaoSelecionada no grid
	private SceReqMaterial requisicaoSelecionada;
	
	private SceReqMaterial requisicaoConfirmada;
	
	public SceReqMaterial getRequisicaoConfirmada() {
		return requisicaoConfirmada;
	}

	public void setRequisicaoConfirmada(SceReqMaterial requisicaoConfirmada) {
		this.requisicaoConfirmada = requisicaoConfirmada;
	}

	// requisicao que armazena o nome da impressora
	// (necessário, devido ao suggestion exigir um objeto
	// para a descricaoInput e descricaoLabel)
	private LinhaReportVO requisicaoNomeImpressora;

	// filtros de pesquisa
	private Integer codigoCentroCustoRequisitante;
	private Integer codigoCentroCustoAplicacao;
	private String nomeImpressora;
	private Short seqAlmoxarifado;
	private DominioSimNao estorno;
	private DominioSimNao automatica;

	// número da requisição selecionada
	private Integer numeroRequisicao;
	private Integer numeroReqRedirecionar;

	// indica situações, para habilitação/desabilitação
	// de campos na tela
	private Boolean situacaoGerada;
	private Boolean situacaoConfirmada;

	// indica se o botão novo será renderizado
	private Boolean exibirBotaoNovo;

	// lista de requisições retornadas na pesquisa
	private List<SceReqMaterial> listaRequisicoesMateriais;

	// indica as permissões do usuário, de forma a controlar habilitação e
	// renderização
	// de campos na tela
	private Boolean possuiPermissaoAlterarRM = Boolean.TRUE;
	private Boolean possuiPermissaoConfirmarRM = Boolean.TRUE;
	// TODO: remover essas permissoes do codigo(inicio)
	private Boolean possuiPermissaoConsultarRMsConfirmadasNaoImpressas = Boolean.TRUE;
	private Boolean possuiPermissaoConsultarRMsConfirmadas = Boolean.TRUE;
	private Boolean possuiPermissaoConsultarRMsGeradas = Boolean.TRUE;
	// TODO: remover essas permissoes do codigo(fim)
	private Boolean possuiPermissaoEstornarRM = Boolean.TRUE;
	private Boolean possuiPermissaoVisualizarItensRequisicaoMaterial = Boolean.TRUE;
	private Boolean possuiPermissaoImprimirRM = Boolean.TRUE;
	private Boolean possuiPermissaoGerarRM = Boolean.TRUE;
	private Boolean possuiPermissaoCancelarRMConfirmada = Boolean.TRUE;
	private Boolean possuiPermissaoCancelarRMGerada = Boolean.TRUE;
	private Boolean possuiPermissaoEfetivarRM = Boolean.TRUE;
	
	private boolean isAlmoxarife;

	// indica se os campos serão desabilitados
	private Boolean desabilitarIndImpresso = Boolean.FALSE;
	private Boolean desabilitarImpressoraDestino = Boolean.FALSE;

	// indica se os botões de ação serão habilitados
	private Boolean exibirBotoes = Boolean.FALSE;

	// indica se o centro custo requisitante será preenchido
	private Boolean preencherCentroCusto = Boolean.FALSE;

	private String origem;
	
	private static final String VISUALIZAR_ITEM = "visualizarItemRequisicaoMaterial";
	private static final String IMPRIMIR_ITEM = "estoque-imprimirRequisicaoMaterial";
	private static final String GERACAO_REQUISICAO_MATERIAL = "estoque-geracaoRequisicaoMaterial";

	// atributo utilizado para corrigir bug ** Defeito em Desenvolvimento #15463
	// **
	private Boolean origemConsultarRM;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@Inject
	private SecurityController securityController;
	
	private Boolean habilitaAlterarRM;

	private enum OrigemTela {
		CONSULTAR_GERAL("consultaGeral"), CONSULTAR_RM("consultarRM");

		OrigemTela(String desc) {
			descricao = desc;
		}

		private String descricao;

		public String getDescricao() {
			return descricao;
		}
	}
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void iniciar() {
		this.setRequisicaoConfirmada(null);

	 


		verificaOrigem();

		if (!getOrigemConsultarRM()) {
			setConsultaGeral(Boolean.TRUE);
		} else {
			setConsultaGeral(Boolean.FALSE);
		}
		
		isAlmoxarife = this.getCascaFacade().usuarioTemPerfil(this.obterLoginUsuarioLogado(), "ADM29");
		
		if(getPreencherCentroCusto() && !this.getDataModel().getPesquisaAtiva()){
			//seta o centro de custo do usuário no suggestion
			getRequisicao().setCentroCusto(centroCustoFacade
					.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao());
		
		}
		
		// Remover as permissoes antigas: gerarRM, alterarRM e confirmarRM dos
		// botões da tela.
		if (!securityController.usuarioTemPermissao("pesquisarRM", "pesquisar")) {
			setPossuiPermissaoAlterarRM(Boolean.FALSE);
			setPossuiPermissaoConfirmarRM(Boolean.FALSE);
			setPossuiPermissaoConsultarRMsConfirmadasNaoImpressas(Boolean.FALSE);
			setPossuiPermissaoConsultarRMsConfirmadas(Boolean.FALSE);
			setPossuiPermissaoConsultarRMsGeradas(Boolean.FALSE);
			setPossuiPermissaoGerarRM(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("estornarRM", "estornar")) {
			setPossuiPermissaoEstornarRM(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("visualizarItensRequisicaoMaterial", "consultar")) {
			setPossuiPermissaoVisualizarItensRequisicaoMaterial(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("imprimirRM", "imprimir")) {
			setPossuiPermissaoImprimirRM(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("efetivarRM", "efetivar")) {
			setPossuiPermissaoEfetivarRM(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("cancelarRMGerada", "cancelar")) {
			setPossuiPermissaoCancelarRMGerada(Boolean.FALSE);
		}
		if (!securityController.usuarioTemPermissao("cancelarRMConfirmada", "cancelar")) {
			setPossuiPermissaoCancelarRMConfirmada(Boolean.FALSE);
		}

		setExibirBotaoNovo(getOrigemConsultarRM() && getPossuiPermissaoGerarRM());

		if (!getConsultaGeral() && !dataModel.getPesquisaAtiva()) {
			if (getPossuiPermissaoConsultarRMsConfirmadasNaoImpressas() && getPossuiPermissaoConsultarRMsConfirmadas()) {
				getRequisicao().setIndImpresso(null);
				getRequisicao().setNomeImpressora(null);
				setDesabilitarIndImpresso(Boolean.FALSE);
			} else if (getPossuiPermissaoConsultarRMsConfirmadas() && getPossuiPermissaoConsultarRMsGeradas()) {
				getRequisicao().setIndImpresso(null);
				getRequisicao().setNomeImpressora(null);
				setDesabilitarIndImpresso(Boolean.FALSE);
				setDesabilitarImpressoraDestino(Boolean.TRUE);
			} else if (getPossuiPermissaoConsultarRMsConfirmadas()) {
				setDesabilitarIndImpresso(Boolean.FALSE);
			} else if (getPossuiPermissaoConsultarRMsConfirmadasNaoImpressas()) {
				getRequisicao().setIndImpresso(DominioImpresso.N);
				setDesabilitarIndImpresso(Boolean.TRUE);
			}
		}

		desabilitarCampos();
	
	}
	

	public void iniciar(String origem) {
		setOrigem(origem);
		iniciar();
	}
	
	public String obterMatriculaNomeServidor(Integer seqRm, Integer tipoServidor) {
		String ret = "";
		SceReqMaterial rm = this.estoqueFacade.obterRequisicaoMaterial(seqRm);
		if (rm != null) {
			switch (tipoServidor) {
				case 0:
					ret = rm.getServidor().getMatriculaNomeServidor();
					break;
				case 1:
					ret = rm.getServidorConfirmado().getMatriculaNomeServidor();
					break;
				case 2:
					ret = rm.getServidorEfetivado().getMatriculaNomeServidor();
					break;
			}
		}
		
		return ret;
	}
	
	public void iniciarConsultarRM(){
	 

	 

		this.iniciar("consultarRM");
	
	}
	

	private void verificaOrigem() {
		if (getOrigem() != null && getOrigem().equals(OrigemTela.CONSULTAR_RM.getDescricao()) && getOrigemConsultarRM() == null) {
			setOrigemConsultarRM(Boolean.TRUE);
			setPreencherCentroCusto(Boolean.TRUE);
		} else if (getOrigem() == null && getOrigemConsultarRM() == null) {
			setOrigemConsultarRM(Boolean.FALSE);
			setPreencherCentroCusto(Boolean.FALSE);
		}
	}

	@Override
	public List<SceReqMaterial> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		inserirValoresParaFiltrosPesquisa();
		if (getConsultaGeral()) {
			setListaRequisicoesMateriais(estoqueFacade.pesquisarRequisicaoMaterialConsultaGeral(firstResult, maxResult,
					orderProperty, asc, getRequisicao().getSeq(), getRequisicao().getDtGeracao(), getRequisicao()
							.getDtConfirmacao(), getRequisicao().getDtEfetivacao(), getRequisicao().getIndSituacao(),
					getRequisicao().getEstorno(), getRequisicao().getAutomatica(), getNomeImpressora(), getSeqAlmoxarifado(),
					getCodigoCentroCustoRequisitante(), getCodigoCentroCustoAplicacao(), getRequisicao().getIndImpresso()));
		} else {
			List<FccCentroCustos> hierarquiaCc = null;
			List<SceAlmoxarifado> listaAlmox = null;
			List<DominioSituacaoRequisicaoMaterial> listaSituacao = new ArrayList<DominioSituacaoRequisicaoMaterial>();

			tratarPermissoesListaSituacoes(listaSituacao);
			try {
				if (isAlmoxarife) {
					listaAlmox = estoqueFacade.pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(null, new Date());
				} else {
					hierarquiaCc = centroCustoFacade.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(null, DominioCaracteristicaCentroCusto.GERAR_RM); 
				}
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
			setListaRequisicoesMateriais(estoqueFacade.pesquisarRequisicaoMaterialConsultaRM(firstResult, maxResult,
					orderProperty, asc, getRequisicao().getSeq(), getRequisicao().getDtGeracao(), getRequisicao()
							.getDtConfirmacao(), getRequisicao().getDtEfetivacao(), getRequisicao().getIndSituacao(),
					getRequisicao().getEstorno(), getNomeImpressora(), getSeqAlmoxarifado(), getCodigoCentroCustoRequisitante(),
					getCodigoCentroCustoAplicacao(), getRequisicao().getIndImpresso(),hierarquiaCc, listaAlmox, listaSituacao));
		}

		setSeqAlmoxarifado(null);
		setCodigoCentroCustoRequisitante(null);
		setCodigoCentroCustoAplicacao(null);
		setNomeImpressora(null);

		return getListaRequisicoesMateriais();
	}

	@Override
	public Long recuperarCount() {
		Long quantidadeRegistros = null;
		inserirValoresParaFiltrosPesquisa();

		if (getConsultaGeral()) {
			quantidadeRegistros = estoqueFacade.pesquisarRequisicaoMaterialConsultaGeralCount(getRequisicao().getSeq(),
					getRequisicao().getDtGeracao(), getRequisicao().getDtConfirmacao(), getRequisicao().getDtEfetivacao(),
					getRequisicao().getIndSituacao(), getRequisicao().getEstorno(), getRequisicao().getAutomatica(),
					getNomeImpressora(), getSeqAlmoxarifado(), getCodigoCentroCustoRequisitante(),
					getCodigoCentroCustoAplicacao(), getRequisicao().getIndImpresso());
		} else {
			List<FccCentroCustos> hierarquiaCc = null;
			List<SceAlmoxarifado> listaAlmox = null;
			List<DominioSituacaoRequisicaoMaterial> listaSituacao = new ArrayList<DominioSituacaoRequisicaoMaterial>();

			tratarPermissoesListaSituacoes(listaSituacao);
			try {
				if (isAlmoxarife) {
					listaAlmox = estoqueFacade.pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(null, new Date());
				} else {
					hierarquiaCc = centroCustoFacade.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(null, DominioCaracteristicaCentroCusto.GERAR_RM); 
				}
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}

			quantidadeRegistros = estoqueFacade.pesquisarRequisicaoMaterialConsultaRMCount(getRequisicao().getSeq(),
					getRequisicao().getDtGeracao(), getRequisicao().getDtConfirmacao(), getRequisicao().getDtEfetivacao(),
					getRequisicao().getIndSituacao(), getRequisicao().getEstorno(), getNomeImpressora(), getSeqAlmoxarifado(),
					getCodigoCentroCustoRequisitante(), getCodigoCentroCustoAplicacao(), getRequisicao().getIndImpresso(),hierarquiaCc, listaAlmox, listaSituacao);
		}

		setSeqAlmoxarifado(null);
		setCodigoCentroCustoRequisitante(null);
		setCodigoCentroCustoAplicacao(null);
		setNomeImpressora(null);

		return quantidadeRegistros;
	}

	/**
	 * Executa a pesquisa de requisições
	 */
	public String pesquisar() {
		String retorno = null;
		Long count = recuperarCount();
		// caso o usuário possua a permissão alterarRM e passe um valor para o
		// número da requisição,
		// será verificado se no retorno da pesquisa possui apenas um
		// elemento, e se este tem como situação o valor Gerada
		if (getPossuiPermissaoAlterarRM() && getOrigemConsultarRM()) {
			if (getRequisicao().getSeq() != null) {
				if (count==1L) {
					SceReqMaterial requisicaoRetornada = this.estoqueFacade.obterRequisicaoMaterial(getRequisicao().getSeq());
					if (DominioSituacaoRequisicaoMaterial.G.equals(requisicaoRetornada.getIndSituacao())) {
						retorno = GERACAO_REQUISICAO_MATERIAL;
					} else {
						retorno = VISUALIZAR_ITEM;
					}
				} else {
					dataModel.reiniciarPaginator();			
				}
			} else {
				dataModel.reiniciarPaginator();
			}
		} else {
			dataModel.reiniciarPaginator();	
			retorno = redirecionaVisualizarItens(retorno);
		}

		if (count != 0) {
			setExibirBotoes(Boolean.TRUE);
		} else {
			setExibirBotoes(Boolean.FALSE);
		}

		setExibirBotaoNovo(getOrigemConsultarRM());

		setNumeroRequisicao(null);
		//setRequisicaoSelecionada(null);
		return retorno;
	}
	
	public String consultarRM(){
		return VISUALIZAR_ITEM;
	}
	
	public String geracaoRequisicaoMaterial(){
		return GERACAO_REQUISICAO_MATERIAL;
	}
	
	public String imprimirRM(){
		return IMPRIMIR_ITEM;
	}

	private String redirecionaVisualizarItens(String retorno) {
		// se tenha informado o número da requisição, manda pra tela de
		// visualizar
		if (getRequisicao().getSeq() != null) {
			List<SceReqMaterial> list = this.dataModel.getPaginator().recuperarListaPaginada(0, 2, null, true);
			if (list != null && list.size()==1) {
				retorno = VISUALIZAR_ITEM;
			}
		}
		return retorno;
	}

	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(
			String parametro) {
		return estoqueFacade.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}

	public List<LinhaReportVO> pesquisarNomesImpressorasRequisicaoMaterial(String parametro) {
		return estoqueFacade.pesquisarNomesImpressorasRequisicaoMaterial(parametro);
	}

	/**
	 * Pesquisa centros de custos do usuário, tanto o centro de custo de atuação
	 * do usuário, como os centros de custo filhos deste
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(Object parametro) {
		return centroCustoFacade.pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(parametro);
	}

	/**
	 * Pesquisa centros de custos requisitantes de acordo com a origem da tela
	 * (consultaGeral ou consultaRM)
	 * 
	 * @param parametro
	 * @return
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @since 14/03/2012
	 */
	public List<FccCentroCustos> pesquisarCentrosCustosRequisitantePeloCodigo(String parametro)
			throws ApplicationBusinessException, ApplicationBusinessException {
		List<FccCentroCustos> result = new ArrayList<FccCentroCustos>();
		
		if(isAlmoxarife){
			result.addAll(pesquisarTodosCentroCustoAtivos(parametro));
		}
		else{
			result.addAll(centroCustoFacade.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(parametro, DominioCaracteristicaCentroCusto.GERAR_RM));
		}
		
		return result;
	}

	/**
	 * Filtra e retorna as situações, de acordo com as permissões que o usuário
	 * possui
	 * 
	 * @return
	 */
	public List<DominioSituacaoRequisicaoMaterial> listarSituacoes() {

		List<DominioSituacaoRequisicaoMaterial> listaSituacoes = new ArrayList<DominioSituacaoRequisicaoMaterial>();

		if (!getConsultaGeral()) {
			tratarPermissoesListaSituacoes(listaSituacoes);
		} else {
			tratarPermissoesListaSituacoes(listaSituacoes);
			listaSituacoes.add(DominioSituacaoRequisicaoMaterial.E);
			listaSituacoes.add(DominioSituacaoRequisicaoMaterial.A);
		}

		return listaSituacoes;
	}

	/**
	 * Obtém a requisição selecionada no grid, a qual será utilizada para
	 * verificar qual ação poderá ser executada sobre a mesma (alterar,
	 * confirmar, estornar, efetivar, consultar, imprimir)
	 */
	public void obterRequisicaoMaterialVerificarAcaoPermitida() {
		setRequisicaoSelecionada(estoqueFacade.obterRequisicaoMaterial(getNumeroRequisicao()));
	}

	/**
	 * Desabilita campos, de acordo com a permissão do usuário
	 */
	public void desabilitarCampos() {
		if (!getConsultaGeral() && DominioSituacaoRequisicaoMaterial.G.equals(getRequisicao().getIndSituacao())) {
			getRequisicao().setDtEfetivacao(null);
			getRequisicao().setDtConfirmacao(null);
			getRequisicao().setIndImpresso(null);
			getRequisicao().setNomeImpressora(null);
			setEstorno(null);
			setSituacaoGerada(Boolean.TRUE);
			setSituacaoConfirmada(Boolean.FALSE);
		} else if (!getConsultaGeral() && DominioSituacaoRequisicaoMaterial.C.equals(getRequisicao().getIndSituacao())) {
			getRequisicao().setDtEfetivacao(null);
			getRequisicao().setNomeImpressora(null);
			setEstorno(null);
			setSituacaoConfirmada(Boolean.TRUE);
			setSituacaoGerada(Boolean.FALSE);
		} else {
			setSituacaoGerada(Boolean.FALSE);
			setSituacaoConfirmada(Boolean.FALSE);
		}
	}

	/**
	 * Método que trata exibição de situações de requisição, de acordo com
	 * permissões, para cada tela (Consulta RM ou Consulta Geral)
	 * 
	 * @param listaSituacoes
	 */
	private void tratarPermissoesListaSituacoes(List<DominioSituacaoRequisicaoMaterial> listaSituacoes) {
		if (listaSituacoes != null) {
			listaSituacoes.add(DominioSituacaoRequisicaoMaterial.G);
			if (isAlmoxarife || getConsultaGeral()) {
				listaSituacoes.add(DominioSituacaoRequisicaoMaterial.C);
			}
		}
	}
	
	/**
	 * Pesquisa almoxarifados que possuem o mesmo centro de custo do usuário
	 * @param parametro
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(String parametro) throws ApplicationBusinessException {
		if (isAlmoxarife) {
			try {
				return estoqueFacade.pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(parametro, new Date());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		} else {
			return this.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
		}
	}

	/**
	 * Método que preenche os valores dos filtros de pesquisa
	 */
	private void inserirValoresParaFiltrosPesquisa() {

		setListaRequisicoesMateriais(new ArrayList<SceReqMaterial>());

		if (getRequisicao().getAlmoxarifado() != null && getRequisicao().getAlmoxarifado().getSeq() != null) {
			setSeqAlmoxarifado(getRequisicao().getAlmoxarifado().getSeq());
		}

		if (getRequisicao().getCentroCusto() != null && getRequisicao().getCentroCusto().getCodigo() != null) {
			setCodigoCentroCustoRequisitante(getRequisicao().getCentroCusto().getCodigo());
		}

		if (getRequisicao().getCentroCustoAplica() != null && getRequisicao().getCentroCustoAplica().getCodigo() != null) {
			setCodigoCentroCustoAplicacao(getRequisicao().getCentroCustoAplica().getCodigo());
		}

		if (getRequisicaoNomeImpressora() != null && getRequisicaoNomeImpressora().getTexto1() != null) {
			setNomeImpressora(getRequisicaoNomeImpressora().getTexto1());
		}

		if (DominioSimNao.S.equals(estorno)) {
			getRequisicao().setEstorno(Boolean.TRUE);
		} else if (DominioSimNao.N.equals(estorno)) {
			getRequisicao().setEstorno(Boolean.FALSE);
		} else {
			getRequisicao().setEstorno(null);
		}

		if (DominioSimNao.S.equals(getAutomatica())) {
			getRequisicao().setAutomatica(Boolean.TRUE);
		} else if (DominioSimNao.N.equals(getAutomatica())) {
			getRequisicao().setAutomatica(Boolean.FALSE);
		} else {
			getRequisicao().setAutomatica(null);
		}

	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		setRequisicao(new SceReqMaterial());
		setRequisicaoNomeImpressora(null);
		setRequisicaoSelecionada(new SceReqMaterial());
		setEstorno(null);
		setExibirBotaoNovo(false);
		setSituacaoGerada(Boolean.FALSE);
		setSituacaoConfirmada(Boolean.FALSE);
		setHabilitaAlterarRM(false);
		if (getPreencherCentroCusto()) {
			getRequisicao().setCentroCusto(centroCustoFacade.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao());
		} else {
			getRequisicao().setCentroCusto(null);
		}
		getRequisicao().setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
		setSeqAlmoxarifado(null);
		dataModel.setPesquisaAtiva(false);
		setAutomatica(null);
	}

	/**
	 * Método responsável por efetivar uma requisição de material
	 */
	public void efetivarRequisicao() {

		if (getRequisicaoSelecionada().getSeq() == null) {
			
			apresentarMsgNegocio(Severity.ERROR, "ERRO_SELECIONAR_REQUISICAO");
		} else {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPUTADA, e);
			}
			try {
				getEstoqueBeanFacade().efetivarRequisicaoMaterial(getRequisicaoSelecionada(), nomeMicrocomputador);
				apresentarMsgNegocio(Severity.INFO, "REQUISICAO_MATERIAL_EFETIVADA_COM_SUCESSO", Integer.toString(getRequisicaoSelecionada().getSeq()));
				dataModel.reiniciarPaginator();
			} catch (BaseException e) {
				getRequisicaoSelecionada().setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void selecionarRequisicaoMaterial() {
		if(this.getRequisicaoSelecionada()!=null){
			this.habilitaAlterarRM = true;
			} 
	}

	/**
	 * Método responsável por confirmar uma requisição de material
	 */
	public void confirmarRequisicao() {
		if (getRequisicaoSelecionada().getSeq() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_SELECIONAR_REQUISICAO");
		} else {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPUTADA, e);
			}
			try {
				getRequisicaoSelecionada().setIndSituacao(DominioSituacaoRequisicaoMaterial.C);

				getEstoqueBeanFacade().gravarRequisicaoMaterial(getRequisicaoSelecionada(), nomeMicrocomputador);
				requisicaoConfirmada = this.estoqueFacade.obterRequisicaoMaterial(getRequisicaoSelecionada().getSeq());
				apresentarMsgNegocio(Severity.INFO,"REQUISICAO_MATERIAL_CONFIRMADA_COM_SUCESSO", getRequisicaoSelecionada().getSeq().toString());
				dataModel.reiniciarPaginator();
			} catch (BaseException e) {
				getRequisicaoSelecionada().setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Método responsável por estornar uma requisição de material
	 */
	public void estornarRequisicao() {
		if (getNumeroRequisicao() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_SELECIONAR_REQUISICAO");
		} else {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPUTADA, e);
			}
			try {

				getEstoqueBeanFacade().estornarRequisicaoMaterial(getRequisicaoSelecionada(), nomeMicrocomputador);
				apresentarMsgNegocio(Severity.INFO, "REQUISICAO_MATERIAL_ESTORNADA_COM_SUCESSO", Integer.toString(getNumeroRequisicao()));
				dataModel.reiniciarPaginator();
			} catch (BaseException e) {
				getRequisicaoSelecionada().setEstorno(Boolean.FALSE);
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public SceReqMaterial getRequisicao() {
		return requisicao;
	}

	public void setRequisicao(SceReqMaterial requisicao) {
		this.requisicao = requisicao;
	}

	public Integer getCodigoCentroCustoRequisitante() {
		return codigoCentroCustoRequisitante;
	}

	public void setCodigoCentroCustoRequisitante(Integer codigoCentroCustoRequisitante) {
		this.codigoCentroCustoRequisitante = codigoCentroCustoRequisitante;
	}

	public Integer getCodigoCentroCustoAplicacao() {
		return codigoCentroCustoAplicacao;
	}

	public void setCodigoCentroCustoAplicacao(Integer codigoCentroCustoAplicacao) {
		this.codigoCentroCustoAplicacao = codigoCentroCustoAplicacao;
	}

	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}

	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}

	public DominioSimNao getEstorno() {
		return estorno;
	}

	public void setEstorno(DominioSimNao estorno) {
		this.estorno = estorno;
	}

	public Integer getNumeroRequisicao() {
		return numeroRequisicao;
	}

	public void setNumeroRequisicao(Integer numeroRequisicao) {
		this.numeroRequisicao = numeroRequisicao;
	}

	public Integer getNumeroReqRedirecionar() {
		return numeroReqRedirecionar;
	}

	public void setNumeroReqRedirecionar(Integer numeroReqRedirecionar) {
		this.numeroReqRedirecionar = numeroReqRedirecionar;
	}

	public Boolean getSituacaoGerada() {
		return situacaoGerada;
	}

	public void setSituacaoGerada(Boolean situacaoGerada) {
		this.situacaoGerada = situacaoGerada;
	}

	public Boolean getSituacaoConfirmada() {
		return situacaoConfirmada;
	}

	public void setSituacaoConfirmada(Boolean situacaoConfirmada) {
		this.situacaoConfirmada = situacaoConfirmada;
	}

	public SceReqMaterial getRequisicaoSelecionada() {
		return requisicaoSelecionada;
	}

	public void setRequisicaoSelecionada(SceReqMaterial requisicaoSelecionada) {
		this.requisicaoSelecionada = requisicaoSelecionada;
	}

	public Boolean getPossuiPermissaoAlterarRM() {
		return possuiPermissaoAlterarRM;
	}

	public void setPossuiPermissaoAlterarRM(Boolean possuiPermissaoAlterarRM) {
		this.possuiPermissaoAlterarRM = possuiPermissaoAlterarRM;
	}

	public Boolean getPossuiPermissaoConfirmarRM() {
		return possuiPermissaoConfirmarRM;
	}

	public void setPossuiPermissaoConfirmarRM(Boolean possuiPermissaoConfirmarRM) {
		this.possuiPermissaoConfirmarRM = possuiPermissaoConfirmarRM;
	}

	public Boolean getPossuiPermissaoConsultarRMsConfirmadasNaoImpressas() {
		return possuiPermissaoConsultarRMsConfirmadasNaoImpressas;
	}

	public void setPossuiPermissaoConsultarRMsConfirmadasNaoImpressas(Boolean possuiPermissaoConsultarRMsConfirmadasNaoImpressas) {
		this.possuiPermissaoConsultarRMsConfirmadasNaoImpressas = possuiPermissaoConsultarRMsConfirmadasNaoImpressas;
	}

	public Boolean getPossuiPermissaoConsultarRMsConfirmadas() {
		return possuiPermissaoConsultarRMsConfirmadas;
	}

	public void setPossuiPermissaoConsultarRMsConfirmadas(Boolean possuiPermissaoConsultarRMsConfirmadas) {
		this.possuiPermissaoConsultarRMsConfirmadas = possuiPermissaoConsultarRMsConfirmadas;
	}

	public Boolean getPossuiPermissaoConsultarRMsGeradas() {
		return possuiPermissaoConsultarRMsGeradas;
	}

	public void setPossuiPermissaoConsultarRMsGeradas(Boolean possuiPermissaoConsultarRMsGeradas) {
		this.possuiPermissaoConsultarRMsGeradas = possuiPermissaoConsultarRMsGeradas;
	}

	public Boolean getPossuiPermissaoEstornarRM() {
		return possuiPermissaoEstornarRM;
	}

	public void setPossuiPermissaoEstornarRM(Boolean possuiPermissaoEstornarRM) {
		this.possuiPermissaoEstornarRM = possuiPermissaoEstornarRM;
	}

	public Boolean getPossuiPermissaoVisualizarItensRequisicaoMaterial() {
		return possuiPermissaoVisualizarItensRequisicaoMaterial;
	}

	public void setPossuiPermissaoVisualizarItensRequisicaoMaterial(Boolean possuiPermissaoVisualizarItensRequisicaoMaterial) {
		this.possuiPermissaoVisualizarItensRequisicaoMaterial = possuiPermissaoVisualizarItensRequisicaoMaterial;
	}

	public Boolean getPossuiPermissaoImprimirRM() {
		return possuiPermissaoImprimirRM;
	}

	public void setPossuiPermissaoImprimirRM(Boolean possuiPermissaoImprimirRM) {
		this.possuiPermissaoImprimirRM = possuiPermissaoImprimirRM;
	}

	public LinhaReportVO getRequisicaoNomeImpressora() {
		return requisicaoNomeImpressora;
	}

	public void setRequisicaoNomeImpressora(LinhaReportVO requisicaoNomeImpressora) {
		this.requisicaoNomeImpressora = requisicaoNomeImpressora;
	}

	public String getNomeImpressora() {
		return nomeImpressora;
	}

	public void setNomeImpressora(String nomeImpressora) {
		this.nomeImpressora = nomeImpressora;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public List<SceReqMaterial> getListaRequisicoesMateriais() {
		return listaRequisicoesMateriais;
	}

	public void setListaRequisicoesMateriais(List<SceReqMaterial> listaRequisicoesMateriais) {
		this.listaRequisicoesMateriais = listaRequisicoesMateriais;
	}

	public Boolean getConsultaGeral() {
		return consultaGeral;
	}

	public void setConsultaGeral(Boolean consultaGeral) {
		this.consultaGeral = consultaGeral;
	}

	private void setDesabilitarImpressoraDestino(Boolean desabilitarImpressoraDestino) {
		this.desabilitarImpressoraDestino = desabilitarImpressoraDestino;

	}

	public Boolean getDesabilitarImpressoraDestino() {
		return desabilitarImpressoraDestino;
	}

	private void setDesabilitarIndImpresso(Boolean desabilitarIndImpresso) {
		this.desabilitarIndImpresso = desabilitarIndImpresso;

	}

	public Boolean getDesabilitarIndImpresso() {
		return desabilitarIndImpresso;
	}

	public Boolean getExibirBotoes() {
		return exibirBotoes;
	}

	public void setExibirBotoes(Boolean exibirBotoes) {
		this.exibirBotoes = exibirBotoes;
	}

	public Boolean getPreencherCentroCusto() {
		return preencherCentroCusto;
	}

	public void setPreencherCentroCusto(Boolean preencherCentroCusto) {
		this.preencherCentroCusto = preencherCentroCusto;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public IEstoqueBeanFacade getEstoqueBeanFacade() {
		return estoqueBeanFacade;
	}

	public void setEstoqueBeanFacade(IEstoqueBeanFacade estoqueBeanFacade) {
		this.estoqueBeanFacade = estoqueBeanFacade;
	}

	public Boolean getOrigemConsultarRM() {
		return origemConsultarRM;
	}

	public void setOrigemConsultarRM(Boolean origemConsultarRM) {
		this.origemConsultarRM = origemConsultarRM;
	}

	public List<FccCentroCustos> pesquisarTodosCentroCustoAtivos(String parametro) {
		return centroCustoFacade.pesquisarCentroCustosAtivos(parametro);
	}

	/**
	 * Verifica se deve desabilitar o botão estornar. False não desabilita, e
	 * true desabilita
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 15/03/2012
	 */
	public Boolean desabilitarBotaoEstornar() {
		Boolean result = false;
		if (!possuiPermissaoEstornarRM || getRequisicaoSelecionada() == null
				|| !DominioSituacaoRequisicaoMaterial.E.equals(getRequisicaoSelecionada().getIndSituacao())
				|| (getRequisicaoSelecionada() != null && isRequisicaoSelecionadaAutomatica())) {
			result = true;
		}
		return result;
	}

	public Boolean desabilitarBotaoNovo() {
		Boolean result = false;

		if (!possuiPermissaoGerarRM) {
			result = true;
		}

		return result;
	}

	public Boolean getPossuiPermissaoGerarRM() {
		return possuiPermissaoGerarRM;
	}

	public void setPossuiPermissaoGerarRM(Boolean possuiPermissaoGerarRM) {
		this.possuiPermissaoGerarRM = possuiPermissaoGerarRM;
	}

	public Boolean getPossuiPermissaoCancelarRMGerada() {
		return possuiPermissaoCancelarRMGerada;
	}

	public void setPossuiPermissaoCancelarRMGerada(Boolean possuiPermissaoCancelarRMGerada) {
		this.possuiPermissaoCancelarRMGerada = possuiPermissaoCancelarRMGerada;
	}

	public Boolean getPossuiPermissaoCancelarRMConfirmada() {
		return possuiPermissaoCancelarRMConfirmada;
	}

	public void setPossuiPermissaoCancelarRMConfirmada(Boolean possuiPermissaoCancelarRMConfirmada) {
		this.possuiPermissaoCancelarRMConfirmada = possuiPermissaoCancelarRMConfirmada;
	}

	public Boolean desabilitarBotaoCancelar() {
		Boolean retorno = Boolean.TRUE;

		if (getRequisicaoSelecionada() != null
				&& !isRequisicaoSelecionadaAutomatica()
				&& (DominioSituacaoRequisicaoMaterial.G.equals(getRequisicaoSelecionada().getIndSituacao()) || DominioSituacaoRequisicaoMaterial.C
						.equals(getRequisicaoSelecionada().getIndSituacao()))
				&& ((DominioSituacaoRequisicaoMaterial.C.equals(getRequisicaoSelecionada().getIndSituacao()) && getPossuiPermissaoCancelarRMConfirmada()) || (DominioSituacaoRequisicaoMaterial.G
						.equals(getRequisicaoSelecionada().getIndSituacao()) && getPossuiPermissaoCancelarRMGerada()))) {
			retorno = Boolean.FALSE;
		}
		if(requisicaoConfirmada!=null){
			retorno = Boolean.FALSE;
		}

		return retorno;
	}

	/**
	 * Em conversa com o analista no dia 16/03, ele informou que a estória 5627
	 * não iria ser implementada no momento, mas necessitava de poder cancelar,
	 * por isto a criação deste método.
	 * 
	 * @author bruno.mourao
	 * @since 17/03/2012
	 */
	public void cancelarRM() {
		//carrega requisicao seleciona com joins, na pesquisa isto nao é feito pois é utilizada uma projection
		setRequisicaoSelecionada(estoqueFacade.obterRequisicaoMaterial(getRequisicaoSelecionada().getSeq()));
		requisicaoSelecionada.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPUTADA, e);
		}
		try {
			estoqueFacade.persistirRequisicaoMaterial(requisicaoSelecionada, nomeMicrocomputador);

			apresentarMsgNegocio(Severity.INFO,
					"REQUISICAO_MATERIAL_CANCELADA_COM_SUCESSO",
					Integer.toString(getRequisicaoSelecionada().getSeq()));
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public Boolean desabilitarBotaoEfetivar() {

		Boolean result = Boolean.TRUE;
		// Consulta RM

		if (!getConsultaGeral() && getRequisicaoSelecionada() != null && !isRequisicaoSelecionadaAutomatica()) {
			if (getPossuiPermissaoEfetivarRM()
					&& DominioSituacaoRequisicaoMaterial.C.equals(getRequisicaoSelecionada().getIndSituacao())) {
				result = Boolean.FALSE;
			}
		}
		
		if(requisicaoConfirmada!=null){
			result = Boolean.FALSE;
		}
		return result;
	}

	private Boolean isRequisicaoSelecionadaAutomatica() {
		Boolean result = Boolean.FALSE;
		if (getRequisicaoSelecionada() != null && getRequisicaoSelecionada().getAutomatica() != null) {
			result = getRequisicaoSelecionada().getAutomatica();
		}
		return result;
	}

	/**
	 * Retorna true se o botão imprimir deve ser desabilitado
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 17/03/2012
	 */
	public Boolean desabilitarBotaoImprimir() {

		Boolean result = false;

		// se nenhuma requisição estiver selecionada, desabilita
		if (getRequisicaoSelecionada() == null) {
			result = true;
		}

		// Se for consulta geral
		if (getConsultaGeral()) {
			// Usuário precisa ter permissão imprimirRM
			if (!possuiPermissaoImprimirRM) {
				result = true;
			}

			// consulta RM
		} else {
			if (isRequisicaoSelecionadaAutomatica()) {
				return true;
			}
			// RN32
			if (possuiPermissaoConsultarRMsConfirmadasNaoImpressas) {
				result = true;
			}
			// RN 33
			if (possuiPermissaoConsultarRMsConfirmadasNaoImpressas && possuiPermissaoConsultarRMsConfirmadas) {
				// RN 35
				if (getRequisicaoSelecionada() != null
						&& DominioSituacaoRequisicaoMaterial.C.equals(getRequisicaoSelecionada().getIndSituacao())
						&& DominioImpresso.N.equals(getRequisicaoSelecionada().getIndImpresso())) {
					result = false;
				}
			}

		}
		
		if(requisicaoConfirmada!=null){
			result = Boolean.FALSE;
		}

		return result;
	}

	public String noSelectionLabelSituacao() {
		String retorno = "";
		// Se for consulta geral, pode ter a opção Todas
		if (getConsultaGeral() || possuiTodasPermissoesSituacao()) {
			retorno = "Todas";
		}

		return retorno;
	}

	private Boolean possuiTodasPermissoesSituacao() {

		Boolean retorno = true;

		retorno = retorno && (possuiPermissaoConsultarRMsConfirmadasNaoImpressas || possuiPermissaoConsultarRMsConfirmadas);
		retorno = retorno && possuiPermissaoConsultarRMsGeradas;

		return retorno;
	}

	public Boolean getPossuiPermissaoEfetivarRM() {
		return possuiPermissaoEfetivarRM;
	}

	public void setPossuiPermissaoEfetivarRM(Boolean possuiPermissaoEfetivarRM) {
		this.possuiPermissaoEfetivarRM = possuiPermissaoEfetivarRM;
	}

	public DominioSimNao getAutomatica() {
		return automatica;
	}

	public void setAutomatica(DominioSimNao automatica) {
		this.automatica = automatica;
	}

	public DynamicDataModel<SceReqMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceReqMaterial> dataModel) {
		this.dataModel = dataModel;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	protected boolean isAlmoxarife() {
		return isAlmoxarife;
	}

	protected void setAlmoxarife(boolean isAlmoxarife) {
		this.isAlmoxarife = isAlmoxarife;
	}

	public Boolean getHabilitaAlterarRM() {
		return habilitaAlterarRM;
	}

	public void setHabilitaAlterarRM(Boolean habilitaAlterarRM) {
		this.habilitaAlterarRM = habilitaAlterarRM;
	}
	
	
	
}