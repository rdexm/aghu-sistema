package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.util.Date;


/**
 * Os dados armazenados nesse objeto representam o detalhamento da movimentaçao do leito
 * 
 * @author Waldenê
 */
public class DetalharLeitosVO {

	private Date criado;          //0
	private Date informado;		//2
	private Integer matricula; 
	private Short vinCodigo;
	private String lancado;         //1

	// GETs AND SEts
	
	public Date getCriado() {
		return criado;
	}
	public void setCriado(Date criado) {
		this.criado = criado;
	}
	public String getLancado() {
		return lancado;
	}
	public void setLancado(String lancado) {
		this.lancado = lancado;
	}
	public Date getInformado() {
		return informado;
	}
	public void setInformado(Date informado) {
		this.informado = informado;
	}
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
	
}
