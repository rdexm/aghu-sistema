package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoVO;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Controller das ações da pagina de listagem de diagnostico.
 * 
 * @author fsantos
 * 
 */
public class DiagnosticoPerinatoPaginatorController extends ActionController implements
		ActionPaginator {

	private static final long serialVersionUID = 6589546988357451478L;


	// ----- PAGINAS
	private final String PAGE_CADASTRO_DIAGNOSTICO = "diagnosticoCRUD";

	private DiagnosticoFiltro filtro;

	private boolean pesquisaAtiva;

	private boolean permManterDiagnostico;

	private boolean permConsultarDiagnostico;

	private DiagnosticoVO vo;
	
	private DominioSimNao indPlacar;

	@Inject @Paginator
	private DynamicDataModel<DiagnosticoVO> dataModel;
	@Inject
	private IEmergenciaFacade emergenciaFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		setVo(null);
		// atualiza permissao para consultar
		setPermConsultarDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarDiagnostico", "consultar"));
		// atualiza permissao para manter
		setPermManterDiagnostico(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar"));
		
		// atualiza permissao para edicao
		this.dataModel.setUserEditPermission(isPermManterDiagnostico());
		
		filtro = new DiagnosticoFiltro();
	}


	/**
	 * Ação do botão PESQUISAR da pagina diagnostico
	 */
	public void pesquisar() {
		filtro.setIndPlacar(getIndPlacarToBoolean(this.indPlacar));
		pesquisaAtiva = true;
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Ação do botão LIMPAR da pagina diagnostico
	 */
	public void limparPesquisa() {
		indPlacar = null;
		filtro.setDescricao(null);
		filtro.setSeq(null);
		filtro.setIndPlacar(null);
		filtro.setIndSituacao(null);
		setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão NOVO/Editar da pagina diagnostico
	 */
	public String redirecionarCadastro() {
		return PAGE_CADASTRO_DIAGNOSTICO;
	}

	/**
	 * Ação do botão ATIVAR/INATIVAR da pagina diagnostico
	 * 
	 * @param seq
	 */
	public void ativarInativar(Integer seq) {
		McoDiagnostico diagnostico = this.emergenciaFacade.obterMcoDiagnostico(seq);
		if (diagnostico != null) {
			try {
				this.emergenciaFacade.ativarInativarDiagnostico(diagnostico);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_SITUACAO_DIAGNOSTICO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.obterListaDiagnosticoCount(getFiltro());
	}

	@Override
	public List<DiagnosticoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		try {
			return this.emergenciaFacade.pesquisarDiagnosticos(firstResult,maxResults, orderProperty, asc, getFiltro());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private Boolean getIndPlacarToBoolean(DominioSimNao indPlacar) {
		if (indPlacar == DominioSimNao.S) {
			return Boolean.TRUE;
		}
		if (indPlacar == DominioSimNao.N) {
			return Boolean.FALSE;
		}
		return null;
	}

	public DiagnosticoFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(DiagnosticoFiltro filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<DiagnosticoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DiagnosticoVO> dataModel) {
		this.dataModel = dataModel;
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

	public boolean isPermManterDiagnostico() {
		return permManterDiagnostico;
	}

	public void setPermManterDiagnostico(boolean permManterDiagnostico) {
		this.permManterDiagnostico = permManterDiagnostico;
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

	public DiagnosticoVO getVo() {
		return vo;
	}

	public void setVo(DiagnosticoVO vo) {
		this.vo = vo;
	}
}
