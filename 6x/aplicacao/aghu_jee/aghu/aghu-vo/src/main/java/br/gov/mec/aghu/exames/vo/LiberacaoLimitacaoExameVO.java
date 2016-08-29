package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dansantos
 *
 */
public class LiberacaoLimitacaoExameVO  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1670191413594082543L;
	
	private String sigla;
	
	private Integer manSeq;
	
	private String nomeUsualMaterial;
	
	private Date dthrLimite;
	
	private Integer atdSeq;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}

	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}

	public Date getDthrLimite() {
		return dthrLimite;
	}

	public void setDthrLimite(Date dthrLimite) {
		this.dthrLimite = dthrLimite;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	} 
}
