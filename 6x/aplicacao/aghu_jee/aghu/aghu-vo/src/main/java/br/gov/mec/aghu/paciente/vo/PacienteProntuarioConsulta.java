package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO para representar paciente, prontuário e consulta
 * 
 * @author luismoura
 * 
 */
public class PacienteProntuarioConsulta implements BaseBean {
	private static final long serialVersionUID = -1212025775604297111L;

	private Integer codigo;
	private String nome;
	private Date dtNascimento;

	private Integer prontuario;
	private Integer consulta;

	public PacienteProntuarioConsulta() {

	}

	public PacienteProntuarioConsulta(Integer prontuario, Integer consulta, Integer codigo, String nome, Date dtNascimento) {
		this.prontuario = prontuario;
		this.consulta = consulta;
		this.codigo = codigo;
		this.nome = nome;
		this.dtNascimento = dtNascimento;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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