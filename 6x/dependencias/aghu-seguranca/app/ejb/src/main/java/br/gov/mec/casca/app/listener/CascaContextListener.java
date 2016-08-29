package br.gov.mec.casca.app.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.ajax4jsf.webapp.WebXml;

public class CascaContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		String contextName = event.getServletContext().getServletContextName(); 
		
		System.out.println("CONTEXT NAME ---> " + contextName);
		
		try {
			new WebXml().init(context, contextName);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

}
