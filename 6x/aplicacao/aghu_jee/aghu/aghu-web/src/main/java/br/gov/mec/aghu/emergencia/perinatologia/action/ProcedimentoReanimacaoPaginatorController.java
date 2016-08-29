package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de listagem de diagnostico.
 * 
 * @author marcelo.corati
 * 
 */
public class ProcedimentoReanimacaoPaginatorController extends ActionController implements
		ActionPaginator {

	private static final long serialVersionUID = 6589546988357451478L;



	
	private final String PAGE_CADASTRO = "procedimentoReanimacaoCRUD";

	private DiagnosticoFiltro filtro;

	private boolean pesquisaAtiva;

	private boolean permManter;

	private boolean permConsultarDiagnostico;

	private DominioSimNao indPlacar;
	
	private ProcedimentoReanimacaoVO vo;

	//@Inject @Paginator
	//private DynamicDataModel<DiagnosticoVO> dataModel;
	
	
	@Inject @Paginator
	private DynamicDataModel<ProcedimentoReanimacaoVO> dataModel;
		
	@EJB
	private IPacienteFacade pacienteFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		
		// atualiza permissao para alterar
		//setPermConsultarDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar"));
		// atualiza permissao para consultar
		//setPermManterDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarDiagnostico", "consultar"));

		// atualiza permissao para edicao
		this.dataModel.setUserEditPermission(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterProcReanim", "manter"));
		this.setPermManter(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterProcReanim", "manter"));
		
		filtro = new DiagnosticoFiltro();
	}


	/**
	 * Ação do botão PESQUISAR da pagina diagnostico
	 */
	public void pesquisar() {
		pesquisaAtiva = true;
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Ação do botão LIMPAR da pagina diagnostico
	 */
	public void limparPesquisa() {
		filtro.setDescricao(null);
		filtro.setIndSituacao(null);
		setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão NOVO/Editar da pagina diagnostico
	 */
	public String redirecionarCadastro() {
		//return PAGE_CADASTRO_DIAGNOSTICO;
		return PAGE_CADASTRO;
	}

	// ### Paginação ###
	@Override
	public Long recuperarCount() {
			DominioSituacao situacao	= null;
			if(filtro.getIndSituacao() != null){
				situacao = filtro.getIndSituacao();
			}
			return this.pacienteFacade.listarProcReanimacaoCount(filtro.getDescricao(), situacao);
	}

	@Override
	public List<ProcedimentoReanimacaoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
			//return this.perinatologiaFacade.pesquisarDiagnosticos(firstRIPacienteFacadesult,maxResults, orderProperty, asc, getFiltro());
			DominioSituacao situacao = null;
			if(filtro.getIndSituacao() != null){
				situacao = filtro.getIndSituacao();
			}
			return this.pacienteFacade.listarProcReanimacao(filtro.getDescricao(), situacao, firstResult, maxResults, orderProperty, asc);	
	}

	public DiagnosticoFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(DiagnosticoFiltro filtro) {
		this.filtro = filtro;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public boolean isPermManter() {
		return permManter;
	}


	public void setPermManter(boolean permManter) {
		this.permManter = permManter;
	}


	public boolean isPermConsultarDiagnostico() {
		return permConsultarDiagnostico;
	}

	public void setPermConsultarDiagnostico(boolean permConsultarDiagnostico) {
		this.permConsultarDiagnostico = permConsultarDiagnostico;
	}

	public DominioSimNao getIndPlacar() {
		return indPlacar;
	}

	public void setIndPlacar(DominioSimNao indPlacar) {
		this.indPlacar = indPlacar;
	}

	public DynamicDataModel<ProcedimentoReanimacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ProcedimentoReanimacaoVO> dataModel) {
		this.dataModel = dataModel;
	}


	public ProcedimentoReanimacaoVO getVo() {
		return vo;
	}


	public void setVo(ProcedimentoReanimacaoVO vo) {
		this.vo = vo;
	}
	
}
