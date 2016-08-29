package br.gov.mec.aghu.registrocolaborador.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Usuario", description = "Informações do Usuário do Sistema" )
public class Usuario extends Pessoa {

	private static final long serialVersionUID = 1012702369408179852L;
	
	@ApiModelProperty(value = "Vínculo no HCPA", required=true)
	private Short vinculo;
	
	@ApiModelProperty(value = "Matrícula no HCPA", required=true)
	private Integer matricula;
	
	@ApiModelProperty(value = "Nome do usuário no HCPA", required=true)
	private String login;

	public Usuario(Short vinculo, Integer matricula, String login) {
		this.vinculo = vinculo;
		this.matricula = matricula;
		this.login = login;
	}

	public Usuario() {
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
