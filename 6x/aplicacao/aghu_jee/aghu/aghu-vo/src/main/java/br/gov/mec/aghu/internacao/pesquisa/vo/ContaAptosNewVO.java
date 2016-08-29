package br.gov.mec.aghu.internacao.pesquisa.vo;

/**
 * VO utilizado para armazenar os valores de retorno de AINK_PES_REF_PRO.conta_aptos_new.
 * 
 * @author Marcelo Tocchetto
 */
public class ContaAptosNewVO {

	private Integer aptos;
	private Integer aptosAdm;
	private Integer aptosAsat;

	public ContaAptosNewVO(Integer aptos, Integer aptosAdm, Integer aptosAsat) {
		super();
		this.aptos = aptos;
		this.aptosAdm = aptosAdm;
		this.aptosAsat = aptosAsat;
	}

	public ContaAptosNewVO() {
	}

	public Integer getAptos() {
		return this.aptos;
	}

	public void setAptos(Integer aptos) {
		this.aptos = aptos;
	}

	public Integer getAptosAdm() {
		return this.aptosAdm;
	}

	public void setAptosAdm(Integer aptosAdm) {
		this.aptosAdm = aptosAdm;
	}

	public Integer getAptosAsat() {
		return this.aptosAsat;
	}

	public void setAptosAsat(Integer aptosAsat) {
		this.aptosAsat = aptosAsat;
	}

}