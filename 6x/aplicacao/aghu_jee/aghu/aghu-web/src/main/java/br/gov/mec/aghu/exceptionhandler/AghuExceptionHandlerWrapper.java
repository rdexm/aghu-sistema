package br.gov.mec.aghu.exceptionhandler;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.weld.context.NonexistentConversationException;

import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.CupsException;
import br.gov.mec.aghu.core.exception.EmptyReportException;

/**
 * Classe responsável pelo tratamento implicito para casos de exceções
 * específicas no aghu.
 * 
 * 
 * @author geraldo
 * 
 */
public class AghuExceptionHandlerWrapper extends ExceptionHandlerWrapper {

	private static final String MESSAGES = "Messages";

	private static final Log LOG = LogFactory.getLog(AghuExceptionHandlerWrapper.class);

	private final javax.faces.context.ExceptionHandler wrapped;

	public AghuExceptionHandlerWrapper(
			final javax.faces.context.ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null){
			LOG.info("AghuExceptionHandlerWrapper.handle() - Não existe um faces context para esta thread.");
			return;
		}
		final ExternalContext externalContext = facesContext.getExternalContext();
		
		for (final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
			
			Throwable t = it.next().getContext().getException();		
			
			while (t.getCause() != null && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			
			
			if (t instanceof AuthorizationException) {
				
				AuthorizationException erroAutorizacao = (AuthorizationException) t;

				StringBuilder msgError = new StringBuilder(75);
				msgError.append("Capturado erro de permissão: ");
				if (erroAutorizacao.getParameters() != null && erroAutorizacao.getParameters().length > 0)
				{
					for(Object o : erroAutorizacao.getParameters())
					{
						msgError.append(o.toString()).append(' ');
					}
				}
				LOG.warn(msgError.toString());
				
				String mensagem = WebUtil.initLocalizedMessage(erroAutorizacao.getMessage(), null, erroAutorizacao.getParameters());
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
			
				try {
					if (facesContext.getCurrentPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
						LOG.warn("Erro detectado na fase de render response" + msgError.toString()+" - Redirecionando para tela de erroPermissao...");
						String redirectTo = externalContext.getRequestScheme() + "://" +
								externalContext.getRequestServerName() + ":" + externalContext.getRequestServerPort()
								+ externalContext.getRequestContextPath() + "/erroPermissao.xhtml?faces-redirect=true";
						try {
							if (!externalContext.isResponseCommitted()){
								externalContext.redirect(redirectTo);
							}
							
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
						}
						facesContext.responseComplete();
					}
				} finally {
					it.remove();
				}
			}// AuthorizationException
			else if (t instanceof ViewExpiredException || t instanceof NonexistentConversationException) {
				LOG.info("Capturado erro de viewExpired ou Conversation");
				Exception erro = (Exception) t;

				String mensagem = WebUtil.initLocalizedMessage(erro.getMessage(), null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
			
				try {
					if (facesContext.getCurrentPhaseId().equals(PhaseId.RESTORE_VIEW)) {
						LOG.info("Erro detectado na fase de restore view. redirecionando para tela de erro.");
						String redirectTo = externalContext.getRequestScheme() + "://" +
								externalContext.getRequestServerName() + ":" + externalContext.getRequestServerPort()
								+ externalContext.getRequestContextPath() + "/erroConversacao.xhtml?faces-redirect=true";
						try {
							if (!externalContext.isResponseCommitted()){
								externalContext.redirect(redirectTo);
							}	
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
						}
						facesContext.responseComplete();
					}
				} finally {
					it.remove();
				}
			}// ViewExpiredException
			else if (t instanceof BaseOptimisticLockException) {
				LOG.info("Capturado erro de controle de concorrencia. Registro alterado por outro usuario.");
				BaseOptimisticLockException erroConcorrencia = (BaseOptimisticLockException) t;
				String mensagem = WebUtil.initLocalizedMessage(erroConcorrencia.getMessage(), null, erroConcorrencia.getParameters());
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();
			}// BaseOptimisticLockException BaseStaleStateException
			else if (t instanceof CupsException) {
				LOG.info("Capturado erro do Cups.");
				CupsException erro = (CupsException) t;
				String mensagem = WebUtil.initLocalizedMessage(erro.getMessage(), null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();
			}//StaleObjectStateException
			else if (t instanceof EmptyReportException) {
				LOG.info("Relatório com dados vazios.");
				EmptyReportException erro = (EmptyReportException) t;
				String mensagem = WebUtil.initLocalizedMessage(erro.getMessage(), null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();
			}//EmptyReportException
			else if (t instanceof BaseRuntimeException) {
				LOG.info("Capturado erro basico do AGHU - BaseRuntimeException.");
				BaseRuntimeException erroBasico = (BaseRuntimeException) t;
				String mensagem = WebUtil.initLocalizedMessage(erroBasico.getMessage(), null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();				
			}// BaseRuntimeException
			else if (t instanceof ConstraintViolationException) {
				LOG.info("Capturado erro de violacao de constraint.");
				ConstraintViolationException constraint = (ConstraintViolationException) t;
				String mensagem = WebUtil.initLocalizedMessage(constraint.getMessage(), null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();
			}// ConstraintViolationException
			else if (t instanceof StaleObjectStateException) {
				LOG.info("Capturado erro de controle de concorrencia. Registro alterado por outro usuario.");
				String mensagem = WebUtil.initLocalizedMessage("OPTIMISTIC_LOCK", null);
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
				facesContext.addMessage(MESSAGES, fm);
				it.remove();
			}//StaleObjectStateException			
		}// FOR

		getWrapped().handle();
	}
}