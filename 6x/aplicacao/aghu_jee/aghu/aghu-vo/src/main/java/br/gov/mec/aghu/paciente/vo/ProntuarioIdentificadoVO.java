package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

/**
 * Os dados armazenados nesse objeto representam os prontuários identificados
 * ulitizados na tela de 'Relatório de Protuários Identificados.'.
 * 
 * @author Ricardo Costa
 */
public class ProntuarioIdentificadoVO {

	private String codigo;
	private String descricao;
	private String descricao1;
	private String descricao2;
	private String prontuario;
	private String nome;
	private String ltoLtoId;
	private String prontuario1;
	private String codigo2;
	private String desprezar;
	private Date dtInicial;
	private Date dtFinal;
	private String nomeMae;
	private String dtNascimento;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao1() {
		return descricao1;
	}

	public void setDescricao1(String descricao1) {
		this.descricao1 = descricao1;
	}

	public String getDescricao2() {
		return descricao2;
	}

	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public String getProntuario1() {
		return prontuario1;
	}

	public void setProntuario1(String prontuario1) {
		this.prontuario1 = prontuario1;
	}

	public String getCodigo2() {
		return codigo2;
	}

	public void setCodigo2(String codigo2) {
		this.codigo2 = codigo2;
	}

	public String getDesprezar() {
		return desprezar;
	}

	public void setDesprezar(String desprezar) {
		this.desprezar = desprezar;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}
	
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getDtNascimento() {
		return dtNascimento;
	}

}
