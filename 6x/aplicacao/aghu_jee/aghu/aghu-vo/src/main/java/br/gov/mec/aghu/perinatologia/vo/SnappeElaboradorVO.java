package br.gov.mec.aghu.perinatologia.vo;

import java.util.Date;

import br.gov.mec.aghu.model.McoSnappesId;

public class SnappeElaboradorVO{
	
	private McoSnappesId id;
	private Date criadoEm;
	private Short serVinCodigo;
	private Integer serMatricula;
	private String nome; 
	public McoSnappesId getId() {
		return id;
	}
	public void setId(McoSnappesId id) {
		this.id = id;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	
}