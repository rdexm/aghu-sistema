package br.gov.mec.casca.app.business;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;


@Name("sessionKeepAliveTokenON")
@Scope(ScopeType.APPLICATION)
public class SessionKeepAliveTokenON {

	@Logger
	private Log log;

	private Map<String, Boolean> keepAliveMap = new HashMap<String, Boolean>();
	
	/**
	 *
	 * @param key
	 * @return old value, if any
	 */
	public void setKeepAlive(String key) {
		
		if (key != null) {
			this.keepAliveMap.put(key, Boolean.TRUE);
			this.log.debug("Set keep alive for [" + key + "]");
		} else {
			this.log.warn("Invalid key [" + key + "]");			
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return keep alive flag, if any
	 */
	public Boolean consumeKeepAlive(String key) {
		
		Boolean result = null;
		
		result = this.keepAliveMap.put(key, Boolean.FALSE);
		if (result == null) {
			result = Boolean.FALSE;
		}
		this.log.debug("Consumed keep alive for [" + key + "] --> (" + result + ")");
		
		return result;
	}
	
	@Override
	public String toString() {
	
		String result = null;
		
		result = super.toString();
		result += " map: " + this.keepAliveMap.toString();
		
		return result;
	}
}
