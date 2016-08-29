package br.gov.mec.aghu.casca.senha.action;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Protocolo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.seguranca.Token;
import br.gov.mec.aghu.core.seguranca.TokenIdentityException;

public class SenhaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(SenhaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 9040662857498046018L;

	private enum SenhaControllerMsgEnum {
		CASCA_MENSAGEM_SENHA_NAO_CONFERE, CASCA_MENSAGEM_SENHA_NOVA_IGUAL_ATUAL, CASCA_MENSAGEM_SENHA_ATUAL_NAO_CONFERE, CASCA_MENSAGEM_SENHA_ALTERADA,
	}

	public enum SenhaNegocioExceptionCode implements BusinessExceptionCode {
		CASCA_NAO_POSSIVEL_ALTERAR_SENHA_AGHU, CASCA_MENSAGEM_ERRO_GERAR_TOKEN;
	}

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	// @RequestParameter
	private String email;

//	// @Out(required=false)
//	private String msg;

	private String senhaAtual;

	private String novaSenha;

	private String confirmarSenha;

	private String login;

	private boolean redirecionar;
	
	private boolean exibirSenhaAtual = true;

	
	public void processarParametro() {
		if (email != null) {
			setRedirecionar(true);
		}
	}
	
	@PostConstruct
	public void iniciar(){
		this.begin(conversation);
		String senhaTemporaria = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tkn");
		if (senhaTemporaria != null){
			this.exibirSenhaAtual = false;
		}
	}

	
	public void salvar() {
		try {
			AghParametros param = null;
			Boolean possivelAlterarSenhaAghu = Boolean.TRUE;
			try {
				param = parametroFacade
						.obterAghParametro(AghuParametrosEnum.P_POSSIVEL_ALTERAR_SENHA_AGHU);
				if (param != null && "N".equalsIgnoreCase(param.getVlrTexto())) {
					possivelAlterarSenhaAghu = Boolean.FALSE;
				}
			} catch (ApplicationBusinessException e) {
				LOG.info("Exceção capturada: ", e);
			}

			if (!possivelAlterarSenhaAghu) {
				throw new ApplicationBusinessException(
						SenhaNegocioExceptionCode.CASCA_NAO_POSSIVEL_ALTERAR_SENHA_AGHU);
			}

			login = this.obterLoginUsuarioLogado();

			if (!exibirSenhaAtual || this.validarSenhaAtual()) {
				if (this.novaSenha != null
						&& this.novaSenha.equals(this.confirmarSenha)) {
					if (!exibirSenhaAtual || !this.getSenhaAtual().equals(this.getNovaSenha())) {
						if (exibirSenhaAtual){
							this.cascaFacade.alterarSenha(login, this.senhaAtual, this.novaSenha);
						}else{
							this.cascaFacade.alterarSenhaSemValidacarSenhaAtual(login, this.novaSenha);
						}					
						apresentarMsgNegocio(SenhaControllerMsgEnum.CASCA_MENSAGEM_SENHA_ALTERADA
								.name());

					} else {
						apresentarMsgNegocio(
								Severity.WARN,
								SenhaControllerMsgEnum.CASCA_MENSAGEM_SENHA_NOVA_IGUAL_ATUAL
										.name());
					}
				} else {
					apresentarMsgNegocio(
							Severity.WARN,
							SenhaControllerMsgEnum.CASCA_MENSAGEM_SENHA_NAO_CONFERE
									.name());
				}
			} else {
				apresentarMsgNegocio(
						Severity.WARN,
						SenhaControllerMsgEnum.CASCA_MENSAGEM_SENHA_ATUAL_NAO_CONFERE
								.name());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private boolean validarSenhaAtual() {
		boolean retorno = false;
		
		try {
			cascaFacade.validarSenha( login,  senhaAtual);
			retorno = true;
		} catch (ApplicationBusinessException e) {
			LOG.warn(e.getMessage(), e);
			retorno = false;
		}
		return retorno;
	}

	public void enviarSenha() {

		try {

			AghParametros param = null;
			Boolean possivelAlterarSenhaAghu = Boolean.TRUE;
			try {
				param = parametroFacade
						.obterAghParametro(AghuParametrosEnum.P_POSSIVEL_ALTERAR_SENHA_AGHU);
				if (param != null && "N".equalsIgnoreCase(param.getVlrTexto())) {
					possivelAlterarSenhaAghu = Boolean.FALSE;
				}
			} catch (ApplicationBusinessException e) {
				LOG.info("Exceção capturada: ", e);
			}

			if (!possivelAlterarSenhaAghu) {
				throw new ApplicationBusinessException(
						SenhaNegocioExceptionCode.CASCA_NAO_POSSIVEL_ALTERAR_SENHA_AGHU);
			}

			if (login != null) {
				
				String senhaTemporaria = geraSenhaRandomica();
				StringBuilder url = new StringBuilder(getBaseUrl().toString());
				url.append("/pages/casca/senha/trocarSenha.jsf")
				.append("?email=S");
				
				String token = this.gerarToken(senhaTemporaria);
				cascaFacade.enviarTokenSenhaUsuario(login, token, url.toString());
				apresentarMsgNegocio("CASCA_MENSAGEM_EMAIL_ENVIADO");

				this.login = "";
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private String gerarToken(String senhaTemporaria) throws ApplicationBusinessException {
		String token = null;
		try {
			token = Token.createToken(login, senhaTemporaria );
			token =  CoreUtil.encodeURL(token);
		} catch (TokenIdentityException | UnsupportedEncodingException e) {
			throw new ApplicationBusinessException(SenhaNegocioExceptionCode.CASCA_MENSAGEM_ERRO_GERAR_TOKEN, e);
		}
		return token;
		
	}
	
	/**
	 * Senha gerada randomicamente para o token gerado no caso do usuário que
	 * esqueceu a senha.
	 * 
	 * @return
	 */
	private String geraSenhaRandomica() {
		return String.valueOf(new Date().getTime());
	}

	private String getBaseUrl() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		String servidor = request.getServerName();
		String contexto = request.getContextPath();
		Aplicacao aplicacao = cascaFacade.obterAplicacaoPorContexto(contexto, servidor);
		String protocolo = aplicacao.getProtocolo() != null ? aplicacao.getProtocolo().getDescricao() : Protocolo.HTTPS.getDescricao();
		
		int serverPort = request.getServerPort();
		StringBuilder baseUrl = new StringBuilder(protocolo).append("://").append(servidor);
		if (serverPort != 80) {
			baseUrl.append(':').append(serverPort);
		}
		baseUrl.append(request.getContextPath());

		return baseUrl.toString();
	}



	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setCascaFacade(ICascaFacade c) {
		this.cascaFacade = c;
	}

	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRedirecionar(boolean redirect) {
		this.redirecionar = redirect;
	}

	public boolean isRedirecionar() {
		return redirecionar;
	}

	public String getSenhaAtual() {
		return this.senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}
	
	public Boolean getPossivelAlterarSenhaAghu() {
		AghParametros param = null;
		boolean possivelAlterarSenhaAghu = Boolean.TRUE;
		try {
			param = parametroFacade.obterAghParametro(AghuParametrosEnum.P_POSSIVEL_ALTERAR_SENHA_AGHU);
			if(param != null && "N".equalsIgnoreCase(param.getVlrTexto())){
				possivelAlterarSenhaAghu = Boolean.FALSE;
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada: ", e);
		}
		return possivelAlterarSenhaAghu;
	}

	public boolean isExibirSenhaAtual() {
		return exibirSenhaAtual;
	}

	public void setExibirSenhaAtual(boolean exibirSenhaAtual) {
		this.exibirSenhaAtual = exibirSenhaAtual;
	}

}