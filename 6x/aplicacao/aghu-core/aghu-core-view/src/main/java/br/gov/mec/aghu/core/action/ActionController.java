package br.gov.mec.aghu.core.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.enterprise.context.Conversation;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.casca.autenticacao.AghuPrincipal;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.exception.UsuarioNaoLogadoException;
import br.gov.mec.aghu.core.seguranca.Token;
import br.gov.mec.aghu.core.seguranca.TokenIdentityException;
//import org.ajax4jsf.component.UIAjaxCommandLink;

/**
 * Classe base para as Actions controllers de interface do AGHU.
 * substitui: AGHUController
 * 
 * 
 * @author Cristiano Quadros
 *
 */
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
@Action
public abstract class ActionController implements Serializable {
	
	private static final long serialVersionUID = -5526409070103792619L;
	private static final Log LOG = LogFactory.getLog(ActionController.class);
	
	private static final String ENCODE="ISO-8859-1";
	private static final String CONTENT_TYPE="text/csv";
	private static final String EXTENSAO=".csv";
	
	@Inject
	protected Conversation conversation;
	
	@Inject
	private HostRemotoCache hostRemotoCache;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	/**
	 * Retorna o login do usuário logado.
	 * 
	 */
	protected String obterLoginUsuarioLogado() {
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if (principal == null) {
			throw new UsuarioNaoLogadoException();
		}
		return principal.getName();
	}
	
	protected boolean isLoggedIn() {
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		return principal != null;
	}
	
	public Object obterTokenUsuarioLogado() {
		String returnValue = "";
		
		String user = obterLoginUsuarioLogado();
		if (user != null) {
			/*HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			String encryptedPassword = (String) session.getAttribute(SessionAttributes.RESULTADO_CRIPTOGRAFIA.toString());

			try {
				returnValue = Token.createToken(user, encryptedPassword);
			} catch (TokenIdentityException e) {
				LOG.error("Erro ao gerar o token do usuário", e);
			}*/
			Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();

			if (principal instanceof AghuPrincipal) {
				AghuPrincipal aghuPrincipal = (AghuPrincipal) principal;
				String encryptedPassword = aghuPrincipal.getEncryptedResult();
				try {
					returnValue = Token.createToken(user, encryptedPassword);
				} catch (TokenIdentityException e) {
					LOG.error("Erro ao gerar o token do usuário", e);
				}
			} else {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO
						, SessionAttributes.RESULTADO_CRIPTOGRAFIA.toString() + " falhou! Principal não é um AghuPrincipal.");
			}
		}

		return returnValue;
	}
	
	public void download(byte[] fileOut, String fileName, String contentType) throws IOException {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType(contentType);

		if (fileName == null) {
			response.setHeader("Content-Disposition","attachment;filename=" + "relatorio.pdf");
		} else {
			response.setHeader("Content-Disposition","attachment;filename=" + fileName);
		}

		response.getCharacterEncoding();

		final OutputStream out = response.getOutputStream();

		out.write(fileOut);
		out.flush();
		out.close();
		fc.responseComplete();
	}

	public ResourceBundle getBundle() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getResourceBundle(context, WebUtil.RESOURCE_BUNDLE);		
	}
	
	
	private void apresentarStatusMessages(Severity s, String codeBundle, String bundleFileName, Object... params) {
		javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(s);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);	
		String message =  WebUtil.initLocalizedMessage(codeBundle, bundleFileName, params);
		context.addMessage("Messages", new FacesMessage(severity, message, message));
	}
	
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void apresentarStatusMessages(String componentID,Severity s, String codeBundle, String bundleFileName, Object... params) {
		javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(s);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);		
		String message =  WebUtil.initLocalizedMessage(codeBundle, bundleFileName, params);
		context.addMessage(componentID, new FacesMessage(severity, message, message));
	}
	
	/**
	 * Apresenta uma mensagem de erro ao usuário através da adição de uma status
	 * messages.
	 * 
	 * @param e
	 */
	protected void apresentarExcecaoNegocio(BaseException e) {
		this.apresentarStatusMessages(e.getSeverity(), e.getLocalizedMessage(), null, e.getParameters());
	}
	
	/**
	 * Apresenta uma coleção de mensagens de erro, encapsulados em uma
	 * BaseListException.
	 * 
	 * @param e
	 */
	protected void apresentarExcecaoNegocio(BaseListException e) {
		for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
			BaseException aghuNegocioException = errors.next();
			this.apresentarExcecaoNegocio(aghuNegocioException);
		}
	}

	/**
	 * Exibe uma mensagem de erro relativa a um erro de validação na model.
	 * 
	 * @param e
	 */
	public void apresentarExcecaoNegocio(BaseRuntimeException e) {
		this.apresentarStatusMessages(Severity.ERROR, e.getLocalizedMessage(), null, e.getParameters());
	}

	public void apresentarMsgNegocio(String msgKey, String bundle) {
		this.apresentarStatusMessages(Severity.INFO, msgKey, bundle);		
	}
	
	public void apresentarMsgNegocio(Severity severity, String msgKey, Object... params) {
		this.apresentarStatusMessages(severity, msgKey, null, params);		
	}

	public void apresentarMsgNegocio(String msgKey) {		
		this.apresentarStatusMessages(Severity.INFO, msgKey, null);
	}	
	
	public void apresentarMsgNegocio(String componentID,Severity severity, String msgKey, Object... params) {
		this.apresentarStatusMessages(componentID, severity, msgKey, null, params);		
	}


	
	/**
	 * Inicia uma conversação
	 * AGHUNegocioListaException.
	 * 
	 * @param e
	 */	
	
	public void begin(Conversation conversation) {
		if (conversation.isTransient()){
			begin(conversation, false);
		}
	}
	
	
	public void begin(Conversation conversation, boolean createNew) {
		if (!conversation.isTransient() && createNew){
			conversation.end();
		}
		conversationContext.setConcurrentAccessTimeout(300000);
		conversation.begin();
		LOG.debug("Conversação Iniciada [" + conversation.getId()  + "]: " + conversation.getTimeout()); 
	}
	

	/**
	 * Fecha uma conversação
	 * AGHUNegocioListaException.
	 * 
	 * @param e
	 */	
	public void end(Conversation conversation) {
		if (!conversation.isTransient()){
			conversation.end();
			LOG.debug("Conversação Finalizada [" + conversation.getId()  + "]");
		}
	}
	
	/**
	 * Fecha uma conversação
	 * AGHUNegocioListaException.
	 * 
	 * @param e
	 */	
