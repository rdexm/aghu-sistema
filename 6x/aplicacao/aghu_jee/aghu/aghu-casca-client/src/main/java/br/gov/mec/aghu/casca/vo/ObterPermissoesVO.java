package br.gov.mec.aghu.casca.vo;

import java.io.Serializable;

public class ObterPermissoesVO implements Serializable {

	private static final long serialVersionUID = -773538218411665981L;

	private String aplicacao;
	
	private String target;
	
	private String action;

	public ObterPermissoesVO(String aplicacao, String target, String action) {
		this.aplicacao = aplicacao;
		this.target = target;
		this.action = action;
	}

	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
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
	
}
