package br.gov.mec.controller;

import java.net.UnknownHostException;
import java.security.Principal;

import javax.ejb.EJB;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.http.HttpConversationContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.autenticacao.AghuPrincipal;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.action.SessionAttributes;
import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Classe que agrupa métodos ouvintes de eventos relacionados ao login e ao
 * logout dos usuários no AGHU.
 * 
 * @author geraldo
 * 
 */
public class LoginObserver {
	
	
	private ICascaFacade cascaFacade = ServiceLocator.getBean(
			ICascaFacade.class, "aghu-casca");

	private IParametroFacade parametroFacade = ServiceLocator.getBean(
			IParametroFacade.class, "aghu-configuracao");

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private HostRemotoCache hostRemoto;
	
	@Inject
    private HttpConversationContext conversationContext;	

	private static final Log LOG = LogFactory.getLog(LoginObserver.class);

	/**
	 * Método acionado sempre que um login é executado com sucesso no AGHU,
	 * disparando as ações associadas.
	 * 
	 * @param eventoLogin
	 * @throws ApplicationBusinessException
	 * @throws UnknownHostException
	 */
	public void observarAcessoBemSucedido(@Observes EventoUsuarioLogado eventoLogin) throws ApplicationBusinessException, UnknownHostException {
		String loginUsuario = eventoLogin.getUsername();

		Usuario usuario = cascaFacade.recuperarUsuario(loginUsuario);

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

		this.registrarAcessoBemSucedido(loginUsuario);
		this.atualizarUltimoAcesso(usuario, session);
		this.ajustarTempoSessao(usuario, session);
		this.criptografarSenha(session, eventoLogin.getPassword());
		this.gerarPendenciasSolicitacaoConsultoria();
		
		session.setAttribute(SessionAttributes.USUARIO_LOGADO.toString(), loginUsuario);
	}

	private void gerarPendenciasSolicitacaoConsultoria() {
		prescricaoMedicaFacade.gerarPendenciasSolicitacaoConsultoria();
	}

	private void atualizarUltimoAcesso(Usuario usuario, HttpSession session)
			throws ApplicationBusinessException {
		session.setAttribute(SessionAttributes.DATA_ULTIMO_ACESSO.toString(),
				usuario.getDataUltimoAcesso());
	}

	/**
	 * Faz a criptografia da senha do usuário logado, a ser usada na integração
	 * com outros sistemas.
	 * 
	 * @param session
	 */
	private void criptografarSenha(HttpSession session, String senha) {
		try {
			AghParametros parametroManterSenhaCriptografada = this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_MANTER_SENHA_CRIPTOGRAFADA);

			if (Boolean.parseBoolean(parametroManterSenhaCriptografada.getVlrTexto())) {
				Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
				
				if (principal instanceof AghuPrincipal) {
					AghuPrincipal aghuPrincipal = (AghuPrincipal) principal;
					String resultadoCriptografia = CriptografiaUtil.criptografar(senha);
					
					aghuPrincipal.setEncryptedResult(resultadoCriptografia);
				} else {
					throw new BaseRuntimeException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, SessionAttributes.RESULTADO_CRIPTOGRAFIA.toString() + " falhou! Principal não é um AghuPrincipal.");
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Não possível obter parâmetro P_MANTER_SENHA_CRIPTOGRAFADA", e);
		}
	}

	/**
	 * Ajusta o tempo de sessão do usuário de acordo com os valores em banco.
	 * 
	 * @param usuario
	 * @param session
	 */
	private void ajustarTempoSessao(Usuario usuario, HttpSession session) {
		session.setMaxInactiveInterval(usuario.getTempoSessaoMinutos() * 60);
		
		int sessionTime = session.getMaxInactiveInterval()*1000;  //pega tempo de sessão e passa para milisegundos
        if (conversationContext != null) {
            if (conversationContext.getDefaultTimeout() < sessionTime){
                conversationContext.setDefaultTimeout(sessionTime);
            }
        }

	}

	/**
	 * Registra acesso bem sucedido ao sistema AGHU.
	 * 
	 * @param eventoLogin
	 * @throws ApplicationBusinessException
	 * @throws UnknownHostException
	 */
	private void registrarAcessoBemSucedido(String usuarioLogado)
			throws ApplicationBusinessException, UnknownHostException {
		LOG.info("Registrando acesso bem sucedido");

		cascaFacade.registrarAcesso(usuarioLogado, hostRemoto
				.getEnderecoIPv4HostRemoto().getHostAddress(),
				"ACESSO BEM SUCEDIDO", true, DominioTipoAcesso.ENTRADA);
	}

	/**
	 * Registra acesso mal sucedido no sistema AGHU.
	 * 
	 * @param eventoLogin
	 * @throws ApplicationBusinessException
	 * @throws UnknownHostException
	 */
	public void registrarAcessoMalSucedido(
			@Observes EventoLoginMalSucedido eventoLogin)
			throws ApplicationBusinessException, UnknownHostException {
		LOG.info("Registrando acesso mal sucedido");
		cascaFacade.registrarAcesso(eventoLogin.getUsername(), hostRemoto
				.getEnderecoIPv4HostRemoto().getHostAddress(),
				"ACESSO MAL SUCEDIDO", false, DominioTipoAcesso.ENTRADA);
	}

	/**
	 * Registra logout acionado pelo usuário.
	 * 
	 * @param eventoLogout
	 * @throws ApplicationBusinessException
	 * @throws UnknownHostException
	 */
	// public void registrarLogout(@Observes PreLoggedOutEvent eventoLogout)
	// throws ApplicationBusinessException, UnknownHostException {
	// cascaFacade.registrarAcesso(identity.getAgent().getLoginName(),
	// hostRemoto
	// .getEnderecoIPv4HostRemoto().getHostAddress(),
	// "LOGOUT ACIONADO PELO USUÁRIO", true, DominioTipoAcesso.SAIDA);
	// }

}
