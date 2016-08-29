package br.gov.mec.aghu.core.etc;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionArrayPaginator;
import br.gov.mec.aghu.core.action.ActionPaginator;

public class BindingComponentsProducer implements Serializable{
	
	private static final long serialVersionUID = 47195359037153341L;
	private static final Log LOG = LogFactory.getLog(BindingComponentsProducer.class);	

	@Inject
	private BeanManager beanManager;
	
	@Produces @Paginator
	public <T> DynamicDataModel<T> getDataTableComponent(InjectionPoint ip, DynamicDataModel<T> dataModel){

		//value
		String parent = ip.getAnnotated().getAnnotation(Paginator.class).value();
		if (parent.isEmpty() || parent.equals("this")){
			ActionPaginator paginator = (ActionPaginator) getBean(ip.getBean().getBeanClass(), beanManager);
			dataModel.setPaginator(paginator);
			
		}else if (!parent.equals("none")){
			try {
				dataModel.setPaginator((ActionPaginator) getBean(Class.forName(parent), beanManager));
			} catch (ClassNotFoundException e) {
					LOG.error("Não foi possível gerar uma instância da classe: " + parent);
			}
		}
		return 	dataModel;
	}
	
	
	@Produces @ArrayPaginator
	public <T> DynamicDataModel<T> getDataTableComponentWithArray(InjectionPoint ip, DynamicDataModel<T> dataModel){

		//value
		String parent = ip.getAnnotated().getAnnotation(ArrayPaginator.class).value();
		if (parent.isEmpty() || parent.equals("this")){
			ActionArrayPaginator paginator = (ActionArrayPaginator) getBean(ip.getBean().getBeanClass(), beanManager);
			dataModel.setArrayPaginator(paginator);
			
		}else if (!parent.equals("none")){
			try {
				dataModel.setArrayPaginator((ActionArrayPaginator) getBean(Class.forName(parent), beanManager));
			} catch (ClassNotFoundException e) {
					LOG.error("Não foi possível gerar uma instância da classe: " + parent);
			}
		}
		return 	dataModel;
	}	
	
	
	public Object getBean(Class<?> beanClass, BeanManager beanManager) {
	    if(beanManager != null) {
	        Set<Bean<?>> beans = beanManager.getBeans(beanClass);
	        
	        if(beans != null && !beans.isEmpty()) {
	            Bean<?> bean = (Bean<?>) beanManager.getBeans(beanClass).iterator().next();
	            CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
	            return beanManager.getReference(bean, beanClass, ctx);    
	        }            
	    }
	    
	    return null;
	}	
	
	
}
