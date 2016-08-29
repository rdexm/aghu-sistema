package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;


/**
 * Controller das ações da pagina de listagem 
 * 
 * @author marcelo.corati
 * 
 */
public class SindromePaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3775666105144404553L;

	private final String PAGE_CADASTRO = "sindromeCRUD";

	private boolean pesquisaAtiva;

	private boolean permManter;

	private boolean permConsultarDiagnostico;
	
	private McoSindrome vo;
	
	private String descricao;
	
	private Integer codigo;
	
	private DominioSituacao indSituacao;
	

	//@Inject @Paginator
	//private DynamicDataModel<DiagnosticoVO> dataModel;
	
	@Inject @Paginator
	private DynamicDataModel<McoSindrome> dataModel;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		
		
		this.setPermManter(getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterSindrome", "manter"));
		
		codigo = null;
		descricao = null;
		indSituacao = null;
	}


	/**
	 * Ação do botão PESQUISAR da pagina 
	 */
	public void pesquisar() {
		pesquisaAtiva = true;
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Ação do botão LIMPAR da pagina 
	 */
	public void limparPesquisa() {
		codigo = null;
		descricao = null;
		indSituacao =  null;
		setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão NOVO/Editar da pagina 
	 */
	public String redirecionarCadastro() {
		//return PAGE_CADASTRO_DIAGNOSTICO;
		return PAGE_CADASTRO;
	}

	// ### Paginação ###
	@Override
	public Long recuperarCount() {
		String situacao	= null;
		if(indSituacao != null){
			situacao = indSituacao.toString();
		}
		return this.emergenciaFacade.listarSindromeCount(codigo, descricao, situacao);
	}

	@Override
	public List<McoSindrome> recuperarListaPaginada(Integer firstResult,Integer maxResults, String orderProperty, boolean asc) {
		String situacao	= null;
		if(indSituacao != null){
			situacao = indSituacao.toString();
		}
		return this.emergenciaFacade.listarSindrome(codigo ,descricao, situacao, firstResult, maxResults, orderProperty, asc);

	}
	
	public void ativarInativar(Integer seq){
		this.pacienteFacade.ativarInativarSindrome(seq);
		apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_SITUACAO_SINDROME");
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

	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Integer getCodigo() {
		return codigo;
	}


	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	public McoSindrome getVo() {
		return vo;
	}


	public void setVo(McoSindrome vo) {
		this.vo = vo;
	}


	public DynamicDataModel<McoSindrome> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<McoSindrome> dataModel) {
		this.dataModel = dataModel;
	}
	
}
