package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class Email  implements Serializable {

	private static final long serialVersionUID = -2905073280578385661L;

	private String email;
	private int ordem;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getOrdem() {
		return ordem;
	}
	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	
	

}
