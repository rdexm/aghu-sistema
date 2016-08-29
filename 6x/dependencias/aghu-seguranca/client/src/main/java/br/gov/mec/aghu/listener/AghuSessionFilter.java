/**
 * 
 * Filtro que verifica se a sessao ainda e valida e caso contrario
 * redireciona o usuario para a tela de login 
 */
package br.gov.mec.aghu.listener;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

/**
 * @author marcelofilho
 *
 * Servlet Filter implementation class AghuSessionFilter
 */
public class AghuSessionFilter implements Filter {

	private final static String TELA_LOGIN = "login.seam";

	@Logger
	private Log log;
	
	/**
	 * Default constructor.
	 */
	public AghuSessionFilter() {
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if ((request instanceof HttpServletRequest)
				&& (response instanceof HttpServletResponse)) {

			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			if (isControleNecessario(httpServletRequest)) {
				if (isSessaoInvalida(httpServletRequest)) {
					log.info("Sessão expirada");
					
					String timeoutUrl = httpServletRequest.getContextPath() + "/" + TELA_LOGIN;
					httpServletResponse.sendRedirect(timeoutUrl);
				}
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * 
	 * Nem todos os recursos devem ser controlados pelo filtro. A pagina de
	 * login, por exemplo, e um deles
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	private boolean isControleNecessario(HttpServletRequest httpServletRequest) {

		String requestPath = httpServletRequest.getRequestURI();

		return !requestPath.contains(TELA_LOGIN);
	}

	/**
	 * @param httpServletResponse
	 * @return
	 */
	private boolean isSessaoInvalida(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestedSessionId() == null
				|| !httpServletRequest.isRequestedSessionIdValid();
	}

}