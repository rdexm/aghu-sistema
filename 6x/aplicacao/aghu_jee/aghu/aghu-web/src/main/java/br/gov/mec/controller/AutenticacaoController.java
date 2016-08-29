package br.gov.mec.controller;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class AutenticacaoController extends ActionController {
	private static final long serialVersionUID = 5586472486471472589L;

	private static final Log LOG = LogFactory.getLog(AutenticacaoController.class);

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	private boolean permAutenticarUsuarioLogado;

	private String username;
	private String password;
	private ServidorIdVO servidorIdVO;
	private String mensagemErro = "ERRO_PERMISSAO_AUTENTICAR_USUARIO"; 

	public void autenticar(boolean validarSenha) {
		servidorIdVO = null;

		String usuarioLogado = super.obterLoginUsuarioLogado();
		this.permAutenticarUsuarioLogado = getPermissionService().usuarioTemPermissao(usuarioLogado, "autenticarUsuarioLogado", "executar");

		if (permAutenticarUsuarioLogado) {
			try {
				if(validarSenha){
					this.cascaFacade.validarSenha(username, password);
				}
				RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(username);
				if (servidor != null) {
					servidorIdVO = new ServidorIdVO(servidor.getId().getVinCodigo(), servidor.getId().getMatricula());
					RequestContext.getCurrentInstance().execute("jsFinalizarAutenticacao();");
				}
			} catch (ApplicationBusinessException e) {
				FacesContext.getCurrentInstance().validationFailed();
				LOG.error("Erro ao executar o serviço", e);
				this.apresentarMensagemErro(e.getMessage(), false);
			} catch (RuntimeException e) {
				FacesContext.getCurrentInstance().validationFailed();
				LOG.error("Erro ao executar o serviço", e);
				this.apresentarMensagemErro("MSG_SERVICO_INDISPONIVEL", true);
			}
		} else {
			FacesContext.getCurrentInstance().validationFailed();
			this.apresentarMensagemErro(mensagemErro, true);
			mensagemErro = "ERRO_PERMISSAO_AUTENTICAR_USUARIO";
		}
	}

	private void apresentarMensagemErro(String message, boolean isFromBundle, Object... params) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (isFromBundle) {
			message = WebUtil.initLocalizedMessage(message, null);
		}
		context.addMessage("Messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ServidorIdVO getServidorIdVO() {
		return servidorIdVO;
	}

	public void setServidorIdVO(ServidorIdVO servidorIdVO) {
		this.servidorIdVO = servidorIdVO;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}
}
