// FIXME REVISAR ESTE PACKAGE OU br.gov.br.casca.model.vo
package br.gov.mec.casca.security.vo;

public class PermissaoVO {

	private String login;
	
	private String target;
	
	private String action;
	
	private String aplicacao;

	public String getLogin() {
		// O login é gravado no banco com caixa alta
		return login != null ? login.toUpperCase() : login;
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
	
	public int hashCode() {
		return toString().hashCode();
	}
}
