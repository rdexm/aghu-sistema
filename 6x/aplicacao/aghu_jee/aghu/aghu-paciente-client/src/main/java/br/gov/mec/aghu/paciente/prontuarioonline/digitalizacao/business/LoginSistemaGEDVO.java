package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.io.Serializable;

public class LoginSistemaGEDVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5788348863856456040L;
	private String usuarioSistema;
	private String senhaSistema;
	
	
	public LoginSistemaGEDVO() {
	}

	public LoginSistemaGEDVO(String usuarioSistema, String senhaSistema) {
		super();
		this.usuarioSistema = usuarioSistema;
		this.senhaSistema = senhaSistema;
	}

	public String getUsuarioSistema() {
		return usuarioSistema;
	}

	public void setUsuarioSistema(String usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

	public String getSenhaSistema() {
		return senhaSistema;
	}

	public void setSenhaSistema(String senhaSistema) {
		this.senhaSistema = senhaSistema;
	}
}
