package br.gov.mec.casca.tools.updater;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;

public abstract class Cleaner<T> {
	
	private Set<Integer> ids;
	
	protected Cleaner(Set<Integer> ids) {
		this.ids = ids;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void clean() throws Exception {
		Type classType = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		DetachedCriteria criteria = DetachedCriteria.forClass((Class)classType);
		manageCriteria(criteria);
		ScrollableResults results = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).scroll(ScrollMode.FORWARD_ONLY);
		while (results.next()) {
			T object = (T)results.get(0);
			Method method = object.getClass().getMethod("getId", new Class[]{});
			Integer id = (Integer)method.invoke(object, new Object[]{});
			if (!ids.contains(id)) {
				preDelete(object);
				HibernateHelper.getSingleton().delete(object);
				postDelete(object);
			}
		}
	}
	
	protected void manageCriteria(DetachedCriteria criteria) {
		
	}
	
	protected void preDelete(T object) {
		
	}
	
	protected void postDelete(T object) {
		
	}
}