package br.gov.mec.aghu.certificacaodigital.vo;

import java.io.Serializable;


public class RelatorioControlePendenciasVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -477276519483140641L;
	private String nomePessoaFisica;
	private Integer matriculaId;
	private Short codigoVinculo;
	private String descricaoCentroCusto;
	private Long countDocumentos;
	
	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}
	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}
	public Integer getMatriculaId() {
		return matriculaId;
	}
	public void setMatriculaId(Integer matriculaId) {
		this.matriculaId = matriculaId;
	}
	public Short getCodigoVinculo() {
		return codigoVinculo;
	}
	public void setCodigoVinculo(Short codigoVinculo) {
		this.codigoVinculo = codigoVinculo;
	}
	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}
	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}
	public Long getCountDocumentos() {
		return countDocumentos;
	}
	public void setCountDocumentos(Long countDocumentos) {
		this.countDocumentos = countDocumentos;
	}
	
	

}