//	public String closeNestedConversationAndRedirect(String pageRedirect){
//		if (!conversation.isTransient() && !conversation.getId().equals(parentCid)){
//			conversation.end();
//			return pageRedirect+"?cid=" + parentCid;
//		}
//		return pageRedirect;
//	}	
	

	/**
	 * Fecha uma conversação
	 * AGHUNegocioListaException.
	 * 
	 * @param e
	 */	
	public void closeConversation() {
		if (!conversation.isTransient()) {
			LOG.debug("Conversação Finalizada [" + conversation.getId()  + "]");
			conversation.end();
		}
	}
	
	

	/**
	 * Retorna o InetAddress do cliente recuperado a partir do request do faces
	 * context.<br />
	 * Não resolve o endereço e nome do localhost para permitir testes em
	 * desenvolvimento.
	 * 
	 * @see InetAddress
	 * @return endereço de rede no formato IPv4
	 * @throws UnknownHostException
	 */
	public InetAddress getEnderecoIPv4HostRemoto() throws UnknownHostException {
		return hostRemotoCache.getEnderecoIPv4HostRemoto();		
	}
	
	/**
	 * Método que retorna o nome do computador na rede, ou o IP
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public String getEnderecoRedeHostRemoto() throws UnknownHostException {
		return hostRemotoCache.getEnderecoRedeHostRemoto();
	}
	
	
	public String getHostName(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		String hostName=null;
		try {
			InetAddress addr = InetAddress.getByName(request.getRemoteAddr());
			hostName = addr.getHostName();
			if (hostName != null && hostName.contains((".hcpa"))) {
				hostName = hostName.substring(0, hostName.lastIndexOf('.'));
			}
		} catch (UnknownHostException e) {
			LOG.error("Não foi possível obter o hostname. Exceção capturada:" + e.getCause() + " " + e.getMessage());
		}		
		return hostName;
	}

	/**
	 * Redireciona arquivo para saída do browse dando a opção para download
	 * 
	 * Abre o arquivo especificado em fileName e escreve o arquivo no OutputStream do Response<br>
	 * utilizando Scanner para converter o conteudo.
	 * Usar para download de arquivos TEXTO com ENCODE / charset.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void download(String fileName) throws IOException {
		download(fileName, fileName, CONTENT_TYPE, ENCODE);
	}
	
	/**
	 * Redireciona arquivo para saída do browse dando a opção para download
	 * 
	 * Abre o arquivo especificado em fileName e escreve o arquivo no OutputStream do Response<br>
	 * utilizando Scanner para converter o conteudo.
	 * Usar para download de arquivos TEXTO com ENCODE / charset.
	 * 
	 * @param fileName
	 * @param headerName
	 * @throws IOException
	 */
	public void download(String fileName, String headerName) throws IOException {
		download(fileName, headerName, CONTENT_TYPE, ENCODE);
	}
	
	/**
	 * Redireciona arquivo para saída do browse dando a opção para download
	 * 
	 * Abre o arquivo especificado em fileName e escreve o arquivo no OutputStream do Response<br>
	 * utilizando Scanner para converter o conteudo.
	 * Usar para download de arquivos TEXTO com ENCODE / charset.
	 * 
	 * @param fileName
	 * @param headerName
	 * @param contentType
	 * @throws IOException
	 */
	public void download(String fileName, String headerName, String contentType) throws IOException {
		download(fileName, headerName, contentType, ENCODE);
	}
	
	/**
	 * Redireciona arquivo para saída do browse dando a opção para download
	 * 
	 * Abre o arquivo especificado em fileName e escreve o arquivo no OutputStream do Response<br>
	 * utilizando Scanner para converter o conteudo.
	 * Usar para download de arquivos TEXTO com ENCODE / charset.
	 * 
	 * @param fileName
	 * @param headerName
	 * @param contentType
	 * @param encode
	 * @throws IOException
	 */
	public void download(String fileName, String headerName, String contentType, String encode) throws IOException {		
		FacesContext fc = FacesContext.getCurrentInstance();
		StringBuilder hn = new StringBuilder(headerName);
		if (!headerName.contains(".")) {
			hn.append(EXTENSAO);
		}
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();		
		response.setContentType(contentType);		
		response.setHeader("Content-Disposition","attachment;filename=" + hn.toString());
		response.getCharacterEncoding();
		OutputStream out = response.getOutputStream();
		Scanner scanner = new Scanner(new FileInputStream(fileName), encode);
		while (scanner.hasNextLine()){
			out.write(scanner.nextLine().getBytes(encode));
			out.write(System.getProperty("line.separator").getBytes(encode));
		}
		scanner.close();
		out.flush();
		out.close();
		fc.responseComplete();
	}
	
	/**
	 * Redireciona arquivo para saída do browse dando a opção para download
	 * 
	 * Escreve o arquivo aFile no OutputStream do Response utilizando BufferedInputStream.<br>
	 * Usar para download de arquivos binarios.
	 * 
	 * @param aFile
	 * @param contentType
	 * @throws IOException 
	 */
	public void download(File aFile, String contentType) throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		
		OutputStream os = null;
		try (FileInputStream fis = new FileInputStream(aFile); 
				BufferedInputStream	bis = new BufferedInputStream(fis) ){
			
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.setContentType(contentType);
			response.addHeader("Content-Disposition", "attachment; filename=" + aFile.getName());
			
			os = response.getOutputStream();
			int len;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
			
		} finally {
			if (aFile != null) {
				aFile.delete();
			}
			
			if (os != null) {
				os.flush();
				os.close();	
			}
			
			fc.responseComplete();
		}
	}

	
	/**
	 * Este método verifica se a página está não está dando submit nela mesma.
	 * 
	 * @return
	 */
	public boolean isPostBack() {
		return FacesContext.getCurrentInstance().isPostback();
	}
	
	/**
	 * Este método verifica se a validação da página falhou.
	 * 
	 * @return
	 */
	public boolean isValidationFailed() {
		return FacesContext.getCurrentInstance().isValidationFailed();
	}
	
	
	public boolean isValidInitMethod() {
		//FacesContext context = FacesContext.getCurrentInstance();
		return true;
	}
	
	
		
	
	public String getRequestParameter(String parameter) {
		  Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		  return params.get(parameter);		
	}
	

	/**
	 * Exibe uma Dialog interna ao XHTML
	 * @param widgetVar - widgetVar representando o componente a ser exibido
	 */
	public void openDialog(String widgetVar){
		RequestContext.getCurrentInstance().execute("PF('"+widgetVar+"').show();");
	}

	/**
	 * Oculta uma Dialog interna ao XHTML
	 * @param widgetVar - widgetVar representando o componente a ser ocultado
	 */
	public void closeDialog(String widgetVar){
		RequestContext.getCurrentInstance().execute("PF('"+widgetVar+"').hide();");
	}

	public <T> List<T> returnSGWithCount(List<T> resultSG, Number count){
		if (resultSG!=null && resultSG.size() > 1){
			FacesMessage message = null;
			try {
				message = new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Total de registros encontrados: " + resultSG.size() + "/" + count, null);
			} catch (Exception e) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Erro ao executar método count do componente suggestion box",null);
			}
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		return resultSG;
	}
	
	
	/**
	 * Obtem o valor de uma propriedade aninhada da página.
	 * 
	 * @param bean
	 * @param property
	 * @return
	 */
	public Object getProperty(String property) {
		if (property==null){
			return null;
		}
		try {
			if (property.contains(".")){
					return PropertyUtils.getNestedProperty(this, property);
			}else{
				return PropertyUtils.getProperty(this, property);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return null;
		} catch (org.apache.commons.beanutils.NestedNullException e) {
			return null;
		}	
		
	}	
	

	
	/**
	 * seta o valor de uma propriedade aninhada da página.
	 * 
	 * @param bean
	 * @param property
	 * @param value
	 * @return
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalAccessException 
	 */
	public void setProperty(String property, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (property == null) {
			return;
		}

		if (property.contains(".")) {
			PropertyUtils.setNestedProperty(this, property, value);
		} else {
			PropertyUtils.setProperty(this, property, value);
		}

	}
	
	public Cookie getCookie(String name) {
		return WebUtil.getCookie(name);
	}
}