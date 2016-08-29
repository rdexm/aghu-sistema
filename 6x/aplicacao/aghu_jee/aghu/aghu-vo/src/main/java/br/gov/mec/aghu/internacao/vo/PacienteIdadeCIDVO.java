package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

public class PacienteIdadeCIDVO {

	// Paciente
	private String nomePaciente;
	private String nomeMae;
	private Date dataNascimento;
	private String sexo;
	private Integer idade;
	
	


	// Prontuario
	private Integer prontuario;

	// CID
	private String cid;
	private String cidNome;

	public PacienteIdadeCIDVO(String nomePaciente, String nomeMae, Date dataNascimento, String sexo, Integer idade,
			Integer prontuario, String cid, String cidNome) {
		super();
		this.nomePaciente = nomePaciente;
		this.nomeMae = nomeMae;
		this.dataNascimento = dataNascimento;
		this.sexo = sexo;
		this.idade = idade;
		this.prontuario = prontuario;
		this.cid = cid;
		this.cidNome = cidNome;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCidNome() {
		return cidNome;
	}

	public void setCidNome(String cidNome) {
		this.cidNome = cidNome;
	}

}
