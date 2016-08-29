package br.gov.mec.aghu.bundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;


public class MessagesResourceBundle extends MultiplePropertiesResourceBundle {
	
	/**
	 * Constante que indica o nome do arquivo de properties com a definicao
	 * de todos os outros arquivos que devem ser carregados como ResourceBundle.
	 * 
	 */
	private static final String BASE_NAME = "messages";
	
	/**
	 * Resource bundle basico da aplicacao.
	 */
	private static final String RESOURCE_BUNDLE_NAME = "br.gov.mec.aghu.bundle.MessagesResourceBundle";
	
	/**
	 * Nome da variavel definida no faces-config para referenciar o ResourceBundle. 
	 */
	private static final String VAR_MESSAGES = "messages";
	

	public MessagesResourceBundle() {
		super(BASE_NAME);
	}
	
	
	/**
	 * Este producer esta sendo usado para injecao do ResourceBundle em classe de Negocio.<br>
	 * Este uso causa uma inviabilidade de deploy separado das camadas de apresentacao (WAR) e de negocio (EJB)<br>
	 * Esta sendo feito desta forma devido a compatibilidade com codigo legado. <br>
	 * Ajustar isso causaria mais problemas na migracao da arquitetura, pois necessitaria separar os arquivos de bundle/properties<br>
	 * entre as camadas. Talvez até causaria problemas de duplicacao de mensagens.
	 * 
	 * Avaliar no futuro a separacao das mensagens.
	 * 
	 * @return
	 */
	@Produces 
	public ResourceBundle getResourceBundleProducer() {
		ResourceBundle bundle;
		try {
			//Tenta carregar um resouce bundle da aplicacao com nome de variavel: messages.
			FacesContext context = FacesContext.getCurrentInstance();
			if (context != null) {
				bundle = context.getApplication().getResourceBundle(context, VAR_MESSAGES);
			} else {
				throw new MissingResourceException("Tentativa de pegar bundle do facesContext falhou.", RESOURCE_BUNDLE_NAME, VAR_MESSAGES);
			}
		} catch (java.util.MissingResourceException e) {
			//Se nenhuma tela foi carregada ainda, o messages não foi carregado e nao esta definido.
			//Neste caso, faz a carga com o Locale default.
			bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, Locale.getDefault());
		}
		
		return bundle;
	}
	
}
