package br.gov.mec.aghu.menu.portal.controller;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@ApplicationScoped
@Named
public class PortalRSSCacheManager {
	
	protected static final int CACHE_MINUTE_TIMEOUT = 30;
	
	private Map<String, PortalRSSCacheItem> cache = new TreeMap<String, PortalRSSCacheItem>();
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public void put(String key, Object value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("PortalRSSCacheManager: Parametros obrigatorios nao informados!");
		}
		cache.put(key, new PortalRSSCacheItem(value, new Date()));
	}
	
	public Object get(String key) throws ApplicationBusinessException {
		Object returnValue = null;
		
		PortalRSSCacheItem item = cache.get(key);
		if (item != null) {
			if (DateUtil.validaDataMaior(
					DateUtil.adicionaMinutos(
							item.getData()
							, getCacheMinuteTimeout())
					, new Date())) {
				returnValue = item.getItem();
			}
		}
		
		return returnValue; 
	}
	
	private int getCacheMinuteTimeout() throws ApplicationBusinessException {
		
		if(this.parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.MINUTOS_CACHE_PORTALRSS)){
			return this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.MINUTOS_CACHE_PORTALRSS);
		}else{
			return CACHE_MINUTE_TIMEOUT;
	
		}
	}	

}
