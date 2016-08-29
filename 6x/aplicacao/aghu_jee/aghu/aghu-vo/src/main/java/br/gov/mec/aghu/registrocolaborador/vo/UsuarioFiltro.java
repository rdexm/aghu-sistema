package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtro para busca de usuário através do web service.
 * 
 * @author claudio
 * 
 */
public class UsuarioFiltro implements Serializable {

	private static final long serialVersionUID = 2871273326353085460L;
	
	private boolean and;
	private String login;
	private Short vinculo;
	private Integer matricula;
	private Long cpf;
	private String numeroIdentidade;
	private String email;
	private String nome;
	private Date dtNascimento;

	public boolean isAnd() {
		return and;
	}

	public void setAnd(boolean and) {
		this.and = and;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getNumeroIdentidade() {
		return numeroIdentidade;
	}

	public void setNumeroIdentidade(String numeroIdentidade) {
		this.numeroIdentidade = numeroIdentidade;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

}
