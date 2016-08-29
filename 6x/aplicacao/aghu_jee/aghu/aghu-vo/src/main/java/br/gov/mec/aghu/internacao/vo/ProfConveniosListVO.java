package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe responsável por agrupar informações a serem exibidos na Pesquisa de Convênios para Profissionais.
 * 
 * @author Waldenê
 * 
 */
public class ProfConveniosListVO implements BaseBean {
	
	private String vinCodigo;
	private String serMatricula;
	private String nome;
	private String cpf;
	private String sigla;
	private String especialidade;
	private String totalConvenios;
	private String seqEspecialidade;

	// GETs e SETs
	
	public String getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(String vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public String getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(String serMatricula) {
		this.serMatricula = serMatricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public void setTotalConvenios(String totalConvenios) {
		this.totalConvenios = totalConvenios;
	}
	public String getTotalConvenios() {
		return totalConvenios;
	}
	public void setSeqEspecialidade(String seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public String getSeqEspecialidade() {
		return seqEspecialidade;
	}


}
