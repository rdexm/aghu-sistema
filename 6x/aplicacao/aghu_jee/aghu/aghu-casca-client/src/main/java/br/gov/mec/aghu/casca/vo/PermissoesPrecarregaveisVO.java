package br.gov.mec.aghu.casca.vo;

import java.io.Serializable;
@Deprecated
public class PermissoesPrecarregaveisVO implements Serializable {

	private static final long serialVersionUID = 8465476088871319546L;

	private String aplicacoes;
	
	private String targets;
	
	private String actions;
	
	private String login;

	public PermissoesPrecarregaveisVO(String aplicacoes, String targets,
			String actions, String login) {
		this.aplicacoes = aplicacoes;
		this.targets = targets;
		this.actions = actions;
		this.login = login;
	}

	public String getAplicacoes() {
		return aplicacoes;
	}

	public void setAplicacoes(String aplicacoes) {
		this.aplicacoes = aplicacoes;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
