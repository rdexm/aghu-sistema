package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class ImprimirRelatorioMedicoVO implements Serializable {

	private static final long serialVersionUID = -2084193160962213669L;
	
	private String endereco;
	private String enderecoFormatado;
	private String nome;
	private String nomePaciente;
	private String especialidade;
	private String descricao;
	private String usuario;
	private Integer identificacao;
	
	private Integer cep;
	private String cidade;
	private String uf;
	private String telefone;
	
	public void formatarEnderecoParteDois(){
		StringBuilder builder = new StringBuilder(200);
		builder.append("Fone: ").append(telefone).append(" - ").append("CEP: ").append(cep.toString()).append(" - ").append(cidade).append(" , ").append(uf);
	
		enderecoFormatado = builder.toString();
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Integer getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(Integer identificacao) {
		this.identificacao = identificacao;
	}

	public String getEnderecoFormatado() {
		return enderecoFormatado;
	}

	public void setEnderecoFormatado(String enderecoFormatado) {
		this.enderecoFormatado = enderecoFormatado;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}