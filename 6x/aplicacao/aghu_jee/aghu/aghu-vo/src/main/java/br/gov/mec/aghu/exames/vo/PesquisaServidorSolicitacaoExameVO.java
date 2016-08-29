package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PesquisaServidorSolicitacaoExameVO {
	
	private Integer matricula;
	private Short vinCodigo;
	private String nome;
	private String nomeUsual;
	private String sigla;
	private String nroRegConselho;
	private Date dtFimVinculo;
	
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNomeUsual() {
		return nomeUsual;
	}
	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getNroRegConselho() {
		return nroRegConselho;
	}
	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	public Date getDtFimVinculo() {
		return dtFimVinculo;
	}
	public void setDtFimVinculo(Date dtFimVinculo) {
		this.dtFimVinculo = dtFimVinculo;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.dtFimVinculo);
		umHashCodeBuilder.append(this.matricula);
		umHashCodeBuilder.append(this.nome);
		umHashCodeBuilder.append(this.nomeUsual);
		umHashCodeBuilder.append(this.nroRegConselho);
		umHashCodeBuilder.append(this.sigla);
		umHashCodeBuilder.append(this.vinCodigo);
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		PesquisaServidorSolicitacaoExameVO other = (PesquisaServidorSolicitacaoExameVO) obj;
		
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.dtFimVinculo, other.dtFimVinculo);
		umEqualsBuilder.append(this.matricula, other.matricula);
		umEqualsBuilder.append(this.nome, other.nome);
		umEqualsBuilder.append(this.nomeUsual, other.nomeUsual);
		umEqualsBuilder.append(this.nroRegConselho, other.nroRegConselho);
		umEqualsBuilder.append(this.sigla, other.sigla);
		umEqualsBuilder.append(this.vinCodigo, other.vinCodigo);
		return umEqualsBuilder.isEquals();
	}

}
