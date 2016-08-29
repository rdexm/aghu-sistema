package br.gov.mec.aghu.sistema.bussiness;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserSessions implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3195880531331926967L;
	
	
	private Map<String, UserSession> userSessions = new HashMap<String, UserSession>();

	public Object getSession(String sessionId) {
		return userSessions.get(sessionId);
	}
	
	public Object removeSession(String sessionId) {
		return userSessions.remove(sessionId);
	}

	public void addSession(String sessionId, String userName, Long createTime, String ip, String host) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yy");
		String formattedDate = sdf.format(new Date(createTime));
		
		
		UserSession us = new UserSession(formattedDate, userName, ip, host);
		
		if (userName != null){
			userSessions.put(sessionId, us);
		}
	}

	public Map<String, UserSession> getUserSessions() {
		return userSessions;
	}

	
	public class UserSession implements Serializable{

		private static final long serialVersionUID = -8662087037500261231L;

		public UserSession(String loginDate, String userName, String ip, String host) {
			super();
			this.loginDate = loginDate;
			this.userName = userName;
			this.ip = ip;
			this.host = host;
		}

		private String loginDate;
		private String userName;
		private String ip;
		private String host;
		
		public String getLoginDate() {
			return loginDate;
		}
		
		public void setLoginDate(String loginDate) {
			this.loginDate = loginDate;
		}
		
		public String getUserName() {
			return userName;
		}
		
		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		public String getIp() {
			return ip;
		}
		
		public void setIp(String ip) {
			this.ip = ip;
		}
		
		public String getHost() {
			return host;
		}
		
		public void setHost(String host) {
			this.host = host;
		}
	}
}
