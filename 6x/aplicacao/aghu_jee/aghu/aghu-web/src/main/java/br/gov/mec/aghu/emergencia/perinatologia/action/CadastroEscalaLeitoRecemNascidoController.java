package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascido;
import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascidoId;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class CadastroEscalaLeitoRecemNascidoController extends ActionController {



	/**
	 * 
	 */
	private static final long serialVersionUID = 5985905230600248374L;

	@Inject                                                          
	private IEmergenciaFacade emergenciaFacade;
	
	private Servidor servidor;
	private LeitoVO leito;	
	
	private final String PAGE_PESQUISA = "pesquisaEscalaLeitoRecemNascido";
	                                                                                                                                                                                                                                                                         
	private Boolean hasPermission;

	@PostConstruct
	public void init() {
		begin(conversation);
		verificarPermissoes();
	}	

	public void verificarPermissoes() {
		hasPermission = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterEscalas", "executar");
	}
		
	public String gravar() {
		try {
			emergenciaFacade.inserirMcoEscalaLeitoRecemNascido(criarEntity());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LEITO_INSERIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cancelar();
	}
	
	private McoEscalaLeitoRecemNascido criarEntity() {
		McoEscalaLeitoRecemNascido entity = new McoEscalaLeitoRecemNascido();
		entity.setLeitoID(getLeito().getLeitoID());
		entity.setServidorMatriculaResponsavel(getServidor().getMatricula());
		entity.setServidorVinCodigoResponsavel(getServidor().getVinculo());
		McoEscalaLeitoRecemNascidoId id = new McoEscalaLeitoRecemNascidoId(getLeito().getLeitoID(), getServidor().getMatricula(), getServidor().getVinculo());
		entity.setId(id);
		return entity;
	}
	
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	public String cancelar() {
		limparParametros();
		return PAGE_PESQUISA;
	}
	
	private void limparParametros(){
		setServidor(null);
		setLeito(null);
	}
	
	 
	public List<LeitoVO> pesquisarLeitos(String param) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarLeitos((String) param),pesquisarLeitosCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<LeitoVO>();
	}
	
	public Long pesquisarLeitosCount(String param) {
		try {
			return emergenciaFacade.pesquisarLeitosCount((String) param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0l;
	}
	
	public List<Servidor> pesquisarServidores(String param) {
		try {
			return  this.returnSGWithCount(emergenciaFacade.pesquisarServidores((String) param),pesquisarServidoresCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<Servidor>();
	}
	
	public Long pesquisarServidoresCount(String param) {
		try {
			return emergenciaFacade.pesquisarServidoresCount((String) param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0l;
	}

	
	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public LeitoVO getLeito() {
		return leito;
	}

	public void setLeito(LeitoVO leito) {
		this.leito = leito;
	}
}
