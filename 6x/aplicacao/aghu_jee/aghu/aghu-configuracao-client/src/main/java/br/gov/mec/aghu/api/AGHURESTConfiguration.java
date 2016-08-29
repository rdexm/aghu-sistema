package br.gov.mec.aghu.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


/**
 * Configura os serviços jax-rs. <br />
 * Quando retorna singletons e classes vazios, serão procuradas anotações JAX-RS
 * annotation.
 * 
 * @author gmaciel
 * 
 * @see http://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html_single/index.html#d4e40
 */
@ApplicationPath("/rs")
public class AGHURESTConfiguration extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> resources = new HashSet<Class<?>>();

	public AGHURESTConfiguration() {
		/*BeanConfig beanConfig = new BeanConfig();
		
		beanConfig.setVersion("v0.1");
		beanConfig.setBasePath("http://localhost:8080/aghu/rs/");
		beanConfig.setResourcePackage("br.gov.mec.aghu.certificacaodigital.service, br.gov.mec.aghu.registrocolaborador.service");
		beanConfig.setDescription("AGHU Rest API");
		beanConfig.setContact("Equipe de Arquitetura AGHU (arquitetura-aghu@googlegroups.com)");
		beanConfig.setTitle("AGHU API Service");
		beanConfig.setScan(true);*/
	}

	@Override
	public Set<Class<?>> getClasses() {
		//resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
		//resources.add(com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider.class);
		//resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
		//resources.add(com.wordnik.swagger.jaxrs.listing.ResourceListingProvider.class);

		return resources;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
