package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;


/**
 * Controller das ações da pagina de listagem de diagnostico.
 * 
 * @author marcelo.corati
 * 
 */
public class BallardPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6589546988357451478L;
	
	private final String PAGE_CADASTRO = "ballardCRUD";

	private boolean pesquisaAtiva;

	private boolean permManter;
	
	private McoTabBallard vo;
	
	@Inject @Paginator
	private DynamicDataModel<McoTabBallard> dataModel;
	
	private Short escore;

	@EJB
	private IEmergenciaFacade emergenciaFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		
		// atualiza permissao para alterar
		//setPermConsultarDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar"));
		// atualiza permissao para consultar
		//setPermManterDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarDiagnostico", "consultar"));

		// atualiza permissao para edicao
		this.dataModel.setUserEditPermission(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterBallard", "manter"));
		this.dataModel.setUserRemovePermission(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterBallard", "manter"));
		this.setPermManter(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterBallard", "manter"));
		
		escore = null;
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
		escore = null;
		setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão NOVO/Editar da pagina diagnostico
	 */
	public String redirecionarCadastro() {
		return PAGE_CADASTRO;
	}
	
	public void excluir()  {
		emergenciaFacade.removerBallard(vo.getSeq());
		apresentarMsgNegocio(Severity.INFO,"EXCLUSAO_BALLARD_SUCESSO");
	}

	// ### Paginação ###
	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.listarBallardCount(escore);
	}

	@Override
	public List<McoTabBallard> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.emergenciaFacade.listarBallard(escore, firstResult, maxResults, orderProperty, asc);
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

	public McoTabBallard getVo() {
		return vo;
	}


	public void setVo(McoTabBallard vo) {
		this.vo = vo;
	}


	public DynamicDataModel<McoTabBallard> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<McoTabBallard> dataModel) {
		this.dataModel = dataModel;
	}


	public Short getEscore() {
		return escore;
	}


	public void setEscore(Short escore) {
		this.escore = escore;
	}
	
}
