package br.gov.mec.aghu.core.mail;

import java.io.Serializable;

/**
 * @author dlaks
 *
 */
public class ContatoEmail implements Serializable {
	
	private static final long serialVersionUID = -3609395411903209851L;

	private String nome;
	
	private String email;

	public ContatoEmail(String email) {
		this.nome = email;
		this.email = email;
	}

	public ContatoEmail(String nome, String email) {
		this.nome = nome;
		this.email = email;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return nome + " [" + email + "]";
	}
	
}
