// FIXME REVISAR ESTE PACKAGE OU br.gov.br.casca.model.vo
package br.gov.mec.aghu.casca.security.vo;



public class PermissaoVO implements Comparable<PermissaoVO> {

	private String login;
	
	private String target;
	
	private String action;
	
	private String aplicacao;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}
	
	public String toString() {
		return String.format("PermissaoVO[%s,%s,%s,%s]", getAplicacao(), getTarget(), getAction(), getLogin());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PermissaoVO other = (PermissaoVO) obj;
		
		return (getAction() != null && getAction().equals(other.getAction()))
			&& (getAplicacao() != null && getAplicacao().equals(other.getAplicacao()))
			&& (getLogin() != null && getLogin().equals(other.getLogin()))
			&& (getTarget() != null && getTarget().equals(other.getTarget()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((aplicacao == null) ? 0 : aplicacao.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}
	
	@Override
	public int compareTo(PermissaoVO other) {
		// OBS: a alteração deste método pode impactar na performance 
		// e comportamento do CASCA em geral
		return getTarget().compareTo(other.getTarget()) + getAction().compareTo(other.getAction());
	}	
}