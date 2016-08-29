package br.gov.mec.aghu.estoque.pesquisa.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.action.GeracaoRequisicaoMaterialController;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaRequisicaoMaterialPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<SceReqMaterial> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaRequisicaoMaterialPaginatorController.class);

	private static final long serialVersionUID = -1094402673817113269L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@Inject
	private GeracaoRequisicaoMaterialController geracaoRequisicaoMaterialController;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	private static final String PAGE_GERACAO_RM = "estoque-geracaoRequisicaoMaterial";
	private static final String PAGE_ORIGEM = "estoque-pesquisaRequisicaoMaterial";

	private DominioSimNao automatica;

	private PesquisaRequisicaoMaterialVO filtro;

	private DominioSituacaoRequisicaoMaterial[] situacoes = { DominioSituacaoRequisicaoMaterial.C, DominioSituacaoRequisicaoMaterial.G };

	private Boolean isGeracaoDeRequisicao = Boolean.FALSE;
	private Boolean isExibiPesquisa = Boolean.FALSE;
	private Boolean mostraModalCancelamento = Boolean.FALSE;
	private SceReqMaterial rmCancelamento;
	private RapServidores servidorLogado;
	private FccCentroCustos ccAtuacao;
	private boolean isAlmoxarife;
	private Boolean pesquisar;
	private Set<Integer> listaHierarquica;
	private boolean possuiCaractGppg;
	private FccCentroCustos ccFipe;
	
	//Consulta estava sendo muito pesada e carregada em todas as pesquisas
	private List<FccCentroCustos> backupAllCentroCustos;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		this.mostraModalCancelamento = false;
		this.pesquisar = false;
		dataModel.reiniciarPaginator();
		this.setIsExibiPesquisa(Boolean.TRUE);
	}

	public void iniciar() {
		if(backupAllCentroCustos == null){
			try {
				backupAllCentroCustos = this.centroCustoFacade
						.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao("", DominioCaracteristicaCentroCusto.GERAR_RM);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		carregarFilltrosPadrao();
		atualizarListaPosGeracaoRequisicao();
		pesquisar();
	
	}

	private void carregarFilltrosPadrao() {
		if (!dataModel.getPesquisaAtiva()) {
			this.filtro = new PesquisaRequisicaoMaterialVO();

			this.servidorLogado = null;

			try {
				servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			} catch (ApplicationBusinessException esr) {
				apresentarExcecaoNegocio(esr);
			}

			this.ccAtuacao = this.centroCustoFacade.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao();
			
			filtro.setCentroCustosReq(this.ccAtuacao);	
			isAlmoxarife = this.cascaFacade.usuarioTemPerfil(servidorLogado.getUsuario(), "ADM29");
			
			ccFipe = this.comprasCadastrosBasicosFacade.obterCcAplicacaoAlteracaoRmGppg(getServidorLogado());
			setPossuiCaractGppg(false);
			if (ccFipe != null) {
				this.listaHierarquica = centroCustoFacade.pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
				possuiCaractGppg = (this.listaHierarquica != null && !this.listaHierarquica.isEmpty());
			}
		}
	}

	private void atualizarListaPosGeracaoRequisicao() {
		if (isGeracaoDeRequisicao) {
			if (isExibiPesquisa) {
				limparPesquisa();
				carregarFilltrosPadrao();
				pesquisar();
				isExibiPesquisa = Boolean.FALSE;
			}
			isGeracaoDeRequisicao = Boolean.FALSE;
		} else {
			carregarFilltrosPadrao();
			pesquisar();
		}
	}

	public void limparPesquisa() {
		this.mostraModalCancelamento = false;
		this.filtro = new PesquisaRequisicaoMaterialVO();
		this.setIsGeracaoDeRequisicao(Boolean.FALSE);
		this.setIsExibiPesquisa(Boolean.FALSE);
		dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {

		Long result = null;

		// Caso não contenha Centro de Custo Requisição
		if (this.filtro.getCentroCustosReq() == null) {
			// Realiza a pesquisa com o centro de custo do usuário logado
			List<FccCentroCustos> centroCustosReq;
			try {
				centroCustosReq = this.obterFccCentroCustos("");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
			if (centroCustosReq != null && centroCustosReq.size() > 0) {
				this.filtro.setCentroCustosReq(centroCustosReq.get(0));
			}
			result = this.estoqueFacade.pesquisaRequisicoesMateriaisCount(this.filtro);
			this.filtro.setCentroCustosReq(null);
			this.filtro.setCentrosCustosRequisicaoHierarquia(null);
		} else {
			result = this.estoqueFacade.pesquisaRequisicoesMateriaisCount(this.filtro);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		asc = Boolean.FALSE;
		List<SceReqMaterial> result = null;

		// Caso não contenha Centro de Custo Requisição
		if (this.filtro.getCentroCustosReq() == null) {
			// Realiza a pesquisa com o centro de custo do usuário logado
			if (backupAllCentroCustos != null && backupAllCentroCustos.size() > 0) {
				try {
					this.filtro.setCentroCustosReq(obterFccCentroCustos("").get(0));
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			result = this.estoqueFacade.pesquisaRequisicoesMateriais(firstResult, maxResult, "seq", asc, filtro);
			this.filtro.setCentroCustosReq(null);
			this.filtro.setCentrosCustosRequisicaoHierarquia(null);
		} else {
			result = this.estoqueFacade.pesquisaRequisicoesMateriais(firstResult, maxResult, "seq", asc, filtro);
		}

		if (result == null) {
			result = new ArrayList<SceReqMaterial>();
		}

		return result;
	}
	
	public void prepararCancelamentoItem(SceReqMaterial item) {
		this.rmCancelamento = item;
		this.setMostraModalCancelamento(true);
	}
	
	public Boolean habilitarSitGerada(SceReqMaterial item) {
		return possuiCaractGppg && listaHierarquica.contains(item.getCentroCustoAplica().getCodigo()) && (item.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C));
	}
	
	public Boolean habilitarCancelamento(SceReqMaterial item) {
		Boolean ret = Boolean.FALSE;

		// se eh o usuario solicitante...
		if (Objects.equals(item.getServidor(),this.servidorLogado) || Objects.equals(item.getCentroCusto(), this.ccAtuacao)) {
			
			if (item.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)) {
				ret = true;	
			}
		}

		// se eh o almoxarife
		if (Objects.equals(item.getAlmoxarifado().getCentroCusto(), this.ccAtuacao) && isAlmoxarife) {
			if (item.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G) || item.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				ret = true;				
			}
		}
		return ret;
	}
	
	public void cancelarRm() {
		SceReqMaterial sceReqMateriaisEstornar = this.estoqueFacade.obterRequisicaoMaterial(this.rmCancelamento.getSeq());
		
		if (sceReqMateriaisEstornar != null) {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			try {
				estoqueFacade.cancelarRequisicaoMaterial(sceReqMateriaisEstornar, nomeMicrocomputador);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CANCELAMENTO_REQUISICAO_MATERIAL");
				pesquisar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		this.rmCancelamento = null;
		this.setMostraModalCancelamento(false);
	}
	
	public void alterarSituacaoGerada(SceReqMaterial item) {
		SceReqMaterial sceReqMateriaisAlterar = this.estoqueFacade.obterRequisicaoMaterial(item.getSeq());
		
		if (sceReqMateriaisAlterar != null) {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			try {
				estoqueFacade.alterarSituacaoGeradaRequisicaoMaterial(sceReqMateriaisAlterar, nomeMicrocomputador);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_SIT_REQUISICAO_MATERIAL");
				pesquisar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		this.rmCancelamento = null;
		this.setMostraModalCancelamento(false);
	}
	

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de CC Requisicao
	public List<FccCentroCustos> obterFccCentroCustos(String parametro) throws ApplicationBusinessException, ApplicationBusinessException {
		// Se for consultaRM, somente ccustos da hierarquia do ccusto do
		// usuario, incluindo atuaçao e lotação
		List<FccCentroCustos> retorno = null;
		if(parametro == null || "".equals(parametro.trim())){
			retorno = backupAllCentroCustos;
		}else{
			retorno = this.centroCustoFacade
					.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(
							parametro, DominioCaracteristicaCentroCusto.GERAR_RM);
		}
		this.filtro.setCentrosCustosRequisicaoHierarquia(retorno);
		return retorno;
	}

	// Metodo para pesquisa na suggestion box de CC Aplicacao
	public List<FccCentroCustos> obterFccCentroCustosAplicacao(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosAplicacaoOrdemDescricao(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de Grupo de Materiais
	public List<ScoGrupoMaterial> obterScoGrupoMaterial(String objPesquisa) {
		Short almoSeq = (this.filtro.getAlmoxarifado() != null) ? this.filtro.getAlmoxarifado().getSeq() : null;
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroAlmoxarifado(almoSeq, objPesquisa);
	}

	public String iniciarRequisicao() {
		geracaoRequisicaoMaterialController.setSeqReq(null);
		geracaoRequisicaoMaterialController.setRmsSeq(null);
		geracaoRequisicaoMaterialController.setEalSeq(null);
		geracaoRequisicaoMaterialController.setReqMaterial(null);
		geracaoRequisicaoMaterialController.setOrigemPesquisa(PAGE_ORIGEM);
		return PAGE_GERACAO_RM;
	}

	public String visualizarGeracaoRM() {
		return PAGE_GERACAO_RM;
	}

	public PesquisaRequisicaoMaterialVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaRequisicaoMaterialVO filtro) {
		this.filtro = filtro;
	}

	public DominioSituacaoRequisicaoMaterial[] getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(DominioSituacaoRequisicaoMaterial[] situacoes) {
		this.situacoes = situacoes;
	}

	public DominioSimNao getAutomatica() {
		return automatica;
	}

	public void setAutomatica(DominioSimNao automatica) {
		this.automatica = automatica;
	}

	public Boolean getIsGeracaoDeRequisicao() {
		return isGeracaoDeRequisicao;
	}

	public void setIsGeracaoDeRequisicao(Boolean isGeracaoDeRequisicao) {
		this.isGeracaoDeRequisicao = isGeracaoDeRequisicao;
	}

	public Boolean getIsExibiPesquisa() {
		return isExibiPesquisa;
	}

	public void setIsExibiPesquisa(Boolean isExibiPesquisa) {
		this.isExibiPesquisa = isExibiPesquisa;
	}

	public DynamicDataModel<SceReqMaterial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceReqMaterial> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getMostraModalCancelamento() {
		return mostraModalCancelamento;
	}

	public void setMostraModalCancelamento(Boolean mostraModalCancelamento) {
		this.mostraModalCancelamento = mostraModalCancelamento;
	}

	public SceReqMaterial getRmCancelamento() {
		return rmCancelamento;
	}

	public void setRmCancelamento(SceReqMaterial rmCancelamento) {
		this.rmCancelamento = rmCancelamento;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public FccCentroCustos getCcAtuacao() {
		return ccAtuacao;
	}

	public void setCcAtuacao(FccCentroCustos ccAtuacao) {
		this.ccAtuacao = ccAtuacao;
	}

	public boolean isAlmoxarife() {
		return isAlmoxarife;
	}

	public void setAlmoxarife(boolean isAlmoxarife) {
		this.isAlmoxarife = isAlmoxarife;
	}

	public Boolean getPesquisar() {
		return pesquisar;
	}

	public void setPesquisar(Boolean pesquisar) {
		this.pesquisar = pesquisar;
	}

	public Set<Integer> getListaHierarquica() {
		return listaHierarquica;
	}

	public void setListaHierarquica(Set<Integer> listaHierarquica) {
		this.listaHierarquica = listaHierarquica;
	}

	public boolean isPossuiCaractGppg() {
		return possuiCaractGppg;
	}

	public void setPossuiCaractGppg(boolean possuiCaractGppg) {
		this.possuiCaractGppg = possuiCaractGppg;
	}

	public FccCentroCustos getCcFipe() {
		return ccFipe;
	}

	public void setCcFipe(FccCentroCustos ccFipe) {
		this.ccFipe = ccFipe;
	}

}