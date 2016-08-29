package br.gov.mec.aghu.recursoshumanos;

import java.io.Serializable;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class Pessoa implements Serializable {

	private static final long serialVersionUID = 5306328867303896056L;
	
	private Usuario usuario;
	private RapServidores rapServidores;
	private RapPessoasFisicas rapPessoaFisica;

	@SuppressWarnings("unused")
	private Pessoa() {
		// ATENÇÃO: Não expor este construtor como public, ocasiona 
		// efeitos colaterais para quem chama o método local getRapServidores()  
	}
	
	public Pessoa(Usuario usuario, RapServidores servidor) {
		super();
		
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário não informado.");
		}
		
		if (servidor == null) {
			throw new IllegalArgumentException("Servidor não informado.");
		}
		
		if (servidor.getUsuario() == null) {
			throw new IllegalArgumentException("Servidor não possui usuário cadastrado.");
		}
		
		if (!usuario.getLogin().equalsIgnoreCase(servidor.getUsuario())) {
			throw new IllegalArgumentException("Login do usuário é diferente do login do servidor.");
		}		
		
		this.usuario = usuario;
		this.rapServidores = servidor;
		this.rapPessoaFisica = servidor.getPessoaFisica();
		
		//Garante que o objeto é carregado, mesmo que fique detached
		this.rapServidores.getCargaHoraria(); 
		this.rapPessoaFisica.getNome(); 		
	}

	public String getNome() {
		if (rapPessoaFisica == null) {
			return "Pessoa não cadastrada";
		} 
		
		return rapPessoaFisica.getNome();
	}

	public String getLogin() {
		if (usuario == null) {
			return "Usuário não cadastrado";
		}
		
		return usuario.getLogin();
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}
}
