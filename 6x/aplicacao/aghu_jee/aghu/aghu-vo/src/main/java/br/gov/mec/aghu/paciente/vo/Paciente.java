package br.gov.mec.aghu.paciente.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO para armazenar os dados de AipPacientes
 * 
 * @author ihaas
 * 
 */
public class Paciente implements BaseBean {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer prontuario;
	private Integer codigo;
	private String nome;
	private String nomeMae;
	private BigDecimal peso;
	private BigDecimal altura;
	private String idade;
	private Date dtNascimento;
	private Date dtObito;
	private Short dddTelefone;
	private Long telefone;
	private String logradouro;
	private String complLogradouro;
	private Integer nroLogradouro;
	private BigDecimal cep;
	private String bairro;
	private String cidade;
	private String unidadeFederacao;
	private String sexo;
	private String cor;
	
	public Paciente() {

	}


	public Paciente(Integer prontuario, Integer codigo, String nome,
			String nomeMae, Date dtNascimento) {
		this.prontuario = prontuario;
		this.codigo = codigo;
		this.nome = nome;
		this.nomeMae = nomeMae;
		this.dtNascimento = dtNascimento;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
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

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Date getDtObito() {
		return dtObito;
	}

	public void setDtObito(Date dtObito) {
		this.dtObito = dtObito;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public Short getDddTelefone() {
		return dddTelefone;
	}

	public void setDddTelefone(Short dddTelefone) {
		this.dddTelefone = dddTelefone;
	}

	public Long getTelefone() {
		return telefone;
	}

	public void setTelefone(Long telefone) {
		this.telefone = telefone;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public BigDecimal getCep() {
		return cep;
	}

	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUnidadeFederacao() {
		return unidadeFederacao;
	}

	public void setUnidadeFederacao(String unidadeFederacao) {
		this.unidadeFederacao = unidadeFederacao;
	}
	
	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

}